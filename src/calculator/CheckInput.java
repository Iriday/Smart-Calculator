package calculator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckInput {
    private static final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private static Matcher matcher;

    private static final String validInput = "valid";
    private static final String unknownCommand = "Unknown command";
    private static final String invalidExpression = "Invalid expression";
    private static final String unknownVariable = "Unknown variable";
    private static final String invalidIdentifier = "Invalid identifier";
    private static final String invalidAssignment = "Invalid assignment";

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

        return validInput;
    }

    private static String singleValue(String input, Map<String, BigDecimal> variables) {

        if (input.matches("[-+]?\\d+|[-+]?\\d+\\.\\d+")) {
            return validInput;
        }

        if (input.matches("[a-zA-Z]+")) {
            return variables.containsKey(input) ? validInput : unknownVariable;
        }

        if (input.startsWith("/")) {
            return unknownCommand;
        }

        return "";
    }

    private static String common(String input) {

        if (input.matches(".*[^\\d +[-]=a-zA-Z*/()^.].*")) {
            return invalidExpression;
        }

        // check the beginning of a string
        if (!input.matches("^[(]*[-+]?[(]*[\\da-zA-Z].*")) {
            return invalidExpression;
        }

        // check the end of a string
        if (!input.matches(".*[\\da-zA-Z][)]*$")) {
            return invalidExpression;
        }

        if (input.matches(".*(\\d+[a-zA-Z]+|[a-zA-Z]+\\d+).*")) {
            return invalidIdentifier;
        }

        return "";
    }

    private static String assignment(String input, Map<String, BigDecimal> variables) {

        if (!input.matches("[a-zA-z]+=([\\da-zA-Z]+|\\d+\\.\\d+)")) {
            return invalidAssignment;
        }

        if (input.matches("[a-zA-Z]+=[a-zA-Z]+")) {
            matcher = Pattern.compile("[a-zA-Z]+$").matcher(input);
            matcher.find();
            String variableName = matcher.group();
            if (!variables.containsKey(variableName)) {
                return unknownVariable;
            }
        }

        return validInput;
    }

    private static String expression(String input, Map<String, BigDecimal> variables) {

        if (input.matches(".*[*]{2,}.*|.*[/]{2,}.*|.*[*&&/].*|.*[/&&*].*")) {
            return invalidExpression;
        }

        // check all variables
        matcher = wordPattern.matcher(input);
        while (matcher.find()) {
            if (!variables.containsKey(matcher.group())) {
                return unknownVariable;
            }
        }

        if (input.contains("(") || input.contains(")")) {
            String parenthesis = input.replaceAll("[^()]+", "");
            if (parenthesis.length() % 2 != 0) {
                return invalidExpression;
            }
        }

        Pattern pattern = Pattern.compile("[\\da-zA-Z]+([()]*[[^+[-]*/^]&&[()]\\*])+?[()]*[\\da-zA-Z]+");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            return invalidExpression;
        }

        return "";
    }
}
