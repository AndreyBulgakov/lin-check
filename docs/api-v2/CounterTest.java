
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class CounterTest {
    public Counter counter;

    @Reset
    public void reset() { counter = new CounterCorrect1(); }

    @Operation
    public int incAndGet() { return counter.incrementAndGet(); }
}


