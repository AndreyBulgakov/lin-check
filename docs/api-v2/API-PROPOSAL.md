### API v1
Для тестирования структуры данных с помощью библиотеки lin-check необходимо создать специальный класс, в котором нужно описать методы-врапперы следующего вида для тестируемых методов:

```java
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
public class HashMapTest {
    Map<Integer, Long> map;

    @Reload
    public void reload() {
        map = new HashMap();
    }

    @Operation(args = {"1:10", "1:1000"})
    public void put(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        res.setValue(map.put(key, Long.valueOf(value)));
    }

    @Operation(args = {"1:10"})
    public void get(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        res.setValue(map.get(key));
    }

    @Operation(args = {})
    public void size(Result res, Object[] args) throws Exception {
        res.setValue(map.size());
    }
}
```

Для настройки входных аргументов используется переменная `args` аннотации `@Operation`.

`args = {}` породит пустой массив, который передастся в метод-враппер как `Object[] args`

`args = {"1:10"}` породит массив длины 1 c элементом `Integer` в интервале [1; 10)

`args = {"1:10", "1:100000"}` породит массив длины 2.

### API v2
Изменения:

* возврат значения через return / throw exception
* передача аргументов в метод-враппер в явном виде
* генерация параметров с помощью генераторов
* введение аннотации `@Param`, которая принимает генератор значений аргумента метода. Можно отдельно создать именованный диапазон и ссылаться на него (cм. `MapTest`)
* в поле generator передаётся класс генератора
* в поле generatorParameters передаются параметры для генерации, которые принимает конструктор генератора

Примеры:

```java
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
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
```

```java
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@Param(name = "key", generator = IntegerParameterGenerator.class, generatorParameters = {"1", "3"})
@Param(name = "value", generator = IntegerParameterGenerator.class, generatorParameters = {"1", "10"})
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
    public Integer get(@Param(name = "key") Integer key) throws Exception {
        return q.get(key);
    }

    @Operation()
    public int size() throws Exception {
        return q.size();
    }

}
```
