package org.forgerock.cuppa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 *
 */
public final class TestBlockBuilder {

    private final Behaviour behaviour;
    private final String description;
    private final List<TestBlock> testBlocks = new ArrayList<>();
    private final List<Hook> beforeHooks = new ArrayList<>();
    private final List<Hook> afterAfter = new ArrayList<>();
    private final List<Hook> beforeEachHooks = new ArrayList<>();
    private final List<Hook> afterEachHooks = new ArrayList<>();
    private final List<Test> tests = new ArrayList<>();

    public TestBlockBuilder(Behaviour behaviour, String description) {
        this.behaviour = behaviour;
        this.description = description;
    }

    public TestBlockBuilder addTestBlock(TestBlock testBlock) {
        testBlocks.add(testBlock);
        return this;
    }

    public TestBlockBuilder addBefore(Optional<String> description, HookFunction function) {
        beforeHooks.add(new Hook(description, function));
        return this;
    }

    public TestBlockBuilder addAfter(Optional<String> description, HookFunction function) {
        afterAfter.add(new Hook(description, function));
        return this;
    }

    public TestBlockBuilder addBeforeEach(Optional<String> description, HookFunction function) {
        beforeEachHooks.add(new Hook(description, function));
        return this;
    }

    public TestBlockBuilder addAfterEach(Optional<String> description, HookFunction function) {
        afterEachHooks.add(new Hook(description, function));
        return this;
    }

    public TestBlockBuilder addTest(Test test) {
        tests.add(test);
        return this;
    }

    public TestBlock build() {
        return new TestBlock(behaviour, description, testBlocks, beforeHooks, afterAfter, beforeEachHooks,
                afterEachHooks, tests);
    }
}
