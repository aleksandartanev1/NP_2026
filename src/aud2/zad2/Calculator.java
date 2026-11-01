package aud2.zad2;

import aud1.zad2.UnknownOperatorException;

public class Calculator {

    private double result;

    public static final char PLUS = '+';
    public static final char MINUS = '-';
    public static final char MULTIPLY = '*';
    public static final char DIVIDE = '/';

    public Calculator() {
        result = 0;
    }

    public void init(){
        result = 0;
        System.out.println("Calculator is on");
        System.out.println("Result = " + result);
    }

    // TODO: getResult

    public void executeOperation(char operation, double operand) throws UnknownOperatorException {
        if(operation == PLUS) {
            result += operand;
        } else if(operation == MINUS) {
            result -= operand;
        } else if(operation == MULTIPLY) {
            result *= operand;
        } else if(operation == DIVIDE) {

            if(operand == 0) {
                return;
            }

            result /= operand;
        } else if(operation == 'r') {
            System.out.println("Final result " + result);
        } else {
            throw new UnknownOperatorException(operation);
        }

        System.out.printf("result %c %.2f= %.2f", operation, operand, result);
        System.out.println("Updated result: " + result);
    }
}
