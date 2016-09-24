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

import org.forgerock.cuppa.functions.HookFunction;

/**
 * A mutable builder that constructs {@link Hook} instances.
 */
public final class HookBuilder {
    private HookType type;
    private Class<?> testClass;
    private Optional<String> description;
    private HookFunction function;

    /**
     * Sets the type of the hook.
     *
     * @param type The type.
     * @return this {@code HookBuilder}.
     */
    public HookBuilder setType(HookType type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the class that the hook was defined in.
     *
     * @param testClass The class.
     * @return this {@code HookBuilder}.
     */
    public HookBuilder setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    /**
     * Sets the description of the hook.
     *
     * @param description The description.
     * @return this {@code HookBuilder}.
     */
    public HookBuilder setDescription(Optional<String> description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the function to be executed.
     *
     * @param function The function.
     * @return this {@code HookBuilder}.
     */
    public HookBuilder setFunction(HookFunction function) {
        this.function = function;
        return this;
    }

    /**
     * Constructs a {@link Hook} using the values set on this builder.
     * @return A new {@link Hook}.
     */
    public Hook build() {
        return new Hook(type, testClass, description, function);
    }
}
