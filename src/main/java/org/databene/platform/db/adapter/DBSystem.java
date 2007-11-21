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

package org.databene.platform.db.adapter;

import org.databene.model.*;
import org.databene.model.system.System;
import org.databene.model.converter.ConvertingIterable;
import org.databene.model.converter.AnyConverter;
import org.databene.platform.db.model.jdbc.JDBCDBImporter;
import org.databene.platform.db.model.*;
import org.databene.platform.db.DBQueryIterable;
import org.databene.platform.db.ResultSet2EntityConverter;
import org.databene.platform.db.ResultSetConverter;
import org.databene.platform.bean.ArrayPropertyExtractor;
import org.databene.commons.*;
import org.databene.model.data.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.DriverManager;

/**
 * Created: 27.06.2007 23:04:19
 */
public class DBSystem implements System {

    private static Log logger = LogFactory.getLog(DBSystem.class);

    protected static final ArrayPropertyExtractor<String> nameExtractor
            = new ArrayPropertyExtractor<String>("name", String.class);

    private String id;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String schema;

    private Database database;

    private Connection connection;
    private Map<String, Map<String, Integer>> tableColumnIndexes;
    //private Map<String, AttributeDescriptor> typeMap;
    private Map<String, EntityDescriptor> typeDescriptors;
    private boolean initialized;

    public DBSystem() {
        this.initialized = false;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    // PlatformAdapter interface ---------------------------------------------------------------------------------------

    public EntityDescriptor[] getTypeDescriptors() {
        assureInitialization();
        if (typeDescriptors == null)
            parseMetaData();
        return CollectionUtil.toArray(typeDescriptors.values(), EntityDescriptor.class);
    }
/*
    public String getType(PreparedStatementWrapper instance) {
        assureInitialization();
        return instance.getTableName();
    }

    public String[] getFeatureNames(PreparedStatementWrapper instance) {
        assureInitialization();
        String tableName = instance.getTableName();
        return getColumnNames(tableName);
    }
*/
    public EntityDescriptor getTypeDescriptor(String tableName) {
        assureInitialization();
        if (typeDescriptors == null)
            parseMetaData();
        EntityDescriptor entityDescriptor = typeDescriptors.get(tableName);
        if (entityDescriptor == null)
            for (EntityDescriptor candidate : typeDescriptors.values())
                if (candidate.getName().equalsIgnoreCase(tableName)) {
                    entityDescriptor = candidate;
                    break;
                }
        return entityDescriptor;
    }

    public void store(Entity entity) {
        assureInitialization();
        try {
            String tableName = entity.getName();
//            if (entity.getName().equals("db_product"))
//                java.lang.System.out.println("pd");
            String[] allColumnNames = getColumnNames(tableName);
            List<String> usedColumnNames = new ArrayList(allColumnNames.length);
            for (String columnName : allColumnNames) {
                if (entity.getComponent(columnName) != null)
                    usedColumnNames.add(columnName);
            }
            String[] array = CollectionUtil.toArray(usedColumnNames, String.class);
            String sql = buildSQLInsert(tableName, usedColumnNames.toArray(array));
            if (logger.isDebugEnabled())
                logger.debug("Storing " + entity);
            PreparedStatement statement = connection.prepareStatement(sql);
//            for (int i = 1; i <= getColumnNames(tableName).length; i++)
//                statement.setObject(i, null);
            for (Map.Entry<String, Object> entry : entity.getComponents().entrySet()) {
                String columnName = entry.getKey();
                int columnIndex = StringUtil.indexOfIgnoreCase(columnName, array) + 1;
                ComponentDescriptor componentDescriptor = getComponentDescriptor(tableName, columnName);
                if (componentDescriptor == null)
                    throw new ConfigurationError("The table '" + tableName + "'" +
                            " does not contain a column '" + columnName + "'");
                Class<Object> javaType = javaTypeForAbstractType(componentDescriptor.getType());
                Object value = AnyConverter.convert(entry.getValue(), javaType);
                statement.setObject(columnIndex, value);
            }
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, Class> javaTypes = CollectionUtil.buildMap(
                "big_integer", BigInteger.class,
                //Types.BINARY, "binary",
                "boolean", Byte.class,
                //Types.BLOB, "",
                "boolean", Boolean.class,
                "char", Character.class,
                //Types.CLOB, "",
                //Types.DATALINK, "",
                "date", Date.class,
                "big_decimal", BigDecimal.class,
                //Types.DISTINCT, "",
                "double", Double.class,
                "float", Float.class,
                "int", Integer.class,
                //Types.JAVA_OBJECT, "",
                //Types.LONGVARBINARY, "binary",
                //Types.LONGVARCHAR, "string",
                //Types.NULL, "",
                //Types.NUMERIC, "double",
                //Types.OTHER, "",
                //Types.REAL, "double",
                //Types.REF, "",
                "short", Short.class,
                //Types.STRUCT, "",
                "date", Date.class,
                //Types.TIMESTAMP, "date",
                "byte", Byte.class,
                //Types.VARBINARY, "",
                "string", String.class
    );

    private Class<Object> javaTypeForAbstractType(String simpleType) {
        Class type = javaTypes.get(simpleType);
        if (type == null)
            throw new UnsupportedOperationException("Not mapped to a Java type: " + simpleType);
        return type;
    }

    private ComponentDescriptor getComponentDescriptor(String tableName, String columnName) {
        EntityDescriptor descriptor = getTypeDescriptor(tableName);
        for (ComponentDescriptor componentDescriptor : descriptor.getComponentDescriptors()) {
            if (componentDescriptor.getName().equalsIgnoreCase(columnName))
                return componentDescriptor;
        }
        return null;
    }

    public void flush() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TypedIterable<Entity> getEntities(String type) {
        DBQueryIterable iterable = new DBQueryIterable(connection, "select * from " + type);
        ResultSet2EntityConverter descriptor = new ResultSet2EntityConverter(getTypeDescriptor(type));
        return new ConvertingIterable<ResultSet, Entity>(iterable, descriptor);
    }

    public void close() {
        if (initialized) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                logger.error(e, e);
            }
            initialized = false;
        }
    }

    public TypedIterable<Object> getIds(String tableName, String selector) {
        assureInitialization();
        List<DBCatalog> catalogs = database.getCatalogs();
        for (DBCatalog catalog : catalogs) {
            DBTable table = catalog.getTable(tableName);
            if (table != null) {
                DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
                DBColumn[] columns = pkConstraint.getColumns();
                String[] pkColumnNames = ArrayPropertyExtractor.convert(columns, "name", String.class);
                String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
                if (selector != null)
                    query += " where " + selector;
                return getBySelector(query);
            }
        }
        List<DBSchema> schemas = database.getSchemas();
        for (DBSchema schema : schemas) {
            DBTable table = schema.getTable(tableName);
            if (table != null) {
                DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
                DBColumn[] columns = pkConstraint.getColumns();
                String[] pkColumnNames = ArrayPropertyExtractor.convert(columns, "name", String.class);
                String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
                if (selector != null)
                    query += " where " + selector;
                return getBySelector(query);
            }
        }
        throw new RuntimeException("Table not found: " + tableName);
    }

    public TypedIterable<Object> getBySelector(String query) {
        assureInitialization();
        DBQueryIterable resultSetIterable = new DBQueryIterable(connection, query);
        return new ConvertingIterable<ResultSet, Object>(resultSetIterable, new ResultSetConverter(true));
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void assureInitialization() {
        if (!initialized)
            init();
    }

    private void parseMetaData() {
        logger.debug("parsing metadata...");
        try {
            this.typeDescriptors = new HashMap<String, EntityDescriptor>();
            this.tableColumnIndexes = new HashMap<String, Map<String, Integer>>();
            JDBCDBImporter importer = new JDBCDBImporter(url, driver, user, password, schema);
            database = importer.importDatabase();
            for (DBCatalog catalog : database.getCatalogs())
                for (DBTable table : catalog.getTables())
                    parseTable(table);
            for (DBSchema schema : database.getSchemas())
                for (DBTable table : schema.getTables())
                    parseTable(table);

        } catch (ImportFailedException e) {
            throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
        }

    }

    private void parseTable(DBTable table) {
        if (logger.isDebugEnabled())
            logger.debug("Parsing table " + table);
        String tableName = table.getName();
        if (tableName.startsWith("BIN$"))
            return;
        EntityDescriptor td = new EntityDescriptor(tableName, false);
        // process foreign keys
        for (DBForeignKeyConstraint constraint : table.getForeignKeyConstraints()) {
            List<DBForeignKeyColumn> foreignKeyColumns = constraint.getForeignKeyColumns();
            if (foreignKeyColumns.size() == 1) {
                DBForeignKeyColumn foreignKeyColumn = foreignKeyColumns.get(0);
                DBColumn targetColumn = foreignKeyColumn.getTargetColumn();
                DBTable targetTable = targetColumn.getTable();
                String fkColumnName = foreignKeyColumn.getForeignKeyColumn().getName();
                ReferenceDescriptor descriptor = new ReferenceDescriptor(fkColumnName);
                descriptor.setSource(id);
                descriptor.setTargetTye(targetTable.getName());
                String type = abstractType(foreignKeyColumn.getForeignKeyColumn().getType().getSqlType());
                descriptor.setType(type);
                td.setComponentDescriptor(descriptor); // overwrite attribute descriptor
                logger.debug("Parsed reference " + table.getName() + '.' + descriptor);
            } else {
                logger.error("Not implemented: Don't know how to handle composite foreign keys");
            }
        }
        // process normal columns
        for (DBColumn column : table.getColumns()) {
            if (logger.isDebugEnabled())
                logger.debug("parsing column: " + column);
            if (td.getComponentDescriptor(column.getName()) != null)
                continue;
            String columnId = table.getName() + '.' + column.getName();
            if (column.isVersionColumn()) {
                logger.debug("Leaving out version column " + columnId);
                continue;
            }
            AttributeDescriptor descriptor = new AttributeDescriptor(column.getName());
            String type = abstractType(column.getType().getSqlType());
            descriptor.setType(type);
            String defaultValue = column.getDefaultValue();
            if (defaultValue != null)
                descriptor.setDetail("values", defaultValue);
            int[] modifiers = column.getModifiers();
            switch (modifiers.length) {
                case 0: break;
                case 1: descriptor.setMaxLength(modifiers[0]);
                        break;
                case 2: descriptor.setMaxLength(modifiers[0]);
                        if (!"string".equals(type))
                            break;
                        descriptor.setPrecision(precision(modifiers[1]));
                        break;
                default:logger.error("ignored size(s) for " + columnId + ": " +
                            ArrayFormat.formatInts(", ", modifiers));
            }
            descriptor.setNullable(column.getNotNullConstraint() == null);
            List<DBConstraint> ukConstraints = column.getUkConstraints();
            for (DBConstraint constraint : ukConstraints) {
                if (constraint.getColumns().length == 1) {
                    assert constraint.getColumns()[0].equals(column); // consistence check
                    descriptor.setUnique(true);
                } else {
                    logger.error("Uniqueness assurance on multiple columns is not supported yet: " + constraint);
                    // TODO v0.4 support uniqueness constraints on combination of columns
                }
            }
            logger.debug("parsed attribute " + columnId + ": " + descriptor);
            td.setComponentDescriptor(descriptor);
        }

        typeDescriptors.put(td.getName(), td);
    }

    private String buildSQLInsert(String tableName, String[] columnNames) {
        String sql = "insert into " + tableName + "(";
        sql += ArrayFormat.format(", ", columnNames);
        sql += ") values (";
        if (columnNames.length> 0)
            sql += "?";
        for (int i = 1; i < columnNames.length; i++)
            sql += ",?";
        sql += ")";
        logger.debug("built SQL statement: " + sql);
        return sql;
    }

    private int getColumnIndex(String tableName, String columnName) {
        tableName = tableName.toLowerCase();
        columnName = columnName.toLowerCase();
        Map<String, Integer> columnIndexes = tableColumnIndexes.get(tableName);
        if (columnIndexes == null) {
            columnIndexes = new HashMap<String, Integer>();
            tableColumnIndexes.put(tableName, columnIndexes);
        }
        Integer index = columnIndexes.get(columnName);
        if (index == null) {
            String[] columnNames = StringUtil.toLowerCase(getColumnNames(tableName));
            index = ArrayUtil.indexOf(columnName, columnNames) + 1;
            if (index == 0)
                throw new IllegalArgumentException("Column not found: " + columnName);
            columnIndexes.put(columnName, index);
        }
        return index;
    }

    private static final Map TYPE_MAP;

    static {

        TYPE_MAP = CollectionUtil.buildMap(
                // TODO v0.4 handle missing SQL types
                //Types.ARRAY, "",
                Types.BIGINT, "big_integer",
                //Types.BINARY, "binary",
                Types.BIT, "boolean",
                //Types.BLOB, "",
                Types.BOOLEAN, "boolean",
                Types.CHAR, "string",
                //Types.CLOB, "",
                //Types.DATALINK, "",
                Types.DATE, "date",
                Types.DECIMAL, "big_decimal",
                //Types.DISTINCT, "",
                Types.DOUBLE, "double",
                Types.FLOAT, "float",
                Types.INTEGER, "int",
                //Types.JAVA_OBJECT, "",
                //Types.LONGVARBINARY, "binary",
                Types.LONGVARCHAR, "string",
                //Types.NULL, "",
                Types.NUMERIC, "double",
                //Types.OTHER, "",
                Types.REAL, "double",
                //Types.REF, "",
                Types.SMALLINT, "short",
                //Types.STRUCT, "",
                Types.TIME, "date",
                Types.TIMESTAMP, "date",
                Types.TINYINT, "byte",
                //Types.VARBINARY, "",
                Types.VARCHAR, "string");
    }

    private String abstractType(int platformType) {
        String result = (String) TYPE_MAP.get(platformType);
        if (result == null)
            throw new ConfigurationError("Platform type not mapped: " + platformType);
        return result;
    }

    private String[] getColumnNames(String tableName) {
        EntityDescriptor typeDescriptor = getTypeDescriptor(tableName);
        return nameExtractor.convert(typeDescriptor.getComponentDescriptors().toArray());
    }

    private void init() {
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            this.connection.setAutoCommit(false);
            initialized = true;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("JDBC driver not found: " + driver, e);
        } catch (SQLException e) {
            throw new RuntimeException("Connecting the database failed. URL: " + url, e);
        }
    }

    private String precision(int scale) {
        if (scale == 0)
            return "1";
        StringBuilder builder = new StringBuilder("0.");
        for (int i = 1; i < scale; i++)
            builder.append('0');
        builder.append(1);
        return builder.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + user + '@' + url + ']';
    }

    public Connection getConnection() {
        assureInitialization();
        return connection;
    }
}
