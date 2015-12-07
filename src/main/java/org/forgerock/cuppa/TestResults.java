package org.forgerock.cuppa;

/**
 * Models the result of a one or many test executions. Multiple test results are combined by using
 * the {@link #combine(TestResults)} method.
 */
final class TestResults {

    static final TestResults EMPTY_RESULTS = new TestResults(0, 0, 0);
    static final TestResults PASSED_RESULT = new TestResults(1, 0, 0);
    static final TestResults FAILED_RESULT = new TestResults(0, 1, 0);
    static final TestResults ERROR_RESULT = new TestResults(0, 0, 1);

    private final int passedTestsCount;
    private final int failedTestsCount;
    private final int erroredTestsCount;

    private TestResults(int passedTestsCount, int failedTestsCount, int erroredTestsCount) {
        this.passedTestsCount = passedTestsCount;
        this.failedTestsCount = failedTestsCount;
        this.erroredTestsCount = erroredTestsCount;
    }

    /**
     * Returns a new {@code TestResults} by combining this {@code TestResults} with the specified
     * {@code TestResults}.
     *
     * @param results The results to combine.
     * @return The combined test results.
     */
    TestResults combine(TestResults results) {
        return new TestResults(passedTestsCount + results.passedTestsCount, failedTestsCount + results.failedTestsCount,
                erroredTestsCount + results.erroredTestsCount);
    }

    int getPassedTestsCount() {
        return passedTestsCount;
    }

    int getFailedTestsCount() {
        return failedTestsCount;
    }

    int getErroredTestsCount() {
        return erroredTestsCount;
    }
}
