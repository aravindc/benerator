/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.databene.commons.collection.OrderedNameMap;

/**
 * Describes an array.<br/><br/>
 * Created: 29.04.2010 07:32:52
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ArrayTypeDescriptor extends TypeDescriptor implements VariableHolder {

	private List<ArrayElementDescriptor> elements;
    private Map<String, InstanceDescriptor> variables;
    
    public ArrayTypeDescriptor(String name) {
	    super(name);
	    this.elements = new ArrayList<ArrayElementDescriptor>();
        this.variables = new OrderedNameMap<InstanceDescriptor>();
    }

    // element handling ------------------------------------------------------------------------------------------------

    public void addElement(ArrayElementDescriptor descriptor) {
        elements.add(descriptor);
    }

    public ArrayElementDescriptor getElement(int index) {
    	return elements.get(index);
    }

    public List<ArrayElementDescriptor> getElements() {
        return elements;
    }

	public int getElementCount() {
	    return elements.size();
    }

    // variable handling -----------------------------------------------------------------------------------------------
    
    public Collection<InstanceDescriptor> getVariables() {
        return variables.values();
    }

    public void addVariable(InstanceDescriptor variable) {
        variables.put(variable.getName(), variable);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        if (elements.size() == 0)
            return super.toString();
        //return new CompositeFormatter(false, false).render(super.toString() + '{', new CompositeAdapter(), "}");
        return getClass().getSimpleName() + elements.toString();
    }

}
