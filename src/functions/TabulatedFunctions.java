package functions;

import java.io.*;

public class TabulatedFunctions {
    private TabulatedFunctions() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса");
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        double functionLeftBorder = function.getLeftDomainBorder();
        double functionRightBorder = function.getRightDomainBorder();
        if (leftX < functionLeftBorder || rightX > functionRightBorder) {
            throw new IllegalArgumentException(
                    "Границы табулирования выходят за область определения функции");
        }

        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        int pointsCount = function.getPointsCount();
        dataOut.writeInt(pointsCount);

        for (int i = 0; i < pointsCount; i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }
        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        int pointsCount = dataIn.readInt();

        if (pointsCount < 2) {
            throw new IOException("Некорректное количество точек: " + pointsCount);
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        int pointsCount = function.getPointsCount();
        writer.print(pointsCount);

        for (int i = 0; i < pointsCount; i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.parseNumbers();

        tokenizer.nextToken();
        if (tokenizer.ttype != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Ожидалось количество точек");
        }

        int pointsCount = (int) tokenizer.nval;
        if (pointsCount < 2) {
            throw new IOException("Некорректное количество точек: " + pointsCount);
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата X точки " + i);
            }
            double x = tokenizer.nval;

            tokenizer.nextToken();
            if (tokenizer.ttype != StreamTokenizer.TT_NUMBER) {
                throw new IOException("Ожидалась координата Y точки " + i);
            }
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}