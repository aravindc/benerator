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

package org.databene.platform.db.model;

import org.databene.commons.collection.OrderedNameMap;

/**
 * TODO Document class.<br/><br/>
 * Created: 23.07.2010 07:29:14
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class DBRow {
	
	DBTable table;
	OrderedNameMap<Object> cells;

	public DBRow(DBTable table) {
	    this.table = table;
	    this.cells = new OrderedNameMap<Object>();
    }

	public void setCellValue(String columnName, Object value) {
	    cells.put(columnName, value);
    }

	public Object getCellValue(String columnName) {
	    return cells.get(columnName);
    }

	public Object[] getPKValues() {
		String[] columnNames = table.getPKColumnNames();
	    return getColumnValues(columnNames);
    }

	public Object[] getFKValues(DBForeignKeyConstraint fk) {
		return getColumnValues(fk.getColumns());
    }

	private Object[] getColumnValues(String[] columnNames) {
		Object[] result = new Object[columnNames.length];
		for (int i = 0; i < columnNames.length; i++)
			result[i] = cells.get(columnNames[i]);
	    return result;
    }

	private Object[] getColumnValues(DBColumn[] columns) {
		Object[] result = new Object[columns.length];
		for (int i = 0; i < columns.length; i++)
			result[i] = cells.get(columns[i].getName());
	    return result;
    }

}
