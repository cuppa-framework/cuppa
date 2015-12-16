package org.forgerock.cuppa.model;

import org.forgerock.cuppa.Behaviour;
import org.forgerock.cuppa.TestFunction;

/**
 * Encapsulates the test ('it') function block.
 */
public final class Test {

    public final Behaviour behaviour;
    public final String description;
    public final TestFunction function;

    // TODO: Assert non null
    public Test(Behaviour behaviour, String description, TestFunction function) {
        this.behaviour = behaviour;
        this.description = description;
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (behaviour != test.behaviour) return false;
        if (!description.equals(test.description)) return false;
        return function.equals(test.function);

    }

    @Override
    public int hashCode() {
        int result = behaviour.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + function.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Test{" +
                "behaviour=" + behaviour +
                ", description='" + description + '\'' +
                '}';
    }
}
