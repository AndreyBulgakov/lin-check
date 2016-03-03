
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param("key", opt={"1:3"})
@Param("value", opt={"1:10"})
public class MapTest {
    public Map<Integer, Integer> q;

    @Reset
    public void reset() {
        q = new NonBlockingHashMap<>();
    }

    @Operation(params = {"key", "value"})
    public Integer put(Long key, Integer value) throws Exception {
        return q.put(key, value);
    }

    @Operation()
    public Integer get(@Param("key") Integer key) throws Exception {
        return q.get(key);
    }

    @Operation()
    public int size() throws Exception {
        return q.size();
    }

}
