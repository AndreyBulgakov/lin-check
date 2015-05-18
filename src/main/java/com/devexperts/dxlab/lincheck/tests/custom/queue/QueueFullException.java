package com.devexperts.dxlab.lincheck.tests.custom.queue;

public class QueueFullException extends Exception {
    public QueueFullException(String message) {
        super(message);
    }

    public QueueFullException() {

    }
}
