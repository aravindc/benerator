/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.id.IdProvider;
import org.databene.id.IdProviderFactory;
import org.databene.id.IdProviderId;
import org.databene.id.IdStrategy;
import org.databene.platform.db.model.jdbc.JDBCDBImporter;
import org.databene.platform.db.model.*;
import org.databene.commons.*;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.db.DBUtil;
import org.databene.model.data.*;
import org.databene.model.depend.DependencyModel;
import org.databene.model.storage.StorageSystem;
import org.databene.model.version.VersionNumber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.PooledConnection;

/**
 * RDBMS implementation of the {@link StorageSystem} interface.<br/>
 * <br/>
 * Created: 27.06.2007 23:04:19
 * @since 0.3
 * @author Volker Bergmann
 */
public class DBSystem implements StorageSystem, IdProviderFactory {
    
	private static final VersionNumber MIN_ORACLE_VERSION = new VersionNumber("10.2.0.4");

	// constants -------------------------------------------------------------------------------------------------------
    
    protected static final ArrayPropertyExtractor<String> nameExtractor
            = new ArrayPropertyExtractor<String>("name", String.class);
    
    public static final IdStrategy<Long>   SEQHILO   = new IdStrategy<Long>("seqhilo", Long.class);
    public static final IdStrategy<Long>   SEQUENCE  = new IdStrategy<Long>("sequence", Long.class);
    public static final IdStrategy<Object> QUERY     = new IdStrategy<Object>("query", Object.class);

    private static final IdStrategy[] ID_STRATEGIES = {
        SEQHILO, SEQUENCE, QUERY
    };
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private String id;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String schema;
    private boolean batch;
    boolean readOnly;
    
    private int fetchSize;

    private Database database;

    private Map<Thread, ThreadContext> contexts;
    private Map<String, TypeDescriptor> typeDescriptors;
    
    private TypeMapper<Class<? extends Object>> driverTypeMapper;
    private DatabaseDialect dialect;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public DBSystem() {
        this(null, null, null, null, null);
    }

    public DBSystem(String id, String url, String driver, String user, String password) {
        this.id = id;
        this.url = url;
        this.user = user;
        this.password = password;
        this.driver = driver;
        this.schema = null;
        this.fetchSize = 100;
        this.batch = false;
        this.contexts = new HashMap<Thread, ThreadContext>();
        this.driverTypeMapper = driverTypeMapper();
        this.readOnly = false;
        if (driver != null && driver.contains("oracle")) {
        	Connection connection = null;
    		try {
				connection = getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				VersionNumber driverVersion = new VersionNumber(metaData.getDriverVersion());
				if (driverVersion.compareTo(MIN_ORACLE_VERSION) < 0)
					logger.warn("Your Oracle driver has a bug in metadata support. Please update to 10.2.0.4 or newer. " +
							"You can use that driver for accessing an Oracle 9 server as well.");
			} catch (SQLException e) {
				throw new ConfigurationError(e);
			} finally {
				close();
			}
        }
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
     * @return the batch
     */
    public boolean isBatch() {
        return batch;
    }

    /**
     * @param batch the batch to set
     */
    public void setBatch(boolean batch) {
        this.batch = batch;
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
    
    public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

    

    // DescriptorProvider interface ------------------------------------------------------------------------------------

	public TypeDescriptor[] getTypeDescriptors() {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptors()");
        if (typeDescriptors == null)
            parseMetaData();
        return CollectionUtil.toArray(typeDescriptors.values(), TypeDescriptor.class);
    }

    public TypeDescriptor getTypeDescriptor(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptor(" + tableName + ")");
        if (typeDescriptors == null)
            parseMetaData();
        TypeDescriptor entityDescriptor = typeDescriptors.get(tableName);
        if (entityDescriptor == null)
            for (TypeDescriptor candidate : typeDescriptors.values())
                if (candidate.getName().equalsIgnoreCase(tableName)) {
                    entityDescriptor = candidate;
                    break;
                }
        return entityDescriptor;
    }

    // StorageSystem interface -----------------------------------------------------------------------------------------

    public void store(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to insert rows into table '" + entity.getName() + "' " +
					"though database '" + id + "' is read-only");
        if (logger.isDebugEnabled())
            logger.debug("Storing " + entity);
        persistOrUpdate(entity, true);
    }

	public void update(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to update table '" + entity.getName() + "' " +
					"though database '" + id + "' is read-only");
        if (logger.isDebugEnabled())
            logger.debug("Updating " + entity);
        persistOrUpdate(entity, false);
	}

	public void flush() {
        if (logger.isDebugEnabled())
            logger.debug("flush()");
    	for (ThreadContext threadContext : contexts.values())
    		threadContext.commit();
    }

    public void close() {
        if (logger.isDebugEnabled())
            logger.debug("close()");
        flush();
        for (IdProvider<? extends Object> idProvider : idProviders.values())
            idProvider.close();
        Iterator<ThreadContext> iterator = contexts.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
    }

    public TypedIterable<Entity> queryEntities(String type, String selector, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("queryEntities(" + type + ")");
        boolean script = false;
    	Connection connection = getThreadContext().connection;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}
    	String sql = null;
    	if (StringUtil.isEmpty(selector))
    	    sql = "select * from " + type;
    	else if (StringUtil.startsWithIgnoreCase(selector, "select"))
    	    sql = selector;
    	else
    	    sql = "select * from " + type + " WHERE " + selector;
    	if (script)
    		sql = '{' + sql + '}';
        HeavyweightIterable<ResultSet> iterable = new QueryIterable(connection, sql, fetchSize, context);
        return new EntityResultSetIterable(iterable, (ComplexTypeDescriptor) getTypeDescriptor(type));
    }

    public long countEntities(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("countEntities(" + tableName + ")");
        String sql = "select count(*) from " + tableName;
        try {
            Connection connection = getThreadContext().connection;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            long count = resultSet.getLong(1);
            resultSet.close();
            statement.close();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException("Error in counting rows of table " + tableName + ". SQL = " + sql, e);
        }
    }

    public <T> TypedIterable<T> queryEntityIds(String tableName, String selector, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("getIds(" + tableName + ", " + selector + ")");
        DBTable table = getTable(tableName);
        String[] pkColumnNames = table.getPKColumnNames();
        if (pkColumnNames.length == 0)
        	throw new ConfigurationError("Cannot create reference to table " + tableName + " since it does not define a primary key");
        String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
        if (selector != null)
            query += " where " + selector;
        return query(query, context);
    }

    public <T> TypedIterable<T> query(String query, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("getBySelector(" + query + ")");
        Connection connection = getThreadContext().connection;
        QueryIterable resultSetIterable = new QueryIterable(connection, query, fetchSize, context);
        return (TypedIterable<T>)new ConvertingIterable<ResultSet, Object>(resultSetIterable, new ResultSetConverter(true));
    }
      
    // IdProviderFactory interface -------------------------------------------------------------------------------------
    
    // TODO v0.5.7 merge with AbstractIdProviderFactory
    private Map<IdProviderId, IdProvider> idProviders = new HashMap<IdProviderId, IdProvider>();
    
    public IdStrategy<? extends Object>[] getIdStrategies() {
        return ID_STRATEGIES;
    }

    public <T> IdProvider<T> idProvider(
            IdStrategy<T> strategy, String param, String scope) {
        IdProviderId pId = new IdProviderId(strategy.getName(), param, scope, this.getId());
        IdProvider<T> provider = idProviders.get(pId);
        if (provider == null) {
            if (SEQHILO.equals(strategy))
                provider = (IdProvider<T>) new SeqHiLoIdProvider(getConnection(), dialect.sequenceAccessorSql(param), 100);
            else if (SEQUENCE.equals(strategy))
                provider = (IdProvider<T>) new LongQueryIdProvider(getConnection(), dialect.sequenceAccessorSql(param));
            else if (QUERY.equals(strategy))
                provider = (IdProvider<T>) new QueryIdProvider(getConnection(), param);
            if (provider != null)
            	idProviders.put(pId, provider);
        }
        return provider;
    }

    public Connection createConnection() {
		try {
            Connection connection = DBUtil.connect(url, driver, user, password);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			connection = (Connection) Proxy.newProxyInstance(classLoader, 
					new Class[] { Connection.class, PooledConnection.class }, 
					new PooledConnectionHandler(this, connection));
            connection.setAutoCommit(false);
            return connection;
        } catch (ConnectFailedException e) {
            throw new RuntimeException("Connecting the database failed. URL: " + url, e);
		} catch (SQLException e) {
			throw new ConfigurationError("Turning off auto-commit failed", e);
		}
	}
	
	public void invalidate() {
		typeDescriptors = null;
	} 
	
	// java.lang.Object overrides ------------------------------------------------------------------
	
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + user + '@' + url + ']';
    }

    // private helpers ------------------------------------------------------------------------------

    private PreparedStatement getStatement(
    		ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) throws SQLException {
        ThreadContext context = getThreadContext();
        return context.getStatement(descriptor, insert, columnInfos);
    }

	private void parseMetaData() {
        logger.debug("parsing metadata...");
        try {
            this.typeDescriptors = new OrderedNameMap<TypeDescriptor>();
            //this.tableColumnIndexes = new HashMap<String, Map<String, Integer>>();
            JDBCDBImporter importer = new JDBCDBImporter(url, driver, user, password, schema, false);
            database = importer.importDatabase();
            String productName = importer.getProductName();
            mapStrategy(productName);
            List<DBTable> tables = dependencyOrderedTables(database);
            for (DBTable table : tables)
                parseTable(table);
        } catch (ConnectFailedException e) {
			throw new ConfigurationError("Database not available. ", e);
        } catch (ImportFailedException e) {
            throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
		}
    }

    private void mapStrategy(String productName) { 
        String filename = "org/databene/platform/db/databene.db_dialect.properties";
        try {
            Map<String, String> mappings = IOUtil.readProperties(filename);
            for (Map.Entry<String, String> entry : mappings.entrySet())
                if (productName.toLowerCase().contains(entry.getKey())) {
                    dialect = (DatabaseDialect) BeanUtil.newInstance(entry.getValue());
                    return;
                }
            dialect = new UnknownDialect(productName);
        } catch (IOException e) {
            throw new ConfigurationError("Database dialect mapping not found: " + filename, e);
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
        ComplexTypeDescriptor complexType = new ComplexTypeDescriptor(tableName);
        
        // process primary keys
        DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
        DBColumn[] columns = pkConstraint.getColumns();
        String[] pkColumnNames = ArrayPropertyExtractor.convert(columns, "name", String.class);
        if (pkColumnNames.length == 1) { // TODO 0.6 support composite primary keys
        	String columnName = pkColumnNames[0];
        	DBColumn column = table.getColumn(columnName);
			table.getColumn(columnName);
            String abstractType = JdbcMetaTypeMapper.abstractType(column.getType());
        	IdDescriptor idDescriptor = new IdDescriptor(columnName, abstractType);
			complexType.addComponent(idDescriptor);
        }

        // process foreign keys
        for (DBForeignKeyConstraint constraint : table.getForeignKeyConstraints()) {
            List<DBForeignKeyColumn> foreignKeyColumns = constraint.getForeignKeyColumns();
            if (foreignKeyColumns.size() == 1) {
                DBForeignKeyColumn foreignKeyColumn = foreignKeyColumns.get(0);
                DBColumn targetColumn = foreignKeyColumn.getTargetColumn();
                DBTable targetTable = targetColumn.getTable();
                String fkColumnName = foreignKeyColumn.getForeignKeyColumn().getName();
                DBColumnType concreteType = foreignKeyColumn.getForeignKeyColumn().getType();
                String abstractType = JdbcMetaTypeMapper.abstractType(concreteType);
                ReferenceDescriptor descriptor = new ReferenceDescriptor(
                        fkColumnName, 
                        abstractType,
                        targetTable.getName());
                descriptor.getLocalType(false).setSource(id);
                descriptor.setMinCount(1L);
                descriptor.setMaxCount(1L);
                boolean nullable = foreignKeyColumn.getForeignKeyColumn().isNullable();
				descriptor.setNullable(nullable);
                complexType.setComponent(descriptor); // overwrite possible id descriptor for foreign keys
                logger.debug("Parsed reference " + table.getName() + '.' + descriptor);
            } else {
                logger.error("Not implemented: Don't know how to handle composite foreign keys");
            }
        }
        // process normal columns
        for (DBColumn column : table.getColumns()) {
            if (logger.isDebugEnabled())
                logger.debug("parsing column: " + column);
            String columnName = column.getName();
            if (complexType.getComponent(columnName) != null)
                continue; // skip columns that were already parsed (fk)
            String columnId = table.getName() + '.' + columnName;
            if (column.isVersionColumn()) {
                logger.debug("Leaving out version column " + columnId);
                continue;
            }
            DBColumnType columnType = column.getType();
            String type = JdbcMetaTypeMapper.abstractType(columnType);
            String defaultValue = column.getDefaultValue();
            SimpleTypeDescriptor typeDescriptor = new SimpleTypeDescriptor(columnId, type);
            if (defaultValue != null)
                typeDescriptor.setDetailValue("values", defaultValue);
            if (column.getSize() != null)
                typeDescriptor.setMaxLength(column.getSize());
            if (column.getFractionDigits() != null) {
            	if ("timestamp".equals(type))
            		typeDescriptor.setPrecision("1970-01-02");
            	else
            		typeDescriptor.setPrecision(decimalPrecision(column.getFractionDigits()));
            }
            //typeDescriptors.put(typeDescriptor.getName(), typeDescriptor);
            PartDescriptor descriptor = new PartDescriptor(columnName);
            descriptor.setLocalType(typeDescriptor);
            descriptor.setMinCount(1L);
            descriptor.setMaxCount(1L);
            descriptor.setNullable(column.getNotNullConstraint() == null);
            List<DBConstraint> ukConstraints = column.getUkConstraints();
            for (DBConstraint constraint : ukConstraints) {
                if (constraint.getColumns().length == 1) {
                    assert constraint.getColumns()[0].equals(column); // consistence check
                    descriptor.setUnique(true);
                } else {
                    logger.warn("Automated uniqueness assurance on multiple columns is not provided yet: " + constraint);
                    // TODO v0.6 support uniqueness constraints on combination of columns
                }
            }
            logger.debug("parsed attribute " + columnId + ": " + descriptor);
            complexType.addComponent(descriptor);
        }

        typeDescriptors.put(complexType.getName(), complexType);
    }

    String createSQLInsert(String tableName, List<ColumnInfo> columnInfos) {
        StringBuilder builder = new StringBuilder("insert into \"").append(tableName).append("\" (");
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

    String createSQLUpdate(String tableName, List<ColumnInfo> columnInfos) {
    	String[] pkColumnNames = getTable(tableName).getPKColumnNames();
    	if (pkColumnNames.length == 0)
    		throw new UnsupportedOperationException("Cannot update table without primary key: " + tableName);
        StringBuilder builder = new StringBuilder("update \"").append(tableName).append("\" set");
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
    
    List<ColumnInfo> getWriteColumnInfos(Entity entity, boolean insert) {
        String tableName = entity.getName();
        DBTable table = getTable(tableName);
        List<String> pkColumnNames = CollectionUtil.toList(table.getPKColumnNames());
        ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
        Collection<ComponentDescriptor> componentDescriptors = typeDescriptor.getComponents();
        List<ColumnInfo> pkInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        List<ColumnInfo> normalInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        ComplexTypeDescriptor entityDescriptor = entity.getDescriptor();
        for (ComponentDescriptor dbCompDescriptor : componentDescriptors) {
            ComponentDescriptor enCompDescriptor = entityDescriptor.getComponent(dbCompDescriptor.getName());
            if (enCompDescriptor != null && enCompDescriptor.getMode() == Mode.ignored)
                continue;
            if (dbCompDescriptor.getMode() != Mode.ignored) {
                String name = dbCompDescriptor.getName();
                String primitiveType = ((SimpleTypeDescriptor) dbCompDescriptor.getType()).getPrimitiveType().getName();
                DBColumn column = table.getColumn(name);
                DBColumnType columnType = column.getType();
                int sqlType = columnType.getJdbcType();
                Class<? extends Object> javaType = driverTypeMapper.concreteType(primitiveType);
                ColumnInfo info = new ColumnInfo(name, sqlType, javaType);
                if (pkColumnNames.contains(name))
    				pkInfos.add(info);
                else
                	normalInfos.add(info);
            }
        }
        if (insert) {
        	pkInfos.addAll(normalInfos);
        	return pkInfos;
        } else {
        	normalInfos.addAll(pkInfos);
        	return normalInfos;
        }
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
            contexts.put(currentThread, context);
        }
        return context;
    }
    
    private Connection getConnection() {
        return getThreadContext().connection;
    }
    
	private void persistOrUpdate(Entity entity, boolean insert) {
        if (typeDescriptors == null)
            parseMetaData();
        List<ColumnInfo> writeColumnInfos = getWriteColumnInfos(entity, insert);
        try {
            String tableName = entity.getName();
            PreparedStatement statement = getStatement(entity.getDescriptor(), insert, writeColumnInfos);
            for (int i = 0; i < writeColumnInfos.size(); i++) {
            	ColumnInfo info = writeColumnInfos.get(i);
                Object componentValue = entity.getComponent(info.name);
                Object jdbcValue = AnyConverter.convert(componentValue, info.type);
                try {
                    if (jdbcValue != null)
                        statement.setObject(i + 1, jdbcValue);
                    else
                        statement.setNull(i + 1, info.sqlType);
                } catch (SQLException e) {
                    throw new RuntimeException("error setting column " + tableName + '.' + info.name, e);
                }
            }
            if (batch)
                statement.addBatch();
            else
                statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error in persisting " + entity, e);
        }
	}

    private class ThreadContext {
        
        Connection connection;
        
        public Map<ComplexTypeDescriptor, PreparedStatement> insertStatements;
        public Map<ComplexTypeDescriptor, PreparedStatement> updateStatements;
        
        public ThreadContext() {
            insertStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            updateStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            connection = createConnection();
        }
        
        void commit() {
            try {
				flushStatements(insertStatements);
				flushStatements(updateStatements);
                if (jdbcLogger.isDebugEnabled())
                    jdbcLogger.debug("Committing connection: " + connection);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private void flushStatements(Map<ComplexTypeDescriptor, PreparedStatement> statements) throws SQLException {
			for (Map.Entry<ComplexTypeDescriptor, PreparedStatement> entry : statements.entrySet()) {
			    PreparedStatement statement = entry.getValue();
			    if (statement != null) {
			        statement.executeBatch();            
			        // need to finish old statement
			        if (jdbcLogger.isDebugEnabled())
			            jdbcLogger.debug("Closing statement: " + statement);
			        DBUtil.close(statement);
			    }
			    entry.setValue(null);
			}
		}

        public PreparedStatement getStatement(ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) {
            try {
                PreparedStatement statement = (insert ? insertStatements.get(descriptor) : updateStatements.get(descriptor));
                if (statement == null) {
                    String sql = (insert ? 
                    		createSQLInsert(descriptor.getName(), columnInfos) : 
                    		createSQLUpdate(descriptor.getName(), columnInfos));
                    if (jdbcLogger.isDebugEnabled())
                        jdbcLogger.debug("Creating prepared statement: " + sql);
                    statement = DBUtil.prepareStatement(connection, sql, readOnly);
                    if (insert)
                    	insertStatements.put(descriptor, statement);
                    else
                    	updateStatements.put(descriptor, statement);
                } else {
                    statement.clearParameters();
                }
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            commit();
            DBUtil.close(connection);
        }
    }

    private String decimalPrecision(int scale) {
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

    static final Log logger = LogFactory.getLog(DBSystem.class);
    static final Log jdbcLogger = LogFactory.getLog("org.databene.benerator.JDBC");

}
