package librariesForTesting.thesis_example;

public class CounterCorrect1 {
    private int c = 0;

    public synchronized int incrementAndGet() {
        c++;
        return c;
    }

    public static void main(String[] args) {
        System.out.println(new CounterCorrect1().incrementAndGet());
    }
}
