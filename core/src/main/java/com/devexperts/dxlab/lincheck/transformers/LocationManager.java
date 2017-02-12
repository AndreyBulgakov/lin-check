package com.devexperts.dxlab.lincheck.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LocationManager {

    private static final LocationManager INSTANCE = new LocationManager();
    private final ArrayList<IdElement> locations = new ArrayList<>(10_000);
    private final Map<IdElement, Integer> locationIds = new ConcurrentHashMap<>();

    public static LocationManager getInstance() {
        return INSTANCE;
    }

    private LocationManager(){
        locations.add(null);
    }

    public int getLocationId(String className, String methodName, int line) {
        // TODO classLoader field?
        IdElement location = new IdElement("ExecutionClassLoader", className, methodName, line);
        Integer id = locationIds.get(location);
        if (id != null)
            return id;
        id = locations.size();
        locations.add(location);
        locationIds.put(location, id);
        return id;
    }

}
