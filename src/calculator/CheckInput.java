package calculator;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CheckInput {
    private static final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private static Matcher matcher;

    private static final String validInput = "valid";
    private static final String unknownCommand = "Unknown command";
    private static final String invalidExpression = "Invalid expression";
    private static final String unknownVariable = "Unknown variable";
    private static final String invalidIdentifier = "Invalid identifier";
    private static final String invalidAssignment = "Invalid assignment";

    static String check(String input, Map<String, BigInteger> variables) {

        if (input.matches(" */.*")) {
            return unknownCommand;
        }
        // check the beginning of the string
        if (!input.matches("^[(]*[-+]?[(]*[\\da-zA-Z].*")) {
            return invalidExpression;
        }
        // check the end of the string
        if (!input.matches(".*[\\da-zA-Z][)]*$")) {
            return invalidExpression;
        }
        if (input.matches(" *[-+]?\\d+ *")) {
            return validInput;
        }
        if (input.matches(" *[a-zA-Z]+ *")) {
            String variable = input.trim();
            if (variables.containsKey(variable)) {
                return validInput;
            } else
                return unknownVariable;
        }
        if (input.matches(".*[^\\d +[-]=a-zA-Z*/()^].*")) {
            return invalidExpression;
        }
        if (input.matches(" *(\\d+[a-zA-Z]+|[a-zA-Z]+\\d+) *= *\\d+ *")) {
            return invalidIdentifier;
        }
        if (input.matches("(.*=.*=.*)|( *[a-zA-Z]+ *= *)")) {
            return invalidAssignment;
        }
        if (input.matches("(.*[a-zA-Z]+\\d+.*)|(.*\\d+[a-zA-Z]+.*)")) {
            return invalidAssignment;

        }
        if (input.matches(" *[a-zA-Z]+ *= *[a-zA-Z]+ *")) {
            Pattern pattern = Pattern.compile("[a-zA-Z] *$");
            matcher = pattern.matcher(input);
            matcher.find();
            String variableName = matcher.group().trim();
            if (!variables.containsKey(variableName)) {
                return unknownVariable;
            }
        }
        if (input.matches(".*\\d+.*=.*[a-zA-Z\\d]+.*")) {
            return invalidAssignment;
        }
        if (input.matches(" *[-+]?\\d+.*[a-zA-Z]+.*")) {
            Pattern variableNamePattern = Pattern.compile("[a-zA-Z]+");
            matcher = variableNamePattern.matcher(input);
            while (matcher.find()) {
                if (!variables.containsKey(matcher.group())) {
                    return unknownVariable;
                }
            }
        }
        if (input.matches(".*[*]{2,}.*|.*[/]{2,}.*|.*[*&&/].*|.*[/&&*].*")) {
            return invalidAssignment;
        }

        int index = input.indexOf("=");
        if (index != -1) {
            if (input.matches(" *[a-zA-Z]+ *= *[\\da-zA-Z]+ *")) {
                return validInput;
            } else {
                return invalidAssignment;
            }
        }
        // check all variables
        matcher = wordPattern.matcher(input);
        while (matcher.find()) {
            if (!variables.containsKey(matcher.group())) {
                return unknownVariable;
            }
        }
        //input = input.substring(index + 1);
        Pattern pattern = Pattern.compile("[\\da-zA-Z]+( *[()]* *[[^+[-]*/^]&&[()]\\*] *)+?[()]* *[\\da-zA-Z]+");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            return invalidExpression;
        }

        if (input.contains("(") || input.contains(")")) {
            String parenthesis = input.replaceAll("[^()]+", "");
            if (parenthesis.length() % 2 != 0) {
                return invalidExpression;
            }
        }
        return validInput;
    }
}
