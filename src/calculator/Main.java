package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    enum Operators {ADD, SUBTRACT, MULTIPLY, DIVIDE, EMPTY}

    private final Pattern numberOrWordPattern = Pattern.compile("\\d+|[a-zA-Z]+");
    private final Pattern numberPattern = Pattern.compile("([-+]?\\d+|[a-zA-Z]+)");
    private final Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
    private Matcher matcher;
    //private Pattern operator;
    private Operators operators = Operators.EMPTY;
    private final String actionsRegex = "[^+-]";
    private final Map<String, Integer> variables = new HashMap<>();

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
                System.out.println("The program can add/subtract numbers, and supports variables.\n" +
                        "Examples: -2 + 4 - 5 + 6, 9 +++ 10 -- 8, 3 --- 5, 14  -   12, a = 2, a + -3 -5 + 2 + a");
                continue;
            }

            if (!validInput(input)) {
                continue;
            }

            if (input.matches(" *[a-zA-Z]+ *= *(\\d+|[a-zA-Z]+).*")) {
                addVariable(input);
                continue;
            }
            matcher = numberPattern.matcher(input);
            if (!matcher.find()) {
                continue;
            }
            String val1 = input.substring(matcher.start(), matcher.end());
            a = valueOf(val1);

            for (; true; ) {
                input = input.substring(matcher.end());

                matcher = numberOrWordPattern.matcher(input);
                if (matcher.find()) {
                    String val2 = input.substring(matcher.start(), matcher.end());
                    b = valueOf(val2);

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

    private void addVariable(String input) {
        matcher = numberOrWordPattern.matcher(input);
        matcher.find();
        String variableName = input.substring(matcher.start(), matcher.end());
        matcher.find();
        int variableValue = valueOf(input.substring(matcher.start(), matcher.end()));
        variables.put(variableName, variableValue);
    }

    private int valueOf(String val) {
        int a;
        try {
            a = Integer.parseInt(val);
        } catch (Exception e) {
            a = variables.get(val);
        }
        return a;
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
        if (!input.matches("^ *[-+]?[\\da-zA-Z].*")) {//".*[\\da-zA-Z]+.*[-+=]+.*[\\da-zA-Z]+ *"////
            System.out.println("Invalid expression");
            return false;
        }
        // check the end of the string
        if (!input.matches(".*[\\da-zA-Z] *$")){
            System.out.println("Invalid expression");
            return false;
        }
        if (input.matches(" *[-+]?\\d+ *")) {
            return true;
        }
        if(input.matches(" *[a-zA-Z]+ *")){
            String variable =input.trim();
            if(variables.containsKey(variable)) {
                return true;
            }else
                System.out.println("Unknown variable");
            return false;
        }
        if (input.matches(".*[^\\d +[-]=a-zA-Z].*")) {
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
                    // break;
                }
            }
        }
        int index = input.indexOf("=");
        if (index != -1) {
            if (input.matches(" *[a-zA-Z]+ *= *[\\da-zA-Z]+ *")) {
                return true;
            }else {
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
        Pattern pattern = Pattern.compile("[\\da-zA-Z]+( *[^+[-]] *)+?[\\da-zA-Z]+");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            System.out.println("Invalid expression");
            valid = false;
        }

        return valid;
    }
}

