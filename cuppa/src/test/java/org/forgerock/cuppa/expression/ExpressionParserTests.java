package org.forgerock.cuppa.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.testng.annotations.Test;

public class ExpressionParserTests {
    @Test
    public void testAnd() {
        // When
        Condition expression = ExpressionParser.parse("and(a,b)");

        // Then
        assertThat(expression).isInstanceOf(AndCondition.class);
        List<Condition> conditions = ((AndCondition) expression).conditions;
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(0)).tag).isEqualTo("a");
        assertThat(conditions.get(1)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(1)).tag).isEqualTo("b");
    }

    @Test
    public void testOr() {
        // When
        Condition expression = ExpressionParser.parse("or(a,b)");

        // Then
        assertThat(expression).isInstanceOf(OrCondition.class);
        List<Condition> conditions = ((OrCondition) expression).conditions;
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(0)).tag).isEqualTo("a");
        assertThat(conditions.get(1)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(1)).tag).isEqualTo("b");
    }

    @Test
    public void testNot() {
        // When
        Condition expression = ExpressionParser.parse("not(a)");

        // Then
        assertThat(expression).isInstanceOf(NotCondition.class);
        Optional<Condition> condition = ((NotCondition) expression).condition;
        assertThat(condition).isPresent();
        assertThat(condition.get()).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) condition.get()).tag).isEqualTo("a");
    }

    @Test
    public void testComplex() {
        // When
        Condition expression = ExpressionParser.parse("and(or(and(a,b),and(c,d)),not(e),f)");

        // Then
        assertThat(expression).isInstanceOf(AndCondition.class);
        List<Condition> andConditions = ((AndCondition) expression).conditions;
        assertThat(andConditions).hasSize(3);
        assertThat(andConditions.get(0)).isInstanceOf(OrCondition.class);

        List<Condition> orConditions = ((OrCondition) andConditions.get(0)).conditions;
        assertThat(orConditions).hasSize(2);

        assertThat(orConditions.get(0)).isInstanceOf(AndCondition.class);
        List<Condition> conditions = ((AndCondition) orConditions.get(0)).conditions;
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(0)).tag).isEqualTo("a");
        assertThat(conditions.get(1)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(1)).tag).isEqualTo("b");

        assertThat(orConditions.get(1)).isInstanceOf(AndCondition.class);
        conditions = ((AndCondition) orConditions.get(1)).conditions;
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(0)).tag).isEqualTo("c");
        assertThat(conditions.get(1)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) conditions.get(1)).tag).isEqualTo("d");

        assertThat(andConditions.get(1)).isInstanceOf(NotCondition.class);
        Optional<Condition> condition = ((NotCondition) andConditions.get(1)).condition;
        assertThat(condition).isPresent();
        assertThat(condition.get()).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) condition.get()).tag).isEqualTo("e");

        assertThat(andConditions.get(2)).isInstanceOf(ContainsCondition.class);
        assertThat(((ContainsCondition) andConditions.get(2)).tag).isEqualTo("f");
    }
}
