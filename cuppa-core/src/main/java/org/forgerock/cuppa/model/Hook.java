package org.forgerock.cuppa.model;

import java.util.Optional;

import org.forgerock.cuppa.HookFunction;

/**
 *
 */
public final class Hook {

    public final Optional<String> description;
    public final HookFunction function;

    public Hook(Optional<String> description, HookFunction function) {
        this.description = description;
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hook hook = (Hook) o;

        if (!description.equals(hook.description)) return false;
        return function.equals(hook.function);

    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + function.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Hook{" +
                (description.isPresent() ? "description='" + description.get() + '\'' : "") +
                '}';
    }
}
