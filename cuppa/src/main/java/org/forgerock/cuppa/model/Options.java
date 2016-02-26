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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

/**
 * A set of options. Options allow Cuppa's data model to be extended to include additional metadata about tests and test
 * blocks.
 */
public final class Options {
    private final Map<Class<? extends Option>, Option> options;

    /**
     * Creates a empty mutable options set.
     */
    public Options() {
        this(new HashMap<>());
    }

    private Options(Map<Class<? extends Option>, Option> options) {
        this.options = options;
    }

    /**
     * Creates an immutable options set from the given options.
     * @param options A set of options to copy.
     * @return An immutable set of options.
     */
    public static Options immutableCopyOf(Options options) {
        return new Options(ImmutableMap.copyOf(options.options));
    }

    /**
     * Creates an mutable options set from the given options.
     *
     * @param options A set of options to copy.
     * @return An mutable set of options.
     */
    public static Options copyOf(Options options) {
        return new Options(new HashMap<>(options.options));
    }

    /**
     * Get an option of the given type. If no such option has been set, then an empty optional will be returned.
     *
     * @param optionClass The class that corresponds to the option.
     * @param <V> The type of the value stored in the option.
     * @param <O> The type of the option.
     * @return An optional value.
     */
    public <V, O extends Option<V>> Optional<V> get(Class<O> optionClass) {
        if (options.containsKey(optionClass)) {
            O option = optionClass.cast(options.get(optionClass));
            return Optional.of(option.get());
        }
        return Optional.empty();
    }

    /**
     * Set an option. If an option of the given type is already set, then it will be overwritten.
     *
     * @param option The option to store.
     * @param <T> The type of the option.
     * @throws UnsupportedOperationException if this class is immutable.
     */
    public <T> void set(Option<T> option) {
        options.put(option.getClass(), option);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Options options1 = (Options) o;

        return options.equals(options1.options);

    }

    @Override
    public int hashCode() {
        return options.hashCode();
    }

    @Override
    public String toString() {
        return options.toString();
    }
}
