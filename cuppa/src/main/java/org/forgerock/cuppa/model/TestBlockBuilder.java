/*
 * Copyright 2016 ForgeRock AS.
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
import java.util.List;

/**
 * A mutable builder that constructs {@link TestBlock} instances.
 */
public final class TestBlockBuilder {
    private TestBlockType type;
    private Behaviour behaviour = Behaviour.NORMAL;
    private Class<?> testClass;
    private String description;
    private List<TestBlock> testBlocks = new ArrayList<>();
    private List<Hook> hooks = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();
    private Options options = Options.EMPTY;

    /**
     * Set the type of the test block.
     * @param type The type.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setType(TestBlockType type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the behaviour of the test block.
     *
     * <p>The behaviour controls how the test block and its descendants behave.</p>
     *
     * @param behaviour The behaviour.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    /**
     * Sets the class that the test block was defined in.
     *
     * @param testClass The test class.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    /**
     * Sets the description of the test block.
     *
     * @param description The description.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the nested test blocks.
     *
     * <p>The list will be copied when {@link #build()} is called.</p>
     *
     * @param testBlocks A list of test blocks.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setTestBlocks(List<TestBlock> testBlocks) {
        this.testBlocks = testBlocks;
        return this;
    }

    /**
     * Sets the hooks defined by the test block.
     *
     * <p>The list will be copied when {@link #build()} is called.</p>
     *
     * @param hooks A list of hooks.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setHooks(List<Hook> hooks) {
        this.hooks = hooks;
        return this;
    }

    /**
     * Sets the tests defined by the test block.
     *
     * <p>The list will be copied when {@link #build()} is called.</p>
     *
     * @param tests A list of tests.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setTests(List<Test> tests) {
        this.tests = tests;
        return this;
    }

    /**
     * Sets the options applied to the test block.
     *
     * @param options The options.
     * @return this {@code TestBlockBuilder}.
     */
    public TestBlockBuilder setOptions(Options options) {
        this.options = options;
        return this;
    }

    /**
     * Constructs a {@link TestBlock} using the values set on this builder.
     * @return A new {@link TestBlock}.
     */
    public TestBlock build() {
        return new TestBlock(type, behaviour, testClass, description, testBlocks, hooks, tests, options);
    }
}
