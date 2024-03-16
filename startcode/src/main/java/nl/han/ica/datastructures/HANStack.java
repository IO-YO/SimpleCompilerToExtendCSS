package nl.han.ica.datastructures;

import java.util.EmptyStackException;

public class HANStack<T> implements IHANStack<T> {
    private static class StackNode<T> {
        private T data;
        private StackNode<T> next;

        public StackNode(T data) {
            this.data = data;
        }
    }

    private StackNode<T> top;

    // Implements the push method from IHANStack
    @Override
    public void push(T value) {
        StackNode<T> t = new StackNode<>(value);
        t.next = top;
        top = t;
    }

    // Implements the pop method from IHANStack
    @Override
    public T pop() {
        if (top == null) throw new EmptyStackException();
        T item = top.data;
        top = top.next;
        return item;
    }

    // Implements the peek method from IHANStack
    @Override
    public T peek() {
        if (top == null) throw new EmptyStackException();
        return top.data;
    }
}
