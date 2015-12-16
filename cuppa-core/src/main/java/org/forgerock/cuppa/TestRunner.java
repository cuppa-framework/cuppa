package org.forgerock.cuppa;

import static org.forgerock.cuppa.Behaviour.ONLY;
import static org.forgerock.cuppa.Behaviour.SKIP;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 *
 */
public class TestRunner {

    public void runTests(TestBlock testBlock, boolean ignoreTestsNotMarkedAsOnly, Reporter reporter) {
        runTests(testBlock, ignoreTestsNotMarkedAsOnly, testBlock.behaviour, reporter, TestFunction::apply);
    }

    private void runTests(TestBlock testBlock, boolean ignoreTestsNotMarkedAsOnly, Behaviour behaviour,
            Reporter reporter, TestWrapper outerTestWrapper) {
        Behaviour combinedBehaviour = behaviour.combine(testBlock.behaviour);
        TestWrapper testWrapper = createWrapper(testBlock, outerTestWrapper, reporter);
        try {
            reporter.describeStart(testBlock);
            for (Hook hook : testBlock.beforeHooks) {
                try {
                    hook.function.apply();
                } catch (Exception e) {
                    reporter.hookError(hook, e);
                    return;
                }
            }
            for (Test t : testBlock.tests) {
                if (ignoreTestsNotMarkedAsOnly && combinedBehaviour.combine(t.behaviour) == ONLY
                        || !ignoreTestsNotMarkedAsOnly) {
                    testWrapper.apply(() -> runTest(t, combinedBehaviour, reporter));
                }
            }
            testBlock.testBlocks.stream().forEach((d) -> {
                runTests(d, ignoreTestsNotMarkedAsOnly, combinedBehaviour, reporter, testWrapper);
            });
        } catch (HookException e) {
            if (e.getTestBlock() != testBlock) {
                throw e;
            }
        } catch (Exception e) {
            // This should never happen if the test framework is correct because all exceptions from user code should've
            // been caught by now.
            throw new RuntimeException(e);
        } finally {
            runAfterHooks(testBlock, reporter);
            reporter.describeEnd(testBlock);
        }
    }

    private void runTest(Test test, Behaviour behaviour, Reporter reporter) {
        if (behaviour.combine(test.behaviour) != SKIP) {
            try {
                test.function.apply();
                reporter.testPass(test);
            } catch (AssertionError e) {
                reporter.testFail(test, e);
            } catch (PendingException e) {
                reporter.testPending(test);
            } catch (Exception e) {
                reporter.testError(test, e);
            }
        } else {
            reporter.testSkip(test);
        }
    }

    private TestWrapper createWrapper(TestBlock testBlock, TestWrapper outerTestRunner, Reporter reporter) {
        return outerTestRunner.compose((f) -> {
            try {
                for (Hook hook : testBlock.beforeEachHooks) {
                    try {
                        hook.function.apply();
                    } catch (Exception e) {
                        reporter.hookError(hook, e);
                        throw new HookException(testBlock, e);
                    }
                }
                f.apply();
            } finally {
                for (Hook hook : testBlock.afterEachHooks) {
                    try {
                        hook.function.apply();
                    } catch (Exception e) {
                        reporter.hookError(hook, e);
                        throw new HookException(testBlock, e);
                    }
                }
            }
        });
    }

    private void runAfterHooks(TestBlock testBlock, Reporter reporter) {
        for (Hook hook : testBlock.afterHook) {
            try {
                hook.function.apply();
            } catch (Exception e) {
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
