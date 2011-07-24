/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.dbunit;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.script.ScriptUtil;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.commons.Context;
import org.databene.commons.ArrayFormat;
import org.databene.commons.xml.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Iterates the rows defined an a DBUnit dataset file. It supports the normal format as well as the flat format.<br/>
 * <br/>
 * Created: 05.08.2007 07:43:36
 * @author Volker Bergmann
 */
public class DbUnitEntityIterator implements DataIterator<Entity> {

    private static final Logger logger = LoggerFactory.getLogger(DbUnitEntityIterator.class);

    private Context context;
    
    private List<Row> rows;

    private int nextRowNum;
    
    private DataModel dataModel = DataModel.getDefaultInstance();

    public DbUnitEntityIterator(String uri, Context context) throws IOException {
        this.context = context;
        this.rows = new ArrayList<Row>();
        Document document = readDocument(uri);
        if (isFlatDataset(document))
            parseFlatDataset(document);
        else
            parseDataset(document);
        processScripts();
        this.nextRowNum = 0;
    }
    
    // DataIterator interface implementation ---------------------------------------------------------------------------

    public Class<Entity> getType() {
    	return Entity.class;
    }
    
	public DataContainer<Entity> next(DataContainer<Entity> container) {
        if (nextRowNum >= rows.size())
        	return null;
        Row row = rows.get(nextRowNum);
        String[] rowValues = row.getValues();
        ComplexTypeDescriptor descriptor = getType(row);
		Entity result = new Entity(descriptor);
        for (int i = 0; i < rowValues.length; i++) {
            String rowValue = rowValues[i];
			result.setComponent(row.getColumnName(i), rowValue);
        }
        nextRowNum++;
        return container.setData(result);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        // This is not implemented, so why make the class a Heavyweight? 
        // Because we might need to process very long files one day and 
        // don't want to change the contract for that
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private ComplexTypeDescriptor getType(Row row) {
        String name = row.getTableName();
        ComplexTypeDescriptor type = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(name);
        if (type == null)
            type = new ComplexTypeDescriptor(name);
        return type;
    }

    private void processScripts() {
        for (Row row : rows) {
            String[] cells = row.getValues();
            for (int i = 0; i < cells.length; i++) {
                cells[i] = String.valueOf(ScriptUtil.evaluate(cells[i], context));
            }
        }
    }

    private boolean isFlatDataset(Document document) {
        return document.getDocumentElement().getElementsByTagName("table").getLength() == 0;
    }

    private Document readDocument(String uri) throws IOException {
        return XMLUtil.parse(uri);
    }

    private void parseDataset(Document document) {
        Element documentElement = document.getDocumentElement();
        // parse tableNodes
        NodeList tableNodes = documentElement.getElementsByTagName("table");
//        this.tables = new Table[tableNodes.getLength()];
        for (int tablenum = 0; tablenum < tableNodes.getLength(); tablenum++) {
            Element tableNode = (Element) tableNodes.item(tablenum);
            String tableName = tableNode.getAttribute("name");
            // parse columns
            NodeList columns = tableNode.getElementsByTagName("column");
            String[] columnNames = new String[columns.getLength()];
            for (int colnum = 0; colnum < columns.getLength(); colnum++) {
                Element column = (Element) columns.item(colnum);
                columnNames[colnum] = column.getTextContent();
            }
            Table table = new Table(tableName, columnNames);
//            this.tables[tablenum] = table;
            // parse rows
            NodeList rows = tableNode.getElementsByTagName("row");
            for (int rownum = 0; rownum < rows.getLength(); rownum++) {
                Element row = (Element) rows.item(rownum);
                NodeList cellNodes = row.getElementsByTagName("*");
                String[] values = new String[cellNodes.getLength()];
                for (int cellnum = 0; cellnum < cellNodes.getLength(); cellnum++) {
                    Element cell = (Element) cellNodes.item(cellnum);
                    values[cellnum] = cell.getTextContent();
                }
                this.rows.add(new Row(tableName, table.getColumnNames(), values));
            }
        }
    }

    private void parseFlatDataset(Document document) {
        Element documentElement = document.getDocumentElement();
        // parse tableNodes
        NodeList rowNodes = documentElement.getChildNodes();
        for (int rownum = 0; rownum < rowNodes.getLength(); rownum++) {
            Node node = rowNodes.item(rownum);
            if (node instanceof Element) {
                Element tableNode = (Element)node;
                String tableName = tableNode.getNodeName();
                NamedNodeMap attributes = tableNode.getAttributes();
                String[] columnNames = new String[attributes.getLength()];
                String[] values = new String[attributes.getLength()];
                for (int childnum = 0; childnum < attributes.getLength(); childnum++) {
                    Attr attNode = (Attr) attributes.item(childnum);
                    columnNames[childnum] = attNode.getNodeName();
                    values[childnum] = attNode.getValue();
                }
                Row row = new Row(tableName, columnNames, values);
                logger.debug("parsed row " + row);
                rows.add(row);
            }
        }
    }

    private static class Table {
        private String name;
        private String[] columnNames;

        public Table(String name, String[] columns) {
            this.name = name;
            this.columnNames = columns;
        }

        @Override
        public String toString() {
            return name + '[' + ArrayFormat.format(columnNames) + ']';
        }

        public String[] getColumnNames() {
            return columnNames;
        }
    }

    private static class Row {

//        private Table table;
        private String tableName;
        private String[] columnNames;
        private String[] values;

        public Row(String tableName, String[] columnNames, String[] values) {
            this.tableName = tableName;
            this.columnNames = columnNames;
            this.values = values;
        }

        public String getTableName() {
            return tableName;
        }

        public String[] getValues() {
            return values;
        }

        public String getColumnName(int i) {
            return columnNames[i];
        }

        @Override
        public String toString() {
            return tableName  + '[' + ArrayFormat.format(values) + ']';
        }
    }

}
