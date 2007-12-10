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
import org.databene.model.Composite;

import java.util.Map;

/**
 * Abstraction of an entity.<br/>
 * <br/>
 * Created: 20.08.2007 19:20:22
 */
public class Entity implements Composite {

    private OrderedMap<String, Object> components;
    private EntityDescriptor descriptor;
    /**
     *
     * @param descriptor the name of the entity, it may be null
     * @param componentKeyValues
     */
    public Entity(EntityDescriptor descriptor, Object ... componentKeyValues) {
        this.descriptor = descriptor;
        this.components = new OrderedMap<String, Object>();
        for (int i = 0; i < componentKeyValues.length; i += 2)
            setComponent((String)componentKeyValues[i], componentKeyValues[i + 1]);
    }

    public String getName() {
        return descriptor.getName();
    }

    public EntityDescriptor getDescriptor() {
        return descriptor;
    }

    public Object getComponent(String componentName) {
        Object component = components.get(componentName);
        if (component == null && !descriptor.isCaseSensitive()) {
            Map.Entry<String, Object> entry = getComponentEntry(componentName);
            if (entry != null)
                component = entry.getValue();
        }
        return component;
    }
    
    public boolean componentIsSet(String componentName) {
        return components.containsKey(componentName);
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponent(String componentName, Object component) {
        Map.Entry<String, Object> entry = getComponentEntry(componentName);
        if (entry != null)
            entry.setValue(component);
        else
            this.components.put(componentName, component);
    }

    // java.lang.overrides ---------------------------------------------------------------------------------------------

    public String toString() {
        return descriptor.getName() + components;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Entity that = (Entity) o;
        if (!this.descriptor.getName().equals(that.descriptor.getName()))
            return false;
        return this.components.equalsIgnoreOrder(that.components);
    }

    public int hashCode() {
        return descriptor.hashCode() * 29 + components.hashCode();
    }

    // helper methods --------------------------------------------------------------------------------------------------

    protected Map.Entry<String, Object> getComponentEntry(String componentName) {
        for (Map.Entry<String, Object> entry : components.entrySet())
            if (entry.getKey().equals(componentName) || (!descriptor.isCaseSensitive() && entry.getKey().equalsIgnoreCase(componentName)))
                return entry;
        return null;
    }

}
