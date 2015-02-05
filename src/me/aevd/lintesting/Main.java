package me.aevd.lintesting;

public class Main {
    public static void main(String[] args) {
        Counter counter = new CounterWithoutAnySync();
        CounterCaller caller = new CounterCaller(counter);
        System.out.println(caller.call(0));
        System.out.println(caller.call(-1));
        System.out.println(caller.call(0));
    }
}
