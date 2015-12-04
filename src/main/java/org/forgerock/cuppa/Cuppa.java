package org.forgerock.cuppa;

import java.util.Stack;

/**
 * Heart of the Cuppa test framework. Responsible for registering and maintaining the state of the
 * tests to be run and running the registered tests and providing the test results back to the test
 * runner.
 *
 * <p>Test class register themselves by simply being instantiated, (if using the recommended format
 * of test classes), and the tests are run by calling {@link #runTests()}.</p>
 *
 * <p>Test runner implementations are responsible for calling {@link #runTests()}, which will run
 * all the registered tests and provide the test results back to the calling test runner for
 * output.</p>
 */
public final class Cuppa {

    private static DescribeBlock root;
    private static Stack<DescribeBlock> stack;
    private static boolean runningTests;

    static {
        reset();
    }

    private Cuppa() {
    }

    /**
     * Registers a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public static void describe(String description, Function function) {
        assertNotRunningTests("describe");
        DescribeBlock currentDescribeBlock = getCurrentDescribeBlock();
        DescribeBlock describeBlock = new DescribeBlock(description);
        currentDescribeBlock.addDescribeBlock(describeBlock);
        stack.push(describeBlock);
        try {
            function.apply();
        } finally {
            stack.pop();
        }
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public static void when(String description, Function function) {
        assertNotRunningTests("when");
        assertNotRootDescribeBlock("when", "describe");
        describe(description, function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test function.
     * @param function The test function.
     */
    public static void it(String description, Function function) {
        assertNotRunningTests("it");
        assertNotRootDescribeBlock("it", "when", "describe");
        getCurrentDescribeBlock().addTest(new TestBlock(description, function));
    }

    private static void assertNotRunningTests(String blockType) {
        if (runningTests) {
            throw new CuppaException(new IllegalStateException("Cannot declare new '" + blockType
                    + "' block whilst running tests"));
        }
    }

    private static void assertNotRootDescribeBlock(String blockType, String... allowedBlockTypes) {
        if (getCurrentDescribeBlock().equals(root)) {
            throw new CuppaException(new IllegalStateException("A '" + blockType + "' must be nested within a "
                    + String.join(", ", allowedBlockTypes) + " function"));
        }
    }

    /**
     * Runs all the tests that have been loaded into the test framework.
     */
    static TestResults runTests() {
        if (stack.size() != 1) {
            throw new IllegalStateException("Invariant broken! The stack should never be empty.");
        }
        runningTests = true;
        root.runTests();
        return root.getTestResults();
    }

    /**
     * For test use only.
     *
     * <p>Resets the test framework state.</p>
     */
    static void reset() {
        runningTests = false;
        root = new DescribeBlock("");
        stack = new Stack<DescribeBlock>() {
            { push(root); }
        };
    }

    private static DescribeBlock getCurrentDescribeBlock() {
        return stack.peek();
    }
}
