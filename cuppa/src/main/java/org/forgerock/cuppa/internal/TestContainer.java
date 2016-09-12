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

package org.forgerock.cuppa.internal;

import static org.forgerock.cuppa.model.Behaviour.NORMAL;
import static org.forgerock.cuppa.model.TestBlockType.ROOT;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.forgerock.cuppa.Cuppa;
import org.forgerock.cuppa.CuppaException;
import org.forgerock.cuppa.TestBuilder;
import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Option;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.TagsOption;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.model.TestBlockType;

/**
 * Singleton container for user-defined tests.
 */
public enum TestContainer {
    /**
     * The singleton.
     */
    INSTANCE;

    private final ThreadLocal<Deque<Context>> contexts = ThreadLocal.withInitial(() -> new ArrayDeque<>());

    /**
     * Registers a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public void describe(String description, TestBlockFunction function) {
        new TestBuilderImpl().describe(description, function);
    }

    /**
     * Returns a builder for registering a described suite of tests to be run.
     *
     * @param type The type of the test block.
     * @param behaviour If {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the 'describe' block.
     * @param options The set of options applied to the test block.
     */
    void testBlock(TestBlockType type, Behaviour behaviour, String description, TestBlockFunction function,
            Options options) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("describe");
        TestBlockBuilder testBlockBuilder = new TestBlockBuilder(type, behaviour, context.testClass, description,
                options);
        context.stack.addLast(testBlockBuilder);
        try {
            function.apply();
        } finally {
            context.stack.removeLast();
            context.getCurrentDescribeBlock().addTestBlock(testBlockBuilder.build());
        }
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public void when(String description, TestBlockFunction function) {
        assertIsInTestDefinitionContext("when");
        new TestBuilderImpl().when(description, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param function The 'before' block.
     */
    public void before(HookFunction function) {
        before(null, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param description The description of the 'before' block.
     * @param function The 'before' block.
     */
    public void before(String description, HookFunction function) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("before");
        assertNotRootDescribeBlock("before");
        context.getCurrentDescribeBlock().addBefore(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param function The 'after' block.
     */
    public void after(HookFunction function) {
        after(null, function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param description The description of the 'after' block.
     * @param function The 'after' block.
     */
    public void after(String description, HookFunction function) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("after");
        assertNotRootDescribeBlock("after");
        context.getCurrentDescribeBlock().addAfter(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(HookFunction function) {
        beforeEach(null, function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param description The description of the 'beforeEach' block.
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(String description, HookFunction function) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("beforeEach");
        assertNotRootDescribeBlock("beforeEach");
        context.getCurrentDescribeBlock().addBeforeEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param function The 'afterEach' block.
     */
    public void afterEach(HookFunction function) {
        afterEach(null, function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param description The description of the 'afterEach' block.
     * @param function The 'afterEach' block.
     */
    public void afterEach(String description, HookFunction function) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("afterEach");
        assertNotRootDescribeBlock("afterEach");
        context.getCurrentDescribeBlock().addAfterEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test function.
     * @param function The test function.
     */
    public void it(String description, TestFunction function) {
        it(NORMAL, description, Optional.of(function), new Options());
    }

    /**
     * Registers a pending test.
     *
     * <p>A pending test has no implementation and acts as a reminder to the developer to write the implementation
     * later.</p>
     *
     * @param description The description of the test.
     */
    public void it(String description) {
        it(NORMAL, description, Optional.empty(), new Options());
    }

    /**
     * Registers a test function to be run.
     *
     * @param behaviour If {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the test function.
     * @param function The test function.
     * @param options The set of options applied to the test.
     */
    public void it(Behaviour behaviour, String description, Optional<TestFunction> function, Options options) {
        TestDefinitionContext context = assertIsInTestDefinitionContext("it");
        assertNotRootDescribeBlock("it");
        context.getCurrentDescribeBlock()
                .addTest(new Test(behaviour, context.testClass, description, function, options));
    }


    /**
     * Decorate a test or block of tests with additional options.
     *
     * @param options Options to apply to the test/block.
     * @return An object for building a test or test block with the given options.
     */
    public TestBuilder with(Option... options) {
        TestBuilderImpl testBuilder = new TestBuilderImpl();
        return testBuilder.with(options);
    }

    /**
     * Mark a test or block of tests to be skipped.
     *
     * @return An object for building the test or test block that will be skipped.
     */
    public TestBuilder skip() {
        return new TestBuilderImpl().skip();
    }

    /**
     * Mark a test or block of tests as the only tests that should be run.
     *
     * @return An object for building the test or test block that will be run.
     */
    public TestBuilder only() {
        return new TestBuilderImpl().only();
    }

    /**
     * Decorates tests with a set of tags. Tags can be used to group tests together to be included or excluded from a
     * test run.
     *
     * @param tags String identifiers that can be used when running Cuppa to include or exclude a test/block.
     * @return An option.
     */
    public Option tags(String... tags) {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(tags));
        return new TagsOption(set);
    }

    /**
     * For internal use only. Code that executes Cuppa tests should be wrapped in this method, which will ensure that
     * test code doesn't try to declare more tests.
     *
     * @param testRunner A function that will run tests.
     */
    public void runTests(Runnable testRunner) {
        contexts.get().addLast(new TestRunContext());
        try {
            testRunner.run();
        } finally {
            contexts.get().removeLast();
        }
    }

    /**
     * Define tests within the context of a test class. All code that may call {@link Cuppa} methods
     * should be wrapped in this method.
     *
     * @param testClass The class that the tests are defined in.
     * @param testDefiner A function that will define tests, usually by instantiating the given test class.
     * @return A test block containing all the defined tests.
     */
    public TestBlock defineTests(Class<?> testClass, Runnable testDefiner) {
        TestDefinitionContext context = new TestDefinitionContext(testClass);
        contexts.get().addLast(context);
        try {
            testDefiner.run();
            return context.rootBuilder.build();
        } finally {
            contexts.get().removeLast();
        }
    }

    private TestDefinitionContext assertIsInTestDefinitionContext(String blockType) {
        if (contexts.get().isEmpty()) {
            throw new CuppaException("Attempted to defined Cuppa tests from outside of Cuppa's control. Is something"
                    + " else instantiating your test class?");
        }
        if (contexts.get().getLast() instanceof TestRunContext) {
            throw new CuppaException("'" + blockType + "' may only be nested within a 'describe' or 'when' block");
        }
        return (TestDefinitionContext) contexts.get().getLast();
    }

    private void assertNotRootDescribeBlock(String blockType) {
        TestDefinitionContext context = assertIsInTestDefinitionContext(blockType);
        if (context.getCurrentDescribeBlock().equals(context.rootBuilder)) {
            throw new CuppaException("'" + blockType + "' must be nested within a 'describe' or 'when' block");
        }
    }

    private interface Context {
    }

    private static final class TestDefinitionContext implements Context {
        private final Deque<TestBlockBuilder> stack = new ArrayDeque<>();
        private final TestBlockBuilder rootBuilder;
        private final Class<?> testClass;

        private TestDefinitionContext(Class<?> testClass) {
            this.testClass = testClass;
            rootBuilder = new TestBlockBuilder(ROOT, NORMAL, testClass, "", new Options());
            stack.addLast(rootBuilder);
        }

        private TestBlockBuilder getCurrentDescribeBlock() {
            return stack.getLast();
        }
    }

    private static final class TestRunContext implements Context {
    }
}
