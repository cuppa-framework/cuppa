package org.forgerock.cuppa;

import static org.forgerock.cuppa.Cuppa.getRootTestBlock;

import java.util.Optional;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 *
 */
public class ModelFinder {
    public static Test findTest(String description) {
        return findTest(getRootTestBlock(), description).get();
    }

    private static Optional<Test> findTest(TestBlock block, String description) {
        Optional<Test> test = block.tests.stream().filter(t -> t.description.equals(description)).findFirst();
        if (test.isPresent()) {
            return test;
        }
        return block.testBlocks.stream().map(b -> findTest(b, description)).filter(t -> t.isPresent()).map(t -> t.get()).findFirst();
    }

    public static TestBlock findTestBlock(String description) {
        return findTestBlock(getRootTestBlock(), description).get();
    }

    private static Optional<TestBlock> findTestBlock(TestBlock block, String description) {
        if (block.description.equals(description)) {
            return Optional.of(block);
        }
        return block.testBlocks.stream().map(b -> findTestBlock(b, description)).filter(t -> t.isPresent()).map(t -> t.get()).findFirst();
    }

    public static Hook findHook(String description) {
        return findHook(getRootTestBlock(), Optional.of(description)).get();
    }

    private static Optional<Hook> findHook(TestBlock block, Optional<String> description) {
        Optional<Hook> beforeHook = block.beforeHooks.stream().filter(h -> h.description.equals(description)).findFirst();
        if (beforeHook.isPresent()) {
            return beforeHook;
        }
        Optional<Hook> afterHook = block.afterHook.stream().filter(h -> h.description.equals(description)).findFirst();
        if (afterHook.isPresent()) {
            return afterHook;
        }
        Optional<Hook> beforeEachHook = block.beforeEachHooks.stream().filter(h -> h.description.equals(description)).findFirst();
        if (beforeEachHook.isPresent()) {
            return beforeEachHook;
        }
        Optional<Hook> afterEachHook = block.afterEachHooks.stream().filter(h -> h.description.equals(description)).findFirst();
        if (afterEachHook.isPresent()) {
            return afterEachHook;
        }
        return block.testBlocks.stream().map(b -> findHook(b, description)).filter(t -> t.isPresent()).map(t -> t.get()).findFirst();
    }
}
