/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.databene.commons.Composite;
import org.databene.commons.CompositeFormatter;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.ListBasedSet;
import org.databene.commons.collection.OrderedNameMap;

/**
 * Describes a type that aggregates {@link ComponentDescriptor}s.<br/>
 * <br/>
 * Created: 03.03.2008 10:56:16
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ComplexTypeDescriptor extends TypeDescriptor {

    private Map<String, ComponentDescriptor> componentMap;
    private Map<String, InstanceDescriptor> variables;

    public ComplexTypeDescriptor(String name) {
        this(name, (String) null);
    }

    public ComplexTypeDescriptor(String name, ComplexTypeDescriptor parent) {
    	super(name, parent);
        init();
    }
    
    public ComplexTypeDescriptor(String name, String parentName) {
        super(name, parentName);
        init();
    }
    
    protected void init() {
    	super.init();
        this.componentMap = new OrderedNameMap<ComponentDescriptor>();
        this.variables = new OrderedNameMap<InstanceDescriptor>();
    }
    
    // component handling ----------------------------------------------------------------------------------------------

    public void addComponent(ComponentDescriptor descriptor) {
        String name = descriptor.getName();
/*
        ComponentDescriptor parentsDescriptor = null;
        if (parent != null)
            parentsDescriptor = ((EntityDescriptor) parent).getComponent(name);
        if (parentsDescriptor != null) {
            if (descriptor.getClass() == parentsDescriptor.getClass())
                descriptor.setParent(parentsDescriptor);
            descriptor.setName(parentsDescriptor.getName());
        }
*/
        componentMap.put(name, descriptor);
    }

    public ComponentDescriptor getComponent(String name) {
        ComponentDescriptor descriptor = componentMap.get(name);
        if (descriptor == null)
            for (ComponentDescriptor candidate : componentMap.values())
                if (candidate.getName().equalsIgnoreCase(name)) {
                    descriptor = candidate;
                    break;
                }
        if (descriptor == null && getParent() != null)
            descriptor = ((ComplexTypeDescriptor)getParent()).getComponent(name);
        return descriptor;
    }

    public Collection<ComponentDescriptor> getComponents() {
        Set<String> componentNames = componentMap.keySet();
        Map<String, ComponentDescriptor> tmp = new OrderedNameMap<ComponentDescriptor>();
        if (getParent() != null) {
            for (ComponentDescriptor d : ((ComplexTypeDescriptor)getParent()).getComponents()) {
                String name = d.getName();
                if (!StringUtil.containsIgnoreCase(componentNames, name))
                    tmp.put(name, d);
            }
        }
        for (ComponentDescriptor d : componentMap.values())
            tmp.put(d.getName(), d);
        return tmp.values();
    }

    public Collection<ComponentDescriptor> getDeclaredComponents() {
        Set<ComponentDescriptor> declaredDescriptors = new ListBasedSet<ComponentDescriptor>(componentMap.size());
        for (ComponentDescriptor d : componentMap.values())
            declaredDescriptors.add(d);
        return declaredDescriptors;
    }

    // variable handling -----------------------------------------------------------------------------------------------
    
    public Collection<InstanceDescriptor> getVariables() {
        return variables.values();
    }

    public void addVariable(InstanceDescriptor variable) {
        variables.put(variable.getName(), variable);
    }
    
    // construction helper methods -------------------------------------------------------------------------------------

    public ComplexTypeDescriptor withComponent(ComponentDescriptor componentDescriptor) {
        addComponent(componentDescriptor);
        return this;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        if (componentMap.size() == 0)
            return super.toString();
        return new CompositeFormatter(false, false).render(super.toString() + '{', new CompositeAdapter(), "}");
    }
    
    // helper for rendering --------------------------------------------------------------------------------------------

    public class CompositeAdapter implements Composite<ComponentDescriptor> {

        public ComponentDescriptor getComponent(String key) {
            return componentMap.get(key);
        }

        public void setComponent(String key, ComponentDescriptor value) {
            componentMap.put(key, value);
        }

        public Map<String, ComponentDescriptor> getComponents() {
            return componentMap;
        }

    }
}