package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    enum Operators {ADD, SUBTRACT, MULTIPLY, DIVIDE, EMPTY}

    private Pattern numberPattern;
    private Matcher matcher;
    //private Pattern operator;
    private Operators operators = Operators.EMPTY;
    private String actionsRegex = "[^+-]";

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
            input = scanner.nextLine();

            if ("/exit".equalsIgnoreCase(input)) {
                System.out.println("Bye!");
                break;
            } else if ("/help".equalsIgnoreCase(input)) {
                System.out.println("The program can add, and subtract numbers. Examples: -2 + 4 - 5 + 6, 9 +++ 10 -- 8, 3 --- 5, 14  -   12.");
                continue;
            }
            if (!checkInput(input)) {
                continue;
            }

            numberPattern = Pattern.compile("[-+]?\\d+");
            matcher = numberPattern.matcher(input);
            if (!matcher.find()) {
                continue;
            }
            a = Integer.parseInt(input.substring(matcher.start(), matcher.end()));

            numberPattern = Pattern.compile("\\d+");//[-+]?
            for (; true; ) {
                input = input.substring(matcher.end());

                matcher = numberPattern.matcher(input);
                if (matcher.find()) {
                    b = Integer.parseInt(input.substring(matcher.start(), matcher.end()));

                    String actions = input.substring(0, matcher.start()).replaceAll(actionsRegex, "");//.split
                    a = result(actions, a, b);
                    //System.out.println(a);
                } else {
                    break;
                }
            }
            System.out.println(a);
        }
    }

    private int result(String actions, int a, int b) {
        int result = -1;

        for (char action : actions.toCharArray()) {
            if (action == '+') {
                operators = operators == Operators.EMPTY || operators == Operators.ADD ? Operators.ADD : Operators.SUBTRACT;

            } else if (action == '-') {
                operators = operators == Operators.EMPTY || operators == Operators.ADD ? Operators.SUBTRACT : Operators.ADD;
            }
        }

        switch (operators) {
            case ADD:
                result = add(a, b);
                break;
            case SUBTRACT:
                result = subtract(a, b);
                break;
        }
        operators = Operators.EMPTY;
        return result;
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int subtract(int a, int b) {
        return a - b;
    }

    private boolean checkInput(String input) {
        boolean valid = true;

        if (input.isEmpty() || input.matches(" +")) {
            valid = true;
        } else if (input.matches(" */.*")) {
            System.out.println("Unknown command");
            valid = false;
        } else if (input.matches(" *[-+]?\\d+ *")) {
            valid = true;
        } else if (input.matches(".*[^\\d +[-]].*")) {
            System.out.println("Invalid expression");
            valid = false;
        } else if (!input.matches(".*\\d+.*[-+]+.*\\d+ *")) {
            System.out.println("Invalid expression");
            valid = false;
        }
        return valid;
    }
}

