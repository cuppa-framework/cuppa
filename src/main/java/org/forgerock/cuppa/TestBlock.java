package org.forgerock.cuppa;

import static org.forgerock.cuppa.TestResults.*;

/**
 * Encapsulates the test ('it') function block.
 */
class TestBlock {

    private final String description;
    private final Function function;
    private Result result;

    TestBlock(String description, Function function) {
        this.description = description;
        this.function = function;
    }

    void runTest() {
        try {
            function.apply();
            result = Result.PASSED;
        } catch (AssertionError e) {
            result = Result.FAILED;
        } catch (Exception e) {
            result = Result.ERROR;
        }
    }

    TestResults getTestResults() {
        switch (result) {
            case PASSED:
                return PASSED_RESULT;
            case FAILED:
                return FAILED_RESULT;
            case ERROR:
                return ERROR_RESULT;
            default:
                throw new IllegalStateException("What!! That's not cricket!");
        }
    }

    /**
     * Represents the outcome state of a test run.
     */
    private enum Result {
        PASSED,
        FAILED,
        ERROR
    }
}
