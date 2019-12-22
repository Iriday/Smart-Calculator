package calculator;

public class CalculatorTestDrive {//CalculatorMain

    public static void main(String[] args) {

        CalculatorModel calculatorModel = new CalculatorModel();
        CalculatorControllerConsole calculatorControllerConsole = new CalculatorControllerConsole(calculatorModel);
    }
}
