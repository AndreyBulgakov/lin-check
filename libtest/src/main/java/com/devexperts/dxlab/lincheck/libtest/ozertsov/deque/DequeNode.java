package com.devexperts.dxlab.lincheck.libtest.ozertsov.deque;

/**
 * Created by alexander on 18.02.17.
 */
import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal Deque node class. This class is used by both EBDeque and
 * LockFreeDeque.
 *
 * @param <E>
 *            type of element inside node
 */
class DequeNode<E> {
    /**
     * Data on the node.
     */
    final E data;
    /**
     * Right pointer.
     */
    AtomicReference<DequeNode<E>> right;
    /**
     * Left Pointer.
     */
    AtomicReference<DequeNode<E>> left;

    /**
     * @param d
     *            default value of element
     */
    public DequeNode(E d) {
        this.data = d;
        this.right = new AtomicReference<DequeNode<E>>();
        this.left = new AtomicReference<DequeNode<E>>();
    }

    /**
     * @param r
     *            right node of node
     */
    public void setRight(DequeNode<E> r) {
        this.right.set(r);
    }

    /**
     * @param l
     *            left node of node
     */
    public void setLeft(DequeNode<E> l) {
        this.left.set(l);
    }

}