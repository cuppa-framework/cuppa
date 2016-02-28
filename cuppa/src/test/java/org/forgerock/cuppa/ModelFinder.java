/*
 * Copyright 2015-2016 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.forgerock.cuppa;

import static java.util.stream.Stream.concat;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.forgerock.cuppa.internal.TestContainer;
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
        return getTests(TestContainer.INSTANCE.getRootTestBlock())
                .filter(t -> t.description.equals(description))
                .findFirst()
                .get();
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
        return getTestBlocks(TestContainer.INSTANCE.getRootTestBlock())
                .filter(b -> b.description.equals(description))
                .findFirst()
                .get();
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
        return getHooks(TestContainer.INSTANCE.getRootTestBlock())
                .filter(h -> h.description.equals(Optional.of(description)))
                .findFirst()
                .get();
    }

    private static Stream<Hook> getHooks(TestBlock block) {
        return concat(block.hooks.stream(), block.testBlocks.stream().flatMap(ModelFinder::getHooks));
    }
}
