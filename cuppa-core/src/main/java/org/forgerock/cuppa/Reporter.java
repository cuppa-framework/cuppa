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
     * Records the outcome of a test.
     *
     * @param description The description of the test.
     * @param outcome The outcome of the test.
     */
    void testOutcome(String description, Outcome outcome);

    /**
     * The possible outcomes of a test.
     */
    enum Outcome {
        PASSED,
        FAILED,
        ERRORED
    }
}
