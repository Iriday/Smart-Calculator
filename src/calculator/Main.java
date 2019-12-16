package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    /*enum Operators {ADD, SUBTRACT, MULTIPLY, DIVIDE, EMPTY}*/

    private final Pattern numberOrWordPattern = Pattern.compile("\\d+|[a-zA-Z]+");
    //private final Pattern numberPattern = Pattern.compile("([-+]?\\d+|[a-zA-Z]+)");
    private final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private Matcher matcher;
    //private final String actionsRegex = "[^+-]";
    private final Map<String, BigInteger> variables = new HashMap<>();

    public static void main(String[] args) {

        Main main = new Main();
        main.process();
    }

    private void process() {
        Scanner scanner = new Scanner(System.in);
        String input;
        int a;
        int b;

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

            if (!validInput(input)) {
                continue;
            }
            if (input.matches(" *[a-zA-Z]+ *= *(\\d+|[a-zA-Z]+).*")) {
                addVariable(input);
                continue;
            }

            System.out.println(compute(infixToPostfix(compressOperators(input))));
        }
    }

    private void addVariable(String input) {
        matcher = numberOrWordPattern.matcher(input);
        matcher.find();
        String variableName = input.substring(matcher.start(), matcher.end());
        matcher.find();
        BigInteger variableValue = valueOf(input.substring(matcher.start(), matcher.end()));
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

    public static BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    public static BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    public static BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    public static BigInteger divide(BigInteger a, BigInteger b) {
        return a.divide(b);
    }

    public static BigInteger power(BigInteger a, BigInteger b) {
        BigInteger result = a;
        for (BigInteger i = BigInteger.ONE; i.compareTo(b) < 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(a);
        }
        return result;
    }

    public static String compressOperators(String input) {
        Pattern plusesPattern = Pattern.compile("[+]{2,}");
        Matcher matcher = plusesPattern.matcher(input);
        input = matcher.replaceAll("+");

        Pattern pattern1 = Pattern.compile("[-]{2,}");
        matcher = pattern1.matcher(input);
        while (matcher.find()) {
            String val = matcher.group();
            if (val.length() % 2 == 0) {
                input = input.replaceAll(val, "+");
            } else {
                input = input.replaceAll(val, "-");
            }
            matcher = pattern1.matcher(input);
        }
        return input;
    }

    public static Queue<String> infixToPostfix(String infix) {
        //String postFix = "";
        //Stack<String> stack = new Stack<>();
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
                //System.out.println("+ or -");
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

    public BigInteger compute(Queue<String> inputInPostfix) {
        Stack<BigInteger> result = new Stack<>();

        for (String element : inputInPostfix) {
            if (element.matches("^[+[-]]\\d+|[\\da-zA-Z]+")) {
                result.push(valueOf(element));
            } else if (element.equals("+")) {
                BigInteger a = result.pop();
                BigInteger b = result.pop();
                BigInteger value = add(a, b);
                result.push(value);
            } else if (element.equals("-")) {
                BigInteger a = result.pop();
                BigInteger b = result.pop();
                result.push(subtract(b, a));
            } else if (element.equals("*")) {
                result.push(multiply(result.pop(), result.pop()));
            } else if (element.equals("/")) {
                BigInteger a = result.pop();
                BigInteger b = result.pop();
                result.push(divide(b, a));
            } else if (element.equals("^")) {
                BigInteger a = result.pop();
                BigInteger b = result.pop();
                result.push(power(b, a));
            }
        }
        return result.peek();
    }

    private boolean validInput(String input) {
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

