package ozertsov.set;

/**
 * Created by alexander on 18.02.17.
 */
public enum ParallelType {
    /**
     * The annotated method is not thread safe. It's caller's duty to correctly
     * synchronization before call this method.
     */
    ThreadUnSafe,

    /**
     * The annotated method is thread safe. It can be called in multi-threaded
     * program.
     */
    ThreadSafe,

    /**
     * The annotated method is thread safe. And its implementation is not based
     * on lock. Scalability of this method should be better than ThreadUnSafe
     * and ThreadSafe methods.
     */
    LockFree
}