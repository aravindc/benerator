/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.array;

import org.databene.commons.ArrayFormat;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.converter.ThreadSafeConverter;
import org.databene.commons.converter.AnyConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;

/**
 * Converts an array of feature values to an entity.<br/>
 * <br/>
 * Created: 26.08.2007 12:27:45
 * @author Volker Bergmann
 */
public class Array2EntityConverter extends ThreadSafeConverter<Object[], Entity> {
	
    private ComplexTypeDescriptor descriptor;
    private String[] attributeNames;
    
    Escalator escalator = new LoggerEscalator();

    public Array2EntityConverter(ComplexTypeDescriptor descriptor, String[] featureNames) {
    	super(Object[].class, Entity.class);
        this.descriptor = descriptor;
        this.attributeNames = featureNames;
    }

    public Entity convert(Object[] sourceValue) {
        Entity entity = new Entity(descriptor);
        int length;
        if (sourceValue.length > attributeNames.length) {
        	escalator.escalate("Row has more columns than specified in the file header", this, sourceValue);
        	length = attributeNames.length;
        } else
        	length = sourceValue.length;
        for (int i = 0; i < length; i++) {
        	Object value = sourceValue[i];
            String featureName = attributeNames[i];
            ComponentDescriptor component = descriptor.getComponent(featureName);
            if (component != null) {
	            TypeDescriptor componentType = component.getTypeDescriptor();
	            if (componentType instanceof SimpleTypeDescriptor)
	            	value = AnyConverter.convert(value, ((SimpleTypeDescriptor) componentType).getPrimitiveType().getJavaType());
            }
			entity.setComponent(featureName, value);
        }
        return entity;
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + ArrayFormat.format(", ", attributeNames) + ']';
    }
    
}
