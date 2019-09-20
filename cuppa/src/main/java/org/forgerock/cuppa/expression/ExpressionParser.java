package org.forgerock.cuppa.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is responsible to parse an expression tag to a {@link Condition} .
 */
public final class ExpressionParser {

    private ExpressionParser() {
    }

    /**
     * Parse the expressionTags to a Condition.
     * @param expressionTags the expression to parse
     * @return The condition
     */
    public static Condition parse(String expressionTags) {
        if (expressionTags.isEmpty()) {
            return (t) -> true;
        }

        Stack<String> operators = new Stack<>();
        Stack<Integer> numberOfGroupsPerOperator = new Stack<>();
        Stack<Condition> groups = new Stack<>();

        String currentWord = "";
        char[] chars = expressionTags.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case '(':
                    pushOperator(expressionTags, operators, numberOfGroupsPerOperator, currentWord, i);
                    currentWord = "";
                    break;
                case ',':
                case ';':
                    addEqualCondition(groups, currentWord, numberOfGroupsPerOperator);
                    currentWord = "";
                    break;
                case ')':
                    pushComplexCondition(operators, numberOfGroupsPerOperator, groups, currentWord);
                    currentWord = "";
                    break;
                default:
                    currentWord += c;
                    break;
            }
        }

        if (groups.size() != 1 || numberOfGroupsPerOperator.size() != 0 || operators.size() != 0) {
            throw new IllegalArgumentException("malformed expression " + expressionTags);
        }

        return groups.pop();
    }

    private static void pushComplexCondition(Stack<String> operators, Stack<Integer> numberOfGroupsPerOperator,
            Stack<Condition> groups, String currentWord) {
        addEqualCondition(groups, currentWord, numberOfGroupsPerOperator);

        String operator = operators.pop();
        int number = numberOfGroupsPerOperator.pop();
        List<Condition> tags = new ArrayList<>(groups.subList(groups.size() - number, groups.size()));

        for (; number > 0; number--) {
            groups.pop();
        }

        groups.push(ConditionFactory.get(operator, tags));
        if (!numberOfGroupsPerOperator.isEmpty()) {
            numberOfGroupsPerOperator.push(numberOfGroupsPerOperator.pop() + 1);
        }
    }

    private static void pushOperator(String expressionTags, Stack<String> operators,
            Stack<Integer> numberOfGroupsPerOperator, String currentWord, int i) {
        if ("".equals(currentWord)) {
            throw new IllegalArgumentException("malformed expression ( " + expressionTags + " ). "
                    + "An operator was expected column " + i);
        }
        operators.push(currentWord);
        numberOfGroupsPerOperator.push(0);
    }

    private static void addEqualCondition(Stack<Condition> groups, String currentWord, Stack<Integer> numbers) {
        String trimmedWord = currentWord.trim();
        if (!"".equals(trimmedWord)) {
            groups.push(new ContainsCondition(trimmedWord));
            numbers.push(numbers.pop() + 1);
        }
    }
}
