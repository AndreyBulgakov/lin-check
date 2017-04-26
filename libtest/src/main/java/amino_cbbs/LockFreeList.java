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

import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by alexander on 17.02.17.
 */
public class LockFreeList<E> extends AbstractList<E> {

    private AtomicReference<Descriptor> descriptor;

    private AtomicReferenceArray<AtomicReferenceArray<E>> memory;

    public LockFreeList() {
        descriptor = new AtomicReference<>(new Descriptor(new WriteOperation(null, 0, false), 0));
        memory = new AtomicReferenceArray<>(32);
    }

    @Override
    public E get(int index) {
        int indexInBucket = getIndexInBucket(index);
        AtomicReferenceArray<E> bucket = getBucket(index);
        return bucket.get(indexInBucket);
    }

    @Override
    public int size() {
        Descriptor currentDesc = this.descriptor.get();
        int size = currentDesc.size;
        if (currentDesc.writeOperation.pending) {
            size = size - 1;
        }
        return size;
    }

    @Override
    public boolean add(E element) {
        Descriptor currentDesc;
        Descriptor nextDescriptor;
        do {
            currentDesc = this.descriptor.get();
            completeWrite(currentDesc.writeOperation);
            int bucketNumber = getNumberOfBucket(currentDesc.size);
            if (memory.get(bucketNumber) == null) {
                allocBucket(bucketNumber);
            }
            WriteOperation writeOperation = new WriteOperation(element, currentDesc.size);
            nextDescriptor = new Descriptor(writeOperation, currentDesc.size + 1);
        }
        while (!this.descriptor.compareAndSet(currentDesc, nextDescriptor));
        completeWrite(nextDescriptor.writeOperation);
        return true;
    }

    @Override
    public E set(int index, E newValue) {
        int size = this.size();
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        AtomicReferenceArray<E> bucket = getBucket(index);
        return bucket.getAndSet(getIndexInBucket(index), newValue);
    }

    private void allocBucket(int bucketNumber) {
        int bucketSize = getBucketSize(bucketNumber);
        memory.compareAndSet(bucketNumber, null, new AtomicReferenceArray<E>(bucketSize));
    }

    private void completeWrite(WriteOperation writeOperation) {
        if (writeOperation.pending) {
            int indexInBucket = getIndexInBucket(writeOperation.position);
            AtomicReferenceArray<E> bucket = getBucket(writeOperation.position);
            bucket.compareAndSet(indexInBucket, writeOperation.oldValue, writeOperation.newValue);
            writeOperation.pending = false;
        }
    }

    private int getBucketSize(int numberOfBucket) {
        return 2 << numberOfBucket;
    }

    private int getIndexInBucket(int position) {
        int pos = position + 2;
        return (Integer.highestOneBit(pos) ^ pos);
    }

    private int getNumberOfBucket(int position) {
        int pos = position + 2;
        return (Integer.numberOfTrailingZeros(Integer.highestOneBit(pos)) - 1);
    }

    private AtomicReferenceArray<E> getBucket(int position) {
        int bucketNumber = getNumberOfBucket(position);
        return memory.get(bucketNumber);
    }

    private class Descriptor {
        private final WriteOperation writeOperation;
        private final int size;

        private Descriptor(WriteOperation writeOperation, int size) {
            this.writeOperation = writeOperation;
            this.size = size;
        }
    }

    private class WriteOperation {
        private volatile boolean pending;
        private final E oldValue;
        private final E newValue;
        private final int position;

        public WriteOperation(E newValue, int position) {
            this(newValue, position, true);
        }

        public WriteOperation(E newValue, int position, boolean pending) {
            this(null, newValue, position, pending);
        }

        public WriteOperation(E oldValue, E newValue, int position, boolean pending) {
            this.pending = pending;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.position = position;
        }
    }
}