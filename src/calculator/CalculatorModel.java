package calculator;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorModel {
    private static final Pattern numberWordPattern = Pattern.compile("\\d+[.]\\d+|[\\da-zA-Z]+");
    private final Map<String, BigDecimal> variables = new HashMap<>();
    private static final String help = "The program can add/subtract/multiply/divide numbers, supports parenthesis, variables, power operator.\n" +
            "Examples: 9 +++ 10 -- 8 * 3 / 2,\n" +
            "3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1),\n" +
            "a = 2,\n" +
            "b = 3,\n" +
            "a + -3 -5 + (b + a),\n" +
            "2 * 2^3.";

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
            BigDecimal result = compute(InfixPostfixConverter.infixToPostfix(input));
            return result.toString();
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
            default:
                return CheckInput.UNKNOWN_COMMAND;
        }
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
            if (element.matches("^[+[-]]\\d+[.]\\d+|\\d+[.]\\d+|^[+[-]]\\d+|[\\da-zA-Z]+")) {
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

