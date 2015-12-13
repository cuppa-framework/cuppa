package org.forgerock.cuppa;

/**
 * Encapsulates the test ('it') function block.
 */
class TestBlock {

    private final String description;
    private final TestFunction function;

    TestBlock(String description, TestFunction function) {
        this.description = description;
        this.function = function;
    }

    void runTest(Reporter reporter) {
        try {
            function.apply();
            reporter.testPass(description);
        } catch (AssertionError e) {
            reporter.testFail(description, e);
        } catch (Exception e) {
            reporter.testError(description, e);
        }
    }
}
