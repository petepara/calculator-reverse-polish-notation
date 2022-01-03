package main.com.ppdev;

public class CalculatorRunner {

    public static void main(String[] args) throws Exception {
        CalculatorService calculator = new PolishNotationCalculatorService();
        calculator.run();
    }
}
