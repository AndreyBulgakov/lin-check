package me.aevd.lintesting.queue;

public class QueueEmptyException extends Exception {
    public QueueEmptyException(String message) {
        super(message);
    }

    public QueueEmptyException() {

    }
}
