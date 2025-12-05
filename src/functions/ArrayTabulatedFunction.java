package functions;

import java.io.Serializable;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private int pointsCount;
    private FunctionPoint[] points;
    private static final double EPSILON = 1e-9;
    private static final long serialVersionUID = 1L;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 5];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 5];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX() + EPSILON) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 5];
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON)
            return Double.NaN;

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                if (Math.abs(x - x1) < EPSILON) {
                    return points[i].getY();
                }
                if (Math.abs(x - x2) < EPSILON) {
                    return points[i + 1].getY();
                }

                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + ((y2 - y1) * (x - x1)) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }

        if (index > 0 && point.getX() <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }

        if (index > 0 && x <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }

        points[index].setX(x);
    }

    @Override
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне диапазона [0, " + (pointsCount - 1) + "]");
        }

        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку - останется меньше двух точек");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
        points[pointsCount] = null;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX() - EPSILON) {
            insertIndex++;
        }

        if (pointsCount >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length + 5];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
}