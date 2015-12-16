package org.forgerock.cuppa.model;

import static org.forgerock.cuppa.Behaviour.ONLY;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import org.forgerock.cuppa.Behaviour;

/**
 * Encapsulates the 'describe' and 'when' function blocks and all nested 'describe', 'when' and
 * test ('it') function blocks.
 */
public final class TestBlock {

    public final Behaviour behaviour;
    public final String description;
    public final ImmutableList<TestBlock> testBlocks;
    public final ImmutableList<Hook> beforeHooks;
    public final ImmutableList<Hook> afterHook;
    public final ImmutableList<Hook> beforeEachHooks;
    public final ImmutableList<Hook> afterEachHooks;
    public final ImmutableList<Test> tests;

    //TODO document will convert to immutable copies
    public TestBlock(Behaviour behaviour, String description, List<TestBlock> testBlocks, List<Hook> beforeHooks,
            List<Hook> afterHook, List<Hook> beforeEachHooks, List<Hook> afterEachHooks, List<Test> tests) {
        Objects.requireNonNull(behaviour, "Test block must have a behaviour");
        Objects.requireNonNull(description, "Test block must have a description");
        Objects.requireNonNull(description, "Test block must have a testBlocks"); //TODO later
        Objects.requireNonNull(description, "Test block must have a description");
        Objects.requireNonNull(description, "Test block must have a description");
        Objects.requireNonNull(description, "Test block must have a description");
        Objects.requireNonNull(description, "Test block must have a description");
        Objects.requireNonNull(description, "Test block must have a description");
        this.behaviour = behaviour;
        this.description = description;
        this.testBlocks = ImmutableList.copyOf(testBlocks);
        this.beforeHooks = ImmutableList.copyOf(beforeHooks);
        this.afterHook = ImmutableList.copyOf(afterHook);
        this.beforeEachHooks = ImmutableList.copyOf(beforeEachHooks);
        this.afterEachHooks = ImmutableList.copyOf(afterEachHooks);
        this.tests = ImmutableList.copyOf(tests);
    }

    public boolean hasOnlyTests() {
        return behaviour == ONLY
                || tests.stream().anyMatch(t -> t.behaviour == ONLY)
                || testBlocks.stream().anyMatch(TestBlock::hasOnlyTests);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestBlock testBlock = (TestBlock) o;

        if (behaviour != testBlock.behaviour) return false;
        if (!description.equals(testBlock.description)) return false;
        if (!testBlocks.equals(testBlock.testBlocks)) return false;
        if (!beforeHooks.equals(testBlock.beforeHooks)) return false;
        if (!afterHook.equals(testBlock.afterHook)) return false;
        if (!beforeEachHooks.equals(testBlock.beforeEachHooks)) return false;
        if (!afterEachHooks.equals(testBlock.afterEachHooks)) return false;
        return tests.equals(testBlock.tests);

    }

    @Override
    public int hashCode() {
        int result = behaviour.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + testBlocks.hashCode();
        result = 31 * result + beforeHooks.hashCode();
        result = 31 * result + afterHook.hashCode();
        result = 31 * result + beforeEachHooks.hashCode();
        result = 31 * result + afterEachHooks.hashCode();
        result = 31 * result + tests.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestBlock{" +
                "behaviour=" + behaviour +
                ", description='" + description + '\'' +
                ", testBlocks=" + testBlocks +
                ", beforeHooks=" + beforeHooks +
                ", afterHook=" + afterHook +
                ", beforeEachHooks=" + beforeEachHooks +
                ", afterEachHooks=" + afterEachHooks +
                ", tests=" + tests +
                '}';
    }
}
