/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.*;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.expression.ConstantExpression;
import org.databene.jdbacl.ColumnInfo;
import org.databene.jdbacl.DBUtil;
import org.databene.jdbacl.DatabaseDialect;
import org.databene.jdbacl.DatabaseDialectManager;
import org.databene.jdbacl.PooledConnectionHandler;
import org.databene.jdbacl.ResultSetConverter;
import org.databene.jdbacl.dialect.OracleDialect;
import org.databene.jdbacl.model.DBCatalog;
import org.databene.jdbacl.model.DBColumn;
import org.databene.jdbacl.model.DBColumnType;
import org.databene.jdbacl.model.DBForeignKeyConstraint;
import org.databene.jdbacl.model.DBMetaDataImporter;
import org.databene.jdbacl.model.DBPrimaryKeyConstraint;
import org.databene.jdbacl.model.DBSchema;
import org.databene.jdbacl.model.DBTable;
import org.databene.jdbacl.model.DBUniqueConstraint;
import org.databene.jdbacl.model.Database;
import org.databene.jdbacl.model.cache.CachingDBImporter;
import org.databene.jdbacl.model.jdbc.JDBCDBImporter;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.*;
import org.databene.model.storage.AbstractStorageSystem;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemInserter;
import org.databene.model.storage.StorageSystemUpdater;
import org.databene.model.version.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;

import javax.sql.PooledConnection;

/**
 * RDBMS implementation of the {@link StorageSystem} interface.<br/>
 * <br/>
 * Created: 27.06.2007 23:04:19
 * @since 0.3
 * @author Volker Bergmann
 */
public class DBSystem extends AbstractStorageSystem {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DBSystem.class);
    
    private static final int DEFAULT_FETCH_SIZE = 100;

	private static final VersionNumber MIN_ORACLE_VERSION = new VersionNumber("10.2.0.4");
	
	// constants -------------------------------------------------------------------------------------------------------
    
    protected static final ArrayPropertyExtractor<String> nameExtractor
            = new ArrayPropertyExtractor<String>("name", String.class);
    
	private static final TypeDescriptor[] EMPTY_TYPE_DESCRIPTOR_ARRAY = new TypeDescriptor[0];
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private String id;
    private String environment;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String catalog;
    private String schema;
    private String includeTables;
    private String excludeTables;
    boolean metaDataCache;
    boolean batch;
    boolean readOnly;
    boolean lazy;
    boolean acceptUnknownColumnTypes;
    
    private int fetchSize;

    private Database database;

    private Map<Thread, ThreadContext> contexts;
    private Map<String, TypeDescriptor> typeDescriptors;
    Map<String, DBTable> tables;
    
    private TypeMapper driverTypeMapper;
    DatabaseDialect dialect;
    private boolean dynamicQuerySupported;
    
	private boolean connectedBefore;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public DBSystem(String id, String url, String driver, String user, String password) {
    	this();
        setId(id);
        setUrl(url);
        setUser(user);
        setPassword(password);
        setDriver(driver);
        checkOracleDriverVersion(driver);
    }

    public DBSystem(String id, String environment) {
    	this();
    	setId(id);
        setEnvironment(environment);
    }

	private DBSystem() {
        setSchema(null);
        setIncludeTables(".*");
        setExcludeTables(null);
        setFetchSize(DEFAULT_FETCH_SIZE);
        setMetaDataCache(false);
        setBatch(false);
        setReadOnly(false);
        setLazy(true);
        setDynamicQuerySupported(true);
        this.typeDescriptors = null;
        this.contexts = new HashMap<Thread, ThreadContext>();
        this.driverTypeMapper = driverTypeMapper();
        this.connectedBefore = false;
    }

	// properties ------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getEnvironment() {
		return (environment != null ? environment : user);
	}

    private void setEnvironment(String environment) {
    	if (StringUtil.isEmpty(environment)) {
    		this.environment = null;
    		return;
    	}
    	LOGGER.debug("setting environment '{}'", environment);
		try {
			JDBCConnectData connectData = DBUtil.getConnectData(environment);
			this.environment = environment;
			this.url = connectData.url;
			this.driver = connectData.driver;
			this.catalog = connectData.catalog;
			this.schema = connectData.schema;
			this.user = connectData.user;
			this.password = connectData.password;
		} catch (IOException e) {
			throw new ConfigurationError("Failed to read settings for environment " + environment);
		}
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
        this.password = StringUtil.emptyToNull(password);
    }

    public String getCatalog() {
    	return catalog;
    }

	public void setCatalog(String catalog) {
    	this.catalog = catalog;
    }

	public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = StringUtil.emptyToNull(StringUtil.trim(schema));
    }
    
    @Deprecated
	public void setTableFilter(String tableFilter) {
    	setIncludeTables(tableFilter);
    }

	public String getIncludeTables() {
    	return includeTables;
    }

	public void setIncludeTables(String includeTables) {
    	this.includeTables = includeTables;
    }

	public String getExcludeTables() {
    	return excludeTables;
    }

	public void setExcludeTables(String excludeTables) {
    	this.excludeTables = excludeTables;
    }

	public boolean isMetaDataCache() {
		return metaDataCache;
	}
	
	public void setMetaDataCache(boolean metaDataCache) {
		this.metaDataCache = metaDataCache;
	}
	
    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
    
    public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

    public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public void setDynamicQuerySupported(boolean dynamicQuerySupported) {
    	this.dynamicQuerySupported = dynamicQuerySupported;
    }

	public void setAcceptUnknownColumnTypes(boolean acceptUnknownColumnTypes) {
    	this.acceptUnknownColumnTypes = acceptUnknownColumnTypes;
    }

    

    // DescriptorProvider interface ------------------------------------------------------------------------------------

	public TypeDescriptor[] getTypeDescriptors() {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptors()");
        parseMetadataIfNecessary();
        if (typeDescriptors == null)
        	return EMPTY_TYPE_DESCRIPTOR_ARRAY;
        else
        	return CollectionUtil.toArray(typeDescriptors.values(), TypeDescriptor.class);
    }

    public TypeDescriptor getTypeDescriptor(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("getTypeDescriptor(" + tableName + ")");
        parseMetadataIfNecessary();
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
			throw new IllegalStateException("Tried to insert rows into table '" + entity.type() + "' " +
					"though database '" + id + "' is read-only");
        if (logger.isDebugEnabled())
            logger.debug("Storing " + entity);
        persistOrUpdate(entity, true);
    }

	public void update(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to update table '" + entity.type() + "' " +
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
        Iterator<ThreadContext> iterator = contexts.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
    }

	public Entity queryEntityById(String tableName, Object id) {
        try {
	        logger.debug("queryEntityById({}, {})", tableName, id);
	        ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
	        PreparedStatement query = getThreadContext().getSelectByPKStatement(descriptor);
	        query.setObject(1, id); // TODO v1.0 support composite keys
	        ResultSet resultSet = query.executeQuery();
	        if (resultSet.next())
	        	return ResultSet2EntityConverter.convert(resultSet, descriptor);
	        else
	        	return null;
        } catch (SQLException e) {
	        throw new RuntimeException("Error querying " + tableName, e);
        }
    }

    @SuppressWarnings("null")
    public TypedIterable<Entity> queryEntities(String type, String selector, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("queryEntities(" + type + ")");
    	Connection connection = getThreadContext().connection;
        boolean script = false;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}
    	String sql = null;
    	 // TODO v0.6.4 move the case distinction to EntityResultIterable (esp. useful for dynamic queries)
    	if (StringUtil.isEmpty(selector))
    	    sql = "select * from " + type;
    	else if (StringUtil.startsWithIgnoreCase(selector, "select") || StringUtil.startsWithIgnoreCase(selector, "'select"))
    	    sql = selector;
    	else if (selector.startsWith("ftl:") || !script)
    	    sql = "select * from " + type + " WHERE " + selector;
    	else
    	    sql = "'select * from " + type + " WHERE ' + " + selector;
    	if (script)
    		sql = '{' + sql + '}';
        HeavyweightIterable<ResultSet> iterable = createQuery(sql, context, connection);
        return new EntityResultSetIterable(iterable, (ComplexTypeDescriptor) getTypeDescriptor(type));
    }

    public long countEntities(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("countEntities(" + tableName + ")");
        String query = "select count(*) from " + tableName;
        return DBUtil.queryLong(query, getThreadContext().connection);
    }

    public <T> TypedIterable<T> queryEntityIds(String tableName, String selector, Context context) {
        logger.debug("queryEntityIds({}, {})", tableName, selector);
        
        // check for script
        boolean script = false;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}

    	// find out pk columns
    	DBTable table = getTable(tableName);
        String[] pkColumnNames = table.getPKColumnNames();
        if (pkColumnNames.length == 0)
        	throw new ConfigurationError("Cannot create reference to table " + tableName + " since it does not define a primary key");
        
        // construct selector
        String query = "select " + ArrayFormat.format(pkColumnNames) + " from " + tableName;
        if (selector != null) {
        	if (script)
        		query = "{'" + query + " where ' + " + selector + "}";
        	else
        		query += " where " + selector;
        }
        return query(query, context);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> HeavyweightTypedIterable<T> query(String query, Context context) {
        if (logger.isDebugEnabled())
            logger.debug("query(" + query + ")");
        Connection connection = getThreadContext().connection;
        QueryIterable resultSetIterable = createQuery(query, context, connection);
        ResultSetConverter converter = new ResultSetConverter(Object.class, true);
		return (HeavyweightTypedIterable<T>) new ConvertingIterable<ResultSet, Object>(resultSetIterable, converter);
    }
    
    public Consumer<Entity> inserter() {
    	return new StorageSystemInserter(this);
    }
    
    public Consumer<Entity> inserter(String table) {
    	return new StorageSystemInserter(this, table);
    }
    
    public Consumer<Entity> updater() {
    	return new StorageSystemUpdater(this);
    }
    
    // database-specific interface -------------------------------------------------------------------------------------

    public boolean tableExists(String tableName) {
        if (logger.isDebugEnabled())
            logger.debug("tableExists(" + tableName + ")");
        return (getTypeDescriptor(tableName) != null);
    }

    public void createSequence(String name) throws SQLException {
		getDialect().createSequence(name, 1, getThreadContext().connection);
    }

    public void dropSequence(String name) {
        execute(getDialect().renderDropSequence(name));
    }

    public void execute(String sql) {
    	try {
	        DBUtil.executeUpdate(sql, getThreadContext().connection);
        } catch (SQLException e) {
	        throw new RuntimeException(e);
        }
    }
    
    public long nextSequenceValue(String sequenceName) {
    	return DBUtil.queryLong(getDialect().renderFetchSequenceValue(sequenceName), getThreadContext().connection);
    }
    
    public void setSequenceValue(String sequenceName, long increment) throws SQLException {
    	getDialect().setSequenceValue(sequenceName, increment, getThreadContext().connection);
    }
    
    public Connection createConnection() {
		try {
            Connection connection = DBUtil.connect(url, driver, user, password);
            if (!connectedBefore) {
            	DBUtil.logMetaData(connection);
            	connectedBefore = true;
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			connection = (Connection) Proxy.newProxyInstance(classLoader, 
					new Class[] { Connection.class, PooledConnection.class }, 
					new PooledConnectionHandler(connection, readOnly));
            connection.setAutoCommit(false);
            return connection;
        } catch (ConnectFailedException e) {
            throw new RuntimeException("Connecting the database failed. URL: " + url, e);
		} catch (SQLException e) {
			throw new ConfigurationError("Turning off auto-commit failed", e);
		}
	}
	
    public Connection getConnection() {
        return getThreadContext().connection;
    }
    
	public void invalidate() {
		typeDescriptors = null;
		tables = null;
		if (environment != null) {
			File bufferFile = CachingDBImporter.getCacheFile(environment);
			if (bufferFile.exists()) {
				if (!bufferFile.delete() && metaDataCache) {
					LOGGER.error("Deleting " + bufferFile + " failed");
					metaDataCache = false;
				} else
					LOGGER.info("Deleted meta data cache file: " + bufferFile);

			}
		}
	} 
	
	public void parseMetaData() {
        this.tables = new HashMap<String, DBTable>();
        this.typeDescriptors = new OrderedNameMap<TypeDescriptor>();
        //this.tableColumnIndexes = new HashMap<String, Map<String, Integer>>();
        getDialect(); // make sure dialect is initialized
        database = getDbMetaData();
        if (lazy)
        	logger.info("Fetching table details and ordering tables by dependency");
        else
        	logger.info("Ordering tables by dependency");
        List<DBTable> tables = DBUtil.dependencyOrderedTables(database);
        for (DBTable table : tables)
            parseTable(table);
    }
	
    public DatabaseDialect getDialect() {
    	if (dialect == null) {
        	try {
        		DatabaseMetaData metaData = getThreadContext().connection.getMetaData();
				dialect = DatabaseDialectManager.getDialectForProduct(metaData.getDatabaseProductName());
    		} catch (SQLException e) {
    	        throw new ConfigurationError("Database meta data access failed", e);
    		}
    	}
    	return dialect;
    }
    
    public String getSystem() {
    	return getDialect().getSystem();
    }
    
	public Database getDbMetaData() {
		if (database == null)
			fetchDbMetaData();
        return database;
	}

	// java.lang.Object overrides ------------------------------------------------------------------
	
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + user + '@' + url + ']';
    }

    // private helpers ------------------------------------------------------------------------------

	private void checkOracleDriverVersion(String driver) {
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

	private void fetchDbMetaData() {
		try {
		    DBMetaDataImporter importer = createJDBCImporter();
		    if (metaDataCache)
		    	importer = new CachingDBImporter(importer, getEnvironment());
		    database = importer.importDatabase();
		} catch (ConnectFailedException e) {
			throw new ConfigurationError("Database not available. ", e);
		} catch (ImportFailedException e) {
		    throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
		}
	}

	private JDBCDBImporter createJDBCImporter() throws ConnectFailedException {
		JDBCDBImporter importer = new JDBCDBImporter(url, driver, user, password);
		importer.setCatalogName(catalog);
		importer.setSchemaName(schema);
		importer.setIncludeTables(includeTables);
		importer.setExcludeTables(excludeTables);
		importer.setImportingIndexes(false);
		importer.setImportingUKs(true);
		importer.setFaultTolerant(true);
		importer.setLazy(lazy);
		return importer;
	}

	private QueryIterable createQuery(String query, Context context, Connection connection) {
	    return new QueryIterable(connection, query, fetchSize, (dynamicQuerySupported ? context : null));
    }
      
    private PreparedStatement getStatement(
    		ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) {
        ThreadContext context = getThreadContext();
        return context.getStatement(descriptor, insert, columnInfos);
    }

    private void parseTable(DBTable table) {
        if (logger.isDebugEnabled())
            logger.debug("Parsing table " + table);
        String tableName = table.getName();
        tables.put(tableName.toUpperCase(), table);
        ComplexTypeDescriptor complexType;
        if (lazy) 
        	complexType = new LazyTableComplexTypeDescriptor(table, this);
        else
        	complexType = mapTableToComplexTypeDescriptor(table, new ComplexTypeDescriptor(tableName));
        typeDescriptors.put(tableName, complexType);
    }

	public ComplexTypeDescriptor mapTableToComplexTypeDescriptor(DBTable table, ComplexTypeDescriptor complexType) {
        // process primary keys
        DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
        if (pkConstraint != null) {
	        String[] pkColumnNames = pkConstraint.getColumnNames();
	        if (pkColumnNames.length == 1) { // TODO v0.7 support composite primary keys
	        	String columnName = pkColumnNames[0];
	        	DBColumn column = table.getColumn(columnName);
				table.getColumn(columnName);
	            String abstractType = JdbcMetaTypeMapper.abstractType(column.getType(), acceptUnknownColumnTypes);
	        	IdDescriptor idDescriptor = new IdDescriptor(columnName, abstractType);
				complexType.setComponent(idDescriptor);
	        }
        }

        // process foreign keys
        for (DBForeignKeyConstraint constraint : table.getForeignKeyConstraints()) {
            String[] foreignKeyColumnNames = constraint.getForeignKeyColumnNames();
            if (foreignKeyColumnNames.length == 1) {
                String fkColumnName = foreignKeyColumnNames[0];
                DBTable targetTable = constraint.getRefereeTable();
                DBColumn fkColumn = constraint.getTable().getColumn(fkColumnName);
                DBColumnType concreteType = fkColumn.getType();
                String abstractType = JdbcMetaTypeMapper.abstractType(concreteType, acceptUnknownColumnTypes);
                ReferenceDescriptor descriptor = new ReferenceDescriptor(
                        fkColumnName, 
                        abstractType,
                        targetTable.getName());
                descriptor.getLocalType(false).setSource(id);
                descriptor.setMinCount(new ConstantExpression<Long>(1L));
                descriptor.setMaxCount(new ConstantExpression<Long>(1L));
                boolean nullable = fkColumn.isNullable();
				descriptor.setNullable(nullable);
                complexType.setComponent(descriptor); // overwrite possible id descriptor for foreign keys
                logger.debug("Parsed reference " + table.getName() + '.' + descriptor);
            } else {
                logger.warn("Can't handle composite foreign key automatically: " + constraint);
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
            String type = JdbcMetaTypeMapper.abstractType(columnType, acceptUnknownColumnTypes);
            String defaultValue = column.getDefaultValue();
            SimpleTypeDescriptor typeDescriptor = new SimpleTypeDescriptor(columnId, type);
            if (defaultValue != null)
                typeDescriptor.setDetailValue("constant", defaultValue);
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
            descriptor.setMinCount(new ConstantExpression<Long>(1L));
            descriptor.setMaxCount(new ConstantExpression<Long>(1L));
            descriptor.setNullable(column.getNotNullConstraint() == null);
            List<DBUniqueConstraint> ukConstraints = column.getUkConstraints();
            for (DBUniqueConstraint constraint : ukConstraints) {
                if (constraint.getColumnNames().length == 1) {
                    descriptor.setUnique(true);
                } else {
                    logger.warn("Automated uniqueness assurance on multiple columns is not provided yet: " + constraint);
                    // TODO v0.7 support uniqueness constraints on combination of columns
                }
            }
            logger.debug("parsed attribute " + columnId + ": " + descriptor);
            complexType.addComponent(descriptor);
        }
		return complexType;
	}

    List<ColumnInfo> getWriteColumnInfos(Entity entity, boolean insert) {
        String tableName = entity.type();
        DBTable table = getTable(tableName);
        List<String> pkColumnNames = CollectionUtil.toList(table.getPKColumnNames());
        ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
        Collection<ComponentDescriptor> componentDescriptors = typeDescriptor.getComponents();
        List<ColumnInfo> pkInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        List<ColumnInfo> normalInfos = new ArrayList<ColumnInfo>(componentDescriptors.size());
        ComplexTypeDescriptor entityDescriptor = entity.descriptor();
        for (ComponentDescriptor dbCompDescriptor : componentDescriptors) {
            ComponentDescriptor enCompDescriptor = entityDescriptor.getComponent(dbCompDescriptor.getName());
            if (enCompDescriptor != null && enCompDescriptor.getMode() == Mode.ignored)
                continue;
            if (dbCompDescriptor.getMode() != Mode.ignored) {
                String name = dbCompDescriptor.getName();
                SimpleTypeDescriptor type = (SimpleTypeDescriptor) dbCompDescriptor.getTypeDescriptor();
				PrimitiveType primitiveType = type.getPrimitiveType();
				if (primitiveType == null) {
					if (!acceptUnknownColumnTypes)
						throw new ConfigurationError("Column type of " + entityDescriptor.getName() + "." + 
							dbCompDescriptor.getName() + " unknown: " + type.getName());
					else if (entity.get(type.getName()) instanceof String)
						primitiveType = PrimitiveType.STRING;
					else
						primitiveType = PrimitiveType.OBJECT;
				}
				String primitiveTypeName = primitiveType.getName();
                DBColumn column = table.getColumn(name);
                DBColumnType columnType = column.getType();
                int sqlType = columnType.getJdbcType();
                Class<?> javaType = driverTypeMapper.concreteType(primitiveTypeName);
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

    DBTable getTable(String tableName) {
    	parseMetadataIfNecessary();
        DBSchema dbSchema = database.getCatalog(catalog).getSchema(this.schema);
        if (dbSchema != null) {
            DBTable table = dbSchema.getTable(tableName);
            if (table != null)
                return table;
        }
        for (DBCatalog catalog : database.getCatalogs()) {
            for (DBSchema schema2 : catalog.getSchemas()) {
                DBTable table = schema2.getTable(tableName);
                if (table != null)
                    return table;
            }
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
    
	private void persistOrUpdate(Entity entity, boolean insert) {
        parseMetadataIfNecessary();
        List<ColumnInfo> writeColumnInfos = getWriteColumnInfos(entity, insert);
        try {
            String tableName = entity.type();
            PreparedStatement statement = getStatement(entity.descriptor(), insert, writeColumnInfos);
            for (int i = 0; i < writeColumnInfos.size(); i++) {
            	ColumnInfo info = writeColumnInfos.get(i);
                Object componentValue = entity.getComponent(info.name);
                Object jdbcValue = componentValue;
                if (info.type != null)
                	jdbcValue = AnyConverter.convert(jdbcValue, info.type);
                try {
                    if (jdbcValue != null || (dialect instanceof OracleDialect && (info.sqlType == Types.NCLOB || info.sqlType == Types.OTHER))) // the second expression causes a workaround for Oracle's unability to perform a setNull() for NCLOBs and NVARCHAR2
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
        } catch (Exception e) {
            throw new RuntimeException("Error in persisting " + entity, e);
        }
	}
	
	private void parseMetadataIfNecessary() {
	    if (typeDescriptors == null)
            parseMetaData();
    }

    private class ThreadContext {
        
        Connection connection;
        
        public Map<ComplexTypeDescriptor, PreparedStatement> insertStatements;
        public Map<ComplexTypeDescriptor, PreparedStatement> updateStatements;
        public Map<ComplexTypeDescriptor, PreparedStatement> selectByPKStatements;
        
        public ThreadContext() {
            insertStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            updateStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
            selectByPKStatements = new OrderedMap<ComplexTypeDescriptor, PreparedStatement>();
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
			        // need to finish old statement
		            if (batch)
		                statement.executeBatch();
			        if (jdbcLogger.isDebugEnabled())
			            jdbcLogger.debug("Closing statement: " + statement);
			        DBUtil.close(statement);
			    }
			    entry.setValue(null);
			}
		}

        public PreparedStatement getSelectByPKStatement(ComplexTypeDescriptor descriptor) {
            try {
                PreparedStatement statement = selectByPKStatements.get(descriptor);
                if (statement == null)
                    statement = createSelectByPKStatement(descriptor);
                else
                    statement.clearParameters();
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private PreparedStatement createSelectByPKStatement(ComplexTypeDescriptor descriptor) throws SQLException {
	        PreparedStatement statement;
	        String tableName = descriptor.getName();
	        DBTable table = tables.get(tableName.toUpperCase());
	        if (table == null)
	        	throw new IllegalArgumentException("Table not found: " + tableName);
	        StringBuilder builder = new StringBuilder("select * from ").append(tableName).append(" where");
	        for (String idColumnName : descriptor.getIdComponentNames())
	        	builder.append(' ').append(idColumnName).append("=?");
	        statement = DBUtil.prepareStatement(connection, builder.toString(), readOnly);
        	selectByPKStatements.put(descriptor, statement);
	        return statement;
        }

        public PreparedStatement getStatement(ComplexTypeDescriptor descriptor, boolean insert, List<ColumnInfo> columnInfos) {
            try {
                PreparedStatement statement = (insert ? insertStatements.get(descriptor) : updateStatements.get(descriptor));
                if (statement == null)
                    statement = createStatement(descriptor, insert, columnInfos);
                else
                    statement.clearParameters();
                return statement;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

		private PreparedStatement createStatement(ComplexTypeDescriptor descriptor, boolean insert,
                List<ColumnInfo> columnInfos) throws SQLException {
	        PreparedStatement statement;
	        String tableName = descriptor.getName();
	        DBTable table = tables.get(tableName.toUpperCase());
	        if (table == null)
	        	throw new IllegalArgumentException("Table not found: " + tableName);
	        String sql = (insert ? 
	        		dialect.insert(table, columnInfos) : 
	        		dialect.update(table, getTable(tableName).getPKColumnNames(), columnInfos));
	        if (jdbcLogger.isDebugEnabled())
	            jdbcLogger.debug("Creating prepared statement: " + sql);
	        statement = DBUtil.prepareStatement(connection, sql, readOnly);
	        if (insert)
	        	insertStatements.put(descriptor, statement);
	        else
	        	updateStatements.put(descriptor, statement);
	        return statement;
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

    private TypeMapper driverTypeMapper() {
        return new TypeMapper(
                "byte",        Byte.class,
                "short",       Short.class,
                "int",         Integer.class,
                "big_integer", Long.class,
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

    static final Logger logger = LoggerFactory.getLogger(DBSystem.class);
    static final Logger jdbcLogger = LoggerFactory.getLogger("org.databene.benerator.JDBC");

}
