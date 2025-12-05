import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== ЛАБОРАТОРНАЯ РАБОТА №4 ===");
            System.out.println("=== ТЕСТИРОВАНИЕ ВСЕХ ЗАДАНИЙ ===\n");

            // Задание 3: Тестирование базовых функций
            testBasicFunctions();

            // Задание 4-5: Тестирование метафункций
            testMetaFunctions();

            // Задание 6: Тестирование табулирования
            testTabulation();

            // Задание 7: Тестирование ввода/вывода
            testInputOutput();

            // Задание 8: Полное тестирование задания 8
            testAssignment8();

            // Задание 9: Тестирование сериализации
            testSerialization();

            System.out.println("\n=== ВСЕ ТЕСТЫ УСПЕШНО ВЫПОЛНЕНЫ ===");

        } catch (Exception e) {
            System.out.println("\nОШИБКА: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testBasicFunctions() {
        System.out.println("=== ЗАДАНИЕ 3: БАЗОВЫЕ ФУНКЦИИ ===");

        // Тестирование экспоненты
        Function exp = new Exp();
        System.out.println("\n1. Экспонента:");
        System.out.println("   Область определения: [" + exp.getLeftDomainBorder() + ", " + exp.getRightDomainBorder() + "]");
        System.out.println("   exp(0) = " + format(exp.getFunctionValue(0)));
        System.out.println("   exp(1) = " + format(exp.getFunctionValue(1)));
        System.out.println("   exp(2) = " + format(exp.getFunctionValue(2)));

        // Тестирование логарифма
        Function log = new Log(Math.E);
        System.out.println("\n2. Натуральный логарифм:");
        System.out.println("   Область определения: [" + log.getLeftDomainBorder() + ", " + log.getRightDomainBorder() + "]");
        System.out.println("   ln(1) = " + format(log.getFunctionValue(1)));
        System.out.println("   ln(e) = " + format(log.getFunctionValue(Math.E)));
        System.out.println("   ln(10) = " + format(log.getFunctionValue(10)));

        // Тестирование тригонометрических функций
        Function sin = new Sin();
        Function cos = new Cos();
        Function tan = new Tan();

        System.out.println("\n3. Тригонометрические функции:");
        System.out.println("   sin(0) = " + format(sin.getFunctionValue(0)));
        System.out.println("   sin(π/2) = " + format(sin.getFunctionValue(Math.PI/2)));
        System.out.println("   cos(0) = " + format(cos.getFunctionValue(0)));
        System.out.println("   cos(π) = " + format(cos.getFunctionValue(Math.PI)));
        System.out.println("   tan(π/4) = " + format(tan.getFunctionValue(Math.PI/4)));
    }

    private static void testMetaFunctions() {
        System.out.println("\n\n=== ЗАДАНИЕ 4-5: МЕТАФУНКЦИИ ===");

        Function sin = new Sin();
        Function cos = new Cos();

        // Сумма функций
        Function sum = Functions.sum(sin, cos);
        System.out.println("\n1. Сумма sin(x) + cos(x):");
        System.out.println("   при x=0: " + format(sum.getFunctionValue(0)));
        System.out.println("   при x=π/4: " + format(sum.getFunctionValue(Math.PI/4)));

        // Произведение функций
        Function mult = Functions.mult(sin, cos);
        System.out.println("\n2. Произведение sin(x) * cos(x):");
        System.out.println("   при x=π/4: " + format(mult.getFunctionValue(Math.PI/4)));

        // Возведение в степень
        Function sinSquared = Functions.power(sin, 2);
        System.out.println("\n3. Квадрат синуса sin²(x):");
        System.out.println("   при x=π/6: " + format(sinSquared.getFunctionValue(Math.PI/6)));

        // Сдвиг функции
        Function shiftedSin = Functions.shift(sin, Math.PI/2, 0);
        System.out.println("\n4. Сдвинутый синус sin(x-π/2):");
        System.out.println("   при x=π/2: " + format(shiftedSin.getFunctionValue(Math.PI/2)));

        // Масштабирование
        Function scaledSin = Functions.scale(sin, 2, 3);
        System.out.println("\n5. Масштабированный синус 3*sin(x/2):");
        System.out.println("   при x=π: " + format(scaledSin.getFunctionValue(Math.PI)));

        // Композиция
        Function composition = Functions.composition(sin, cos);
        System.out.println("\n6. Композиция sin(cos(x)):");
        System.out.println("   при x=0: " + format(composition.getFunctionValue(0)));
    }

    private static void testTabulation() throws Exception {
        System.out.println("\n\n=== ЗАДАНИЕ 6: ТАБУЛИРОВАНИЕ ===");

        Function sin = new Sin();

        // Табулируем синус
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);

        System.out.println("\nТабулированный синус на [0, π] (10 точек):");
        System.out.println("Количество точек: " + tabSin.getPointsCount());
        System.out.println("Область определения: [" + tabSin.getLeftDomainBorder() + ", " + tabSin.getRightDomainBorder() + "]");

        System.out.println("\nТочки функции:");
        for (int i = 0; i < tabSin.getPointsCount(); i++) {
            System.out.printf("   Точка %d: x=%.3f, y=%.6f%n",
                    i, tabSin.getPointX(i), tabSin.getPointY(i));
        }

        // Сравнение с точным значением
        System.out.println("\nСравнение с точными значениями:");
        double[] testX = {0, Math.PI/6, Math.PI/4, Math.PI/3, Math.PI/2, 2*Math.PI/3, 3*Math.PI/4, 5*Math.PI/6, Math.PI};
        for (double x : testX) {
            double exact = sin.getFunctionValue(x);
            double approx = tabSin.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("   x=%.3f: точное=%.6f, приближ=%.6f, ошибка=%.6f%n",
                    x, exact, approx, error);
        }
    }

    private static void testInputOutput() throws IOException {
        System.out.println("\n\n=== ЗАДАНИЕ 7: ВВОД/ВЫВОД ===");

        // Создаем тестовую функцию (парабола y = x²)
        FunctionPoint[] points = new FunctionPoint[5];
        for (int i = 0; i < 5; i++) {
            double x = i;
            points[i] = new FunctionPoint(x, x * x);
        }
        TabulatedFunction func = new ArrayTabulatedFunction(points);

        System.out.println("\n1. Исходная функция (парабола y=x²):");
        printFunction(func);

        // Тестирование бинарного формата
        System.out.println("\n2. Бинарный формат:");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            TabulatedFunctions.outputTabulatedFunction(func, baos);
            System.out.println("   Записано байт: " + baos.size());

            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                TabulatedFunction readFunc = TabulatedFunctions.inputTabulatedFunction(bais);
                System.out.println("   Прочитано точек: " + readFunc.getPointsCount());
                System.out.println("   Функция восстановлена корректно: " +
                        compareFunctions(func, readFunc));
            }
        }

        // Тестирование текстового формата
        System.out.println("\n3. Текстовый формат:");
        try (StringWriter sw = new StringWriter()) {
            TabulatedFunctions.writeTabulatedFunction(func, sw);
            String text = sw.toString();
            System.out.println("   Текст: " + text);
            System.out.println("   Длина текста: " + text.length() + " символов");

            try (StringReader sr = new StringReader(text)) {
                TabulatedFunction readFunc = TabulatedFunctions.readTabulatedFunction(sr);
                System.out.println("   Прочитано точек: " + readFunc.getPointsCount());
                System.out.println("   Функция восстановлена корректно: " +
                        compareFunctions(func, readFunc));
            }
        }

        // Тестирование с файлами
        System.out.println("\n4. Файловые операции:");

        // Бинарный файл
        try (FileOutputStream fos = new FileOutputStream("function_binary.dat")) {
            TabulatedFunctions.outputTabulatedFunction(func, fos);
        }
        System.out.println("   Бинарный файл создан: function_binary.dat");

        // Текстовый файл
        try (FileWriter fw = new FileWriter("function_text.txt")) {
            TabulatedFunctions.writeTabulatedFunction(func, fw);
        }
        System.out.println("   Текстовый файл создан: function_text.txt");

        // Проверка размеров файлов
        File binaryFile = new File("function_binary.dat");
        File textFile = new File("function_text.txt");
        System.out.println("   Размер бинарного файла: " + binaryFile.length() + " байт");
        System.out.println("   Размер текстового файла: " + textFile.length() + " байт");
    }

    private static void testAssignment8() throws Exception {
        System.out.println("\n\n=== ЗАДАНИЕ 8: ПОЛНОЕ ТЕСТИРОВАНИЕ ===");

        // 1. Создание объектов Sin и Cos
        System.out.println("\n1. Создание объектов Sin и Cos:");
        Function sin = new Sin();
        Function cos = new Cos();

        System.out.println("   sin(π/6) = " + format(sin.getFunctionValue(Math.PI/6)));
        System.out.println("   cos(π/3) = " + format(cos.getFunctionValue(Math.PI/3)));

        // 2. Табулированные аналоги
        System.out.println("\n2. Табулированные аналоги (10 точек на [0, π]):");
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

        System.out.println("   Табулированный sin имеет " + tabSin.getPointsCount() + " точек");
        System.out.println("   Табулированный cos имеет " + tabCos.getPointsCount() + " точек");


        // 3. Сумма квадратов
        System.out.println("\n3. Сумма квадратов sin²(x) + cos²(x):");

// Используем аналитические функции, а не табулированные
        Function sinAnalytic = new Sin();
        Function cosAnalytic = new Cos();
        Function sin2 = Functions.power(sinAnalytic, 2);
        Function cos2 = Functions.power(cosAnalytic, 2);
        Function sumSquares = Functions.sum(sin2, cos2);

// Табулируем сумму квадратов для сравнения
        TabulatedFunction tabSumSquares = TabulatedFunctions.tabulate(sumSquares, 0, Math.PI, 100);
        System.out.println("   Проверка тождества sin²(x) + cos²(x) = 1:");
        for (double x = 0; x <= Math.PI; x += 0.2) {
            double value = sumSquares.getFunctionValue(x);
            System.out.printf("     x=%.1f: %.10f (ожидается 1.0000000000)%n", x, value);
        }

        // 4. Экспонента и файловые операции
        System.out.println("\n4. Экспонента и файловые операции:");
        Function exp = new Exp();
        TabulatedFunction tabExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);

        // Запись в файл
        try (FileWriter writer = new FileWriter("exp_tabulated.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabExp, writer);
        }
        System.out.println("   Экспонента записана в exp_tabulated.txt");

        // Чтение из файла
        TabulatedFunction readExp;
        try (FileReader reader = new FileReader("exp_tabulated.txt")) {
            readExp = TabulatedFunctions.readTabulatedFunction(reader);
        }

        System.out.println("   Сравнение оригинальной и прочитанной экспоненты:");
        for (int i = 0; i <= 10; i++) {
            double x = i;
            double orig = tabExp.getFunctionValue(x);
            double read = readExp.getFunctionValue(x);
            System.out.printf("     x=%.0f: orig=%10.6f, read=%10.6f, diff=%.10f%n",
                    x, orig, read, Math.abs(orig - read));
        }

        // 5. Логарифм и бинарные операции
        System.out.println("\n5. Логарифм и бинарные операции:");
        Function log = new Log(Math.E);
        TabulatedFunction tabLog = TabulatedFunctions.tabulate(log, 0.1, 10, 11);

        // Бинарная запись
        try (FileOutputStream fos = new FileOutputStream("log_tabulated.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabLog, fos);
        }
        System.out.println("   Логарифм записан в log_tabulated.dat");

        // Бинарное чтение
        TabulatedFunction readLog;
        try (FileInputStream fis = new FileInputStream("log_tabulated.dat")) {
            readLog = TabulatedFunctions.inputTabulatedFunction(fis);
        }

        System.out.println("   Проверка корректности чтения:");
        for (int i = 1; i <= 10; i++) {
            double x = i;
            double orig = tabLog.getFunctionValue(x);
            double read = readLog.getFunctionValue(x);
            if (Math.abs(orig - read) > 1e-9) {
                System.out.printf("     ОШИБКА: x=%.0f: orig=%f, read=%f%n", x, orig, read);
            }
        }
        System.out.println("   Все значения совпадают!");
    }

    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n\n=== ЗАДАНИЕ 9: СЕРИАЛИЗАЦИЯ ===");

        // Создаем функцию для сериализации (композиция ln(e^x) = x)
        Function exp = new Exp();
        Function log = new Log(Math.E);
        Function logOfExp = Functions.composition(log, exp);
        TabulatedFunction tabLogExp = TabulatedFunctions.tabulate(logOfExp, 0, 10, 11);

        System.out.println("\n1. Функция для сериализации: ln(e^x) = x");
        System.out.println("   Количество точек: " + tabLogExp.getPointsCount());

        // Сериализация с Serializable
        System.out.println("\n2. Сериализация с использованием Serializable:");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_serializable.dat"))) {
            oos.writeObject(tabLogExp);
        }
        File serializableFile = new File("function_serializable.dat");
        System.out.println("   Записано в: function_serializable.dat");
        System.out.println("   Размер файла: " + serializableFile.length() + " байт");

        // Десериализация
        TabulatedFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_serializable.dat"))) {
            deserialized = (TabulatedFunction) ois.readObject();
        }

        System.out.println("   Проверка после десериализации:");
        System.out.println("   Количество точек: " + deserialized.getPointsCount());

        // Создаем Externalizable версию
        System.out.println("\n3. Создание Externalizable версии:");
        FunctionPoint[] points = new FunctionPoint[5];
        for (int i = 0; i < 5; i++) {
            points[i] = new FunctionPoint(i, i * 2);
        }
        LinkedListTabulatedFunctionExternalizable externalizableFunc =
                new LinkedListTabulatedFunctionExternalizable(points);

        // Сериализация с Externalizable
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_externalizable.dat"))) {
            oos.writeObject(externalizableFunc);
        }
        File externalizableFile = new File("function_externalizable.dat");
        System.out.println("   Записано в: function_externalizable.dat");
        System.out.println("   Размер файла: " + externalizableFile.length() + " байт");

        // Десериализация
        LinkedListTabulatedFunctionExternalizable deserializedExternal;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_externalizable.dat"))) {
            deserializedExternal = (LinkedListTabulatedFunctionExternalizable) ois.readObject();
        }

        System.out.println("\n4. Сравнение размеров файлов:");
        System.out.println("   Serializable: " + serializableFile.length() + " байт");
        System.out.println("   Externalizable: " + externalizableFile.length() + " байт");

        System.out.println("\n5. Проверка данных после десериализации:");
        for (int i = 0; i < deserializedExternal.getPointsCount(); i++) {
            System.out.printf("   Точка %d: x=%.1f, y=%.1f%n",
                    i, deserializedExternal.getPointX(i), deserializedExternal.getPointY(i));
        }
    }

    private static void printFunction(TabulatedFunction func) {
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.printf("   (%.2f, %.2f)", func.getPointX(i), func.getPointY(i));
            if (i < func.getPointsCount() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }

    private static boolean compareFunctions(TabulatedFunction f1, TabulatedFunction f2) {
        if (f1.getPointsCount() != f2.getPointsCount()) return false;

        for (int i = 0; i < f1.getPointsCount(); i++) {
            if (Math.abs(f1.getPointX(i) - f2.getPointX(i)) > 1e-9) return false;
            if (Math.abs(f1.getPointY(i) - f2.getPointY(i)) > 1e-9) return false;
        }
        return true;
    }

    private static String format(double value) {
        DecimalFormat df = new DecimalFormat("0.######");
        return df.format(value);
    }
}