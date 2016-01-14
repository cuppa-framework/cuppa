package org.forgerock.cuppa.junit;

import org.forgerock.cuppa.CuppaTestProvider;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * A JUnit reporter that adapts between the Cuppa {@link Reporter} interface and JUnit's
 * {@link RunNotifier}.
 */
final class ReportJUnitAdapter implements Reporter {

    private final Class<?> testClass;
    private final RunNotifier notifier;

    /**
     * Constructs a reporter that adapts events to JUnit.
     *
     * @param testClass The class containing the test defintions.
     * @param notifier The JUnit {@link RunNotifier} instance.
     */
    ReportJUnitAdapter(Class<?> testClass, RunNotifier notifier) {
        this.testClass = testClass;
        this.notifier = notifier;
    }

    @Override
    public void start() {
    }

    @Override
    public void end() {
    }

    @Override
    public void describeStart(TestBlock testBlock) {
    }

    @Override
    public void describeEnd(TestBlock testBlock) {
    }

    @Override
    public void hookError(Hook hook, Throwable cause) {
        CuppaTestProvider.filterStackTrace(cause);
        notifier.fireTestFailure(new Failure(getDescription(hook.description.orElse("Hook")), cause));
    }

    @Override
    public void testStart(Test test) {
        notifier.fireTestStarted(getDescription(test.description));
    }

    @Override
    public void testEnd(Test test) {
        notifier.fireTestFinished(getDescription(test.description));
    }

    private Description getDescription(String description) {
        return Description.createTestDescription(testClass, description);
    }

    @Override
    public void testPass(Test test) {
    }

    @Override
    public void testFail(Test test, AssertionError e) {
        CuppaTestProvider.filterStackTrace(e);
        notifier.fireTestFailure(new Failure(getDescription(test.description), e));
    }

    @Override
    public void testError(Test test, Throwable e) {
        CuppaTestProvider.filterStackTrace(e);
        notifier.fireTestFailure(new Failure(getDescription(test.description), e));
    }

    @Override
    public void testPending(Test test) {
        notifier.fireTestIgnored(getDescription(test.description));
    }

    @Override
    public void testSkip(Test test) {
        notifier.fireTestIgnored(getDescription(test.description));
    }
}
