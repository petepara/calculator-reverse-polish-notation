package main.com.ppdev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolishNotationCalculatorService implements CalculatorService {
    private String inputData = "";
    @Override
    public double calculate(String expression) {
        String preparedExpression = preparingExpression(expression);
        String expressionInRPN = convertToReversePolishNotation(preparedExpression);
        return calculateExpressionInReversePolishNotation(expressionInRPN);
    }

    @Override
    public void run() {
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter expression");
            inputData = inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputData= preparingExpression(inputData);

        System.out.println("\nReverse polish notation is: \n" + convertToReversePolishNotation(inputData)+"\n");
        System.out.println("Result:");
        System.out.println(calculate(inputData));
    }

    private String preparingExpression(String expression) {
        return expression.replaceAll("^-|(?<=\\()-", "0-");
    }

    private String convertToReversePolishNotation(String expression) {
        Pattern pattern = Pattern.compile("[^\\d+-/*%^()]");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            throw new IllegalArgumentException("Your expression contains bad symbols!");
        }
        StringBuilder current = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        int priority;

        try {
            for (int i = 0; i < expression.length(); i++) {
                priority = getPriority(expression.charAt(i));
                if (priority == 0) {
                    current.append(expression.charAt(i));
                } else if (priority == 1) {
                    stack.push(expression.charAt(i));
                } else if (priority > 1) {
                    if (expression.charAt(i) != '%') {
                        current.append(' ');
                    }
                    while (!stack.isEmpty()) {
                        if (getPriority(stack.peek()) >= priority) {
                            current.append(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(expression.charAt(i));
                } else {
                    current.append(' ');
                    while (getPriority(stack.peek()) != 1) {
                        current.append(stack.pop());
                    }
                    stack.pop();
                }
            }
        } catch (RuntimeException ex) {
            System.err.println("Something wrong with symbols in your expression");
            ex.printStackTrace();
        }
        while (!stack.isEmpty()) {
            current.append(stack.pop());
        }
        return current.toString();

    }

    private double calculateExpressionInReversePolishNotation(String expressionInRPN) {

        StringBuilder operand = new StringBuilder();
        Deque<Double> stack = new ArrayDeque<>();
        try {
            for (int i = 0; i < expressionInRPN.length(); i++) {
                if (expressionInRPN.charAt(i) == ' ') {
                    continue;
                }
                if (getPriority(expressionInRPN.charAt(i)) == 0) {
                    while (expressionInRPN.charAt(i) != ' '
                            && getPriority(expressionInRPN.charAt(i)) == 0) {
                        operand.append(expressionInRPN.charAt(i++));

                        if (i == expressionInRPN.length()) {
                            break;
                        }
                    }
                    stack.push(Double.parseDouble(operand.toString()));
                    operand.setLength(0);
                }
                if (getPriority(expressionInRPN.charAt(i)) > 1) {
                    double a = stack.pop();
                    double b = 0;
                    if (expressionInRPN.charAt(i) == '%') {
                        b = 0.01;
                        stack.push(b * a);
                        continue;
                    }
                    b = stack.pop();
                    if (expressionInRPN.charAt(i) == '^') {
                        for (int j = 1; j < a; j++) {
                            b *= b;
                        }
                        stack.push(b);
                    } else if (expressionInRPN.charAt(i) == '+') {
                        stack.push(b + a);
                    } else if (expressionInRPN.charAt(i) == '-') {
                        stack.push(b - a);
                    } else if (expressionInRPN.charAt(i) == '*') {
                        stack.push(b * a);
                    } else if (expressionInRPN.charAt(i) == '/') {
                        stack.push(b / a);
                    }
                }
            }
        } catch (RuntimeException ex) {
            System.err.println("Something wrong with symbols in your expression");
            ex.printStackTrace();
        }
        return stack.pop();
    }

    private int getPriority(char token) {
        if (token == '%' || token == '^') {
            return 4;
        } else if (token == '*' || token == '/') {
            return 3;
        } else if (token == '+' || token == '-') {
            return 2;
        } else if (token == '(') {
            return 1;
        } else if (token == ')') {
            return -1;
        } else {
            return 0;
        }
    }
}
