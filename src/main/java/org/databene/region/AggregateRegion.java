package org.databene.region;

import java.util.*;

/**
 * Created: 25.06.2006 22:46:09
 * @deprecated non-final version
 */
public class AggregateRegion extends Region {

    private Map<String, BasicRegion> basicRegions;

    public AggregateRegion(String code) {
        super(code);
        basicRegions = new HashMap<String, BasicRegion>();
    }

    public void add(Region region) {
        if (region instanceof BasicRegion)
            basicRegions.put(region.getCode(), (BasicRegion) region);
        else if (region instanceof AggregateRegion)
            add(((AggregateRegion)region).getBasicRegions());
        else
            throw new UnsupportedOperationException("add() not supported for: " );
    }

    public void add(Collection<? extends Region> regions) {
        for (Region region : regions)
            add(region);
    }

    public Collection<BasicRegion> getBasicRegions() {
        return basicRegions.values();
    }

    public boolean contains(Region region) {
        if (region instanceof BasicRegion)
            return basicRegions.containsValue(region);
        else if (region instanceof AggregateRegion) {
            Collection<BasicRegion> regions = ((AggregateRegion) region).getBasicRegions();
            for (BasicRegion basicRegion : regions)
                if (!this.basicRegions.containsValue(basicRegion))
                    return false;
            return true;
        } else
            throw new IllegalArgumentException("Unsupported type: " + region);
    }

    public Collection countries() {
        Map<String, Region> countries = new HashMap<String, Region>();
        for (BasicRegion region : basicRegions.values())
            countries.put(region.getCode().substring(0, 2), region);
        return countries.values();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return code;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof BasicRegion)
            return (o).equals(this);
        if (o instanceof AggregateRegion) {
            Collection<BasicRegion> oc = ((AggregateRegion)o).getBasicRegions();
            if (basicRegions.size() != oc.size())
                return false;
            for (BasicRegion or : oc) {
                if (!basicRegions.containsValue(or))
                    return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (basicRegions.size() == 1)
            return basicRegions.values().iterator().next().hashCode();
        return (basicRegions != null ? basicRegions.hashCode() : 0);
    }
}
