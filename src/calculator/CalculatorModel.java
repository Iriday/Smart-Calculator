package calculator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorModel {
    private static final  Pattern numberWordPattern = Pattern.compile("[\\da-zA-Z]+");
    private final Map<String, BigInteger> variables = new HashMap<>();
    private static final String help ="The program can add/subtract/multiply/divide numbers, supports parenthesis, variables, power operator.\n" +
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
        if (input.matches("[a-zA-Z]+=[\\da-zA-Z]+")) {
            addVariable(input);
            return "";//"variable added"
        }

        BigInteger result = compute(InfixPostfixConverter.infixToPostfix(input));
        return result.toString();

    }

    private void addVariable(String input) {
        Matcher matcher = numberWordPattern.matcher(input);
        matcher.find();
        String variableName = matcher.group();
        matcher.find();
        BigInteger variableValue = valueOf(matcher.group());
        variables.put(variableName, variableValue);
    }

    private BigInteger valueOf(String val) {
        BigInteger a;
        if (val.matches("[a-zA-Z]+")) {
            a = variables.get(val);
        } else {
            a = new BigInteger(val);
        }
        return a;
    }

    public BigInteger compute(Queue<String> inputPostfix) {
        Stack<BigInteger> result = new Stack<>();
        BigInteger a;
        BigInteger b;
        for (String element : inputPostfix) {
            if (element.matches("^[+[-]]\\d+|[\\da-zA-Z]+")) {
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

