/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.CollectionUtil;
import org.databene.commons.collection.ListBasedSet;
import org.databene.commons.collection.NamedValueList;
import org.databene.commons.collection.OrderedNameMap;

/**
 * Describes a type that aggregates {@link ComponentDescriptor}s.<br/>
 * <br/>
 * Created: 03.03.2008 10:56:16
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ComplexTypeDescriptor extends TypeDescriptor implements VariableHolder {

	public static final String __SIMPLE_CONTENT = "__SIMPLE_CONTENT";

    private NamedValueList<ComponentDescriptor> components;
    private Map<String, InstanceDescriptor> variables;
    
    // constructors ----------------------------------------------------------------------------------------------------

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
    
    // component handling ----------------------------------------------------------------------------------------------

    public void addComponent(ComponentDescriptor descriptor) {
    	String componentName = descriptor.getName();
		if (parent != null && ((ComplexTypeDescriptor) parent).getComponent(componentName) != null)
			descriptor.setParent(((ComplexTypeDescriptor) parent).getComponent(componentName));
        components.add(componentName, descriptor);
    }

	public void setComponent(ComponentDescriptor component) {
    	String componentName = component.getName();
		if (parent != null && ((ComplexTypeDescriptor) parent).getComponent(componentName) != null)
			component.setParent(((ComplexTypeDescriptor) parent).getComponent(componentName));
        components.set(componentName, component);
	}
   
    public ComponentDescriptor getComponent(String name) {
        ComponentDescriptor descriptor = components.someValueOfName(name);
        if (descriptor == null && getParent() != null)
            descriptor = ((ComplexTypeDescriptor)getParent()).getComponent(name);
        return descriptor;
    }

    public List<ComponentDescriptor> getComponents() {
        NamedValueList<ComponentDescriptor> result = NamedValueList.createCaseInsensitiveList();
        
        for (ComponentDescriptor ccd : components.values())
        	result.add(ccd.getName(), ccd);
        if (getParent() != null) {
            List<ComponentDescriptor> parentComponents = ((ComplexTypeDescriptor) getParent()).getComponents();
			for (ComponentDescriptor pcd : parentComponents) {
                String name = pcd.getName();
				if (!components.containsName(name)) {
	                ComponentDescriptor ccd = components.someValueOfName(name);
	                if (ccd != null)
	                    result.add(name, ccd);
	                else
	                	result.add(name, pcd);
				}
            }
        }
        return result.values();
    }

    public Collection<ComponentDescriptor> getDeclaredComponents() {
        Set<ComponentDescriptor> declaredDescriptors = new ListBasedSet<ComponentDescriptor>(components.size());
        for (ComponentDescriptor d : components.values())
            declaredDescriptors.add(d);
        return declaredDescriptors;
    }

	public boolean isDeclaredComponent(String componentName) {
		return components.containsName(componentName);
	}

    public String[] getIdComponentNames() {
    	ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
		for (ComponentDescriptor descriptor : getComponents())
			if (descriptor instanceof IdDescriptor)
				builder.add(descriptor.getName());
		return builder.toArray();
    }
	
    public List<ReferenceDescriptor> getReferenceComponents() {
    	return CollectionUtil.extractItemsOfType(ReferenceDescriptor.class, getComponents());
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

    @Override
    protected void init() {
    	super.init();
        this.components = new NamedValueList<ComponentDescriptor>(NamedValueList.INSENSITIVE);
        this.variables = new OrderedNameMap<InstanceDescriptor>();
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        if (components.size() == 0)
            return super.toString();
        //return new CompositeFormatter(false, false).render(super.toString() + '{', new CompositeAdapter(), "}");
        return getName() + getComponents().toString();
    }

}