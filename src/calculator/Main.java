package calculator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //int a;
        //int b;
        String[] input;

        int sum = 0;
        while (true) {
            input = scanner.nextLine().split(" ");

            if (input.length >= 2) {
                for (String num : input) {
                    try {
                        sum += Integer.parseInt(num);
                    } catch (Exception e) {
                        break;
                    }
                }
                System.out.println(sum);
                sum = 0;

            } else if (input.length == 1) {
                if ("/exit".equalsIgnoreCase(input[0])) {
                    System.out.println("Bye!");
                    break;

                } else if ("/help".equalsIgnoreCase(input[0])) {
                    System.out.println("The program calculates the sum of numbers");

                } else {
                    try {
                        System.out.println(Integer.parseInt(input[0]));
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
    }
}
