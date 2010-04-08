/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.model.jdbc;

import org.databene.commons.ConnectFailedException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Escalator;
import org.databene.commons.ImportFailedException;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.ObjectNotFoundException;
import org.databene.commons.StringUtil;
import org.databene.commons.Level;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.db.DBUtil;
import org.databene.platform.db.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Imports a DB model via JDBC.<br/><br/>
 * Created: 06.01.2007 19:16:45
 * @author Volker Bergmann
 */
public final class JDBCDBImporter implements DBImporter {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDBImporter.class);

    private final Connection connection;

    private final String user;
    
    private String  catalogName;
    private String  schemaName;
    private Pattern tablePattern;
    private boolean importingIndexes;

    private String productName;
    private Escalator escalator = new LoggerEscalator();
    private ErrorHandler errorHandler;

    public JDBCDBImporter(String url, String driverClassname, String user, String password) throws ConnectFailedException {
        this(url, driverClassname, user, password, null, ".*", true);
    }

    public JDBCDBImporter(String url, String driver, String user, String password, 
    		String schemaName, String tablePattern, boolean importingIndexes) throws ConnectFailedException {
    	this(DBUtil.connect(url, driver, user, password), user, schemaName, tablePattern, importingIndexes);
    }

    public JDBCDBImporter(Connection connection, String user, 
    		String schemaName, String tablePattern, boolean importingIndexes) {
    	this.connection = connection;
        this.user = user;
        this.schemaName = schemaName;
        this.tablePattern = Pattern.compile(tablePattern != null ? tablePattern : ".*");
        this.importingIndexes = importingIndexes;
        this.errorHandler = new ErrorHandler(getClass());
    }

    public Database importDatabase() throws ImportFailedException {
        logger.info("Importing database metadata. Be patient, this may take some time...");
        long startTime = System.currentTimeMillis();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            productName = metaData.getDatabaseProductName();
            if (logger.isDebugEnabled())
                logger.debug("Product name: " + productName);
            Database database = new Database();
            importCatalogs(database, metaData);
            importSchemas(database, metaData);
            importTables(database, metaData);
            importColumns(database, metaData);
            importPrimaryKeys(database, metaData);
            if (importingIndexes)
                importIndexes(database, metaData);
            importImportedKeys(database, metaData);
            return database;
        } catch (SQLException e) {
            throw new ImportFailedException(e);
        } finally {
            DBUtil.close(connection);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Imported database metadata within " + duration + " ms.");
        }
    }

    private void importCatalogs(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.info("Importing catalogs");
        ResultSet catalogSet = metaData.getCatalogs();
        int catalogCount = 0;
        while (catalogSet.next()) {
            String catalogName = catalogSet.getString(1);
            logger.debug("found catalog " + catalogName);
            if ((schemaName == null && user.equalsIgnoreCase(catalogName)) || (schemaName != null && schemaName.equalsIgnoreCase(catalogName)))
                this.catalogName = catalogName;
            database.addCatalog(new DBCatalog(catalogName));
            catalogCount++;
        }
        if (catalogCount == 0)
            database.addCatalog(new DBCatalog(null));
        catalogSet.close();
    }

    private void importSchemas(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.info("Importing schemas");
        ResultSet schemaSet = metaData.getSchemas();
        while (schemaSet.next()) {
            String schemaName = schemaSet.getString(1);
            logger.debug("found schema " + schemaName);
            if (!schemaName.equalsIgnoreCase(this.schemaName) && !(this.schemaName == null && this.user.equalsIgnoreCase(schemaName)))
                continue;
            logger.debug("importing schema " + schemaName);
            this.schemaName = schemaName;
            DBSchema schema = new DBSchema(schemaName);
            database.addSchema(schema);
        }
        schemaSet.close();
    }

    private void importTables(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.info("Importing tables");
        ResultSet tableSet = metaData.getTables(catalogName, schemaName, null, null);
        while (tableSet.next()) {

            // parsing ResultSet line
            String tCatalogName = tableSet.getString(1);
            String tSchemaName = tableSet.getString(2);
            String tableName = tableSet.getString(3);
            if (tableName.startsWith("BIN$")) {
                if (isOracle() && tableName.startsWith("BIN$"))
                    escalator.escalate("BIN$ table found (for improved performance " +
                                "execute 'PURGE RECYCLEBIN;')", this, tableName);
                continue;
            }
            if (ignoreTable(tableName))
            	continue;
            String tableType = tableSet.getString(4); // Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
            String tableRemarks = tableSet.getString(5);
            if (logger.isDebugEnabled())
                logger.debug("found table: " + tCatalogName + ", " + tSchemaName + ", " + tableName + ", " + tableType + ", " + tableRemarks);

            // building table
            DBTable table = new DBTable(tableName);
            table.setDoc(tableRemarks);
            DBSchema schema = database.getSchema(tSchemaName);
            table.setSchema(schema);
            if (schema != null)
                schema.addTable(table);
            DBCatalog catalog = database.getCatalog(tCatalogName);
            if (catalog != null)
                catalog.addTable(table);
        }
        tableSet.close();
    }

	private boolean isOracle() {
		return productName.toLowerCase().startsWith("oracle");
	}

    private void importColumns(Database database, DatabaseMetaData metaData) {
        for (DBCatalog catalog : database.getCatalogs()) {
        	try {
        		importColumns(database, catalog, metaData);
        	} catch (SQLException e) {
        		// possibly we try to access a catalog to which we do not have access rights
        		errorHandler.handleError("Error in parsing colmns of catalog " + catalog.getName(), e);
        	}
        }
    }

    private void importColumns(Database database, DBCatalog catalog, DatabaseMetaData metaData) throws SQLException {
        String catalogName = catalog.getName();
        String schemaPattern = (database.getSchemas().size() == 1 ? database.getSchemas().get(0).getName() : schemaName);
        logger.info("Importing columns for catalog '" + catalogName + "' and schemaPattern '" + schemaName + "'");
        if (isOracle()) // fix for Oracle varchar column size, see http://kr.forums.oracle.com/forums/thread.jspa?threadID=554236
        	DBUtil.executeUpdate("ALTER SESSION SET NLS_LENGTH_SEMANTICS=CHAR", connection);
        ResultSet columnSet = null;
        try {
        	columnSet = metaData.getColumns(catalogName, schemaPattern, null, null);
	        ResultSetMetaData setMetaData = columnSet.getMetaData();
	        if (setMetaData.getColumnCount() == 0)
	            return;
	        while (columnSet.next()) {
	            String schemaName = columnSet.getString(2);
	            String tableName = columnSet.getString(3);
	            if (tableName.startsWith("BIN$")) {
	                if (logger.isDebugEnabled())
	                    logger.debug("ignoring column: " + catalogName + ", " + schemaName + ", " + tableName);
	                continue;
	            }
	            if (ignoreTable(tableName))
	            	continue;
	            String columnName = columnSet.getString(4);
	            int sqlType = columnSet.getInt(5);
	            String columnType = columnSet.getString(6);
	            int columnSize = columnSet.getInt(7);
	            int decimalDigits = columnSet.getInt(9);
	            boolean nullable = columnSet.getBoolean(11);
	            String comment = columnSet.getString(12);
	            String defaultValue = columnSet.getString(13);
	
	            if (logger.isDebugEnabled())
	                logger.debug("found column: " + catalogName + ", " + schemaName + ", " + tableName + ", "
	                        + columnName + ", " + sqlType + ", " + columnType + ", " + columnSize + ", " + decimalDigits
	                        + ", " + nullable + ", " + comment + ", " + defaultValue);
	
	            Integer fractionDigits = (decimalDigits > 0 ? decimalDigits : null);
	            DBColumn column = new DBColumn(columnName, DBColumnType.getInstance(sqlType, columnType), columnSize, fractionDigits);
	            if (!StringUtil.isEmpty(comment))
	                column.setDoc(comment);
	            if (!StringUtil.isEmpty(defaultValue)) {
	                if (!column.getType().isAlpha())
	                    defaultValue = removeBrackets(defaultValue); // some driver adds brackets to number defaults
	                column.setDefaultValue(defaultValue.trim()); // oracle thin driver produces "1 "
	            }
	            if (!nullable)
	                column.setNullable(false);
	
	            DBTable table = catalog.getTable(tableName);
	            if (table == null) {
	                DBSchema schema = database.getSchema(schemaName);
	                if (schema != null)
	                    table = schema.getTable(tableName);
	            }
	            if (table != null)
	                table.addColumn(column);
	            // not used: importVersionColumnInfo(catalogName, table, metaData);
	        }
        } finally {
        	DBUtil.close(columnSet);
        }
    }

/*
    private void importVersionColumnInfo(DBCatalog catalogName, DBTable table, DatabaseMetaData metaData) throws SQLException {
        ResultSet versionColumnSet = metaData.getVersionColumns(catalogName.getName(), null, table.getName());
//        DBUtil.print(versionColumnSet);
        while (versionColumnSet.next()) {
            // short scope = versionColumnSet.getString(1);
            String columnName = versionColumnSet.getString(2);
            //int dataType = versionColumnSet.getInt(3);
            //String typeName = versionColumnSet.getString(4);
            //int columnSize = versionColumnSet.getInt(5);
            //int bufferLength = versionColumnSet.getInt(6);
            //short decimalDigits = versionColumnSet.getShort(7);
            //short pseudoColumn = versionColumnSet.getShort(8);
            DBColumn column = table.getColumn(columnName);
            column.setVersionColumn(true);
        }
    }
*/
    private void importPrimaryKeys(Database database, DatabaseMetaData metaData) {
        int count = 0;
        DBSchema schema = database.getSchema(schemaName);
        if (schema != null)
            for (DBTable table : schema.getTables()) {
                if (ignoreTable(table.getName()))
                	continue;
                importPrimaryKeys(metaData, table);
                count++;
            }
        if (count > 0)
            return;
        DBCatalog catalog = database.getCatalog(catalogName);
        if (catalog != null)
            for (DBTable table : catalog.getTables()) {
                if (ignoreTable(table.getName()))
                	continue;
                importPrimaryKeys(metaData, table);
            }
    }

    private void importPrimaryKeys(DatabaseMetaData metaData, DBTable table) {
        logger.debug("Importing primary keys for table " + table);
        ResultSet pkset = null;
        try {
	        pkset = metaData.getPrimaryKeys(catalogName, schemaName, table.getName());
	        TreeMap<Short, DBColumn> pkComponents = new TreeMap<Short, DBColumn>();
	        String pkName = null;
	        while (pkset.next()) {
	        	String tableName = pkset.getString(3);
	            if (!tableName.equals(table.getName())) // Bug fix for Firebird: 
	            	continue;							// When querying X, it returns the pks of XY to
	
	            String columnName = pkset.getString(4);
	            DBColumn column = table.getColumn(columnName);
	            short keySeq = pkset.getShort(5);
	            pkComponents.put(keySeq, column);
	            pkName = pkset.getString(6);
	            if (logger.isDebugEnabled())
	                logger.debug("found pk column " + column + ", " + keySeq + ", " + pkName);
	        }
	        DBColumn[] columnArray = new DBColumn[pkComponents.size()];
	        columnArray = pkComponents.values().toArray(columnArray);
	        DBPrimaryKeyConstraint constraint = new DBPrimaryKeyConstraint(pkName, columnArray);
	        table.setPrimaryKeyConstraint(constraint);
	        for (DBColumn column : columnArray)
	            column.addUkConstraint(constraint);
        } catch (SQLException e) {
        	errorHandler.handleError("Error importing primary key of table " + table.getName());
        } finally {
        	DBUtil.close(pkset);
        }
    }

    private void importIndexes(Database database, DatabaseMetaData metaData) {
        for (DBCatalog catalog : database.getCatalogs()) {
        	try {
        		importIndexes(database, catalog, metaData);
        	} catch (SQLException e) {
        		// possibly we try to query a catalog to which we do not have access rights
        		errorHandler.handleError("Error parsing metadata of catalog " + catalog.getName());
        	}
        }
    }

	private void importIndexes(Database database, DBCatalog catalog, DatabaseMetaData metaData) throws SQLException {
	    for (DBTable table : catalog.getTables()) {
	    	if (ignoreTable(table.getName()))
	    		continue;
	        logger.debug("Importing indexes for table '" + table.getName() + "'");
	        OrderedNameMap<DBIndexInfo> tableIndexes = new OrderedNameMap<DBIndexInfo>();
	        ResultSet indexSet = null;
	        try {
		        indexSet = metaData.getIndexInfo(catalog.getName(), null, table.getName(), false, false);
		        //DBUtil.print(indexSet);
		        while (indexSet.next()) {
		            String indexName = null;
		            try {
		                boolean unique = !indexSet.getBoolean(4);
		                String indexCatalogName = indexSet.getString(5);
		                indexName = indexSet.getString(6);
		                short indexType = indexSet.getShort(7);
		                /* not used: 
		                 * tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
		                 * tableIndexClustered - this is a clustered index
		                 * tableIndexHashed - this is a hashed index
		                 * tableIndexOther - this is some other style of index
		                 */
		                short ordinalPosition = indexSet.getShort(8);
		                if (ordinalPosition == 0)
		                    continue;
		                String columnName = indexSet.getString(9);
		                String ascOrDesc = indexSet.getString(10);
		                Boolean ascending = (ascOrDesc != null ? ascOrDesc.charAt(0) == 'A' : null);
		                int cardinality = indexSet.getInt(11);
		                int pages = indexSet.getInt(12);
		                String filterCondition = indexSet.getString(13);
		                if (logger.isDebugEnabled())
		                    logger.debug("found " + (unique ? "unique index " : "index ") + indexName + ", " 
		                            + indexCatalogName + ", " + indexType + ", " 
		                            + ordinalPosition + ", " + columnName + ", " + ascOrDesc + ", " 
		                            + cardinality + ", " + pages + ", " + filterCondition);
		                DBIndexInfo index = tableIndexes.get(indexName);
		                if (index == null) {
		                    index = new DBIndexInfo(indexName, indexType, indexCatalogName, unique,
		                        ordinalPosition, columnName,
		                        ascending, cardinality, pages, filterCondition);
		                    tableIndexes.put(indexName, index);
		                } else {
		                    index.addColumn(ordinalPosition, columnName);
		                }
		            } catch (Exception e) {
		            	errorHandler.handleError("Error parsing indexes: ", e);
		            }
		        }
	        } finally {
	        	DBUtil.close(indexSet);
	        }
	        for (DBIndexInfo indexInfo : tableIndexes.values()) {
                DBIndex index = null;
	            try {
	                DBColumn[] columns = table.getColumns(indexInfo.columnNames);
	                if (indexInfo.unique) {
	                    DBUniqueConstraint constraint = new DBUniqueConstraint(indexInfo.name, columns);
	                    table.addUniqueConstraint(constraint);
	                    index = new DBUniqueIndex(indexInfo.name, constraint);
	                } else {
	                    index = new DBNonUniqueIndex(indexInfo.name, columns);
	                }
	                if (!StringUtil.isEmpty(indexInfo.catalogName)) {
	                    DBCatalog ct = database.getCatalog(indexInfo.catalogName);
	                    if (ct != null)
	                        ct.addIndex(index);
	                }
	                table.addIndex(index);
	            } catch (ObjectNotFoundException e) {
	                logger.error("Error parsing index: " + index, e);
	            }
	        }
	    }
    }

    private void importImportedKeys(Database database, DatabaseMetaData metaData) {
        logger.info("Importing imported keys");
        int count = 0;
        for (DBSchema schema : database.getSchemas())
            for (DBTable table : schema.getTables()) {
                if (ignoreTable(table.getName()))
                	continue;
                importImportedKeys(table.getCatalog(), table.getSchema(), table, metaData);
                count++;
            }
        if (count > 0)
            return;
        for (DBCatalog catalog : database.getCatalogs())
            for (DBTable table : catalog.getTables()) {
                if (ignoreTable(table.getName()))
                	continue;
                importImportedKeys(table.getCatalog(), table.getSchema(), table, metaData);
                count++;
            }
    }

    private void importImportedKeys(DBCatalog catalog, DBSchema schema, DBTable table, DatabaseMetaData metaData) {
        logger.debug("Importing imported keys for table " + table.getName());
        String catalogName = (catalog != null ? catalog.getName() : null);
        String tableName = table.getName();
        String schemaName = (schema != null ? schema.getName() : null);
        ResultSet resultSet = null;
        try {
	        resultSet = metaData.getImportedKeys(catalogName, schemaName, tableName);
	        List<ImportedKey> importedKeys = new ArrayList<ImportedKey>();
	        ImportedKey recent = null;
	        while (resultSet.next()) {
	            ImportedKey cursor = ImportedKey.parse(resultSet, catalog, schema, table);
	            if (cursor == null) 
	            	continue;
	            if (cursor.key_seq > 1) {
	            	DBTable targetTable = cursor.getPkTable();
	                DBColumn foreignKeyColumn = table.getColumn(cursor.fkcolumn_name);
	                DBColumn targetColumn = targetTable.getColumn(cursor.pkcolumn_name);
	                assert recent != null;
	                recent.addForeignKeyColumn(foreignKeyColumn, targetColumn);
	            } else
	                importedKeys.add(cursor);
	            recent = cursor;
	        }
	        for (ImportedKey key : importedKeys) {
	            DBForeignKeyConstraint foreignKeyConstraint = new DBForeignKeyConstraint(key.fk_name);
	            for (DBForeignKeyColumn foreignKeyColumn : key.getForeignKeyColumns()) {
	                foreignKeyConstraint.addForeignKeyColumn(foreignKeyColumn);
	            }
	            table.addForeignKeyConstraint(foreignKeyConstraint);
	            if (logger.isDebugEnabled())
	            	logger.debug("Imported foreign key: " + foreignKeyConstraint);
	        }
        } catch (SQLException e) {
        	errorHandler.handleError("Error importing foreign key constraints", e);
        } finally {
	        DBUtil.close(resultSet);
        }
     }

	private boolean ignoreTable(String tableName) {
	    return tableName.contains("$") || !tablePattern.matcher(tableName).matches();
    }

    private static String removeBrackets(String defaultValue) {
        if (StringUtil.isEmpty(defaultValue))
            return defaultValue;
        if (!defaultValue.startsWith("(") || !defaultValue.endsWith(")"))
            return defaultValue;
        return removeBrackets(defaultValue.substring(1, defaultValue.length() - 1));
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }
    
    public void setFaultTolerant(boolean faultTolerant) {
    	this.errorHandler = new ErrorHandler(getClass().getName(), (faultTolerant ? Level.warn : Level.error));
    }
}
