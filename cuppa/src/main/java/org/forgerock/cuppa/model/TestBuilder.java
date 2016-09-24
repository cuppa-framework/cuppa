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

import java.util.Optional;

import org.forgerock.cuppa.functions.TestFunction;

/**
 * A mutable builder that constructs {@link Test} instances.
 */
public final class TestBuilder {
    private Behaviour behaviour = Behaviour.NORMAL;
    private Class<?> testClass;
    private String description;
    private Optional<TestFunction> function;
    private Options options = Options.EMPTY;

    /**
     * Sets the behaviour, which controls how the test behaves.
     *
     * @param behaviour The behaviour.
     * @return this {@code TestBuilder}.
     */
    public TestBuilder setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    /**
     * Sets the class that the test was defined in.
     *
     * @param testClass The class.
     * @return this {@code TestBuilder}.
     */
    public TestBuilder setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    /**
     * Sets the description of the test.
     * @param description The description.
     * @return this {@code TestBuilder}.
     */
    public TestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the body of the test. If the {@link Optional} is empty the test is classified as pending.
     *
     * @param function The function.
     * @return this {@code TestBuilder}.
     */
    public TestBuilder setFunction(Optional<TestFunction> function) {
        this.function = function;
        return this;
    }

    /**
     * Sets the options applied to the test.
     *
     * @param options The options.
     * @return this {@code TestBuilder}.
     */
    public TestBuilder setOptions(Options options) {
        this.options = options;
        return this;
    }

    /**
     * Constructs a {@link Test} using the values set on this builder.
     * @return A new {@link Test}.
     */
    public Test build() {
        return new Test(behaviour, testClass, description, function, options);
    }
}
