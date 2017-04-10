package com.devexperts.dxlab.lincheck.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class helps to manage location ids.
 * It bimaps program code locations (see {@link ElementId}) and integer identifiers.
 */
public class LocationManager {
    private static final LocationManager INSTANCE = new LocationManager();
    private final ArrayList<ElementId> locations = new ArrayList<>();
    private final Map<ElementId, Integer> locationIds = new HashMap<>();

    private LocationManager() {
        locations.add(null);
    }

    public static LocationManager getInstance() {
        return INSTANCE;
    }

    synchronized int getLocationId(String className, String methodName, String methodDesc, int instructionNumber) {
        ElementId elementId = new ElementId(className, methodName, methodDesc, instructionNumber);
        return getLocationId(elementId);
    }

    /**
     * Get location id which is associated with specified program code location
     */
    synchronized int getLocationId(ElementId elementId) {
        Integer id = locationIds.get(elementId);
        if (id != null)
            return id;
        id = locations.size();
        locations.add(elementId);
        locationIds.put(elementId, id);
        return id;
    }

    public synchronized ElementId getElementId(int locationId) {
        return locations.get(locationId);
    }

    @Override
    public String toString() {
        return "LocationManager{" +
                "locations=" + locations +
                '}';
    }
}
