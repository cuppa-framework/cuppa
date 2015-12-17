package org.forgerock.cuppa.model;

import java.util.Objects;
import java.util.Optional;

import org.forgerock.cuppa.functions.TestFunction;

/**
 * Encapsulates the test ('it') function block.
 */
public final class Test {

    /**
     * Controls how the test behaves.
     */
    public final Behaviour behaviour;

    /**
     * The description of the test. Will be used for reporting.
     */
    public final String description;

    /**
     * The body of the test. If the {@link Optional} is empty the test is classified as pending.
     */
    public final Optional<TestFunction> function;

    /**
     * Constructs a new test.
     *
     * @param behaviour The behaviour of the test.
     * @param description The description of the test. Will be used for reporting.
     * @param function The body of the test. If the {@link Optional} is empty the test is
     *     classified as pending.
     */
    public Test(Behaviour behaviour, String description, Optional<TestFunction> function) {
        Objects.requireNonNull(behaviour, "Test must have a behaviour");
        Objects.requireNonNull(description, "Test must have a description");
        Objects.requireNonNull(function, "Test must have a function");
        this.behaviour = behaviour;
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
        Test test = (Test) o;
        return Objects.equals(behaviour, test.behaviour)
            && Objects.equals(description, test.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behaviour, description, function);
    }

    @Override
    public String toString() {
        return "Test{"
            + "behaviour=" + behaviour
            + ", description='" + description + '\''
            + '}';
    }
}
