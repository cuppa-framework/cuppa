/*
 * Copyright 2015-2016 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.forgerock.cuppa;

import static org.forgerock.cuppa.model.HookType.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.internal.EmptyTestBlockFilter;
import org.forgerock.cuppa.internal.HookException;
import org.forgerock.cuppa.internal.OnlyTestBlockFilter;
import org.forgerock.cuppa.internal.TagTestBlockFilter;
import org.forgerock.cuppa.internal.TestContainer;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Runs Cuppa tests.
 */
public final class Runner {
    private static final ServiceLoader<ConfigurationProvider> CONFIGURATION_PROVIDER_LOADER
            = ServiceLoader.load(ConfigurationProvider.class);
    private static final TestBlock EMPTY_TEST_BLOCK = new TestBlock(Behaviour.NORMAL, Cuppa.class, "",
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), new Options());

    private final List<Function<TestBlock, TestBlock>> coreTestTransforms;
    private final Configuration configuration;

    /**
     * Creates a new runner with no run tags and a configuration loaded from the classpath.
     */
    public Runner() {
        this(Tags.EMPTY_TAGS);
    }

    /**
     * Creates a new runner with the given run tags and a configuration loaded from the classpath.
     *
     * @param runTags Tags to filter the tests on.
     */
    public Runner(Tags runTags) {
        this(runTags, getConfiguration());
    }

    /**
     * Creates a new runner with the given run tags and configuration.
     *
     * @param runTags Tags to filter the tests on.
     * @param configuration Cuppa configuration to control the behaviour of the runner.
     */
    public Runner(Tags runTags, Configuration configuration) {
        coreTestTransforms = Arrays.asList(new OnlyTestBlockFilter(), new TagTestBlockFilter(runTags),
                new EmptyTestBlockFilter());
        this.configuration = configuration;
    }

    /**
     * Runs the tests contained in the provided test block and any nested test blocks, using the provided reporter.
     *
     * @param rootBlock The root test block that contains all tests to be run.
     * @param reporter The reporter to use to report test results.
     */
    public void run(TestBlock rootBlock, Reporter reporter) {
        TestContainer.INSTANCE.runTests(() -> {
            reporter.start();
            TestBlock transformedRootBlock = transformTests(rootBlock, configuration.testTransforms);
            runTests(transformedRootBlock, transformedRootBlock.behaviour, reporter, TestFunction::apply);
            reporter.end();
        });
    }

    /**
     * Instantiates the test classes, which define tests as side effects, and return the root test block.
     *
     * @param testClasses The test classes that contain the tests to be executed.
     * @return The root block that contains all other test blocks and their tests.
     */
    public TestBlock defineTests(Iterable<Class<?>> testClasses) {
        return defineTests(testClasses, configuration.testInstantiator);
    }

    private TestBlock defineTests(Iterable<Class<?>> testClasses, TestInstantiator testInstantiator) {
        return StreamSupport.stream(testClasses.spliterator(), false)
                .map(c -> TestContainer.INSTANCE.defineTests(c, () -> {
                    try {
                        testInstantiator.instantiate(c);
                    } catch (CuppaException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to instantiate test class", e);
                    }
                }))
                .reduce(EMPTY_TEST_BLOCK, this::mergeRootTestBlocks);
    }

    private TestBlock mergeRootTestBlocks(TestBlock testBlock1, TestBlock testBlock2) {
        return new TestBlock(Behaviour.NORMAL, Cuppa.class, "", Stream.concat(testBlock1.testBlocks.stream(),
                testBlock2.testBlocks.stream()).collect(Collectors.toList()), Collections.emptyList(),
                Collections.emptyList(), new Options());
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        Iterator<ConfigurationProvider> iterator = CONFIGURATION_PROVIDER_LOADER.iterator();
        if (iterator.hasNext()) {
            ConfigurationProvider configurationProvider = iterator.next();
            if (iterator.hasNext()) {
                throw new CuppaException("There must only be a single configuration provider available on the "
                        + "classpath");
            }
            configurationProvider.configure(configuration);
        }
        return configuration;
    }

    private TestBlock transformTests(TestBlock rootBlock, List<Function<TestBlock, TestBlock>> transforms) {
        return Stream.concat(transforms.stream(), coreTestTransforms.stream())
                .reduce(Function.identity(), Function::andThen)
                .apply(rootBlock);
    }

    private void runTests(TestBlock testBlock, Behaviour behaviour, Reporter reporter, TestWrapper outerTestWrapper) {
        Behaviour combinedBehaviour = behaviour.combine(testBlock.behaviour);
        TestWrapper testWrapper = createWrapper(testBlock, outerTestWrapper, reporter);
        try {
            reporter.describeStart(testBlock);
            for (Hook hook : testBlock.hooksOfType(BEFORE)) {
                try {
                    hook.function.apply();
                } catch (Throwable e) {
                    reporter.hookError(hook, e);
                    return;
                }
            }
            for (org.forgerock.cuppa.model.Test t : testBlock.tests) {
                testWrapper.apply(() -> runTest(t, combinedBehaviour, reporter));
            }
            testBlock.testBlocks.stream()
                    .forEach((d) -> runTests(d, combinedBehaviour, reporter, testWrapper));
        } catch (HookException e) {
            if (e.getTestBlock() != testBlock) {
                throw e;
            }
        } catch (Throwable e) {
            // This should never happen if the test framework is correct because
            // all exceptions from user code should've been caught by now.
            throw new RuntimeException(e);
        } finally {
            runAfterHooks(testBlock, reporter);
            reporter.describeEnd(testBlock);
        }
    }

    private void runTest(org.forgerock.cuppa.model.Test test, Behaviour behaviour, Reporter reporter) {
        if (!test.function.isPresent()) {
            reporter.testPending(test);
        } else if (behaviour.combine(test.behaviour) != Behaviour.SKIP) {
            try {
                reporter.testStart(test);
                test.function.get().apply();
                reporter.testPass(test);
            } catch (Throwable e) {
                reporter.testFail(test, e);
            } finally {
                reporter.testEnd(test);
            }
        } else {
            reporter.testSkip(test);
        }
    }

    private TestWrapper createWrapper(TestBlock testBlock, TestWrapper outerTestRunner, Reporter reporter) {
        return outerTestRunner.compose((f) -> {
            try {
                for (Hook hook : testBlock.hooksOfType(BEFORE_EACH)) {
                    try {
                        hook.function.apply();
                    } catch (Throwable e) {
                        reporter.hookError(hook, e);
                        throw new HookException(testBlock, e);
                    }
                }
                f.apply();
            } finally {
                for (Hook hook : testBlock.hooksOfType(AFTER_EACH)) {
                    try {
                        hook.function.apply();
                    } catch (Throwable e) {
                        reporter.hookError(hook, e);
                        throw new HookException(testBlock, e);
                    }
                }
            }
        });
    }

    private void runAfterHooks(TestBlock testBlock, Reporter reporter) {
        for (Hook hook : testBlock.hooksOfType(AFTER)) {
            try {
                hook.function.apply();
            } catch (Throwable e) {
                reporter.hookError(hook, e);
                return;
            }
        }
    }

    @FunctionalInterface
    private interface TestWrapper {
        void apply(TestFunction testRunner) throws Exception;

        default TestWrapper compose(TestWrapper after) {
            return (f) -> apply(() -> after.apply(f));
        }
    }
}
