package calculator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
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

    public String process(String input) {
        input = input.replaceAll(" +", "");

        if (input.isEmpty()) {
            return input;
        }
        if ("/exit".equalsIgnoreCase(input)) {
            return "Bye!";
        } else if ("/help".equalsIgnoreCase(input)) {
            return help;
        }

        String checkResult = CheckInput.check(input, variables);
        if (!checkResult.equals("valid")) {
            return checkResult;
        }
        if (input.matches("[a-zA-Z]+=(\\d+|\\d+[.]\\d+|[a-zA-Z]+)")) {
            addVariable(input);
            return "";//"variable added"
        }
        try {
            BigDecimal result = compute(InfixPostfixConverter.infixToPostfix(input));
            return result.toString();
        } catch (ArithmeticException e) {
            return e.getMessage();
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

