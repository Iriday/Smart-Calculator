package calculator;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CheckInput {
    private static final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private static Matcher matcher;

    static boolean validInput(String input, Map<String, BigInteger> variables) {
        boolean valid = true;

        if (input.isEmpty() || input.matches(" +")) {
            return true;
        }
        if (input.matches(" */.*")) {
            System.out.println("Unknown command");
            return false;
        }
        // check the beginning of the string
        if (!input.matches("^[(]*[-+]?[(]*[\\da-zA-Z].*")) {
            System.out.println("Invalid expression");
            return false;
        }
        // check the end of the string
        if (!input.matches(".*[\\da-zA-Z][)]*$")) {
            System.out.println("Invalid expression");
            return false;
        }
        if (input.matches(" *[-+]?\\d+ *")) {
            return true;
        }
        if (input.matches(" *[a-zA-Z]+ *")) {
            String variable = input.trim();
            if (variables.containsKey(variable)) {
                return true;
            } else
                System.out.println("Unknown variable");
            return false;
        }
        if (input.matches(".*[^\\d +[-]=a-zA-Z*/()^].*")) {
            System.out.println("Invalid expression");
            return false;
        }
        if (input.matches(" *(\\d+[a-zA-Z]+|[a-zA-Z]+\\d+) *= *\\d+ *")) {
            System.out.println("Invalid identifier");
            return false;
        }
        if (input.matches("(.*=.*=.*)|( *[a-zA-Z]+ *= *)")) {
            System.out.println("Invalid assignment");
            return false;
        }
        if (input.matches("(.*[a-zA-Z]+\\d+.*)|(.*\\d+[a-zA-Z]+.*)")) {
            System.out.println("Invalid assignment");
            return false;

        }
        if (input.matches(" *[a-zA-Z]+ *= *[a-zA-Z]+ *")) {
            Pattern pattern = Pattern.compile("[a-zA-Z] *$");
            matcher = pattern.matcher(input);
            matcher.find();
            String variableName = matcher.group().trim();
            if (!variables.containsKey(variableName)) {
                System.out.println("Unknown variable");
                return false;
            }
        }
        if (input.matches(".*\\d+.*=.*[a-zA-Z\\d]+.*")) {
            System.out.println("Invalid assignment");
            return false;
        }
        if (input.matches(" *[-+]?\\d+.*[a-zA-Z]+.*")) {
            Pattern variableNamePattern = Pattern.compile("[a-zA-Z]+");
            matcher = variableNamePattern.matcher(input);
            while (matcher.find()) {
                if (!variables.containsKey(matcher.group())) {
                    System.out.println("Unknown variable");
                    return false;
                }
            }
        }
        if (input.matches(".*[*]{2,}.*|.*[/]{2,}.*|.*[*&&/].*|.*[/&&*].*")) {
            System.out.println("Invalid assignment");
            return false;
        }

        int index = input.indexOf("=");
        if (index != -1) {
            if (input.matches(" *[a-zA-Z]+ *= *[\\da-zA-Z]+ *")) {
                return true;
            } else {
                System.out.println("Invalid assignment");
                return false;
            }
        }
        // check all variables
        matcher = wordPattern.matcher(input);
        while (matcher.find()) {
            if (!variables.containsKey(matcher.group())) {
                System.out.println("Unknown variable");
                return false;
            }
        }
        //input = input.substring(index + 1);
        Pattern pattern = Pattern.compile("[\\da-zA-Z]+( *[()]* *[[^+[-]*/^]&&[()]\\*] *)+?[()]* *[\\da-zA-Z]+");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            System.out.println("Invalid expression");
            valid = false;
        }

        if (input.contains("(") || input.contains(")")) {
            String parenthesis = input.replaceAll("[^()]+", "");
            if (parenthesis.length() % 2 != 0) {
                System.out.println("Invalid expression");
                return false;
            }
        }
        return valid;
    }
}
