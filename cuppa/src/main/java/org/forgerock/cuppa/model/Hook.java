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
 * A hook is code that is executed before or after a test is run. It may optionally have a
 * description to help with debugging when a hook throws an exception.
 */
public final class Hook {

    /**
     * An optional description.
     */
    public final Optional<String> description;

    /**
     * A function to be executed (possibly more than once).
     */
    public final HookFunction function;

    /**
     * Constructs a new hook.
     *
     * @param description An optional description.
     * @param function A function to be executed (possibly more than once).
     */
    public Hook(Optional<String> description, HookFunction function) {
        Objects.requireNonNull(description, "Hook must have a description");
        Objects.requireNonNull(function, "Hook must have a function");
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

        return Objects.equals(description, hook.description)
            && Objects.equals(function, hook.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, function);
    }

    @Override
    public String toString() {
        return "Hook{"
            + (description.isPresent() ? "description='" + description.get() + '\'' : "")
            + '}';
    }
}
