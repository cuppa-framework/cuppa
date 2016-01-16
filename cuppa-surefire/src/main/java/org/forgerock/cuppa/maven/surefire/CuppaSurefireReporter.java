package org.forgerock.cuppa.maven.surefire;

import org.apache.maven.surefire.report.PojoStackTraceWriter;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.report.SimpleReportEntry;
import org.forgerock.cuppa.CuppaTestProvider;
import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Reporter for Maven Surefire and Failsafe plugins.
 */
final class CuppaSurefireReporter implements Reporter {

    private final RunListener listener;

    /**
     * Constructs a reporter that adapts events to Surefire.
     *
     * @param listener The JUnit {@link RunListener} instance.
     */
    CuppaSurefireReporter(RunListener listener) {
        this.listener = listener;
    }

    @Override
    public void start() {
        listener.testSetStarting(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void end() {
        listener.testSetCompleted(new SimpleReportEntry(CuppaSurefireProvider.class.getName(), "Cuppa"));
    }

    @Override
    public void describeStart(TestBlock testBlock) {
    }

    @Override
    public void describeEnd(TestBlock testBlock) {
    }

    @Override
    public void hookError(Hook hook, Throwable cause) {
    }

    @Override
    public void testStart(org.forgerock.cuppa.model.Test test) {
        listener.testStarting(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description));
    }

    @Override
    public void testEnd(org.forgerock.cuppa.model.Test test) {
    }

    @Override
    public void testPass(org.forgerock.cuppa.model.Test test) {
        listener.testSucceeded(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description));
    }

    @Override
    public void testFail(org.forgerock.cuppa.model.Test test, AssertionError cause) {
        CuppaTestProvider.filterStackTrace(cause);
        listener.testFailed(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description,
                new PojoStackTraceWriter(test.testClass.getCanonicalName(), test.description, cause), 0));
    }

    @Override
    public void testError(org.forgerock.cuppa.model.Test test, Throwable cause) {
        CuppaTestProvider.filterStackTrace(cause);
        listener.testError(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description,
                new PojoStackTraceWriter(test.testClass.getCanonicalName(), test.description, cause), 0));
    }

    @Override
    public void testPending(org.forgerock.cuppa.model.Test test) {
        listener.testSkipped(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description));
    }

    @Override
    public void testSkip(org.forgerock.cuppa.model.Test test) {
        listener.testSkipped(new SimpleReportEntry(test.testClass.getCanonicalName(), test.description));
    }
}
