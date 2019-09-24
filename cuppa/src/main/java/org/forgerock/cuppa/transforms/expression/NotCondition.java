package org.forgerock.cuppa.transforms.expression;

import java.util.Collection;
import java.util.List;

/**
 * A wrapper condition that inverse the wrapped condition.
 */
public class NotCondition extends ConditionWrapper {

    /** An empty NOT definition. */
    public static final NotCondition EMPTY = new NotCondition();
    final Condition condition;

    /**
     * Private constructor to be able to create the {@link #EMPTY} condition.
     */
    private NotCondition() {
        condition = null;
    }

    /**
     * Constructor.
     *
     * @param conditions a singletonList of condition.
     */
    NotCondition(List<Condition> conditions) {
        if (conditions.size() != 1) {
            throw new IllegalArgumentException(NotCondition.class + " must have exactly one child condition/tag");
        }
        this.condition = conditions.get(0);
    }

    @Override
    public final boolean shouldRun(Collection<String> tags) {
        return !condition.shouldRun(tags);
    }

    @Override
    final ConditionWrapper setConditions(List<Condition> conditions) {
        return new NotCondition(conditions);
    }
}

