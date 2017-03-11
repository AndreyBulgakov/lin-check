package com.devexperts.dxlab.lincheck.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// TODO public?
public class LocationManager {

    private static final LocationManager INSTANCE = new LocationManager();
    // TODO dynamic growth - ArrayList already has dynamic growth
    private final ArrayList<IdElement> locations = new ArrayList<>(10_000);
    // TODO current implementation doesn't need in concurrent map - And further?
    private final Map<IdElement, Integer> locationIds = new ConcurrentHashMap<>();

    public static LocationManager getInstance() {
        return INSTANCE;
    }

    private LocationManager(){
        locations.add(null);
    }

    synchronized int getLocationId(ClassLoader loader, String className, String methodName, int line) {
        // TODO classLoader field? - Delete classloader fielld
        IdElement location = new IdElement(loader, className, methodName, line);
        Integer id = locationIds.get(location);
        if (id != null)
            return id;
        id = locations.size();
        locations.add(location);
        locationIds.put(location, id);
        return id;
    }

}
