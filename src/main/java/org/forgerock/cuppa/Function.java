package org.forgerock.cuppa;

import java.util.Objects;

/**
 * Represents a function that accepts no arguments argument and produces a no result.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface Function {

    /**
     * Runs this function.
     */
    void apply();

    /**
     * Returns a composed function that first applies the {@code before} function, and then applies
     * this function. If evaluation of either function throws an exception, it is relayed to the
     * caller of the composed function.
     *
     * @param before The function to apply before this function is applied.
     * @return A composed function that first applies the {@code before} function and then
     *     applies this function
     * @throws NullPointerException If before function is {@code null}.
     */
    default Function compose(Function before) {
        Objects.requireNonNull(before);
        return () -> {
            before.apply();
            apply();
        };
    }
}
