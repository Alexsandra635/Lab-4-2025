package functions.basic;

import functions.Function;

public class Log implements Function {
    private double base;
    private static final double EPSILON = 1e-10;

    public Log(double base) {
        if (base <= 0 || Math.abs(base - 1) < 1e-10) {
            throw new IllegalArgumentException("Основание логарифма должно быть положительным и не равно 1");
        }
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() {
        return 0;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < 0) {
            return Double.NaN;
        }
        if (Math.abs(x) < EPSILON) {  // x = 0
            return Double.NEGATIVE_INFINITY;
        }
        // Для x = 1, ln(1) = 0
        if (Math.abs(x - 1.0) < EPSILON) {
            return 0.0;
        }
        return Math.log(x) / Math.log(base);
    }
}