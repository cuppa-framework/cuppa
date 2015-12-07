package org.forgerock.cuppa;

import static org.forgerock.cuppa.TestResults.*;

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

    TestResults runTest() {
        try {
            function.apply();
            return PASSED_RESULT;
        } catch (AssertionError e) {
            return FAILED_RESULT;
        } catch (Exception e) {
            return ERROR_RESULT;
        }
    }
}
