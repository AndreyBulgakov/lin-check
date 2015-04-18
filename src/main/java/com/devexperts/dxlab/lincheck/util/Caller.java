package com.devexperts.dxlab.lincheck.util;

import java.util.List;

public interface Caller {
    public void reload();
    void call(Actor act, Result result);
    List<CheckerConfiguration> getConfigurations();
}
