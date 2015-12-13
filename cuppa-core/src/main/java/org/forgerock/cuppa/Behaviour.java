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
    SKIP,
    /**
     * Run the test(s) and ignore all other tests not marked as ONLY.
     */
    ONLY;

    /**
     * Combine this behaviour with another behaviour.
     * @param behaviour The other behaviour
     * @return The combined behaviour
     */
    Behaviour combine(Behaviour behaviour) {
        if (this == SKIP || behaviour == SKIP) {
            return SKIP;
        } else if (this == ONLY || behaviour == ONLY) {
            return ONLY;
        }
        return NORMAL;
    }
}
