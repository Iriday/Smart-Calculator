package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private final Pattern numberWordPattern = Pattern.compile("[\\da-zA-Z]+");
    private final Map<String, BigInteger> variables = new HashMap<>();

    public static void main(String[] args) {

        Calculator calculator = new Calculator();
        calculator.process();
    }

    private void process() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine().replaceAll(" +", "");

            if (input.isEmpty()) {
                continue;
            }
            if ("/exit".equalsIgnoreCase(input)) {
                System.out.println("Bye!");
                break;
            } else if ("/help".equalsIgnoreCase(input)) {
                System.out.println("The program can add/subtract/multiply/divide numbers, supports parenthesis, variables, power operator.\n" +
                        "Examples: 9 +++ 10 -- 8 * 3 / 2,\n" +
                        "3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1),\n" +
                        "a = 2,\n" +
                        "b = 3,\n" +
                        "a + -3 -5 + (b + a),\n" +
                        "2 * 2^3.");
                continue;
            }

            if (!CheckInput.validInput(input, variables)) {
                continue;
            }
            if (input.matches("[a-zA-Z]+=[\\da-zA-Z]+")) {
                addVariable(input);
                continue;
            }

            BigInteger result = compute(infixToPostfix(compressOperators(input)));
            System.out.println(result);
        }
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

    public static String compressOperators(String input) {
        Matcher matcher = Pattern.compile("[+]{2,}").matcher(input);
        input = matcher.replaceAll("+");

        Pattern pattern = Pattern.compile("[-]{2,}");
        matcher = pattern.matcher(input);
        while (matcher.find()) {
            String val = matcher.group();
            if (val.length() % 2 == 0) {
                input = input.replaceAll(val, "+");
            } else {
                input = input.replaceAll(val, "-");
            }
            matcher = pattern.matcher(input);
        }
        return input;
    }

    public static Queue<String> infixToPostfix(String infix) {
        Deque<String> stack = new ArrayDeque<>();
        Queue<String> queue = new ArrayDeque<>();

        Pattern pattern = Pattern.compile(
                "(^[+[-]]\\d+)|" +
                        "([(][+[-]]\\d+[)])|" +
                        "([\\da-zA-Z]+)|" +
                        "([+[-]/*()^])");
        Matcher matcher = pattern.matcher(infix);


        while (matcher.find()) {
            String inputElement = matcher.group();

            if (inputElement.matches("[(][+[-]]\\d+[)]")) {
                queue.offer(inputElement.replaceAll("[()]", ""));

            } else if (inputElement.matches("[\\da-zA-Z]+|[+[-]]\\d+")) {
                queue.offer(inputElement);

            } else if (inputElement.matches("[(]")) {
                stack.offer(inputElement);

            } else if (inputElement.matches("[)]")) {
                while (!stack.getLast().equals("(")) {
                    queue.offer(stack.removeLast());
                }
                stack.removeLast();

            } else if (inputElement.matches("[+[-]]")) {
                if (stack.isEmpty()) {
                    stack.offer(inputElement);
                } else {
                    String stackElement = stack.getLast();
                    if ("(".equals(stackElement)) {
                        stack.offer(inputElement);
                    } else {
                        while (!stack.isEmpty() && !"(".equals(stack.getLast())) {
                            queue.offer(stack.removeLast());
                        }
                        stack.offer(inputElement);
                    }
                }
            } else if (inputElement.matches("[*/]")) {
                if (stack.isEmpty()) {
                    stack.offer(inputElement);
                } else {
                    String stackElement = stack.getLast();
                    if ("(".equals(stackElement)) {
                        stack.offer(inputElement);
                    } else if ("+".equals(stackElement) || "-".equals(stackElement)) {
                        stack.offer(inputElement);
                    } else {
                        while (!stack.isEmpty() && !stack.getLast().matches("[+[-](]")) {
                            queue.offer(stack.removeLast());
                        }
                        stack.offer(inputElement);
                    }
                }
            } else if (inputElement.matches("\\^")) {
                stack.offer(inputElement);
            }
        }

        while (!stack.isEmpty()) {
            queue.offer(stack.removeLast());
        }
        //System.out.println(queue);
        return queue;
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

