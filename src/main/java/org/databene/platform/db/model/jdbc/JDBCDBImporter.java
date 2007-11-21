/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.model.ImportFailedException;
import org.databene.commons.StringUtil;
import org.databene.commons.OrderedMap;
import org.databene.commons.ArrayFormat;
import org.databene.platform.db.DBUtil;
import org.databene.platform.db.ResultSetConverter;
import org.databene.platform.db.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

/**
 * Created: 06.01.2007 19:16:45
 */
public final class JDBCDBImporter implements DBImporter {

    private static final Log logger = LogFactory.getLog(JDBCDBImporter.class);

    private final String url;
    private final String user;
    private final String password;
    private final String driverClassname;

    private String catalogName;
    private String schemaName;

    public JDBCDBImporter(String url, String driverClassname, String user, String password) {
        this(url, driverClassname, user, password, null);
    }

    public JDBCDBImporter(String url, String driverClassname, String user, String password, String schemaName) {
        this.url = url;
        this.driverClassname = driverClassname;
        this.user = user;
        this.password = password;
        this.schemaName = schemaName;
    }

    public Database importDatabase() throws ImportFailedException {
        Connection connection = null;
        try {
            Class.forName(driverClassname);
            //DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(url, user, password);
            DatabaseMetaData metaData = connection.getMetaData();
            Database database = new Database();
            importCatalogs(database, metaData);
            importSchemas(database, metaData);
            importTables(database, metaData);
            importColumns(database, metaData);
            importPrimaryKeys(database, metaData);
            importIndexes(database, metaData);
            importImportedKeys(database, metaData);
            return database;
        } catch (SQLException e) {
            throw new ImportFailedException(e);
        } catch (ClassNotFoundException e) {
            throw new ImportFailedException("Database driver not found. ", e);
        } finally {
            DBUtil.close(connection);
        }
    }

    private void importCatalogs(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.debug("Importing catalogs");
        ResultSet catalogSet = metaData.getCatalogs();
        int catalogCount = 0;
        while (catalogSet.next()) {
            logResultSet(catalogSet);
            String catalogName = catalogSet.getString(1);
            if ((schemaName == null && user.equalsIgnoreCase(catalogName)) || (schemaName != null && schemaName.equalsIgnoreCase(catalogName)))
                this.catalogName = catalogName;
            database.addCatalog(new DBCatalog(catalogName));
            catalogCount++;
        }
        if (catalogCount == 0)
            database.addCatalog(new DBCatalog(null));
    }

    private void importSchemas(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.debug("Importing schemas");
        ResultSet schemaSet = metaData.getSchemas();
        while (schemaSet.next()) {
            logResultSet(schemaSet);
            String schemaName = schemaSet.getString(1);
            if (!user.equalsIgnoreCase(schemaName) && !schemaName.equalsIgnoreCase(this.schemaName))
                continue;
            this.schemaName = schemaName;
            //String catalogName = schemaSet.getString(2);
            DBSchema schema = new DBSchema(schemaName);
            //DBCatalog catalogName = database.getCatalog(catalogName);
            //schema.setCatalog(catalogName);
            database.addSchema(schema);
        }
    }

    private void logResultSet(ResultSet schemaSet) {
        if (logger.isDebugEnabled())
            logger.debug("ResultSet: " + ArrayFormat.format(", ", (Object[]) ResultSetConverter.convert(schemaSet, false)));
    }

    private void importTables(Database database, DatabaseMetaData metaData) throws SQLException {
        logger.debug("Importing tables");
        ResultSet tableSet = metaData.getTables(catalogName, schemaName, null, null);
        while (tableSet.next()) {

            // parsing ResultSet line
            String tCatalogName = tableSet.getString(1);
            String tSchemaName = tableSet.getString(2);
            String tableName = tableSet.getString(3);
            if (tableName.startsWith("BIN$"))
                continue;
            String tableType = tableSet.getString(4); // TODO v0.4 Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
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
    }

    private void importColumns(Database database, DatabaseMetaData metaData) throws SQLException {
        for (DBCatalog catalog : database.getCatalogs())
            importColumns(database, catalog, metaData);
    }

    private void importColumns(Database database, DBCatalog catalog, DatabaseMetaData metaData) throws SQLException {
        String catalogName = catalog.getName();
        String schemaPattern = (database.getSchemas().size() == 1 ? database.getSchemas().get(0).getName() : schemaName);
        logger.debug("Importing columns for catalog '" + catalogName + "'");
        ResultSet columnSet = metaData.getColumns(catalogName, schemaPattern, null, null);
        ResultSetMetaData setMetaData = columnSet.getMetaData();
        if (setMetaData.getColumnCount() == 0)
            return;
        while (columnSet.next()) {
            //logResultSet(columnSet);
            //String catalogName = columnSet.getString(1);
            String schemaName = columnSet.getString(2);
            String tableName = columnSet.getString(3);
            if (tableName.startsWith("BIN$"))
                continue;
            String columnName = columnSet.getString(4);
            //logger.debug("Found column: " + tableName + '.' + columnName);
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

            int[] modifiers;
            if (decimalDigits != 0) {
                modifiers = new int[] { columnSize, decimalDigits };
            } else {
                modifiers = new int[] { columnSize };
            }
            DBColumn column = new DBColumn(columnName, DBColumnType.getInstance(sqlType, columnType), modifiers);
            if (!StringUtil.isEmpty(comment))
                column.setDoc(comment);
            if (!StringUtil.isEmpty(defaultValue)) {
                if (!column.getType().isAlpha())
                    defaultValue = removeBrackets(defaultValue);
                column.setDefaultValue(defaultValue.trim()); // TODO v0.4 find out why oracle thin driver produces "1 "
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
// TODO            importVersionColumnInfo(catalogName, table, metaData);
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
    private void importPrimaryKeys(Database database, DatabaseMetaData metaData) throws SQLException {
        int count = 0;
        DBSchema schema = database.getSchema(schemaName);
        if (schema != null)
            for (DBTable table : schema.getTables()) {
                importPrimaryKeys(metaData, table);
                count++;
            }
        if (count > 0)
            return;
        DBCatalog catalog = database.getCatalog(catalogName);
        if (catalog != null)
            for (DBTable table : catalog.getTables()) {
                importPrimaryKeys(metaData, table);
            }
    }

    private void importPrimaryKeys(DatabaseMetaData metaData, DBTable table) throws SQLException {
        logger.debug("Importing primary keys for table " + table);
        ResultSet pkset = metaData.getPrimaryKeys(catalogName, schemaName, table.getName());
        TreeMap<Short, DBColumn> pkComponents = new TreeMap<Short, DBColumn>();
        String pkName = null;
        while (pkset.next()) {
            logResultSet(pkset);
            String columnName = pkset.getString(4);
            DBColumn column = table.getColumn(columnName);
            short keySeq = pkset.getShort(5);
            pkComponents.put(keySeq, column);
            pkName = pkset.getString(6);
        }
        DBColumn[] columnArray = new DBColumn[pkComponents.size()];
        columnArray = pkComponents.values().toArray(columnArray);
        DBPrimaryKeyConstraint constraint = new DBPrimaryKeyConstraint(pkName, columnArray);
        table.setPrimaryKeyConstraint(constraint);
        for (DBColumn column : columnArray)
            column.addUkConstraint(constraint);
    }

    private void importIndexes(Database database, DatabaseMetaData metaData)
            throws SQLException {
        for (DBCatalog catalog : database.getCatalogs()) {
            for (DBTable table : catalog.getTables()) {
                logger.debug("Importing indexes for table '" + table.getName() + "'");
                OrderedMap<String, DBIndexInfo> tableIndexes = new OrderedMap<String, DBIndexInfo>();
                ResultSet indexSet = metaData.getIndexInfo(catalog.getName(), null, table.getName(), false, false);
                //DBUtil.print(indexSet);
                while (indexSet.next()) {
                    logResultSet(indexSet);
                    boolean unique = !indexSet.getBoolean(4);
                    String indexCatalogName = indexSet.getString(5);
                    String indexName = indexSet.getString(6);
                    short indexType = indexSet.getShort(7);
                    /* TODO v0.4
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
                    DBIndexInfo index = tableIndexes.get(indexName);
                    if (index == null) {
                        index = new DBIndexInfo(indexName, indexType, indexCatalogName, unique,
                            ordinalPosition, columnName,
                            ascending, cardinality, pages, filterCondition);
                        tableIndexes.put(indexName, index);
                    } else {
                        index.addColumn(ordinalPosition, columnName);
                    }
                }
                for (DBIndexInfo indexInfo : tableIndexes.values()) {
                    DBIndex index;
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
                }
            }
        }
    }

    private void importImportedKeys(Database database, DatabaseMetaData metaData) throws SQLException {
        int count = 0;
        for (DBCatalog catalog : database.getCatalogs())
            for (DBTable table : catalog.getTables()) {
                importImportedKeys(catalog, null, table, metaData);
                count++;
            }
        if (count > 0)
            return;
        for (DBSchema schema : database.getSchemas())
            for (DBTable table : schema.getTables()) {
                importImportedKeys(null, schema, table, metaData);
                count++;
            }
    }

    private void importImportedKeys(DBCatalog catalog, DBSchema schema, DBTable table, DatabaseMetaData metaData)
            throws SQLException {
        logger.debug("Importing imported keys");
        String catalogName = (catalog != null ? catalog.getName() : null);
        String tableName = (table != null ? table.getName() : null);
        String schemaName = (schema != null ? schema.getName() : null);
        ResultSet resultSet = metaData.getImportedKeys(catalogName, schemaName, tableName);
//        DBUtil.print(resultSet);
        List<ImportedKey> importedKeys = new ArrayList<ImportedKey>();
        ImportedKey recent = null;
        while (resultSet.next()) {
            logResultSet(resultSet);
            tableName = resultSet.getString(2);
            ImportedKey cursor = ImportedKey.parse(resultSet, catalog, schema, table);
            if (cursor.KEY_SEQ > 1) {
                DBColumn foreignKeyColumn = table.getColumn(cursor.FKCOLUMN_NAME);
                DBColumn targetColumn = table.getColumn(cursor.PKCOLUMN_NAME);
                assert recent != null;
                recent.addForeignKeyColumn(foreignKeyColumn, targetColumn);
            } else
                importedKeys.add(cursor);
            recent = cursor;
        }
        for (ImportedKey key : importedKeys) {
            DBForeignKeyConstraint foreignKeyConstraint = new DBForeignKeyConstraint(key.FK_NAME);
            for (DBForeignKeyColumn foreignKeyColumn : key.getForeignKeyColumns()) {
                foreignKeyConstraint.addForeignKeyColumn(foreignKeyColumn);
            }
            table.addForeignKeyConstraint(foreignKeyConstraint);
        }
    }

    // TODO v0.4 check what this metaData provides: attributes, bestRowIdentifier, columnPrivileges, crossReference
    // TODO v0.4 check what this metaData provides: exportedKeys, procedures, procedureColumns,
    // TODO v0.4 check what this metaData provides: superTables, superTypes, tablePrivileges, udts, versionColumns

    private static String removeBrackets(String defaultValue) {
        if (StringUtil.isEmpty(defaultValue))
            return defaultValue;
        if (!defaultValue.startsWith("(") || !defaultValue.endsWith(")"))
            return defaultValue;
        return removeBrackets(defaultValue.substring(1, defaultValue.length() - 1));
    }
}
