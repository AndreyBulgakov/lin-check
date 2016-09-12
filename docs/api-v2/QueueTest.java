
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueTest {

    public Queue queue;

    @Reset
    public void reset() { queue = new QueueSynchronized(10); }

    @Operation
    public void put(@Param(generator = IntegerParameterGenerator.class) int v) throws Exception {
        queue.put(v);
    }

    @Operation
    public int get() throws Exception {
        return queue.get();
    }
}
