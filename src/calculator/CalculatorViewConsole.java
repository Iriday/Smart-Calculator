package calculator;

import java.util.Scanner;

public class CalculatorViewConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final CalculatorControllerConsole controller;

    public CalculatorViewConsole(CalculatorControllerConsole controller) {
        this.controller = controller;
        start();
    }

    private void start() {
        String result;
        String bye = "Bye!";
        while (true) {
            result = controller.process(input());

            if (result.equals("")) {
                continue;
            } else if (result.equals(bye)) {
                output(result);
                break;
            }
            output(result);
        }
    }

    private String input() {
        return scanner.nextLine();
    }

    private void output(String result) {
        System.out.println(result);
    }
}
