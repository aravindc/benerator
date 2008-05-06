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

package org.databene.benerator.main;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.NumberUtil;
import org.databene.commons.RoundedNumberFormat;
import org.databene.commons.StringUtil;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.databene.platform.db.DBSystem;
import org.databene.platform.dbunit.DbUnitEntityExporter;

/**
 * Creates a snapshot of a database schema and exports it in DbUnit XML file format.
 * @since 0.3.04
 * @author Volker Bergmann
 */
public class DBSnapshotTool {
    
	public static final String DB_PASSWORD = "db.password";
	public static final String DB_URL = "db.url";
	public static final String DB_DRIVER = "db.driver";
	public static final String DB_SCHEMA = "db.schema";
	public static final String DB_USER = "db.user";
	
	// TODO v0.6.0 test with each database
    private static final Log logger = LogFactory.getLog(DBSnapshotTool.class);
    
    public static void main(String[] args) {
        logger.info("Starting " + DBSnapshotTool.class.getSimpleName());
        String filename = (args.length > 0 ? args[0] : "snapshot.dbunit.xml");
        
        String dbUrl = System.getProperty(DB_URL);
        if (StringUtil.isEmpty(dbUrl))
            throw new IllegalArgumentException("No database URL specified. " +
            		"Please provide the JDBC URL as an environment property like '-Ddb.url=jdbc:...'");
        String dbDriver = System.getProperty(DB_DRIVER);
        if (StringUtil.isEmpty(dbDriver))
            throw new IllegalArgumentException("No database driver specified. " +
                    "Please provide the JDBC driver class name as an environment property like '-Ddb.driver=...'");
        String dbUser = System.getProperty(DB_USER);
        String dbPassword = System.getProperty(DB_PASSWORD);
        String dbSchema = System.getProperty(DB_SCHEMA);
        
        logger.info("Exporting data of database '" + dbUrl + "' with driver '" + dbDriver + "' as user '" + dbUser 
                + "'" + (dbSchema != null ? " using schema '" + dbSchema + "'" : "") 
                + " to file " + filename);

        export(dbUrl, dbDriver, dbSchema, dbUser, dbPassword, filename);
    }

	private static void export(String dbUrl, String dbDriver, String dbSchema,
			String dbUser, String dbPassword, String filename) {
        if (dbUser == null)
            logger.warn("No JDBC user specified");
        String fileEncoding = System.getProperty("file.encoding");
		long startTime = System.currentTimeMillis();
        DbUnitEntityExporter exporter = new DbUnitEntityExporter(filename, fileEncoding);

        DBSystem db = null;
        int count = 0;
        try {
            db = new DBSystem("db", dbUrl, dbDriver, dbUser, dbPassword);
            if (dbSchema != null)
                db.setSchema(dbSchema);
            //db.setFetchSize(1);
            List<TypeDescriptor> descriptors = Arrays.asList(db.getTypeDescriptors());
            logger.info("Starting export");
            for (TypeDescriptor descriptor : descriptors) {
                logger.info("Exporting table " + descriptor.getName());
                for (Entity entity : db.queryEntities(descriptor.getName(), null)) {
                    exporter.startConsuming(entity);
                    exporter.finishConsuming(entity);
                    count++;
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Exported " + NumberUtil.format(count, 0) + " entities in " + RoundedNumberFormat.format(duration, 0) + " ms (" + RoundedNumberFormat.format(count * 3600000L / duration, 0) + " p.h.)");
        } finally {
            exporter.close();
            if (db != null)
                db.close();
        }
	}
}
