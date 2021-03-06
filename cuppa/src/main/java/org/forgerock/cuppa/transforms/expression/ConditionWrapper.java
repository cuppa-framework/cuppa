package org.forgerock.cuppa.transforms.expression;

import java.util.List;

/**
 * A condition wrapper for Condition that needs to wrap other conditions.
 */
abstract class ConditionWrapper implements Condition {

    /**
     * Create a new Condition with the given conditions.
     * @param conditions the list of conditions
     * @return a new condition
     */
    abstract ConditionWrapper setConditions(List<Condition> conditions);
}
