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

import java.util.Objects;
import java.util.Optional;

import org.forgerock.cuppa.functions.TestFunction;

/**
 * Encapsulates the test ('it') function block.
 */
public final class Test {

    /**
     * Controls how the test behaves.
     */
    public final Behaviour behaviour;

    /**
     * The class that the test was defined in.
     */
    public final Class<?> testClass;

    /**
     * The description of the test. Will be used for reporting.
     */
    public final String description;

    /**
     * The body of the test. If the {@link Optional} is empty the test is classified as pending.
     */
    public final Optional<TestFunction> function;

    /**
     * Constructs a new test.
     *
     * @param behaviour The behaviour of the test.
     * @param testClass The class that the test was defined in.
     * @param description The description of the test. Will be used for reporting.
     * @param function The body of the test. If the {@link Optional} is empty the test is
     *     classified as pending.
     */
    public Test(Behaviour behaviour, Class<?> testClass, String description, Optional<TestFunction> function) {
        Objects.requireNonNull(behaviour, "Test must have a behaviour");
        Objects.requireNonNull(testClass, "Test must have a testClass");
        Objects.requireNonNull(description, "Test must have a description");
        Objects.requireNonNull(function, "Test must have a function");
        this.behaviour = behaviour;
        this.testClass = testClass;
        this.description = description;
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Test test = (Test) o;
        return Objects.equals(behaviour, test.behaviour)
            && Objects.equals(description, test.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behaviour, description, function);
    }

    @Override
    public String toString() {
        return "Test{"
            + "behaviour=" + behaviour
            + ", description='" + description + '\''
            + '}';
    }
}
