package org.databene.region;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a geographical region.
 * Regions may form a composition structure from continents to multi-country regions, countries, states and so on.<br/>
 * <br/>
 * Created: 25.06.2006 22:28:04
 */
public abstract class Region {

    private static Map<String, Region> instances = new HashMap<String, Region>();
    private static Region defaultRegion = new BasicRegion(Locale.getDefault().getCountry());

    static {
        for (Country country : Country.getInstances())
            country.getRegion();
    }

    public static Region getInstance(String regionCode) {
        if (regionCode == null)
            return null;
        Region instance = instances.get(regionCode);
        if (instance == null)
            throw new IllegalArgumentException("No region of this code defined: " + regionCode);
        return instance;
    }

    public static Region valueOf(String regionCode) {
        return getInstance(regionCode);
    }

    protected String code;

    public Region(String code) {
        this.code = code;
        instances.put(code, this);
    }

    public static Region getDefault() {
        return defaultRegion;
    }

    public String getCode() {
        return code;
    }

    public abstract boolean contains(Region region);

    public abstract Collection countries();

}
