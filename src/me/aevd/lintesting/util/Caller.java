package me.aevd.lintesting.util;

public interface Caller {
    public void reload();
    Result call(Actor act);
    CheckerConfiguration getConfiguration();
}
