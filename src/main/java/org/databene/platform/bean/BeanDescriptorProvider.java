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

package org.databene.platform.bean;

import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.model.data.EntityDescriptor;
import org.databene.model.data.AttributeDescriptor;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.TypeMapper;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides EntityDescriptors for JavaBeanClasses
 * Created: 27.06.2007 23:04:19
 */
public class BeanDescriptorProvider implements DescriptorProvider {
    
    private TypeMapper<Class<? extends Object>> mapper;

    public BeanDescriptorProvider() {
        mapper = new TypeMapper<Class<? extends Object>>(
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
    
    public EntityDescriptor[] getTypeDescriptors() {
        return new EntityDescriptor[0]; // There are way too many candidates
    }

    public EntityDescriptor getTypeDescriptor(String typeName) {
        return createTypeDescriptor(typeName);
    }
    
    
/*
    public String getType(Object bean) {
        return bean.getClass().getName();
    }

    public String[] getFeatureNames(Object bean) {
        PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(bean.getClass());
        String[] propertyNames = new String[descriptors.length - 1];
        int i = 0;
        for (PropertyDescriptor descriptor : descriptors)
            if (!"class".equals(descriptor.getName()))
                propertyNames[i++] = descriptor.getName();
        return propertyNames;
    }
*/
    // private helpers -------------------------------------------------------------------------------------------------

    /**
     * @param concreteType
     * @return
     * @see org.databene.model.data.TypeMapper#abstractType(java.lang.Object)
     */
    public String abstractType(Class<? extends Object> concreteType) {
        return mapper.abstractType(concreteType);
    }

    /**
     * @param abstractType
     * @return
     * @see org.databene.model.data.TypeMapper#concreteType(java.lang.String)
     */
    public Class<? extends Object> concreteType(String abstractType) {
        try {
            Class<? extends Object> result = mapper.concreteType(abstractType);
            if (result == null)
                result = Class.forName(abstractType);
            return result;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("No class mapping found for '" + abstractType + "'", e);
        }
    }

    private EntityDescriptor createTypeDescriptor(String typeName) {
        Class<? extends Object> beanClass = BeanUtil.forName(typeName);
        EntityDescriptor td = new EntityDescriptor(typeName, true);
        for (PropertyDescriptor propertyDescriptor : BeanUtil.getPropertyDescriptors(beanClass)) {
            if ("class".equals(propertyDescriptor.getName()))
                continue;
            AttributeDescriptor ad = new AttributeDescriptor(propertyDescriptor.getName());
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            String abstractType = mapper.abstractType(propertyType);
            if (abstractType == null)
                abstractType = propertyType.getName();
            ad.setType(abstractType);
            td.setComponentDescriptor(ad);
        }
        return td;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    public Class<? extends Object> javaTypeForAbstractType(String abstractType) {
        Class<? extends Object> type = mapper.concreteType(abstractType);
        if (type == null)
            throw new UnsupportedOperationException("Not mapped to a Java type: " + abstractType);
        return type;
    }
    


}
