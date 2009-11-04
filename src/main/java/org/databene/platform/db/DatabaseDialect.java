/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import java.util.List;

import org.databene.commons.ArrayUtil;
import org.databene.platform.db.model.DBCatalog;
import org.databene.platform.db.model.DBTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides abstractions of concepts that are implemented differently 
 * by different database vendors.<br/><br/>
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class DatabaseDialect {
	
    private String system;
    protected boolean quoteTableNames;
    
    public DatabaseDialect(String system, boolean quoteTableNames) {
        this.system = system;
        this.quoteTableNames = quoteTableNames;
    }

    public String sequenceAccessorSql(String sequenceName) {
        throw new UnsupportedOperationException("Sequence access not supported for " + system);
    }
    
    public String createSQLInsert(DBTable table, List<ColumnInfo> columnInfos) {
        StringBuilder builder = new StringBuilder("insert into ");
        appendQualifiedTableName(table, builder).append(" (");
        if (columnInfos.size() > 0)
            appendColumnName(columnInfos.get(0).name, builder);
        for (int i = 1; i < columnInfos.size(); i++) {
            builder.append(",");
            appendColumnName(columnInfos.get(i).name, builder);
        }
        builder.append(") values (");
        if (columnInfos.size() > 0)
            builder.append("?");
        for (int i = 1; i < columnInfos.size(); i++)
            builder.append(",?");
        builder.append(")");
        String sql = builder.toString();
        logger.debug("built SQL statement: " + sql);
        return sql;
    }

	public String createSQLUpdate(DBTable table, String[] pkColumnNames, List<ColumnInfo> columnInfos) {
    	if (pkColumnNames.length == 0)
    		throw new UnsupportedOperationException("Cannot update table without primary key: " + table.getName());
        StringBuilder builder = new StringBuilder("update ");
        appendQualifiedTableName(table, builder).append(" set");
        for (int i = 0; i < columnInfos.size(); i++) {
        	if (!ArrayUtil.contains(pkColumnNames, columnInfos.get(i).name)) {
	            builder.append(" ");
	            appendColumnName(columnInfos.get(i).name, builder);
	            builder.append("=?");
	            if (i < columnInfos.size() - pkColumnNames.length - 1)
	            	builder.append(", ");
        	}
        }
        builder.append(" where");
        for (int i = 0; i < pkColumnNames.length; i++) {
        	builder.append(' ');
        	appendColumnName(pkColumnNames[i], builder);
        	builder.append("=?");
        	if (i < pkColumnNames.length - 1)
        		builder.append(" and");
        }
        String sql = builder.toString();
        logger.debug("built SQL statement: " + sql);
        return sql;
    }

    private StringBuilder appendQualifiedTableName(DBTable table, StringBuilder builder) {
    	DBCatalog catalog = table.getCatalog();
		if (catalog != null && catalog.getName() != null)
    		appendQuoted(catalog.getName(), builder).append('.');
    	if (table.getSchema() != null)
    		appendQuoted(table.getSchema().getName(), builder).append('.');
    	return appendQuoted(table.getName(), builder);
    }

	private StringBuilder appendColumnName(String columnName, StringBuilder builder) {
    	return appendQuoted(columnName, builder);
	}
	
    private StringBuilder appendQuoted(String name, StringBuilder builder) {
    	if (quoteTableNames)
    		return builder.append('"').append(name).append('"');
    	else
    		return builder.append(name);
    }

    static final Logger logger = LoggerFactory.getLogger(DBSystem.class);
    
}
