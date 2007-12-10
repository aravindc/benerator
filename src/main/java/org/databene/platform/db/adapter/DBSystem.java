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
import org.databene.benerator.db.ColumnInfo;
import org.databene.benerator.db.EntityResultSetIterable;
import org.databene.benerator.db.JdbcMetaTypeMapper;
import org.databene.platform.db.ResultSetConverter;
import org.databene.platform.db.PooledConnection;
import org.databene.platform.db.DBUtil;
import org.databene.platform.db.DBQueryIterable;
import org.databene.platform.db.model.jdbc.JDBCDBImporter;
import org.databene.platform.db.model.*;
import org.databene.platform.bean.ArrayPropertyExtractor;
import org.databene.commons.*;
import org.databene.model.data.*;
import org.databene.model.depend.DependencyModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * RDBMS implementation of the System interface.<br/>
 * <br/>
 * Created: 27.06.2007 23:04:19
 * @author Volker Bergmann
 */
public class DBSystem implements System {
    // TODO v0.4 move this class to org.databene.benerator.db
    
    private static final Log logger = LogFactory.getLog(DBSystem.class);
    private static final Log sqlLogger = LogFactory.getLog("org.databene.benerator.SQL"); 

    protected static final ArrayPropertyExtractor<String> nameExtractor
            = new ArrayPropertyExtractor<String>("name", String.class);

    private String id;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String schema;
    
    private int fetchSize;

    private Database database;

    private Map<Thread, ThreadContext> contexts;
    private Map<String, EntityDescriptor> typeDescriptors;
    
    private TypeMapper<Class<? extends Object>> driverTypeMapper;
//    private DatabaseStrategy databaseStrategy; TODO v0.4

    public DBSystem() {
        this(null, null, null, null, null);
    }

    public DBSystem(String id, String url, String driver, String user, String password) {
        super();
        this.id = id;
        this.url = url;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.schema = null;
        this.fetchSize = 100;
        this.contexts = new HashMap<Thread, ThreadContext>();
        this.driverTypeMapper = driverTypeMapper();
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
    
    /**
     * @return the fetchSize
     */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * @param fetchSize the fetchSize to set
     */
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    // System interface ------------------------------------------------------------------------------------------------

    public EntityDescriptor[] getTypeDescriptors() {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptors()");
        if (typeDescriptors == null)
            parseMetaData();
        return CollectionUtil.toArray(typeDescriptors.values(), EntityDescriptor.class);
    }

    public EntityDescriptor getTypeDescriptor(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptor(" + tableName + ")");
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
        if (logger.isDebugEnabled())
            logger.debug("Storing " + entity);
        ColumnInfo[] writeColumnInfos = writeColumnInfos(entity);
        try {
            String tableName = entity.getName();
            PreparedStatement insertStatement = getInsertStatement(tableName, writeColumnInfos);
            for (int i = 0; i < writeColumnInfos.length; i++) {
                Object componentValue = entity.getComponent(writeColumnInfos[i].name);
                Class<? extends Object> type = writeColumnInfos[i].type;
                Object jdbcValue = AnyConverter.convert(componentValue, type);
                try {
                    if (jdbcValue != null)
                        insertStatement.setObject(i + 1, jdbcValue);
                    else
                        insertStatement.setNull(i + 1, writeColumnInfos[i].sqlType);
                } catch (SQLException e) {
                    throw new RuntimeException("error setting column " + tableName + '.' + writeColumnInfos[i].name, e);
                }
            }
           insertStatement.addBatch();
//            insertStatement.executeUpdate(); // TODO v0.4 check if batch is supported, enable batch deactivation
        } catch (SQLException e) {
            throw new RuntimeException("Error in persisting " + entity, e);
        }
    }

    public void flush() {
        if (logger.isDebugEnabled())
            logger.debug("flush()");
        try {
        	Iterator<ThreadContext> iterator = contexts.values().iterator();
        	while (iterator.hasNext()) {
        		ThreadContext threadContext = iterator.next();
                if (threadContext.lastInsertStatement != null) {
                    threadContext.lastInsertStatement.executeBatch();            
                    // need to finish old statement
                    DBUtil.close(threadContext.lastInsertStatement);
                }
        		threadContext.lastInsertTable = null;
                threadContext.lastInsertStatement = null;
                threadContext.lastInsertSQL = null;
                threadContext.connection.commit();
        	}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TypedIterable<Entity> getEntities(String type) {
        if (logger.isDebugEnabled())
            logger.debug("getEntities(" + type + ")");
    	Connection connection = getThreadContext().connection;
        Iterable<ResultSet> iterable = new DBQueryIterable(connection, "select * from " + type, fetchSize);
        return new EntityResultSetIterable(iterable, getTypeDescriptor(type));
    }

    public long countEntities(String tableName) { // TODO v0.4 add to System interface
        if (logger.isDebugEnabled())
            logger.debug("countEntities(" + tableName + ")");
        String sql = "select count(*) from " + tableName;
        try {
            Connection connection = getThreadContext().connection;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            long count = resultSet.getLong(1);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException("Error in counting rows of table " + tableName + ". SQL = " + sql, e);
        }
    }

    public void close() {
        if (logger.isDebugEnabled())
            logger.debug("close()");
        flush();
    	Iterator<ThreadContext> iterator = contexts.values().iterator();
    	while (iterator.hasNext()) {
			ThreadContext context = iterator.next();
			iterator.remove();
			context.lastInsertTable = null;
			context.lastInsertStatement = null;
			context.lastInsertSQL = null;
			DBUtil.close(context.connection);
		}
    }

    public TypedIterable<Object> getIds(String tableName, String selector) {
        if (logger.isDebugEnabled())
            logger.debug("getIds(" + tableName + ", " + selector + ")");
        
        DBTable table = getTable(tableName);
        DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
        DBColumn[] columns = pkConstraint.getColumns();
        String[] pkColumnNames = ArrayPropertyExtractor.convert(columns, "name", String.class);
        String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
        if (selector != null)
            query += " where " + selector;
        return getBySelector(query);
    }

    public TypedIterable<Object> getBySelector(String query) {
        if (logger.isDebugEnabled())
            logger.debug("getBySelector(" + query + ")");
    	Connection connection = getThreadContext().connection;
        DBQueryIterable resultSetIterable = new DBQueryIterable(connection, query);
        return new ConvertingIterable<ResultSet, Object>(resultSetIterable, new ResultSetConverter(true));
    }
    
/* TODO v0.4    
    public Generator<? extends Object> idGenerator(String type, String name) {
        if ("sequence".equalsIgnoreCase(type))
            return new SQLLongGenerator(this, databaseStrategy.sequenceAccessorSql(name));
        else if ("seqhilo".equalsIgnoreCase(type))
            return new DBSequenceHiLoGenerator(this, databaseStrategy.sequenceAccessorSql(name), 100);
        else
            throw new IllegalArgumentException("ID generator type unknown for " + this);
    }
*/
    public Connection createConnection() {
		try {
            Class.forName(driver);
            Connection connection = new PooledConnection(DriverManager.getConnection(url, user, password));
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("JDBC driver not found: " + driver, e);
        } catch (SQLException e) {
            throw new RuntimeException("Connecting the database failed. URL: " + url, e);
        }
	}
	
	// java.lang.Object overrides ------------------------------------------------------------------
	
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + user + '@' + url + ']';
    }

    // private helpers ------------------------------------------------------------------------------

    private PreparedStatement getInsertStatement(String tableName, ColumnInfo[] columnInfos) throws SQLException {
        ThreadContext context = getThreadContext();
        if (!tableName.equals(context.lastInsertTable)) {
            Connection connection = getThreadContext().connection;
            if (context.lastInsertStatement != null)
                flush();
            context.lastInsertTable = tableName;
            String sql = createSQLInsert(tableName, columnInfos);
            context.lastInsertSQL = sql;
            context.lastInsertStatement = connection.prepareStatement(sql);
        }
        if (sqlLogger.isDebugEnabled())
            sqlLogger.debug(context.lastInsertSQL);
        return context.lastInsertStatement;
    }

	private void parseMetaData() {
        logger.debug("parsing metadata...");
        try {
            this.typeDescriptors = new OrderedMap<String, EntityDescriptor>();
            //this.tableColumnIndexes = new HashMap<String, Map<String, Integer>>();
            JDBCDBImporter importer = new JDBCDBImporter(url, driver, user, password, schema, false);
            database = importer.importDatabase();
            String productName = importer.getProductName();
            /* TODO v0.4
            if (productName.toLowerCase().startsWith("oracle"))
                databaseStrategy = new OracleDatabaseStrategy();
            else
                database = new UnsupportedDatabaseStrategy(productName);
            */
            // order tables by dependency
            List<DBTable> tables = dependencyOrderedTables(database);
            for (DBTable table : tables)
                parseTable(table);
        } catch (ImportFailedException e) {
            throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
        }
    }

    private static List<DBTable> dependencyOrderedTables(Database database) {
        DependencyModel<DBTable> model = new DependencyModel<DBTable>();
        for (DBCatalog catalog : database.getCatalogs())
            for (DBTable table : catalog.getTables())
                model.addNode(table);
        for (DBSchema schema : database.getSchemas())
            for (DBTable table : schema.getTables())
                model.addNode(table);
        List<DBTable> tables = model.dependencyOrderedObjects(true);
        return tables;
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
                DBColumnType concreteType = foreignKeyColumn.getForeignKeyColumn().getType();
                String abstractType = JdbcMetaTypeMapper.abstractType(concreteType);
                descriptor.setType(abstractType);
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
                continue; // skip columns that were already parsed (fk)
            String columnId = table.getName() + '.' + column.getName();
            if (column.isVersionColumn()) {
                logger.debug("Leaving out version column " + columnId);
                continue;
            }
            AttributeDescriptor descriptor = new AttributeDescriptor(column.getName());
            DBColumnType columnType = column.getType();
            String type = JdbcMetaTypeMapper.abstractType(columnType);
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

    private String createSQLInsert(String tableName, ColumnInfo[] columnInfos) {
        StringBuilder builder = new StringBuilder("insert into ").append(tableName).append("(");
        if (columnInfos.length > 0)
            builder.append(columnInfos[0].name);
        for (int i = 1; i < columnInfos.length; i++)
            builder.append(',').append(columnInfos[i].name);
        builder.append(") values (");
        if (columnInfos.length> 0)
            builder.append("?");
        for (int i = 1; i < columnInfos.length; i++)
            builder.append(",?");
        builder.append(")");
        String sql = builder.toString();
        logger.debug("built SQL statement: " + sql);
        return sql;
    }
/*
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
*/
    
    private ColumnInfo[] writeColumnInfos(Entity entity) {
        String tableName = entity.getName();
        DBTable table = getTable(tableName);
        EntityDescriptor typeDescriptor = getTypeDescriptor(tableName);
        Collection<ComponentDescriptor> componentDescriptors = typeDescriptor.getComponentDescriptors();
        ArrayBuilder<ColumnInfo> builder = new ArrayBuilder<ColumnInfo>(ColumnInfo.class, componentDescriptors.size());
        EntityDescriptor entityDescriptor = entity.getDescriptor();
        for (ComponentDescriptor dbCompDescriptor : componentDescriptors) {
            ComponentDescriptor enCompDescriptor = entityDescriptor.getComponentDescriptor(dbCompDescriptor.getName());
            if (enCompDescriptor != null && enCompDescriptor.getMode() == Mode.ignored)
                continue;
            if (dbCompDescriptor.getMode() != Mode.ignored) {
                String name = dbCompDescriptor.getName();
                String abstractType = dbCompDescriptor.getType();
                DBColumn column = table.getColumn(name);
                DBColumnType columnType = column.getType();
                int sqlType = columnType.getJdbcType();
                Class<? extends Object> javaType = driverTypeMapper.concreteType(abstractType);
                builder.append(new ColumnInfo(name, sqlType, javaType));
            }
        }
        return builder.toArray();
    }

    private DBTable getTable(String tableName) {
        DBSchema dbSchema = database.getSchema(this.schema);
        if (dbSchema != null) {
            DBTable table = dbSchema.getTable(tableName);
            if (table != null)
                return table;
        }
        for (DBCatalog catalog : database.getCatalogs()) {
            DBTable table = catalog.getTable(tableName);
            if (table != null)
                return table;
        }
        for (DBSchema schema2 : database.getSchemas()) {
            DBTable table = schema2.getTable(tableName);
            if (table != null)
                return table;
        }
        throw new ObjectNotFoundException("Table " + tableName);
    }

    private synchronized ThreadContext getThreadContext() {
        Thread currentThread = Thread.currentThread();
        ThreadContext context = contexts.get(currentThread);
        if (context == null) {
            context = new ThreadContext();
            context.connection = createConnection();
            contexts.put(currentThread, context);
        }
        return context;
    }
    
    private static class ThreadContext {
        public Connection connection;
        public String lastInsertTable;
        public String lastInsertSQL;
        public PreparedStatement lastInsertStatement;
        
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

    private TypeMapper<Class<? extends Object>> driverTypeMapper() {
        return new TypeMapper<Class<? extends Object>>(
                "byte",        Byte.class,
                "short",       Short.class,
                "int",         Integer.class,
                "big_integer", BigInteger.class,
                "float",       Float.class,
                "double",      Double.class,
                "big_decimal", BigDecimal.class,
                
                "boolean",     Boolean.class,
                "char",        Character.class,
                "date",        java.sql.Date.class,
                "timestamp",   java.sql.Timestamp.class,
                
                "string",      java.sql.Clob.class,
                "string",      String.class,
                
                "binary",      Blob.class,
                "binary",      byte[].class
                
//              "object",      Object.class,
                
        );
    }

}
