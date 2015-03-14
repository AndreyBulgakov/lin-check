package me.aevd.lintesting.queue;

public class QueueWithoutAnySync implements Queue {
    private int capacity;

    private int indGet;
    private int indPut;
    private int countElements;

    private int[] items;

    private int inc(int i) {
        return (++i == items.length ? 0 : i);
    }

    private int dec(int i) {
        return ((i == 0) ? items.length : i) - 1;
    }

    public QueueWithoutAnySync(int capacity) {
        this.capacity = capacity;
        items = new int[capacity];

        indPut = 0;
        indGet = 0;
        countElements = 0;

    }

    @Override
    public int put(int x) {
        if (countElements == items.length) {
            return -1;
        }
        items[indPut] = x;
        indPut = inc(indPut);
        countElements++;
        return 0;
    }

    @Override
    public int get() {
        if (countElements == 0) {
            return -1;
        }
        int ret = items[indGet];
        indGet = inc(indGet);
        countElements--;
        return ret;
    }
}
