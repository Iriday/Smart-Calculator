package calculator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfixPostfixConverter {

    public static Queue<String> infixToPostfix(String infix) { //examples: 2 + 2 -> 2 2 +,  2 - 2 - 2 -> 2 2 - 2 -,  2 + 2 * 2 / 2 -> 2 2 2 * 2 / +,  2 ^ 2 -> 2 2 ^
        infix = mergeOperators(infix);

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

    public static String mergeOperators(String input) { //examples: 2+++2 -> 2+2,  2--2 -> 2+2,  2+-2 -> 2-2,  2-----+2 -> 2-2
        StringBuilder builder = new StringBuilder();
        char plus = '+';
        char minus = '-';
        int m = 0;
        int p = 0;

        for (int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) != '+' && input.charAt(i) != '-') {
                builder.append(input.charAt(i));
            } else {
                if (input.charAt(i) == plus) {
                    p++;
                } else if (input.charAt(i) == minus) {
                    m++;
                }
                if (input.charAt(i + 1) != '+' && input.charAt(i + 1) != '-') {
                    if (m == 1 && p == 0 || p == 1 && m == 0) {
                        builder.append(input.charAt(i));
                    } else if (m == 0) {
                        builder.append(plus);
                    } else if (p == 0) {
                        builder.append(m % 2 == 0 ? plus : minus);
                    } else if (p == 1 && m == 1) {
                        builder.append(minus);
                    } else if (p > 1 && m == 1) {
                        builder.append(minus);
                    } else if (m > 1 && p == 1) {
                        builder.append(m % 2 == 0 ? plus : minus);
                    }
                    m = 0;
                    p = 0;
                }
            }
        }
        builder.append(input.charAt(input.length() - 1));
        //System.out.println(builder.toString());
        return builder.toString();
    }
}
