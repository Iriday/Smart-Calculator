package calculator;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorModel {
    private static final Pattern numberWordPattern = Pattern.compile("\\d+[.]\\d+|[\\da-zA-Z]+");
    private final Map<String, BigDecimal> variables = new HashMap<>();
    private boolean removeTrailingZeros = true;
    private static final String help = "The program can: add/subtract/multiply/divide numbers, supports parenthesis, variables, power operator.\n" +
            "Example: a = 2\none = 1\n2^3 +++ +8.2 * ((4 - a) * 2.5 * one ) --- +6 / (E * PI)^2\n" +
            "Commands: /exit, /help, /variables";

    public CalculatorModel() {
        addVariables();
    }

    public String process(String input) {
        input = input.replaceAll(" +", "");

        if (input.isEmpty()) {
            return input;
        }
        if (input.startsWith("/")) { //command
            return executeCommand(input);
        }

        String checkResult = CheckInput.check(input, variables);
        if (!checkResult.equals(CheckInput.VALID_INPUT)) {
            return checkResult;
        }
        if (input.contains("=")) { //assignment
            addVariable(input);
            return ""; //variable added
        }
        try {
            String result = compute(InfixPostfixConverter.infixToPostfix(input)).toString();
            if (removeTrailingZeros) {
                result = removeTrailingZeros(result);
            }
            return result;
        } catch (ArithmeticException e) {
            return e.getMessage();
        }
    }

    private void addVariables() {
        variables.put("E", new BigDecimal(Math.E));
        variables.put("PI", new BigDecimal(Math.PI));
    }

    private String executeCommand(String command) {
        switch (command.toLowerCase(Locale.ENGLISH)) {
            case "/exit":
                return "Bye!";
            case "/help":
                return help;
            case "/variables":
                StringBuilder builder = new StringBuilder();
                for (Map.Entry entry : variables.entrySet()) {
                    builder.append(entry.getKey());
                    builder.append(" = ");
                    builder.append(entry.getValue());
                    builder.append("\n");
                }
                return builder.toString();
            case "/trailing_zeros":
                removeTrailingZeros = !removeTrailingZeros;
                return removeTrailingZeros ? "Removing trailing zeros" : "Showing trailing zeros";
            default:
                return CheckInput.UNKNOWN_COMMAND;
        }
    }

    private String removeTrailingZeros(String input) {
        return input.contains(".") && input.charAt(input.length() - 1) == '0' ? input.replaceFirst("\\.?0+$", "") : input;
    }

    private void addVariable(String input) {
        Matcher matcher = numberWordPattern.matcher(input);
        matcher.find();
        String variableName = matcher.group();
        matcher.find();
        BigDecimal variableValue = valueOf(matcher.group());
        variables.put(variableName, variableValue);
    }

    private BigDecimal valueOf(String val) {
        BigDecimal a;
        if (val.matches("[a-zA-Z]+")) {
            a = variables.get(val);
        } else {
            a = new BigDecimal(val);
        }
        return a;
    }

    public BigDecimal compute(Queue<String> inputPostfix) {
        Stack<BigDecimal> result = new Stack<>();
        BigDecimal a;
        BigDecimal b;
        for (String element : inputPostfix) {
            if (element.matches("[+[-]]?\\d+[.]\\d+|[+[-]]?\\d+|[a-zA-Z]+")) {
                result.push(valueOf(element));
            } else if (element.equals("+")) {
                a = result.pop();
                b = result.pop();
                result.push(ArithmeticOperations.add(b, a));
            } else if (element.equals("-")) {
                a = result.pop();
                b = result.pop();
                result.push(ArithmeticOperations.subtract(b, a));
            } else if (element.equals("*")) {
                a = result.pop();
                b = result.pop();
                result.push(ArithmeticOperations.multiply(b, a));
            } else if (element.equals("/")) {
                a = result.pop();
                b = result.pop();
                result.push(ArithmeticOperations.divide(b, a));
            } else if (element.equals("^")) {
                a = result.pop();
                b = result.pop();
                result.push(ArithmeticOperations.power(b, a));
            }
        }
        return result.peek();
    }
}

