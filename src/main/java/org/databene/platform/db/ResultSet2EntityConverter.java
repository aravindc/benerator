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

package org.databene.platform.db;

import org.databene.model.Converter;
import org.databene.model.ConversionException;
import org.databene.model.converter.AnyConverter;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.platform.bean.BeanDescriptorProvider;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Converts a ResultSet's current cursor position to an Entity.<br/>
 * <br/>
 * Created: 23.08.2007 19:30:31
 * @deprecated use ResultSetEntityIterator
 * TODO v0.4 remove
 */
public class ResultSet2EntityConverter implements Converter<ResultSet, Entity> {

    private BeanDescriptorProvider beanDescriptorProvider;
    
    private EntityDescriptor descriptor;

    public ResultSet2EntityConverter(EntityDescriptor descriptor) {
        this.descriptor = descriptor;
        this.beanDescriptorProvider = new BeanDescriptorProvider();
    }

    public Class<Entity> getTargetType() {
        return Entity.class;
    }

    public Entity convert(ResultSet resultSet) throws ConversionException {
        try {
            Entity entity = new Entity(descriptor);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                String columnName = metaData.getColumnName(columnIndex);
                String abstractType = descriptor.getComponentDescriptor(columnName).getType();
                Object javaValue = javaValue(resultSet, columnIndex, abstractType);
                entity.setComponent(columnName, javaValue);
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object javaValue(ResultSet resultSet, int columnIndex, String abstractType) throws SQLException {
        if ("date".equals(abstractType))
            return resultSet.getDate(columnIndex);
        else if ("timestamp".equals(abstractType))
            return resultSet.getTimestamp(columnIndex);
        else if ("string".equals(abstractType))
            return resultSet.getString(columnIndex);
        // try generic conversion
        Object driverValue = resultSet.getObject(columnIndex);
        Class<? extends Object> javaType = beanDescriptorProvider.concreteType(abstractType);
        Object javaValue = AnyConverter.convert(driverValue, javaType);
        return javaValue;
    }
}
