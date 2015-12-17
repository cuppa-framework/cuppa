package org.forgerock.cuppa.functions;

/**
 * Implement this interface to define a test hook. Hooks are used to setup and teardown state for tests.
 *
 * <p>This is a functional interface whose functional method is {@link #apply()}.
 */
@FunctionalInterface
public interface HookFunction {

    /**
     * Defines the behaviour of a test hook.
     *
     * @throws Exception To allow tests and hooks to throw checked exceptions.
     */
    void apply() throws Exception;

    /**
     * Returns a function that does nothing.
     *
     * @return a function that does nothing
     */
    static HookFunction identity() {
        return () -> {
        };
    }
}
