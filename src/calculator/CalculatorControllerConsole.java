package calculator;

class CalculatorControllerConsole {
    private final CalculatorModel calculatorModel;
    private final CalculatorViewConsole calculatorViewConsole;

    CalculatorControllerConsole(CalculatorModel calculatorModel) {
        this.calculatorModel = calculatorModel;
        calculatorViewConsole = new CalculatorViewConsole(this);
    }

    String process(String input) {
        return calculatorModel.process(input);
    }
}
