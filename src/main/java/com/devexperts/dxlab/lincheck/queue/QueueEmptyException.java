package com.devexperts.dxlab.lincheck.queue;

public class QueueEmptyException extends Exception {
    public QueueEmptyException(String message) {
        super(message);
    }

    public QueueEmptyException() {

    }
}
