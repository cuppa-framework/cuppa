package org.forgerock.cuppa;

/**
 * Defines the behaviour of a test or collection of tests.
 */
public enum Behaviour {
    /**
     * Run the test(s).
     */
    NORMAL,
    /**
     * Do not run the test(s). The test(s) may still be included in test reports, but marked as skipped.
     */
    SKIP;

    /**
     * Combine this behaviour with another behaviour.
     * @param behaviour The other behaviour
     * @return The combined behaviour
     */
    Behaviour combine(Behaviour behaviour) {
        if (this == SKIP) {
            return SKIP;
        }
        return behaviour;
    }
}
