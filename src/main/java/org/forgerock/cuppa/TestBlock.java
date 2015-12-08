package org.forgerock.cuppa;

import static org.forgerock.cuppa.Reporter.Outcome.*;

/**
 * Encapsulates the test ('it') function block.
 */
class TestBlock {

    private final String description;
    private final Function function;

    TestBlock(String description, Function function) {
        this.description = description;
        this.function = function;
    }

    void runTest(Reporter reporter) {
        try {
            function.apply();
            reporter.testOutcome(description, PASSED);
        } catch (AssertionError e) {
            reporter.testOutcome(description, FAILED);
        } catch (Exception e) {
            reporter.testOutcome(description, ERRORED);
        }
    }
}
