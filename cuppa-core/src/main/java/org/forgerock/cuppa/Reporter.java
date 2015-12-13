package org.forgerock.cuppa;

/**
 * A strategy for reporting on a suite of test runs.
 */
public interface Reporter {

    /**
     * Called before suite is run.
     */
    void start();

    /**
     * Called after suite run has completed.
     */
    void end();

    /**
     * Called before any tests are run in a describe block.
     *
     * @param description The description of the block.
     */
    void describeStart(String description);

    /**
     * Called after all tests, in a describe block, have completed.
     *
     * @param description The description of the block.
     */
    void describeEnd(String description);

    /**
     * Called after a test has successfully executed without throwing an exception.
     *
     * @param description The description of the test.
     */
    void testPass(String description);

    /**
     * Called after a test has failed due to throwing a assertion error.
     *
     * @param description The description of the test.
     * @param cause The assertion error that the test threw.
     */
    void testFail(String description, AssertionError cause);

    /**
     * Called after a test has failed due to throwing an exception that wasn't an assertion error.
     *
     * @param description The description of the test.
     * @param cause The throwable that the test threw.
     */
    void testError(String description, Throwable cause);
}
