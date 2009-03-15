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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.ArrayUtil;

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
    
    public String createSQLInsert(String tableName, List<ColumnInfo> columnInfos) {
        StringBuilder builder = new StringBuilder("insert into ");
        appendTableName(tableName, builder).append(" (");
        if (columnInfos.size() > 0)
            builder.append('"').append(columnInfos.get(0).name).append('"');
        for (int i = 1; i < columnInfos.size(); i++)
            builder.append(",\"").append(columnInfos.get(i).name).append('"');
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

	public String createSQLUpdate(String tableName, String[] pkColumnNames, List<ColumnInfo> columnInfos) {
    	if (pkColumnNames.length == 0)
    		throw new UnsupportedOperationException("Cannot update table without primary key: " + tableName);
        StringBuilder builder = new StringBuilder("update ");
        appendTableName(tableName, builder).append(" set");
        for (int i = 0; i < columnInfos.size(); i++) {
        	if (!ArrayUtil.contains(pkColumnNames, columnInfos.get(i).name)) {
	            builder.append(" ").append('"').append(columnInfos.get(i).name).append("\"=?");
	            if (i < columnInfos.size() - pkColumnNames.length - 1)
	            	builder.append(", ");
        	}
        }
        builder.append(" where");
        for (int i = 0; i < pkColumnNames.length; i++) {
        	builder.append(' ').append('"').append(pkColumnNames[i]).append("\"=?");
        	if (i < pkColumnNames.length - 1)
        		builder.append(" and");
        }
        String sql = builder.toString();
        logger.debug("built SQL statement: " + sql);
        return sql;
    }

    private StringBuilder appendTableName(String tableName, StringBuilder builder) {
    	if (quoteTableNames)
    		return builder.append('"').append(tableName).append('"');
    	else
    		return builder.append(tableName);
    }

    static final Log logger = LogFactory.getLog(DBSystem.class);
    
}
