package functions;

import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 2L;
    private static final double EPSILON = 1e-9;

    static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode(double x, double y) {
            this.point = new FunctionPoint(x, y);
        }

        public FunctionPoint getPoint() {
            return point;
        }

        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode getPrev() {
            return prev;
        }

        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }

        public FunctionNode getNext() {
            return next;
        }

        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }

    private FunctionNode head;
    private int size;
    private transient FunctionNode lastAccessedNode;
    private transient int lastAccessedIndex;

    private void initializeList() {
        head = new FunctionNode(new FunctionPoint(0, 0));
        head.setNext(head);
        head.setPrev(head);
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        initializeList();
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, 0.0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        initializeList();
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, values[i]));
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше двух");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX() + EPSILON) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        initializeList();
        for (FunctionPoint point : points) {
            addNodeToTail().setPoint(new FunctionPoint(point));
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + (size - 1) + "]");
        }

        if (lastAccessedNode != null && lastAccessedIndex >= 0) {
            int diff = Math.abs(index - lastAccessedIndex);
            if (diff < Math.min(index, size - index)) {
                FunctionNode node = lastAccessedNode;
                int currentIndex = lastAccessedIndex;

                while (currentIndex < index) {
                    node = node.getNext();
                    currentIndex++;
                }
                while (currentIndex > index) {
                    node = node.getPrev();
                    currentIndex--;
                }

                lastAccessedNode = node;
                lastAccessedIndex = index;
                return node;
            }
        }

        FunctionNode node;
        if (index < size / 2) {
            node = head.getNext();
            for (int i = 0; i < index; i++) {
                node = node.getNext();
            }
        } else {
            node = head.getPrev();
            for (int i = size - 1; i > index; i--) {
                node = node.getPrev();
            }
        }

        lastAccessedNode = node;
        lastAccessedIndex = index;
        return node;
    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + size + "]");
        }

        FunctionNode newNode = new FunctionNode(new FunctionPoint(0, 0));

        if (size == 0) {
            newNode.setNext(head);
            newNode.setPrev(head);
            head.setNext(newNode);
            head.setPrev(newNode);
        } else if (index == size) {
            FunctionNode last = head.getPrev();
            last.setNext(newNode);
            newNode.setPrev(last);
            newNode.setNext(head);
            head.setPrev(newNode);
        } else {
            FunctionNode current = getNodeByIndex(index);
            FunctionNode prevNode = current.getPrev();
            prevNode.setNext(newNode);
            newNode.setPrev(prevNode);
            newNode.setNext(current);
            current.setPrev(newNode);
        }

        size++;
        lastAccessedIndex = -1;
        return newNode;
    }

    private FunctionNode addNodeToTail() {
        return addNodeByIndex(size);
    }

    private void deleteNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException(
                    "Индекс " + index + " вне диапазона [0, " + (size - 1) + "]");
        }
        if (size <= 2) {
            throw new IllegalStateException("Нельзя удалить точку - останется меньше двух точек");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.getPrev();
        FunctionNode nextNode = nodeToDelete.getNext();

        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        size--;

        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = (index < size) ? nextNode : head.getNext();
            lastAccessedIndex = (index < size) ? index : 0;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }
    }

    @Override
    public double getLeftDomainBorder() {
        if (size == 0) {
            throw new IllegalStateException("Нет точек в функции");
        }
        return head.getNext().getPoint().getX();
    }

    @Override
    public double getRightDomainBorder() {
        if (size == 0) {
            throw new IllegalStateException("Нет точек в функции");
        }
        return head.getPrev().getPoint().getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }

        FunctionNode current = head.getNext();
        while (current != head && current.getPoint().getX() < x - EPSILON) {
            current = current.getNext();
        }

        if (current != head && Math.abs(current.getPoint().getX() - x) < EPSILON) {
            return current.getPoint().getY();
        }

        if (current == head.getNext() && x < current.getPoint().getX() + EPSILON) {
            return current.getPoint().getY();
        }

        if (current == head && x > head.getPrev().getPoint().getX() - EPSILON) {
            return head.getPrev().getPoint().getY();
        }

        FunctionNode leftNode = current.getPrev();
        FunctionNode rightNode = current;

        if (leftNode == head) {
            leftNode = head.getNext();
        }
        if (rightNode == head) {
            rightNode = head.getPrev();
        }

        double x1 = leftNode.getPoint().getX();
        double y1 = leftNode.getPoint().getY();
        double x2 = rightNode.getPoint().getX();
        double y2 = rightNode.getPoint().getY();

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    @Override
    public int getPointsCount() {
        return size;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        if (index > 0 && point.getX() <= getNodeByIndex(index - 1).getPoint().getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < size - 1 && point.getX() >= getNodeByIndex(index + 1).getPoint().getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }

        node.setPoint(new FunctionPoint(point));
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).getPoint().getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        if (index > 0 && x <= getNodeByIndex(index - 1).getPoint().getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < size - 1 && x >= getNodeByIndex(index + 1).getPoint().getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }

        FunctionPoint newPoint = new FunctionPoint(x, node.getPoint().getY());
        node.setPoint(newPoint);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).getPoint().getY();
    }

    @Override
    public void setPointY(int index, double y) {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint newPoint = new FunctionPoint(node.getPoint().getX(), y);
        node.setPoint(newPoint);
    }

    @Override
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
            current = current.getNext();
        }

        int insertIndex = 0;
        current = head.getNext();
        while (current != head && current.getPoint().getX() < point.getX() - EPSILON) {
            current = current.getNext();
            insertIndex++;
        }

        addNodeByIndex(insertIndex);
        getNodeByIndex(insertIndex).setPoint(new FunctionPoint(point));
    }

    // Для стандартной сериализации (не Externalizable)
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        out.writeInt(size);

        FunctionNode current = head.getNext();
        while (current != head) {
            out.writeDouble(current.getPoint().getX());
            out.writeDouble(current.getPoint().getY());
            current = current.getNext();
        }
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        int savedSize = in.readInt();

        initializeList();
        for (int i = 0; i < savedSize; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            addNodeToTail().setPoint(new FunctionPoint(x, y));
        }
    }
}