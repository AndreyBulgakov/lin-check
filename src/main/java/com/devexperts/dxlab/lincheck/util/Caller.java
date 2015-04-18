package main.java.com.devexperts.dxlab.lincheck.util;

import java.util.List;

public interface Caller {
    public void reload();
    Result call(Actor act);
    List<CheckerConfiguration> getConfigurations();
}
