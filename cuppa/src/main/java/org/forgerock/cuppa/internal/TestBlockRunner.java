/*
 * Copyright 2016 ForgeRock AS.
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

import static org.forgerock.cuppa.model.Behaviour.SKIP;
import static org.forgerock.cuppa.model.HookType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.HookType;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Runs the tests within a test block.
 */
public final class TestBlockRunner {
    private final TestBlock testBlock;
    private final List<TestBlockRunner> parents;
    private final List<TestBlockRunner> children = new ArrayList<>();
    private final Reporter reporter;
    private boolean skipTests;

    /**
     * Constructs a new test block runner.
     *
     * @param testBlock The test block.
     * @param parents The parent runners of this runner.
     * @param reporter The reporter.
     */
    public TestBlockRunner(TestBlock testBlock, List<TestBlockRunner> parents, Reporter reporter) {
        this.testBlock = testBlock;
        this.parents = parents;
        this.reporter = reporter;
        skipTests = testBlock.behaviour == SKIP;
    }

    /**
     * Adds a runner for a child test block.
     *
     * @param runner A runner.
     */
    public void addChild(TestBlockRunner runner) {
        children.add(runner);
    }

    /**
     * Runs all the tests in this test block and any nested test blocks.
     */
    public void run() {
        boolean runBlockHooks = !shouldSkipTests();
        reporter.testBlockStart(testBlock, blocksFromRunners(parents));
        if (runBlockHooks) {
            runBlockHooks(BEFORE);
        }
        for (Test test : testBlock.tests) {
            List<TestBlockRunner> testParents = parentsIncludeThis();
            if (!test.function.isPresent()) {
                reporter.testPending(test, blocksFromRunners(testParents));
            } else if (shouldSkipTests() || test.behaviour == SKIP) {
                reporter.testSkip(test, blocksFromRunners(testParents));
            } else {
                parents.get(0).runTest(test, testParents);
            }
        }
        children.forEach(TestBlockRunner::run);
        if (runBlockHooks) {
            runBlockHooks(AFTER);
        }
        reporter.testBlockEnd(testBlock, blocksFromRunners(parents));
    }

    private void runTest(Test test, List<TestBlockRunner> testParents) {
        List<TestBlock> testParentBlocks = blocksFromRunners(testParents);
        boolean beforeEachHooksFailed = runTestHooks(BEFORE_EACH, test, testParentBlocks);
        if (beforeEachHooksFailed) {
            reporter.testSkip(test, blocksFromRunners(testParents));
        } else {
            boolean isTestInThisRunner = testParents.size() == parents.size() + 1;
            if (isTestInThisRunner) {
                try {
                    reporter.testStart(test, testParentBlocks);
                    test.function.get().apply();
                    reporter.testPass(test, testParentBlocks);
                } catch (Throwable e) {
                    reporter.testFail(test, testParentBlocks, e);
                } finally {
                    reporter.testEnd(test, testParentBlocks);
                }
            } else {
                testParents.get(parents.size() + 1).runTest(test, testParents);
            }
        }
        runTestHooks(AFTER_EACH, test, blocksFromRunners(testParents));
    }

    private boolean runTestHooks(HookType hookType, Test test, List<TestBlock> testParents) {
        return runHooks(testBlock.hooksOfType(hookType), (hook, e) -> {
            reporter.testHookFail(hook, blocksFromRunners(parentsIncludeThis()), test, testParents, e);
            reporter.hookFail(hook, blocksFromRunners(parentsIncludeThis()), e);
        });
    }

    private boolean runBlockHooks(HookType hookType) {
        return runHooks(testBlock.hooksOfType(hookType), (hook, e) -> {
            reporter.blockHookFail(hook, blocksFromRunners(parentsIncludeThis()), e);
            reporter.hookFail(hook, blocksFromRunners(parentsIncludeThis()), e);
        });
    }

    private boolean runHooks(List<Hook> hooks, BiConsumer<Hook, Throwable> failHandler) {
        for (Hook hook : hooks) {
            try {
                hook.function.apply();
            } catch (Throwable e) {
                failHandler.accept(hook, e);
                skipTests = true;
                return true;
            }
        }
        return false;
    }

    private List<TestBlockRunner> parentsIncludeThis() {
        return Stream.concat(parents.stream(), Stream.of(this)).collect(Collectors.toList());
    }

    private boolean shouldSkipTests() {
        return skipTests || parents.stream().anyMatch(p -> p.skipTests);
    }

    private static List<TestBlock> blocksFromRunners(List<TestBlockRunner> runners) {
        return runners.stream().map(r -> r.testBlock).collect(Collectors.toList());
    }
}
