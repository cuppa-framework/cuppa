package org.forgerock.cuppa.internal;

import static org.forgerock.cuppa.model.Behaviour.NORMAL;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import org.forgerock.cuppa.CuppaException;
import org.forgerock.cuppa.functions.HookFunction;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * Singleton container for user-defined tests.
 */
public enum TestContainer {
    /**
     * The singleton.
     */
    INSTANCE;

    private TestBlockBuilder rootBuilder;
    private Deque<TestBlockBuilder> stack;
    private boolean runningTests;

    TestContainer() {
        reset();
    }

    /**
     * Registers a described suite of tests to be run.
     *
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public void describe(String description, TestBlockFunction function) {
        describe(NORMAL, description, function);
    }

    /**
     * Registers a described suite of tests to be run.
     *
     * @param behaviour If {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the 'describe' block.
     * @param function The 'describe' block.
     */
    public void describe(Behaviour behaviour, String description, TestBlockFunction function) {
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
    public void when(String description, TestBlockFunction function) {
        when(NORMAL, description, function);
    }

    /**
     * Registers a 'when' block to be run.
     *
     * @param behaviour If {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the 'when' block.
     * @param function The 'when' block.
     */
    public void when(Behaviour behaviour, String description, TestBlockFunction function) {
        assertNotRunningTests("when");
        assertNotRootDescribeBlock("when", "when", "describe");
        describe(behaviour, description, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param function The 'before' block.
     */
    public void before(HookFunction function) {
        before(null, function);
    }

    /**
     * Registers a 'before' block to be run.
     *
     * @param description The description of the 'before' block.
     * @param function The 'before' block.
     */
    public void before(String description, HookFunction function) {
        assertNotRunningTests("before");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBefore(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param function The 'after' block.
     */
    public void after(HookFunction function) {
        after(null, function);
    }

    /**
     * Registers a 'after' block to be run.
     *
     * @param description The description of the 'after' block.
     * @param function The 'after' block.
     */
    public void after(String description, HookFunction function) {
        assertNotRunningTests("after");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addAfter(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(HookFunction function) {
        beforeEach(null, function);
    }

    /**
     * Registers a 'beforeEach' block to be run.
     *
     * @param description The description of the 'beforeEach' block.
     * @param function The 'beforeEach' block.
     */
    public void beforeEach(String description, HookFunction function) {
        assertNotRunningTests("beforeEach");
        assertNotRootDescribeBlock("when", "describe");
        getCurrentDescribeBlock().addBeforeEach(Optional.ofNullable(description), function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param function The 'afterEach' block.
     */
    public void afterEach(HookFunction function) {
        afterEach(null, function);
    }

    /**
     * Registers a 'afterEach' block to be run.
     *
     * @param description The description of the 'afterEach' block.
     * @param function The 'afterEach' block.
     */
    public void afterEach(String description, HookFunction function) {
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
    public void it(String description, TestFunction function) {
        it(NORMAL, description, function);
    }

    /**
     * Registers a test function to be run.
     *
     * @param behaviour If {@link Behaviour#SKIP} then this test will be skipped.
     * @param description The description of the test function.
     * @param function The test function.
     */
    public void it(Behaviour behaviour, String description, TestFunction function) {
        assertNotRunningTests("it");
        assertNotRootDescribeBlock("it", "when", "describe");
        getCurrentDescribeBlock().addTest(new Test(behaviour, description, Optional.of(function)));
    }

    /**
     * Registers a pending test function that has yet to be implemented.
     *
     * @param description The description of the test function.
     */
    public void it(String description) {
        getCurrentDescribeBlock().addTest(new Test(NORMAL, description, Optional.empty()));
    }

    /**
     * Runs all the tests that have been loaded into the test framework.
     *
     * @param reporter A reporter to apprise of test outcomes.
     */
    public void runTests(Reporter reporter) {
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
     * Returns the test block that contains all user-defined tests and test blocks.
     *
     * @return The root test block.
     */
    public TestBlock getRootTestBlock() {
        return rootBuilder.build();
    }

    /**
     * For test use only.
     *
     * <p>Resets the test framework state.</p>
     */
    public void reset() {
        runningTests = false;
        rootBuilder = new TestBlockBuilder(NORMAL, "");
        stack = new ArrayDeque<>();
        stack.addLast(rootBuilder);
    }

    private void assertNotRunningTests(String blockType) {
        if (runningTests) {
            throw new CuppaException(new IllegalStateException("Cannot declare new '" + blockType
                    + "' block whilst running tests"));
        }
    }

    private void assertNotRootDescribeBlock(String blockType, String... allowedBlockTypes) {
        if (getCurrentDescribeBlock().equals(rootBuilder)) {
            throw new CuppaException(new IllegalStateException("A '" + blockType + "' must be nested within a "
                    + String.join(", ", allowedBlockTypes) + " function"));
        }
    }

    private TestBlockBuilder getCurrentDescribeBlock() {
        return stack.getLast();
    }
}
