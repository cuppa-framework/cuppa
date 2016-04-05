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

/**
 * An option stores a value of the given type. Options allow Cuppa's data model to be extended to include additional
 * metadata about tests and test blocks.
 *
 * <p>To create a Option, you should create a unique subclass for your data. This allows you to avoid colliding with
 * other extensions.</p>
 *
 * <p>Options are immutable. Subclasses should make defensive copies of any mutable data structures passed to them.</p>
 *
 * @param <T> The type of the value stored within this Option.
 *
 * @see Options
 * @see TagsOption
 */
public abstract class Option<T> {
    private final T value;

    /**
     * Create a new option.
     *
     * @param value The immutable value to store in this option.
     */
    protected Option(T value) {
        this.value = value;
    }

    /**
     * Get the value of the option.
     *
     * @return the value of the option.
     */
    public final T get() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Option<?> option = (Option<?>) o;

        return !(value != null ? !value.equals(option.value) : option.value != null);

    }

    @Override
    public final String toString() {
        return value.toString();
    }

    @Override
    public final int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
