package calculator;

import java.math.BigInteger;

public class ArithmeticOperations {

    public static BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    public static BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    public static BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    public static BigInteger divide(BigInteger a, BigInteger b) {
        return a.divide(b);
    }

    public static BigInteger power(BigInteger a, BigInteger b) {
        BigInteger result = a;
        for (BigInteger i = BigInteger.ONE; i.compareTo(b) < 0; i = i.add(BigInteger.ONE)) {
            result = result.multiply(a);
        }
        return result;
    }
}
