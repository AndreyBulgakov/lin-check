package ozertsov.queue;

import java.util.concurrent.atomic.AtomicReference;
/**
 * Created by alexander on 13.02.17.
 */
public class LockFreeQueue<T> {

    private AtomicReference<Node<T>> head, tail;

    public LockFreeQueue() {
        Node<T> dummyNode = new Node<T>(null);
        head = new AtomicReference<Node<T>>(dummyNode);
        tail = new AtomicReference<Node<T>>(dummyNode);
    }

    public void add(T value) {
        Node<T> newNode = new Node<T>(value);
        Node<T> prevTailNode = tail.get();
        prevTailNode.next = newNode;
    }

    public T takeOrNull() {
        Node<T> headNode, valueNode;

        do {
            headNode = head.get();
            valueNode = headNode.next;
        } while (valueNode != null && !head.compareAndSet(headNode, valueNode));

        T value = valueNode != null ? valueNode.value : null;

        if (valueNode != null)
            valueNode.value = null;
        return value;
    }

    private static class Node<E> {
        volatile E value;
        volatile Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

}