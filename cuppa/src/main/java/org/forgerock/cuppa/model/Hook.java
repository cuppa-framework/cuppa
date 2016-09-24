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

import org.forgerock.cuppa.functions.HookFunction;

/**
 * Models a function that is executed before or after a test is run.
 *
 * <p>A {@code Hook} is usually created using {@link org.forgerock.cuppa.Cuppa#before(HookFunction)},
 * {@link org.forgerock.cuppa.Cuppa#after(HookFunction)}, {@link org.forgerock.cuppa.Cuppa#beforeEach(HookFunction)}
 * or {@link org.forgerock.cuppa.Cuppa#afterEach(HookFunction)} but can be constructed directly.</p>
 */
public final class Hook {

    /**
     * The type of the hook.
     */
    public final HookType type;

    /**
     * The class that the hook was defined in.
     */
    public final Class<?> testClass;

    /**
     * An optional description.
     */
    public final Optional<String> description;

    /**
     * A function to be executed (possibly more than once).
     */
    public final HookFunction function;

    // Package private. Use HookBuilder.
    Hook(HookType type, Class<?> testClass, Optional<String> description, HookFunction function) {
        Objects.requireNonNull(type, "Hook must have a type");
        Objects.requireNonNull(testClass, "Hook must have a testClass");
        Objects.requireNonNull(description, "Hook must have a description");
        Objects.requireNonNull(function, "Hook must have a function");
        this.type = type;
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

        Hook hook = (Hook) o;

        return Objects.equals(type, hook.type)
            && Objects.equals(testClass, hook.testClass)
            && Objects.equals(description, hook.description)
            && Objects.equals(function, hook.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, testClass, description, function);
    }

    @Override
    public String toString() {
        return "Hook{"
            + "type=" + type
            + ", testClass=" + testClass
            + (description.isPresent() ? ", description='" + description.get() + '\'' : "")
            + '}';
    }
}
