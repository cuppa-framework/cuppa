package org.forgerock.cuppa.internal.filters.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A wrapper condition that inverse the wrapped condition.
 */
class NotCondition extends ConditionWrapper {

    public static final NotCondition EMPTY = new NotCondition(Collections.emptyList());
    private final Optional<Condition> condition;

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
    public boolean shouldRun(Collection<String> tags) {
        return !condition.orElse(c -> true).shouldRun(tags);
    }

    @Override
    public ConditionWrapper setConditions(Collection<Condition> conditions) {
        return new NotCondition(conditions);
    }
}

