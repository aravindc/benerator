/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.io.File;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Consumer;
import org.databene.benerator.storage.AbstractStorageSystem;
import org.databene.benerator.storage.StorageSystemInserter;
import org.databene.commons.ArrayFormat;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConnectFailedException;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.ImportFailedException;
import org.databene.commons.ObjectNotFoundException;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.version.VersionNumber;
import org.databene.jdbacl.ColumnInfo;
import org.databene.jdbacl.DBUtil;
import org.databene.jdbacl.DatabaseDialect;
import org.databene.jdbacl.DatabaseDialectManager;
import org.databene.jdbacl.JDBCConnectData;
import org.databene.jdbacl.ResultSetConverter;
import org.databene.jdbacl.dialect.OracleDialect;
import org.databene.jdbacl.model.DBCatalog;
import org.databene.jdbacl.model.DBColumn;
import org.databene.jdbacl.model.DBDataType;
import org.databene.jdbacl.model.DBForeignKeyConstraint;
import org.databene.jdbacl.model.DBMetaDataImporter;
import org.databene.jdbacl.model.DBPrimaryKeyConstraint;
import org.databene.jdbacl.model.DBSchema;
import org.databene.jdbacl.model.DBTable;
import org.databene.jdbacl.model.DBUniqueConstraint;
import org.databene.jdbacl.model.Database;
import org.databene.jdbacl.model.cache.CachingDBImporter;
import org.databene.jdbacl.model.jdbc.JDBCDBImporter;
import org.databene.jdbacl.model.jdbc.JDBCMetaDataUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.TypeMapper;
import org.databene.script.PrimitiveType;
import org.databene.script.expression.ConstantExpression;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ConvertingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that serves as parent for classes which connect to databases using JDBC.<br/><br/>
 * Created: 07.01.2013 08:11:25
 * @since 0.8.0
 * @author Volker Bergmann
 */
public abstract class DBSystem extends AbstractStorageSystem {

	protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_FETCH_SIZE = 100;

	private static final VersionNumber MIN_ORACLE_VERSION = VersionNumber.valueOf("10.2.0.4");
	
	// constants -------------------------------------------------------------------------------------------------------
    
	private static final TypeDescriptor[] EMPTY_TYPE_DESCRIPTOR_ARRAY = new TypeDescriptor[0];
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private String id;
    private String environment;
    private String url;
    private String user;
    private String password;
    private String driver;
    private String catalogName;
    private String schemaName;
    private String includeTables;
    private String excludeTables;
    private boolean metaDataCache;
    protected boolean batch;
    protected boolean readOnly;
    private boolean lazy;
    private boolean acceptUnknownColumnTypes;
    
    private int fetchSize;

    protected Database database;
	protected DBMetaDataImporter importer;

    private OrderedNameMap<TypeDescriptor> typeDescriptors;
    protected Map<String, DBTable> tables;
    
    private TypeMapper driverTypeMapper;
    protected DatabaseDialect dialect;
    private boolean dynamicQuerySupported;
    
	private boolean connectedBefore;
	private AtomicInteger invalidationCount;
	
    // constructors ----------------------------------------------------------------------------------------------------

    public DBSystem(String id, String url, String driver, String user, String password, DataModel dataModel) {
    	this(id, dataModel);
        setUrl(url);
        setUser(user);
        setPassword(password);
        setDriver(driver);
        checkOracleDriverVersion(driver);
    }

    public DBSystem(String id, String environment, DataModel dataModel) {
    	this(id, dataModel);
        setEnvironment(environment);
    }

	private DBSystem(String id, DataModel dataModel) {
        setId(id);
        setDataModel(dataModel);
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
        this.driverTypeMapper = driverTypeMapper();
        this.connectedBefore = false;
        this.invalidationCount = new AtomicInteger();
    }

	// properties ------------------------------------------------------------------------------------------------------

    @Override
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
    	logger.debug("setting environment '{}'", environment);
		JDBCConnectData connectData = DBUtil.getConnectData(environment);
		this.environment = environment;
		this.url = connectData.url;
		this.driver = connectData.driver;
		this.catalogName = connectData.catalog;
		this.schemaName = connectData.schema;
		this.user = connectData.user;
		this.password = connectData.password;
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
    	return catalogName;
    }

	public void setCatalog(String catalog) {
    	this.catalogName = catalog;
    }

	public String getSchema() {
        return schemaName;
    }

    public void setSchema(String schema) {
        this.schemaName = StringUtil.emptyToNull(StringUtil.trim(schema));
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

	@Override
	public TypeDescriptor[] getTypeDescriptors() {
        logger.debug("getTypeDescriptors()");
        parseMetadataIfNecessary();
        if (typeDescriptors == null)
        	return EMPTY_TYPE_DESCRIPTOR_ARRAY;
        else
        	return CollectionUtil.toArray(typeDescriptors.values(), TypeDescriptor.class);
    }

    @Override
	public TypeDescriptor getTypeDescriptor(String tableName) {
        logger.debug("getTypeDescriptor({})", tableName);
        parseMetadataIfNecessary();
        return typeDescriptors.get(tableName);
    }

    // StorageSystem interface -----------------------------------------------------------------------------------------

    @Override
	public void store(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to insert rows into table '" + entity.type() + "' " +
					"though database '" + id + "' is read-only");
        logger.debug("Storing {}", entity);
        persistOrUpdate(entity, true);
    }

	@Override
	public void update(Entity entity) {
		if (readOnly)
			throw new IllegalStateException("Tried to update table '" + entity.type() + "' " +
					"though database '" + id + "' is read-only");
        logger.debug("Updating {}", entity);
        persistOrUpdate(entity, false);
	}

	@Override
	public void close() {
        if (database != null)
        	CachingDBImporter.updateCacheFile(database);
        IOUtil.close(importer);
	}
	
	public Entity queryEntityById(String tableName, Object id) {
        try {
	        logger.debug("queryEntityById({}, {})", tableName, id);
	        ComplexTypeDescriptor descriptor = (ComplexTypeDescriptor) getTypeDescriptor(tableName);
	        PreparedStatement query = getSelectByPKStatement(descriptor);
	        query.setObject(1, id); // TODO v0.7.6 support composite keys
	        ResultSet resultSet = query.executeQuery();
	        if (resultSet.next())
	        	return ResultSet2EntityConverter.convert(resultSet, descriptor);
	        else
	        	return null;
        } catch (SQLException e) {
	        throw new RuntimeException("Error querying " + tableName, e);
        }
    }

    @Override
	@SuppressWarnings("null")
    public DataSource<Entity> queryEntities(String type, String selector, Context context) {
        logger.debug("queryEntities({})", type);
    	Connection connection = getConnection();
        boolean script = false;
    	if (selector != null && selector.startsWith("{") && selector.endsWith("}")) {
    		selector = selector.substring(1, selector.length() - 1);
    		script = true;
    	}
    	String sql = null;
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
        DataSource<ResultSet> source = createQuery(sql, context, connection);
        return new EntityResultSetDataSource(source, (ComplexTypeDescriptor) getTypeDescriptor(type));
    }

    public long countEntities(String tableName) {
        logger.debug("countEntities({})", tableName);
        String query = "select count(*) from " + tableName;
        return DBUtil.queryLong(query, getConnection());
    }

    @Override
	public DataSource<?> queryEntityIds(String tableName, String selector, Context context) {
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
        return query(query, true, context);
    }

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public DataSource<?> query(String query, boolean simplify, Context context) {
        logger.debug("query({})", query);
        Connection connection = getConnection();
        QueryDataSource resultSetIterable = createQuery(query, context, connection);
        ResultSetConverter converter = new ResultSetConverter(Object.class, simplify);
		return new ConvertingDataSource<ResultSet, Object>(resultSetIterable, converter);
    }
    
    public Consumer inserter() {
    	return new StorageSystemInserter(this);
    }
    
    public Consumer inserter(String tableName) {
    	return new StorageSystemInserter(this, (ComplexTypeDescriptor) getTypeDescriptor(tableName));
    }
    
    // database-specific interface -------------------------------------------------------------------------------------

    public abstract Connection getConnection();
    
    protected abstract PreparedStatement getSelectByPKStatement(ComplexTypeDescriptor descriptor);

    public boolean tableExists(String tableName) {
        logger.debug("tableExists({})", tableName);
        return (getTypeDescriptor(tableName) != null);
    }

    public void createSequence(String name) throws SQLException {
		getDialect().createSequence(name, 1, getConnection());
    }

    public void dropSequence(String name) {
        execute(getDialect().renderDropSequence(name));
    }

    @Override
	public Object execute(String sql) {
    	try {
	        DBUtil.executeUpdate(sql, getConnection());
	        return null;
        } catch (SQLException e) {
	        throw new RuntimeException(e);
        }
    }
    
    public long nextSequenceValue(String sequenceName) {
    	return DBUtil.queryLong(getDialect().renderFetchSequenceValue(sequenceName), getConnection());
    }
    
    public void setSequenceValue(String sequenceName, long value) throws SQLException {
    	getDialect().setNextSequenceValue(sequenceName, value, getConnection());
    }
    
    protected Connection createConnection() {
		try {
            Connection connection = DBUtil.connect(url, driver, user, password, readOnly);
            if (!connectedBefore) {
            	DBUtil.logMetaData(connection);
            	connectedBefore = true;
            }
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
		tables = null;
		invalidationCount.incrementAndGet();
		if (environment != null) {
			File bufferFile = CachingDBImporter.getCacheFile(environment);
			if (bufferFile.exists()) {
				if (!bufferFile.delete() && metaDataCache) {
					logger.error("Deleting " + bufferFile + " failed");
					metaDataCache = false;
				} else
					logger.info("Deleted meta data cache file: " + bufferFile);

			}
		}
	} 
	
	public int invalidationCount() {
		return invalidationCount.get();
	}
	
	public void parseMetaData() {
        this.tables = new HashMap<String, DBTable>();
        this.typeDescriptors = OrderedNameMap.<TypeDescriptor>createCaseIgnorantMap();
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
        		DatabaseMetaData metaData = getConnection().getMetaData();
				String productName = metaData.getDatabaseProductName();
                VersionNumber productVersion = VersionNumber.valueOf(metaData.getDatabaseMajorVersion() + "." + 
                		metaData.getDatabaseMinorVersion());
				dialect = DatabaseDialectManager.getDialectForProduct(productName, productVersion);
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
				VersionNumber driverVersion = VersionNumber.valueOf(metaData.getDriverVersion());
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
		    importer = createJDBCImporter();
		    if (metaDataCache)
		    	importer = new CachingDBImporter((JDBCDBImporter) importer, getEnvironment());
		    database = importer.importDatabase();
		} catch (ConnectFailedException e) {
			throw new ConfigurationError("Database not available. ", e);
		} catch (ImportFailedException e) {
		    throw new ConfigurationError("Unexpected failure of database meta data import. ", e);
		}
	}

	private JDBCDBImporter createJDBCImporter() {
		JDBCDBImporter importer = JDBCMetaDataUtil.getJDBCDBImporter(getConnection(), user, schemaName, 
				true, false, false, false, includeTables, excludeTables);
		return importer;
	}

	private QueryDataSource createQuery(String query, Context context, Connection connection) {
	    return new QueryDataSource(connection, query, fetchSize, (dynamicQuerySupported ? context : null));
    }
      
    protected abstract PreparedStatement getStatement(ComplexTypeDescriptor descriptor, boolean insert, 
    		List<ColumnInfo> columnInfos);

    private void parseTable(DBTable table) {
        logger.debug("Parsing table {}" + table);
        String tableName = table.getName();
        tables.put(tableName.toUpperCase(), table);
        ComplexTypeDescriptor complexType;
        if (lazy) 
        	complexType = new LazyTableComplexTypeDescriptor(table, this);
        else
        	complexType = mapTableToComplexTypeDescriptor(table, new ComplexTypeDescriptor(tableName, this));
        typeDescriptors.put(tableName, complexType);
    }

	public ComplexTypeDescriptor mapTableToComplexTypeDescriptor(DBTable table, ComplexTypeDescriptor complexType) {
        // process primary keys
        DBPrimaryKeyConstraint pkConstraint = table.getPrimaryKeyConstraint();
        if (pkConstraint != null) {
	        String[] pkColumnNames = pkConstraint.getColumnNames();
	        if (pkColumnNames.length == 1) { // TODO v0.7.6 support composite primary keys
	        	String columnName = pkColumnNames[0];
	        	DBColumn column = table.getColumn(columnName);
				table.getColumn(columnName);
	            String abstractType = JdbcMetaTypeMapper.abstractType(column.getType(), acceptUnknownColumnTypes);
	        	IdDescriptor idDescriptor = new IdDescriptor(columnName, this, abstractType);
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
                DBDataType concreteType = fkColumn.getType();
                String abstractType = JdbcMetaTypeMapper.abstractType(concreteType, acceptUnknownColumnTypes);
                ReferenceDescriptor descriptor = new ReferenceDescriptor(
                        fkColumnName, 
                        this,
                        abstractType,
                        targetTable.getName(),
                        constraint.getRefereeColumnNames()[0]);
                descriptor.getLocalType(false).setSource(id);
                descriptor.setMinCount(new ConstantExpression<Long>(1L));
                descriptor.setMaxCount(new ConstantExpression<Long>(1L));
                boolean nullable = fkColumn.isNullable();
				descriptor.setNullable(nullable);
                complexType.setComponent(descriptor); // overwrite possible id descriptor for foreign keys
                logger.debug("Parsed reference " + table.getName() + '.' + descriptor);
            } else {
                // TODO v0.7.6 handle composite keys
            }
        }
        // process normal columns
        for (DBColumn column : table.getColumns()) {
        	try {
	            logger.debug("parsing column: {}", column);
	            String columnName = column.getName();
	            if (complexType.getComponent(columnName) != null)
	                continue; // skip columns that were already parsed (fk)
	            String columnId = table.getName() + '.' + columnName;
	            if (column.isVersionColumn()) {
	                logger.debug("Leaving out version column {}", columnId);
	                continue;
	            }
	            DBDataType columnType = column.getType();
	            String type = JdbcMetaTypeMapper.abstractType(columnType, acceptUnknownColumnTypes);
	            String defaultValue = column.getDefaultValue();
	            SimpleTypeDescriptor typeDescriptor = new SimpleTypeDescriptor(columnId, this, type);
	            if (defaultValue != null)
	                typeDescriptor.setDetailValue("constant", defaultValue);
	            if (column.getSize() != null)
	                typeDescriptor.setMaxLength(column.getSize());
	            if (column.getFractionDigits() != null) {
	            	if ("timestamp".equals(type))
	            		typeDescriptor.setGranularity("1970-01-02");
	            	else
	            		typeDescriptor.setGranularity(decimalGranularity(column.getFractionDigits()));
	            }
	            //typeDescriptors.put(typeDescriptor.getName(), typeDescriptor);
	            PartDescriptor descriptor = new PartDescriptor(columnName, this);
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
	                    // TODO v0.7.6 support uniqueness constraints on combination of columns
	                }
	            }
	            logger.debug("parsed attribute " + columnId + ": " + descriptor);
	            complexType.addComponent(descriptor);
        	} catch (Exception e) {
        		throw new ConfigurationError("Error processing column " + column.getName() + " of table " + table.getName(), e);
        	}
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
                DBDataType columnType = column.getType();
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

    public DBTable getTable(String tableName) {
    	parseMetadataIfNecessary();
        DBTable table = findTableInConfiguredCatalogAndSchema(tableName);
        if (table != null)
            return table;
        table = findAnyTableOfName(tableName);
        if (table != null) {
           	logger.warn("Table '" + tableName + "' not found " +
           			"in the expected catalog '" + catalogName + "' and schema '" + schemaName + "'. " +
   					"I have taken it from catalog '" + table.getCatalog() + "' and schema '" + table.getSchema() + "' instead. " +
   					"You better make sure this is right and fix the configuration");
            return table;
        }
        throw new ObjectNotFoundException("Table " + tableName);
    }

    private DBTable findAnyTableOfName(String tableName) {
        for (DBCatalog catalog : database.getCatalogs()) {
            for (DBSchema schema : catalog.getSchemas()) {
                DBTable table = schema.getTable(tableName);
                if (table != null)
                    return table;
            }
        }
        return null;
	}

	private DBTable findTableInConfiguredCatalogAndSchema(String tableName) {
        DBCatalog catalog = database.getCatalog(catalogName);
        if (catalog == null)
        	throw new ConfigurationError("Catalog '" + catalogName + "' not found in database '" + id + "'");
		DBSchema dbSchema = catalog.getSchema(schemaName);
        if (dbSchema != null) {
            DBTable table = dbSchema.getTable(tableName);
            if (table != null)
                return table;
        }
        return null;
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
                    boolean criticalOracleType = (dialect instanceof OracleDialect && (info.sqlType == Types.NCLOB || info.sqlType == Types.OTHER));
					if (jdbcValue != null || criticalOracleType) // Oracle is not able to perform setNull() on NCLOBs and NVARCHAR2
                        statement.setObject(i + 1, jdbcValue);
                    else
                        statement.setNull(i + 1, info.sqlType);
                } catch (SQLException e) {
                    throw new RuntimeException("error setting column " + tableName + '.' + info.name, e);
                }
            }
            if (batch) {
                statement.addBatch();
            } else {
                int rowCount = statement.executeUpdate();
                if (rowCount == 0)
                	throw new RuntimeException("Update failed because, since there is no database entry with the PK of " + entity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in persisting " + entity, e);
        }
	}
	
	private void parseMetadataIfNecessary() {
	    if (typeDescriptors == null)
            parseMetaData();
    }

    private String decimalGranularity(int scale) {
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

}
