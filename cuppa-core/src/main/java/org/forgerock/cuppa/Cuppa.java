package org.forgerock.cuppa;

import static org.forgerock.cuppa.Behaviour.NORMAL;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

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

    private static TestBlockBuilder rootBuilder;
    private static Deque<TestBlockBuilder> stack;
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
        describe(NORMAL, description, function);
    }

    /**
     * Registers a described suite of tests to be run.
     *
     * @param behaviour if {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public static void describe(Behaviour behaviour, String description, TestDefinitionFunction function) {
        assertNotRunningTests("describe");
        TestBlockBuilder testBlockBuilder = new TestBlockBuilder(behaviour, description);
        stack.addLast(testBlockBuilder);
        try {
            function.apply();
        } finally {
            stack.removeLast();
            getCurrentDescribeBlock().addTestBlock(testBlockBuilder.build());
        }
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public static void when(String description, TestDefinitionFunction function) {
        when(NORMAL, description, function);
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param behaviour if {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public static void when(Behaviour behaviour, String description, TestDefinitionFunction function) {
        assertNotRunningTests("when");
        assertNotRootDescribeBlock("when", "describe");
        describe(behaviour, description, function);
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
        it(NORMAL, description, function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param behaviour if {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the test function.
     * @param function The test function.
     */
    public static void it(Behaviour behaviour, String description, TestFunction function) {
        assertNotRunningTests("it");
        assertNotRootDescribeBlock("it", "when", "describe");
        getCurrentDescribeBlock().addTest(new Test(behaviour, description, function));
    }

    /**
     * Registers a pending test function that has yet to be implemented.
     *
     * @param description The description of the test function.
     */
    public static void it(String description) {
        getCurrentDescribeBlock().addTest(new Test(NORMAL, description, () -> {
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
        if (getCurrentDescribeBlock().equals(rootBuilder)) {
            throw new CuppaException(new IllegalStateException("A '" + blockType + "' must be nested within a "
                    + String.join(", ", allowedBlockTypes) + " function"));
        }
    }

    /**
     * Runs all the tests that have been loaded into the test framework.
     *
     * @param reporter A reporter to apprise of test outcomes.
     */
    public static void runTests(Reporter reporter) {
        if (stack.size() != 1) {
            throw new IllegalStateException("runTests cannot be run from within a 'describe' or 'when'");
        }
        runningTests = true;
        TestBlock rootBlock = rootBuilder.build();
        reporter.start();
        new TestRunner().runTests(rootBlock, rootBlock.hasOnlyTests(), reporter);
        reporter.end();
    }

    /**
     * For test use only.
     *
     * <p>Resets the test framework state.</p>
     */
    public static void reset() {
        runningTests = false;
        rootBuilder = new TestBlockBuilder(NORMAL, "");
        stack = new ArrayDeque<>();
        stack.addLast(rootBuilder);
    }

    /**
     * Modify a {@link Throwable}'s stacktrace by removing any stack elements that are not relevant to a test. If the
     * {@link Throwable} has a cause, it will also be modified. The modification is applied to all transitive causes.
     *
     * @param throwable a throwable to modify.
     */
    public static void filterStackTrace(Throwable throwable) {
        throwable.setStackTrace(filterStackTrace(throwable.getStackTrace()));
        if (throwable.getCause() != null) {
            filterStackTrace(throwable.getCause());
        }
    }

    private static StackTraceElement[] filterStackTrace(StackTraceElement[] stackTraceElements) {
        Optional<StackTraceElement> first = Arrays.stream(stackTraceElements)
                .filter(s -> s.getClassName().startsWith(Cuppa.class.getPackage().getName()))
                .findFirst();
        if (first.isPresent()) {
            int index = Arrays.asList(stackTraceElements).indexOf(first.get());
            return Arrays.copyOf(stackTraceElements, index);
        }
        return stackTraceElements;
    }

    private static TestBlockBuilder getCurrentDescribeBlock() {
        return stack.getLast();
    }

    public static TestBlock getRootTestBlock() {
        return rootBuilder.build();
    }
}
