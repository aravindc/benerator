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

import org.databene.model.Converter;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.commons.BeanUtil;

import java.beans.PropertyDescriptor;

/**
 * Converts a Bean to an Entity.<br/>
 * <br/>
 * Created: 29.08.2007 08:50:24
 */
public class Bean2EntityConverter<E> implements Converter<E, Entity> {

    private EntityDescriptor descriptor;
    private BeanDescriptorProvider beanDescriptorProvider = new BeanDescriptorProvider();

    public Bean2EntityConverter() {
        this(null);
    }

    public Bean2EntityConverter(EntityDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Class<Entity> getTargetType() {
        return Entity.class;
    }

    public Entity convert(E bean) {
        if (bean == null)
            return null;
        Entity entity = new Entity(descriptor != null ? descriptor : createBeanDescriptor(bean.getClass()));
        for (PropertyDescriptor descriptor : BeanUtil.getPropertyDescriptors(bean.getClass()))
            if (!"class".equals(descriptor.getName()))
                entity.setComponent(descriptor.getName(), BeanUtil.getPropertyValue(bean, descriptor.getName()));
        return entity;
    }

    private EntityDescriptor createBeanDescriptor(Class beanClass) {
        return beanDescriptorProvider.getTypeDescriptor(beanClass.getName());
    }
}
