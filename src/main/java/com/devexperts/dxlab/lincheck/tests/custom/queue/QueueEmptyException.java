package com.devexperts.dxlab.lincheck.tests.custom.queue;

public class QueueEmptyException extends Exception {
    public QueueEmptyException(String message) {
        super(message);
    }

    public QueueEmptyException() {

    }
}
