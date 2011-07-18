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

package org.databene.platform.java;

import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.TypeMapper;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides EntityDescriptors for JavaBeanClasses
 * Created: 27.06.2007 23:04:19
 * @author Volker Bergmann
 */
public class BeanDescriptorProvider extends DefaultDescriptorProvider {
	
	private static final BeanDescriptorProvider DEFAULT_INSTANCE = new BeanDescriptorProvider();
	
	public static BeanDescriptorProvider defaultInstance() {
		return DEFAULT_INSTANCE;
	}

	private TypeMapper mapper;
    
    public BeanDescriptorProvider() {
    	super("bean", false);
        mapper = new TypeMapper(
                "byte", byte.class,
                "byte", Byte.class,

                "short", short.class,
                "short", Short.class,
                
                "int", int.class,
                "int", Integer.class,
                
                "long", long.class,
                "long", Long.class,
                
                "big_integer", BigInteger.class,

                "float", float.class,
                "float", Float.class,
                
                "double", double.class,
                "double", Double.class,

                "big_decimal", BigDecimal.class,

                "boolean", boolean.class,
                "boolean", Boolean.class,

                "char", char.class,
                "char", Character.class,
            
                "date", java.util.Date.class,
                "timestamp", java.sql.Timestamp.class,

                "string", String.class,
                "object", Object.class,
                "binary", byte[].class
            );
    }
    
    // interface -------------------------------------------------------------------------------------------------------

	@Override
	public TypeDescriptor getTypeDescriptor(String abstractTypeName) {
		if (mapper.concreteType(abstractTypeName) != null)
			return null; // the PrimitiveDescriptorProvider is responsible for primitives
		TypeDescriptor result = super.getTypeDescriptor(abstractTypeName);
		if (result == null)
			result = createTypeDescriptor(abstractTypeName);
		return result;
	}

    /**
     * @param concreteType
     * @return the abstract type that corresponds to the specified concrete type
     * @see org.databene.model.data.TypeMapper#abstractType(Class)
     */
    public String abstractType(Class<?> concreteType) {
        String result = mapper.abstractType(concreteType);
        if (result == null)
        	result = concreteType.getName();
		return result;
    }

    /**
     * @param primitiveType
     * @return the abstract type that corresponds to the specified primitive type
     * @see org.databene.model.data.TypeMapper#concreteType(java.lang.String)
     */
    public Class<?> concreteType(String primitiveType) {
        try {
            Class<?> result = mapper.concreteType(primitiveType);
            if (result == null)
                result = Class.forName(primitiveType);
            return result;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("No class mapping found for '" + primitiveType + "'", e);
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

	private TypeDescriptor createTypeDescriptor(String className) {
	    Class<?> beanClass = BeanUtil.forName(className);
	    ComplexTypeDescriptor td = new ComplexTypeDescriptor(className);
	    for (PropertyDescriptor propertyDescriptor : BeanUtil.getPropertyDescriptors(beanClass)) {
	        if ("class".equals(propertyDescriptor.getName()))
	            continue;
	        Class<?> propertyType = propertyDescriptor.getPropertyType();
	        String abstractType = mapper.abstractType(propertyType);
	        if (abstractType == null)
	            abstractType = propertyType.getName();
	        PartDescriptor pd = new PartDescriptor(propertyDescriptor.getName(), abstractType);
	        td.addComponent(pd);
	    }
	    return td;
	}

}
