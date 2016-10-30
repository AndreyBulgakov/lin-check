package thesis_example;

public class CounterWrong2 implements Counter {
    private int c;

    public CounterWrong2() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        return ++c;
    }
}
