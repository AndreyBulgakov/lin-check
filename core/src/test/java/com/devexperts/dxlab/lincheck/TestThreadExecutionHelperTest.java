package com.devexperts.dxlab.lincheck;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Phaser;

public class TestThreadExecutionHelperTest {

    @Test
    public void testBase() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(new ArrayDeque<Integer>(), new Phaser(1),
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("element"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("peek"), Collections.emptyList(), Collections.emptyList())
            ), false);
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createValueResult(2),
            Result.createValueResult(2)
        }, ex.call());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGlobalException() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(new ArrayDeque<Integer>(), new Phaser(1),
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList())
            ), false);
        ex.call();
    }

    @Test
    public void testActorExceptionHandling() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(new ArrayDeque<Integer>(), new Phaser(1),
            Arrays.asList(
                new Actor(ArrayDeque.class.getMethod("addLast", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class)),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(Exception.class, NoSuchElementException.class))
            ), false);
        Assert.assertArrayEquals(new Result[] {
            Result.createVoidResult(),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }

    @Test
    public void testWaits() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(new ArrayDeque<Integer>(), new Phaser(1),
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class))
            ), true);
        ex.waits = new int[] {15, 100};
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }
}