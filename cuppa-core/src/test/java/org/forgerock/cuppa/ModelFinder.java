package org.forgerock.cuppa;

import static java.util.stream.Stream.concat;
import static org.forgerock.cuppa.CuppaTestProvider.getRootTestBlock;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.forgerock.cuppa.model.Hook;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;

/**
 * Provides useful methods for finding test blocks, tests and hooks from the Cuppa test structure.
 */
public final class ModelFinder {

    private ModelFinder() {
    }

    /**
     * Finds the first test with the given description.
     *
     * @param description The description.
     * @return The test.
     * @throws NoSuchElementException If no test was found with the given description.
     */
    public static Test findTest(String description) {
        return getTests(getRootTestBlock()).filter(t -> t.description.equals(description)).findFirst().get();
    }

    private static Stream<Test> getTests(TestBlock block) {
        return concat(block.tests.stream(), block.testBlocks.stream().flatMap(ModelFinder::getTests));
    }

    /**
     * Finds the first test block with the given description.
     *
     * @param description The description.
     * @return The test block.
     * @throws NoSuchElementException If no test block was found with the given description.
     */
    public static TestBlock findTestBlock(String description) {
        return getTestBlocks(getRootTestBlock()).filter(b -> b.description.equals(description)).findFirst().get();
    }

    private static Stream<TestBlock> getTestBlocks(TestBlock block) {
        return concat(Stream.of(block), block.testBlocks.stream().flatMap(ModelFinder::getTestBlocks));
    }

    /**
     * Finds the first hook with the given description.
     *
     * <p>Hooks are searched breadth-first, i.e. going through a test block's before, after,
     * beforeEach and afterEach hooks before searching in nested test blocks.</p>
     *
     * @param description The description.
     * @return The hook.
     * @throws NoSuchElementException If no hook was found with the given description.
     */
    public static Hook findHook(String description) {
        return getHooks(getRootTestBlock())
                .filter(h -> h.description.equals(Optional.of(description)))
                .findFirst()
                .get();
    }

    private static Stream<Hook> getHooks(TestBlock block) {
        Stream<Hook> hooks = concat(block.beforeHooks.stream(),
                concat(block.afterHook.stream(),
                concat(block.beforeEachHooks.stream(),
                block.afterEachHooks.stream())));
        Stream<Hook> nestedHooks = block.testBlocks.stream().flatMap(ModelFinder::getHooks);
        return concat(hooks, nestedHooks);
    }
}
