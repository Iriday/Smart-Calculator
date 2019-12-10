package calculator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a;
        int b;
        String[] input;

        while (true) {
            input = scanner.nextLine().split(" ");

            if (input.length == 2) {
                a = Integer.parseInt(input[0]);
                b = Integer.parseInt(input[1]);
                System.out.println(a + b);

            } else if (input.length == 1) {
                if ("/exit".equalsIgnoreCase(input[0])) {
                    System.out.println("Bye!");
                    break;
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
