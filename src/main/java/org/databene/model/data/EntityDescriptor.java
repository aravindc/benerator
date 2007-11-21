/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.model.data;

import org.databene.commons.OrderedMap;
import org.databene.commons.SystemInfo;
import org.databene.commons.ListBasedSet;
import org.databene.model.operation.MaxOperation;
import org.databene.model.operation.MinOperation;

import java.util.Map;
import java.util.Collection;
import java.util.Set;

/**
 * Created: 30.06.2007 10:09:34
 */
public class EntityDescriptor extends FeatureDescriptor {

    private boolean caseSensitive; // TODO v0.4 define more sophisticated mapping strategy
    private Map<String, ComponentDescriptor> componentMap;

    public EntityDescriptor(String name, boolean caseSensitive) {
        this(name, caseSensitive, null);
    }

    public EntityDescriptor(String name, boolean caseSensitive, EntityDescriptor parent) {
        super(name, parent);
        this.caseSensitive = caseSensitive;
        this.componentMap = new OrderedMap<String, ComponentDescriptor>();
        addDetailConfig("count", Long.class, false, null);
        addDetailConfig("minCount", Long.class, false, 1L, new MaxOperation<Long>());
        addDetailConfig("maxCount", Long.class, false, null, new MinOperation<Long>());
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setComponentDescriptor(ComponentDescriptor descriptor) {
        String name = descriptor.getName();
        ComponentDescriptor parentsDescriptor = null;
        if (parent != null)
            parentsDescriptor = ((EntityDescriptor) parent).getComponentDescriptor(name);
        if (parentsDescriptor != null) {
            if (descriptor.getClass() == parentsDescriptor.getClass())
                descriptor.setParent(parentsDescriptor);
            descriptor.setName(parentsDescriptor.getName());
/*
            if (parentsDescriptor instanceof AttributeDescriptor) {
                AttributeDescriptor ad = (AttributeDescriptor) descriptor;
                AttributeDescriptor pad = (AttributeDescriptor) parentsDescriptor;
                ad.setType(pad.getType());
                ad.setMaxLength(pad.getMaxLength());
            }
*/
        }
        componentMap.put(name, descriptor);
    }

    public ComponentDescriptor getComponentDescriptor(String name) {
        ComponentDescriptor descriptor = componentMap.get(name);
        if (descriptor == null && !caseSensitive)
            for (ComponentDescriptor candidate : componentMap.values())
                if (candidate.getName().equalsIgnoreCase(name)) {
                    descriptor = candidate;
                    break;
                }
        if (descriptor == null && parent != null)
            descriptor = ((EntityDescriptor)parent).getComponentDescriptor(name);
        return descriptor;
    }

    public Collection<ComponentDescriptor> getComponentDescriptors() {
        Map<String, ComponentDescriptor> tmp = new OrderedMap<String, ComponentDescriptor>();
        for (ComponentDescriptor d : componentMap.values())
            tmp.put(d.getName(), d);
        if (parent != null)
            for (ComponentDescriptor d : ((EntityDescriptor)parent).getComponentDescriptors())
                if (!tmp.containsKey(d.getName()))
                    tmp.put(d.getName(), d);
        return tmp.values();
    }

    public Collection<ComponentDescriptor> getDeclaredComponentDescriptors() {
        Set<ComponentDescriptor> declaredDescriptors = new ListBasedSet<ComponentDescriptor>(componentMap.size());
        for (ComponentDescriptor d : componentMap.values())
            declaredDescriptors.add(d);
        return declaredDescriptors;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Long getCount() {
        return (Long)getDetailValue("count");
    }

    public void setCount(Long count) {
        setDetail("count", count);
    }

    // construction helper methods -------------------------------------------------------------------------------------

    public EntityDescriptor withComponent(ComponentDescriptor componentDescriptor) {
        setComponentDescriptor(componentDescriptor);
        return this;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        if (componentMap.size() == 0)
            return super.toString();
        String sep = SystemInfo.lineSeparator();
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append('{').append(sep);
        for (ComponentDescriptor descriptor : componentMap.values())
            builder.append("    ").append(descriptor).append(sep);
        return builder.append('}').toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final EntityDescriptor that = (EntityDescriptor) o;

        if (caseSensitive != that.caseSensitive)
            return false;
        if (!componentMap.equals(that.componentMap)) // TODO consider case 
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (caseSensitive ? 1 : 0);
        result = 29 * result + componentMap.hashCode();
        return result;
    }
}
