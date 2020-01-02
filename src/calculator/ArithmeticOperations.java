package calculator;

import java.math.BigDecimal;

public class ArithmeticOperations {

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b);
    }

    public static BigDecimal power(BigDecimal a, BigDecimal b) {
        BigDecimal result = a;
        for (BigDecimal i = BigDecimal.ONE; i.compareTo(b) < 0; i = i.add(BigDecimal.ONE)) {
            result = result.multiply(a);
        }
        return result;
    }
}
