package com.devexperts.dxlab.lincheck.libtest.ozertsov.deadlock;

/**
 * Created by alexander on 09.02.17.
 */
public class DeadLock {
    static Object lock1;
    static Object lock2;
    static int x;

    public DeadLock(){
        lock1 = new Object();
        lock2 = new Object();
        x = 0;
    }

    public int parent() {
        synchronized(lock1) {
            synchronized(lock2) {
                return x++;
            }
        }
    }

    public int child()
    {
        synchronized(lock2) {
            synchronized(lock1) {
                return x++;
            }
        }
    }

    public int inc(){
        synchronized (this) {
            return x++;
        }
    }

    public int dec(){
        synchronized (this) {
            return x--;
        }
    }

    public int getX(){
        return x;
    }

}
