package org.forgerock.cuppa.transforms.expression;

import java.util.Arrays;
import java.util.List;

/**
 * A factory to creates {@link ConditionWrapper}.
 */
public final class ConditionFactory {

    /**
     * link the operator to an instance of {@link Condition}.
     */
    enum ConditionEnum {
        AND("and", AndCondition.EMPTY),
        OR("or", OrCondition.EMPTY),
        NOT("not", NotCondition.EMPTY);

        private String operator;
        private ConditionWrapper condition;

        ConditionEnum(String operator, ConditionWrapper condition) {
            this.operator = operator;
            this.condition = condition;
        }

        /**
         * Get a ConditionEnum fron an string operator.
         * @param operator The operator we want to get the associated enum
         * @return The ConditionEnum
         */
        static ConditionEnum getFromOperator(String operator) {
            return Arrays.stream(ConditionEnum.values())
                    .filter(c -> c.operator.equals(operator))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(operator + " is not supported. The list of "
                            + "supported operator is and, or, not"));
        }

        Condition getCondition(List<Condition> tags) {
            return condition.setConditions(tags);
        }
    }

    private ConditionFactory() {
    }

    /**
     * Create a {@link Condition}.
     * @param operator The operator we want to create a condition for.
     * @param tags The list of conditions that {@link ConditionWrapper} will include.
     * @return A condition
     */
    static Condition get(String operator, List<Condition> tags) {
        return ConditionEnum.getFromOperator(operator.trim().toLowerCase())
                .getCondition(tags);
    }
}
