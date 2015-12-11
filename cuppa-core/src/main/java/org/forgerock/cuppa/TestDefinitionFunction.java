package org.forgerock.cuppa;

/**
 * Implement this interface to define tests within a describe or when block.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface TestDefinitionFunction {

    /**
     * Defines a set of tests by calling {@link Cuppa#it(String, TestFunction)} and/or defines nested blocks by calling
     * {@link Cuppa#describe(String, TestDefinitionFunction)} or {@link Cuppa#when(String, TestDefinitionFunction)}.
     */
    void apply();
}
