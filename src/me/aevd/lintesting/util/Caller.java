package me.aevd.lintesting.util;

import java.util.List;

public interface Caller {
    public void reload();
    Result call(Actor act);
    List<CheckerConfiguration> getConfigurations();
}
