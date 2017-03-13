package com.devexperts.dxlab.lincheck.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager to contains all actual ids.
 * Used as a message box between ClassLoader's transformations and Strategy
 */
public class LocationManager {

    private static final LocationManager INSTANCE = new LocationManager();
    private final ArrayList<ElementId> locations = new ArrayList<>(10_000);
    private final Map<ElementId, Integer> locationIds = new HashMap<>();

    public static LocationManager getInstance() {
        return INSTANCE;
    }

    private LocationManager(){
        locations.add(null);
    }

    synchronized int getLocationId(String className, String methodName, String methodDesc, int line) {
        ElementId location = new ElementId(className, methodName, methodDesc, line);
        return getOrSetLocationId(location);
    }

    // TODO remove unused method
    synchronized int getLocationId(ElementId elementId) {
        return getOrSetLocationId(elementId);
    }

    private int getOrSetLocationId(ElementId locationId){
        Integer id = locationIds.get(locationId);
        if (id != null) {
            return id;
        }
        id = locations.size();
        locations.add(locationId);
        locationIds.put(locationId, id);
        return id;
    }

}
