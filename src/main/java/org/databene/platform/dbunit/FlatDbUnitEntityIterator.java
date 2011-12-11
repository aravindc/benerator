/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.dbunit;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.script.ScriptUtil;
import org.databene.webdecs.DataContainer;

/**
 * Parses a flat DbUnit dataset file.<br/><br/>
 * Created: 20.09.2011 07:53:15
 * @since 0.7.2
 * @author Volker Bergmann
 */
public class FlatDbUnitEntityIterator extends AbstractDbUnitEntityIterator {

    public FlatDbUnitEntityIterator(String uri, BeneratorContext context) {
    	super(uri, context);
    	DbUnitUtil.skipRootElement(reader);
    }

    // DataIterator interface implementation ---------------------------------------------------------------------------

	public DataContainer<Entity> next(DataContainer<Entity> container) {
		DbUnitUtil.skipNonStartTags(reader);
		if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT)
			return null;
		// map element to entity
        QName name = reader.getName();
        Row row = parseDataset(name.getLocalPart());
        Entity result = mapToEntity(row);
        return container.setData(result);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private Row parseDataset(String tableName) {
    	int columnCount = reader.getAttributeCount();
        String[] columnNames = new String[columnCount];
        String[] cellValues = new String[columnCount];
    	for (int i = 0; i < columnCount; i++) {
    		columnNames[i] = reader.getAttributeLocalName(i);
    		cellValues[i] = reader.getAttributeValue(i);
    	}
        Row row = new Row(tableName, columnNames, cellValues);
        logger.debug("parsed row {}", row);
        return row;
    }

	protected Entity mapToEntity(Row row) {
		String[] cells = row.getValues();
        ComplexTypeDescriptor descriptor = getType(row);
		Entity result = new Entity(descriptor);
        for (int i = 0; i < cells.length; i++) {
            String rowValue = String.valueOf(ScriptUtil.evaluate(cells[i], context));
			result.setComponent(row.getColumnName(i), rowValue);
        }
		return result;
	}

}
