package amino_cbbs;

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