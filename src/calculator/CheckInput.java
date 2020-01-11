package calculator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckInput {
    private static final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private static final Pattern operatorsPattern = Pattern.compile("[*/^]|\\++-?|-+\\+?");
    private static Matcher matcher;

    public static final String VALID_INPUT = "valid";
    public static final String UNKNOWN_COMMAND = "Unknown command";
    public static final String INVALID_EXPRESSION = "Invalid expression";
    public static final String UNKNOWN_VARIABLE = "Unknown variable";
    public static final String INVALID_IDENTIFIER = "Invalid identifier";
    public static final String INVALID_ASSIGNMENT = "Invalid assignment";

    public static String check(String input, Map<String, BigDecimal> variables) {
        String result;

        result = singleValue(input, variables);
        if (!result.equals("")) {
            return result;
        }

        result = common(input);
        if (!result.equals("")) {
            return result;
        }

        if (input.contains("=")) {
            return assignment(input, variables);
        }

        result = expression(input, variables);
        if (!result.equals("")) {
            return result;
        }

        return VALID_INPUT;
    }

    private static String singleValue(String input, Map<String, BigDecimal> variables) {

        if (input.matches("[-+]?\\d+|[-+]?\\d+\\.\\d+")) {
            return VALID_INPUT;
        }

        if (input.matches("[a-zA-Z]+")) {
            return variables.containsKey(input) ? VALID_INPUT : UNKNOWN_VARIABLE;
        }

        if (input.startsWith("/")) {
            return UNKNOWN_COMMAND;
        }

        return "";
    }

    private static String common(String input) {

        if (input.matches(".*[^\\d +[-]=a-zA-Z*/()^.].*")) {
            return INVALID_EXPRESSION;
        }

        // check the beginning of a string
        if (!input.matches("^[(]*[-+]?[\\da-zA-Z].*")) {
            return INVALID_EXPRESSION;
        }

        // check the end of a string
        if (!input.matches(".*[\\da-zA-Z][)]*$")) {
            return INVALID_EXPRESSION;
        }

        if (input.matches(".*(\\d+[a-zA-Z]+|[a-zA-Z]+\\d+).*")) {
            return INVALID_IDENTIFIER;
        }

        return "";
    }

    private static String assignment(String input, Map<String, BigDecimal> variables) {

        if (!input.matches("[a-zA-z]+=([\\da-zA-Z]+|\\d+\\.\\d+)")) {
            return INVALID_ASSIGNMENT;
        }

        if (input.matches("[a-zA-Z]+=[a-zA-Z]+")) {
            matcher = Pattern.compile("[a-zA-Z]+$").matcher(input);
            matcher.find();
            String variableName = matcher.group();
            if (!variables.containsKey(variableName)) {
                return UNKNOWN_VARIABLE;
            }
        }

        return VALID_INPUT;
    }

    private static String expression(String input, Map<String, BigDecimal> variables) {

        // check all variables
        matcher = wordPattern.matcher(input);
        while (matcher.find()) {
            if (!variables.containsKey(matcher.group())) {
                return UNKNOWN_VARIABLE;
            }
        }

        // check parenthesis
        if (input.contains("(") || input.contains(")")) {
            String parenthesis = input.replaceAll("[^()]+", "");
            if (parenthesis.length() % 2 != 0) {
                return INVALID_EXPRESSION;
            }
        }

        // check operators
        String[] operators = input.split("[()]*(\\d+\\.\\d+|[\\da-zA-Z]+)[()]*");
        //System.out.println(Arrays.toString(operators));
        for (int i = 1; i < operators.length; i++) {
            matcher = operatorsPattern.matcher(operators[i]); // "[*/^]|\\++-?|-+\\+?"
            if (!matcher.matches()) {
                return INVALID_EXPRESSION;
            }
        }

        return "";
    }
}
