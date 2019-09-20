package org.forgerock.cuppa.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A wrapper condition that inverse the wrapped condition.
 */
public class NotCondition extends ConditionWrapper {
    /** An empty NOT definition. */
    public static final NotCondition EMPTY = new NotCondition(Collections.emptyList());
    final Optional<Condition> condition;

    /**
     * Constructor.
     *
     * @param conditions a singletonList of condition.
     */
    NotCondition(Collection<Condition> conditions) {
        if (conditions.size() > 1) {
            throw new IllegalArgumentException(NotCondition.class + " cannot have more than one tag");
        }
        this.condition = conditions.stream().findFirst();
    }

    @Override
    public final boolean shouldRun(Collection<String> tags) {
        return !condition.orElse(c -> true).shouldRun(tags);
    }

    @Override
    final ConditionWrapper setConditions(List<Condition> conditions) {
        return new NotCondition(conditions);
    }
}

