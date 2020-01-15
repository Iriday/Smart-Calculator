package calculator;

import java.util.Locale;
import java.util.Map;

public class Commands {
    private static final String help = "The program can: add/subtract/multiply/divide numbers, supports parenthesis, variables, power operator.\n" +
            "Example: a = 2\none = 1\n2^3 +++ +8.2 * ((4 - a) * 2.5 * one ) --- +6 / (E * PI)^2\n" +
            "Commands: /exit, /help, /variables";

    public static String executeCommand(String command, CalculatorModel model) {
        switch (command.toLowerCase(Locale.ENGLISH)) {
            case "/exit":
                return "Bye!";
            case "/help":
                return help;
            case "/variables":
                StringBuilder builder = new StringBuilder();
                for (Map.Entry entry : model.getVariables().entrySet()) {
                    builder.append(entry.getKey());
                    builder.append(" = ");
                    builder.append(entry.getValue());
                    builder.append("\n");
                }
                return builder.toString();
            case "/trailing_zeros":
                model.removeTrailingZeros = !model.removeTrailingZeros;
                return model.removeTrailingZeros ? "Removing trailing zeros" : "Showing trailing zeros";
            default:
                return CheckInput.UNKNOWN_COMMAND;
        }
    }
}
