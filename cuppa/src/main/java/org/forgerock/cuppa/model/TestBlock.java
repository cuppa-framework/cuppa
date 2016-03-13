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

package org.forgerock.cuppa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * The class that the test block was defined in.
     */
    public final Class<?> testClass;

    /**
     * The description of the test block. Will be used for reporting.
     */
    public final String description;

    /**
     * Nested test blocks.
     */
    public final List<TestBlock> testBlocks;

    /**
     * Before hooks. Will be run once before any tests in this test block are executed.
     */
    public final List<Hook> hooks;

    /**
     * Nested tests.
     */
    public final List<Test> tests;

    /**
     * The set of options applied to the block.
     */
    public final Options options;

    /**
     * Constructs a new TestBlock. Will convert mutable lists to immutable lists.
     *
     * @param behaviour Controls how the test block and its descendants behave.
     * @param testClass The class that the test block was defined in.
     * @param description The description of the test block. Will be used for reporting.
     * @param testBlocks Nested test blocks.
     * @param hooks Hooks associated with this test block.
     * @param tests Nested tests.
     * @param options The set of options applied to the block.
     */
    public TestBlock(Behaviour behaviour, Class<?> testClass, String description, List<TestBlock> testBlocks,
            List<Hook> hooks, List<Test> tests, Options options) {
        Objects.requireNonNull(behaviour, "TestBlock must have a behaviour");
        Objects.requireNonNull(testClass, "TestBlock must have a testClass");
        Objects.requireNonNull(description, "TestBlock must have a description");
        Objects.requireNonNull(testBlocks, "TestBlock must have testBlocks");
        Objects.requireNonNull(hooks, "TestBlock must have hooks");
        Objects.requireNonNull(tests, "TestBlock must have tests");
        Objects.requireNonNull(options, "TestBlock must have options");
        this.behaviour = behaviour;
        this.testClass = testClass;
        this.description = description;
        this.testBlocks = Collections.unmodifiableList(new ArrayList<>(testBlocks));
        this.hooks = Collections.unmodifiableList(new ArrayList<>(hooks));
        this.tests = Collections.unmodifiableList(new ArrayList<>(tests));
        this.options = Options.immutableCopyOf(options);
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
            && Objects.equals(testClass, testBlock.testClass)
            && Objects.equals(description, testBlock.description)
            && Objects.equals(testBlocks, testBlock.testBlocks)
            && Objects.equals(hooks, testBlock.hooks)
            && Objects.equals(tests, testBlock.tests)
            && Objects.equals(options, testBlock.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behaviour, testClass, description, testBlocks, hooks, tests, options);
    }

    @Override
    public String toString() {
        return "TestBlock{"
            + "behaviour=" + behaviour
            + ", testClass=" + testClass
            + ", description='" + description + '\''
            + ", testBlocks=" + testBlocks
            + ", hooks=" + hooks
            + ", tests=" + tests
            + ", options=" + options
            + '}';
    }

    /**
     * Get all the registered hooks of the given type, in the order they were defined.
     *
     * @param type The type of hook to filter on.
     * @return An immutable list of hooks.
     */
    public List<Hook> hooksOfType(HookType type) {
        return hooks.stream()
                .filter(h -> h.type == type)
                .collect(Collectors.toList());
    }
}
