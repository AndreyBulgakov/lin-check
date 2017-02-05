# Lin-check
**Lin-check** is a testing framework to check that concurrent data structure is linearizable. The approach is based on linearization definition and tries to find non-linearizable execution with specified operations due to a lot of executions. The execution is represented as a list of actors for every test thread, where the actor is the operation with already counted parameters.

# Usage example
The following example tests that **ConcurrentHashMap** is linearizable.

```java
// This test uses 3 parallel threads and executes 1-3 operations in each
@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
// Create common parameter generators with "key" and "value" names.
// These generators are applied by parameter name
@Param(name = "key", gen = IntGen.class) // conf = "-10:10" by default
@Param(name = "value", gen = IntGen.class, conf = "1:5")
public class ConcurrentHashMapLinearizabilityTest {
    private Map<Integer, Integer> map;

    // This method is invoked before every test invocation
    @Reset
    public void reset() {
        map = new ConcurrentHashMap<>();
    }

    @Operation
    public Integer put(Integer key, Integer value) {
        return map.put(key, value);
    }

    @Operation
    public Integer get(@Param(name = "key") Integer k) {
        return map.get(k);
    }

    @Operation
    @HandleExceptionAsResult(NullPointerException.class)
    public int putIfAbsent(int key, int value) {
        return map.putIfAbsent(key, value);
    }

    // Use JUnit to run test
    @Test
    public void test() {
        LinChecker.check(this);
    }
}
```

The artifacts are available in [Bintray](https://bintray.com/devexperts/Maven/lin-check). For Maven, use `com.devexperts.lincheck:core:<version>` artifact for your tests.

#Operation
The base entity in **lin-check** is operation. It is defined via public method (should be annotated with **@Operation** annotation) and generators for every method parameter. Further operations are used to create actors and execute them concurrently.

# Parameter generators
To generate parameters for operation the `ParameterGenerator` implementation is used. Each parameter should have the generator.

## Primitives
**Lin-check** has generators for all primitive types and String. Note that if an operation has primitive or String parameter then this parameter value is contained in generated byte-code. Thus way, boxing/unboxing does not happen.

## Parameter name
Java 8 introduces the feature ([JEP 188](http://openjdk.java.net/jeps/118)) to store parameter names to class files. If test class is compiled with storing parameter names to class files then they can be used as the name of the generator.

For example, the following code

```
    @Operation
    public Integer get(Integer key) {
        return map.get(key);
    }
```

is similar to the next one.

```
    @Operation
    public Integer get(@Param(name = "key") Integer key) {
        return map.get(key);
    }
```

Unfortunately, this feature is disabled in **javac** compiler by default. Use `-parameters` option to enable it. For example, in **Maven** you can use the following plugin configuration:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgument>-parameters</compilerArgument>
    </configuration>
</plugin>
```

However, some IDEs (such as IntelliJ IDEA) do not understand build system configuration as well as possible and running test from these IDEs is not worked. To solve this issue you should add `-parameters` option for **javac** compiler in your IDE configuration.

# Exception as result
If an operation can throw an exception and this is a normal result (e.g. method **remove** in **Queue** implementation throws **NoSuchElementException** if the queue is empty) you can handle this exception via `@HandleExceptionAsResult` annotation on the operation method. 

The following example processes **NoSuchElementException** as a normal result:

```java
@Operation
@HandleExceptionAsResult(NoSuchElementException.class)
public int remove() {
    return queue.remove();
}

```

# CTest  configuration
**Lin-check** uses `@CTest` annotation for test class to configure testing parameters. The `@CTest` annotation has the following parameters:

* **actorsPerThread** - the range of actors for each thread (presented as array), should be in the following format: `<min_actors>:<max_actors>`;
* **iterations** - number of iterations to be processed;
* **invocationsPerIteration** - number of invocations in each iteration. Default value is `10_000`.

Note that one test class can have several `@CTest` annotations and each configuration is used for testing.

# Output
For every execution **lin-check** produces about all actors for every thread. If the test is done successfully, no additional information is produced. However, if any invocation is not linearizable **lin-check** produces information about this execution results and all possible linearizable results.

Here is an example of non-linearizable queue testing:

```
= Iteration 1 / 100 =
Actors per thread:
[takeOrNull()[w], add(-4)[w]]
[takeOrNull()[w]]
= Iteration 2 / 100 =
Actors per thread:
[takeOrNull()[w], takeOrNull()[w]]
[takeOrNull()[w]]
= Iteration 3 / 100 =
Actors per thread:
[takeOrNull()[w], add(-7)[w]]
[add(3)[w], takeOrNull()[w]]

Non-linearizable execution:
[null, void]
[void, null]

Possible linearizable executions:
[null, void]
[void, 3]

[3, void]
[void, null]

[3, void]
[void, -7]

[null, void]
[void, -7]
```

# Contacts
If you need help, you have a question, or you need further details on how to use **lin-check**, you can refer to the following resources:

* [dxLab](https://code.devexperts.com/) research group at Devexperts
* GitHub issues: [https://github.com/Devexperts/lin-check/issues]()

You can use the following e-mail to contact us directly:

![](dxlab-mail.png)
