/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.Assert;
import org.databene.commons.SystemInfo;
import org.databene.commons.db.DBUtil;
import org.databene.model.consumer.TextFileExporter;
import org.databene.model.data.Entity;
import org.databene.platform.csv.CSVEntityExporter;

/**
 * Exports Entities to a SQL file.<br/><br/>
 * Created: 12.07.2008 09:43:59
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class SQLEntityExporter extends TextFileExporter<Entity> {

    private static final Log logger = LogFactory.getLog(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_URI       = "export.sql";
    private static final String DEFAULT_ENCODING  = SystemInfo.fileEncoding();

    // attributes ------------------------------------------------------------------------------------------------------

    private DBSystem database;

    // constructors ----------------------------------------------------------------------------------------------------

    public SQLEntityExporter() {
        this(DEFAULT_URI);
    }
    
    public SQLEntityExporter(String uri) {
        this(uri, DEFAULT_ENCODING);
    }

    public SQLEntityExporter(String uri, String encoding) {
    	super(uri, encoding);
    	setNullString("null");
    	setDatePattern("''dd-MMM-yyyy''");
    	setTimestampPattern("'TIMESTAMP' ''yyyy-MM-dd HH:mm:ss.SSSSSS''");
    }

    // properties ------------------------------------------------------------------------------------------------------

	public void setDatabase(DBSystem database) {
		this.database = database;
	}

    // Callback methods for parent class functionality -----------------------------------------------------------------

    protected void startConsumingImpl(Entity entity) {
    	Assert.notNull(database, "database");
        if (logger.isDebugEnabled())
            logger.debug("exporting " + entity);
        ColumnInfo[] columnInfos = database.writeColumnInfos(entity);
        String sql = createSQLInsert(entity, columnInfos);
        printer.println(sql);
    }

    protected void postInitPrinter() {
    	// nothing special to do
    }

    String createSQLInsert(Entity entity, ColumnInfo[] columnInfos) {
    	String table = entity.getName();
        StringBuilder builder = new StringBuilder("insert into ").append(table).append(" (");
        if (columnInfos.length > 0)
            builder.append(columnInfos[0].name);
        for (int i = 1; i < columnInfos.length; i++)
            builder.append(',').append(columnInfos[i].name);
        builder.append(") values (");
        for (int i = 0; i < columnInfos.length; i++) {
			if (i > 0)
				builder.append(", ");
            Object value = entity.get(columnInfos[i].name);
			String text = format(value);
			text = DBUtil.escape(text);
            if (value instanceof CharSequence || value instanceof Character)
    			builder.append("'").append(text).append("'");
            else
            	builder.append(text);
        }
        builder.append(");");
        String sql = builder.toString();
        logger.debug("built SQL statement: " + sql);
        return sql;
    }

}
