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
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.Reporter;

/**
 * A collection of static methods for facilitating the writing of tests for Cuppa and its extensions (such as test block
 * transforms).
 */
public final class TestCuppaSupport {
    private TestCuppaSupport() {
    }

    /**
     * Define some Cuppa tests. Use this in a test to avoid declaring a new class for your test's tests.
     *
     * @param testDefiner A function which will call {@link Cuppa} methods to define tests.
     * @return the root test block containing all the tests defined by the given function.
     */
    public static TestBlock defineTests(Runnable testDefiner) {
        return TestContainer.INSTANCE.defineTests(TestCuppaSupport.class, testDefiner);
    }

    /**
     * Helper method to simplify running tests.
     *
     * @param testBlock The root test block that contains all tests to be run.
     * @param reporter The reporter to use to report test results.
     */
    public static void runTests(TestBlock testBlock, Reporter reporter) {
        runTests(testBlock, reporter, Tags.EMPTY_TAGS);
    }

    /**
     * Helper method to simplify running tests.
     *
     * @param testBlock The root test block that contains all tests to be run.
     * @param reporter The reporter to use to report test results.
     * @param tags Tags to filter the tests on.
     */
    public static void runTests(TestBlock testBlock, Reporter reporter, Tags tags) {
        new Runner(tags, new Configuration()).run(testBlock, reporter);
    }

    /**
     * Finds the first test with the given description.
     *
     * @param testBlock the root test block to search under.
     * @param description The description.
     * @return The test.
     * @throws NoSuchElementException If no test was found with the given description.
     */
    public static Test findTest(TestBlock testBlock, String description) {
        return getTests(testBlock)
                .filter(t -> t.description.equals(description))
                .findFirst()
                .get();
    }

    private static Stream<Test> getTests(TestBlock block) {
        return concat(block.tests.stream(), block.testBlocks.stream().flatMap(TestCuppaSupport::getTests));
    }

    /**
     * Finds the first test block with the given description.
     *
     * @param testBlock the root test block to search under.
     * @param description The description.
     * @return The test block.
     * @throws NoSuchElementException If no test block was found with the given description.
     */
    public static TestBlock findTestBlock(TestBlock testBlock, String description) {
        return getTestBlocks(testBlock)
                .filter(b -> b.description.equals(description))
                .findFirst()
                .get();
    }

    private static Stream<TestBlock> getTestBlocks(TestBlock block) {
        return concat(Stream.of(block), block.testBlocks.stream().flatMap(TestCuppaSupport::getTestBlocks));
    }

    /**
     * Finds the first hook with the given description.
     *
     * <p>Hooks are searched breadth-first, i.e. going through a test block's before, after,
     * beforeEach and afterEach hooks before searching in nested test blocks.</p>
     *
     * @param testBlock the root test block to search under.
     * @param description The description.
     * @return The hook.
     * @throws NoSuchElementException If no hook was found with the given description.
     */
    public static Hook findHook(TestBlock testBlock, String description) {
        return getHooks(testBlock)
                .filter(h -> h.description.equals(Optional.of(description)))
                .findFirst()
                .get();
    }

    private static Stream<Hook> getHooks(TestBlock block) {
        return concat(block.hooks.stream(), block.testBlocks.stream().flatMap(TestCuppaSupport::getHooks));
    }
}
