package me.aevd.lintesting.util;

public interface Caller {
    public void reload();
    Result call(Actor act);
    Actor[][] generateActors(int numThreads);
}
