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

import org.databene.commons.Composite;
import org.databene.commons.CompositeFormatter;
import org.databene.commons.collection.OrderedNameMap;

import java.util.Map;

/**
 * Instance of a composite data type as described by a {@link ComplexTypeDescriptor}.<br/>
 * <br/>
 * Created: 20.08.2007 19:20:22
 * @since 0.3
 * @author Volker Bergmann
 */
public class Entity implements Composite<Object> {

    private OrderedNameMap<Object> components;
    private ComplexTypeDescriptor descriptor;

    public Entity(String name, Object ... componentKeyValuePairs) {
        this(new ComplexTypeDescriptor(name), componentKeyValuePairs);
    }

    /**
     * @param descriptor the name of the entity, it may be null
     * @param componentKeyValuePairs
     */
    public Entity(ComplexTypeDescriptor descriptor, Object ... componentKeyValuePairs) {
        this.descriptor = descriptor;
        this.components = OrderedNameMap.createCaseInsensitiveMap();
        for (int i = 0; i < componentKeyValuePairs.length; i += 2)
            setComponent((String)componentKeyValuePairs[i], componentKeyValuePairs[i + 1]);
    }

    public String getName() {
        return (descriptor != null ? descriptor.getName() : null);
    }

    public ComplexTypeDescriptor getDescriptor() {
        return descriptor;
    }
    
    /**
     * Allows for generic 'map-like' access to component values, e.g. by FreeMarker. 
     * @param componentName the name of the component whose value to return.
     * @return the value of the specified component.
     * @since 0.4.0
     */
    public Object get(String componentName) {
        return getComponent(componentName);
    }

    public Object getComponent(String componentName) {
        return components.get(componentName);
    }
    
    public boolean componentIsSet(String componentName) {
        return components.containsKey(componentName);
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponent(String componentName, Object component) {
        components.put(componentName, component);
    }
    
    // java.lang.overrides ---------------------------------------------------------------------------------------------

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

    public String toString() {
        return new CompositeFormatter(true, true).render(getName() + '[', this, "]");
    }
}
