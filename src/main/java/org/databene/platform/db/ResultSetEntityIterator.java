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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.databene.commons.HeavyweightIterator;
import org.databene.commons.converter.AnyConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.platform.bean.BeanDescriptorProvider;

/**
 * Iterates a ResultSet returning Entity objects.
 * 
 * @author Volker Bergmann
 * |since 0.3.04
 */
public class ResultSetEntityIterator implements HeavyweightIterator<Entity> {

    private HeavyweightIterator<ResultSet> resultSetIterator;
    
    private BeanDescriptorProvider beanDescriptorProvider;
    
    private ComplexTypeDescriptor descriptor;

    public ResultSetEntityIterator(HeavyweightIterator<ResultSet> resultSetIterator, ComplexTypeDescriptor descriptor) {
        this.resultSetIterator = resultSetIterator;
        this.descriptor = descriptor;
        this.beanDescriptorProvider = new BeanDescriptorProvider();
    }

    public boolean hasNext() {
        return resultSetIterator.hasNext();
    }
    
    public Entity next() {
        if (!hasNext())
            throw new IllegalStateException("No more row available. Check 'hasNext()' before calling next()!");
        try {
            Entity entity = new Entity(descriptor);
            ResultSet resultSet = resultSetIterator.next();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                String columnName = metaData.getColumnName(columnIndex);
                String typeName;
                if (descriptor != null) {
                    ComponentDescriptor component = descriptor.getComponent(columnName);
                    SimpleTypeDescriptor type = (SimpleTypeDescriptor) component.getType();
                    typeName = type.getPrimitiveType().getName();
                } else
                    typeName = "string";
                Object javaValue = javaValue(resultSet, columnIndex, typeName);
                entity.setComponent(columnName, javaValue);
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

	public void close() {
		 resultSetIterator.close();
	}

    // private helpers ----------------------------------------------------------------------------------------
    
    // TODO v1.0 perf: use a dedicated converter for each column
    private Object javaValue(ResultSet resultSet, int columnIndex, String primitiveType) throws SQLException {
        if ("date".equals(primitiveType))
            return resultSet.getDate(columnIndex);
        else if ("timestamp".equals(primitiveType))
            return resultSet.getTimestamp(columnIndex);
        else if ("string".equals(primitiveType))
            return resultSet.getString(columnIndex);
        // try generic conversion
        Object driverValue = resultSet.getObject(columnIndex);
        Class<? extends Object> javaType = beanDescriptorProvider.concreteType(primitiveType);
        Object javaValue = AnyConverter.convert(driverValue, javaType);
        return javaValue;
    }

}
