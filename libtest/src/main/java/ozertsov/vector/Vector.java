package ozertsov.vector;

import sun.awt.Mutex;

import java.util.ArrayList;

/**
 * Created by alexander on 10.02.17.
 */
public class Vector {

    protected int size;
    protected int length;
    protected ArrayList<Integer> data;
    Mutex mutex;

    public Vector(int length, int[] datas) {
        mutex = new Mutex();
        this.length = length;
        data = new ArrayList<>(length);
        for (int i : datas) {
            data.add(i);
        }
        this.size = datas.length;
    }

    public int getSize() {
        return size;
    }

    public int getLength() {
        return length;
    }

    public int addAll(Vector v) {
        int numElem = v.size + this.size;
        mutex.lock();
        if (numElem > length) {
            this.length = numElem * 2;
        }
        data.addAll(v.data);
        this.size = v.size + this.size;
        mutex.unlock();
        return size;
    }
}