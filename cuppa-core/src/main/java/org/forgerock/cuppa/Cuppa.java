package org.forgerock.cuppa;

import java.util.Optional;
import java.util.Stack;

/**
 * Heart of the Cuppa test framework. Responsible for registering and maintaining the state of the
 * tests to be run and running the registered tests and providing the test results back to the test
 * runner.
 *
 * <p>Test class register themselves by simply being instantiated, (if using the recommended format
 * of test classes), and the tests are run by calling {@link #runTests(Reporter)}.</p>
 *
 * <p>Test runner implementations are responsible for calling {@link #runTests(Reporter)}, which
 * will run all the registered tests and provide the test results back to the calling test runner
 * for output.</p>
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
    public static void describe(String description, TestDefinitionFunction function) {
        assertNotRunningTests("describe");
        DescribeBlock describeBlock = new DescribeBlock(description);
        getCurrentDescribeBlock().addDescribeBlock(describeBlock);
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
    public static void when(String description, TestDefinitionFunction function) {
        assertNotRunningTests("when");
        assertNotRootDescribeBlock("when", "describe");
        describe(description, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param function The 'before' block.
     */
    public static void before(HookFunction function) {
        before(null, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param description The description of the 'before' block.
     * @param function The 'before' block.
     */
    public static void before(String description, HookFunction function) {
        assertNotRunningTests("before");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBefore(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param function The 'after' block.
     */
    public static void after(HookFunction function) {
        after(null, function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param description The description of the 'after' block.
     * @param function The 'after' block.
     */
    public static void after(String description, HookFunction function) {
        assertNotRunningTests("after");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addAfter(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param function The 'beforeEach' block.
     */
    public static void beforeEach(HookFunction function) {
        beforeEach(null, function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param description The description of the 'beforeEach' block.
     * @param function The 'beforeEach' block.
     */
    public static void beforeEach(String description, HookFunction function) {
        assertNotRunningTests("beforeEach");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBeforeEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param function The 'afterEach' block.
     */
    public static void afterEach(HookFunction function) {
        afterEach(null, function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param description The description of the 'afterEach' block.
     * @param function The 'afterEach' block.
     */
    public static void afterEach(String description, HookFunction function) {
        assertNotRunningTests("afterEach");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addAfterEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param description The description of the test function.
     * @param function The test function.
     */
    public static void it(String description, TestFunction function) {
        assertNotRunningTests("it");
        assertNotRootDescribeBlock("it", "when", "describe");
        getCurrentDescribeBlock().addTest(new TestBlock(description, function));
    }

    /**
     * Registers a pending test function that has yet to be implemented.
     *
     * @param description The description of the test function.
     */
    public static void it(String description) {
        getCurrentDescribeBlock().addTest(new TestBlock(description, () -> {
            throw new PendingException();
        }));
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
    static void runTests(Reporter reporter) {
        if (stack.size() != 1) {
            throw new IllegalStateException("Invariant broken! The stack should never be empty.");
        }
        runningTests = true;
        reporter.start();
        root.runTests(reporter);
        reporter.end();
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
