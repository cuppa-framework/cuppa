package org.forgerock.cuppa;

/**
 * Implement this interface to define a test.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface TestFunction {

    /**
     * Defines the behaviour of the test.
     *
     * @throws Exception A test may throw any exception, which will result in a test error.
     */
    void apply() throws Exception;

    /**
     * Returns a function that does nothing.
     *
     * @return a function that does nothing
     */
    static TestFunction identity() {
        return () -> {
        };
    }
}
