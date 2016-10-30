package thesis_example;

public class CounterWrong1 implements Counter {
    private int c;

    public CounterWrong1() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        c++;
        return c;
    }
}
