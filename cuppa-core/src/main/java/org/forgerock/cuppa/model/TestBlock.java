package org.forgerock.cuppa.model;

import static org.forgerock.cuppa.model.Behaviour.ONLY;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * Encapsulates the 'describe' and 'when' function blocks and all nested 'describe', 'when' and
 * test ('it') function blocks.
 */
public final class TestBlock {

    /**
     * Controls how the test block and its descendants behave.
     */
    public final Behaviour behaviour;

    /**
     * The description of the test block. Will be used for reporting.
     */
    public final String description;

    /**
     * Nested test blocks.
     */
    public final ImmutableList<TestBlock> testBlocks;

    /**
     * Before hooks. Will be run once before any tests in this test block are executed.
     */
    public final ImmutableList<Hook> beforeHooks;

    /**
     * After hooks. Will be run once after all tests in this test block are executed.
     */
    public final ImmutableList<Hook> afterHook;

    /**
     * Before each hooks. Will run before each test in this test block is executed.
     */
    public final ImmutableList<Hook> beforeEachHooks;

    /**
     * After each hooks. Will run after each test in this test block is executed.
     */
    public final ImmutableList<Hook> afterEachHooks;

    /**
     * Nested tests.
     */
    public final ImmutableList<Test> tests;

    /**
     * Constructs a new TestBlock. Will convert mutable lists to immutable lists.
     *
     * @param behaviour Controls how the test block and its descendants behave.
     * @param description The description of the test block. Will be used for reporting.
     * @param testBlocks Nested test blocks.
     * @param beforeHooks Before hooks. Will be run once before any tests in this test block are executed.
     * @param afterHook After hooks. Will be run once after all tests in this test block are executed.
     * @param beforeEachHooks Before each hooks. Will run before each test in this test block is executed.
     * @param afterEachHooks After each hooks. Will run after each test in this test block is executed.
     * @param tests Nested tests.
     */
    public TestBlock(Behaviour behaviour, String description, List<TestBlock> testBlocks, List<Hook> beforeHooks,
            List<Hook> afterHook, List<Hook> beforeEachHooks, List<Hook> afterEachHooks, List<Test> tests) {
        Objects.requireNonNull(behaviour, "TestBlock must have a behaviour");
        Objects.requireNonNull(description, "TestBlock must have a description");
        Objects.requireNonNull(testBlocks, "TestBlock must have testBlocks");
        Objects.requireNonNull(beforeHooks, "TestBlock must have beforeHooks");
        Objects.requireNonNull(afterHook, "TestBlock must have afterHook");
        Objects.requireNonNull(beforeEachHooks, "TestBlock must have beforeEachHooks");
        Objects.requireNonNull(afterEachHooks, "TestBlock must have afterEachHooks");
        Objects.requireNonNull(tests, "TestBlock must have tests");
        this.behaviour = behaviour;
        this.description = description;
        this.testBlocks = ImmutableList.copyOf(testBlocks);
        this.beforeHooks = ImmutableList.copyOf(beforeHooks);
        this.afterHook = ImmutableList.copyOf(afterHook);
        this.beforeEachHooks = ImmutableList.copyOf(beforeEachHooks);
        this.afterEachHooks = ImmutableList.copyOf(afterEachHooks);
        this.tests = ImmutableList.copyOf(tests);
    }

    /**
     * Determine if this test block contains any tests or test blocks marked as {@link Behaviour#ONLY}.
     *
     * @return {@code true} if this block, or any block or test under it, is marked as {@link Behaviour#ONLY}.
     */
    public boolean hasOnlyTests() {
        return behaviour == ONLY
                || tests.stream().anyMatch(t -> t.behaviour == ONLY)
                || testBlocks.stream().anyMatch(TestBlock::hasOnlyTests);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestBlock testBlock = (TestBlock) o;

        return Objects.equals(behaviour, testBlock.behaviour)
            && Objects.equals(description, testBlock.description)
            && Objects.equals(testBlocks, testBlock.testBlocks)
            && Objects.equals(beforeHooks, testBlock.beforeHooks)
            && Objects.equals(afterHook, testBlock.afterHook)
            && Objects.equals(beforeEachHooks, testBlock.beforeEachHooks)
            && Objects.equals(afterEachHooks, testBlock.afterEachHooks)
            && Objects.equals(tests, testBlock.tests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behaviour, description, testBlocks, beforeHooks, afterHook, beforeEachHooks, afterEachHooks,
                tests);
    }

    @Override
    public String toString() {
        return "TestBlock{"
            + "behaviour=" + behaviour
            + ", description='" + description + '\''
            + ", testBlocks=" + testBlocks
            + ", beforeHooks=" + beforeHooks
            + ", afterHook=" + afterHook
            + ", beforeEachHooks=" + beforeEachHooks
            + ", afterEachHooks=" + afterEachHooks
            + ", tests=" + tests
            + '}';
    }
}
