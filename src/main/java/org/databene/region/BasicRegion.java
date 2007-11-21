package org.databene.region;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Simple, atomic implementation of the Region class.<br/>
 * <br/>
 * Created: 25.06.2006 22:29:21
 */
public class BasicRegion extends Region {

    public BasicRegion(String code) {
        super(code);
    }

    public boolean contains(Region other) {
        if (other == null)
            return false;
        if (other instanceof BasicRegion) {
            return ((BasicRegion)other).code.startsWith(code);
        } else if (other instanceof AggregateRegion) {
            AggregateRegion aggregate = (AggregateRegion) other;
            for (BasicRegion subRegion : aggregate.getBasicRegions())
                if (!this.contains(subRegion))
                    return false;
            return true;
        } else
            throw new IllegalArgumentException("Unsupported type: " + other);
    }

    public Collection<Region> countries() {
        BasicRegion country = new BasicRegion(code.substring(0, 2));
        ArrayList<Region> list = new ArrayList<Region>();
        list.add(country);
        return list;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return code;
    }

    public int hashCode() {
        return code.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null)
            return false;
        else if (obj instanceof BasicRegion)
            return code.equals(((BasicRegion)obj).code);
        else if (obj instanceof AggregateRegion) {
            AggregateRegion aggregate = (AggregateRegion)obj;
            if (aggregate.getBasicRegions().size() != 1)
                return false;
            BasicRegion basic = aggregate.getBasicRegions().iterator().next();
            return (this.equals(basic));
        } else
            return false;
    }
}
