package com.devexperts.dxlab.lincheck.libtest;

/*
 * #%L
 * libtest
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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