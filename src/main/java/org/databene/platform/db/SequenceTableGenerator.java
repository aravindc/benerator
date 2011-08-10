/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.GeneratorState;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.util.UnsafeNonNullGenerator;
import org.databene.commons.IOUtil;
import org.databene.jdbacl.DBUtil;
import org.databene.script.ScriptUtil;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;

/**
 * Uses a database table to fetch and increment values like a database sequence.<br/><br/>
 * Created: 09.08.2010 14:44:06
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SequenceTableGenerator<E extends Number> extends UnsafeNonNullGenerator<E> {
	
	private String table;
	private String column;
	private DBSystem database;
	private String selector;
	protected Long increment = 1L;
	
	private String query;
	private IncrementorStrategy incrementorStrategy;
	private PreparedStatement parameterizedAccessorStatement;
	
    public SequenceTableGenerator() {
    	this(null, null, null);
    }
    
    public SequenceTableGenerator(String table, String column, DBSystem db) {
    	this(table, column, db, null);
    }
    
    public SequenceTableGenerator(String table, String column, DBSystem db, String selector) {
    	this.table = table;
	    this.column = column;
	    this.database = db;
	    this.selector = selector;
    }
    
	public void setTable(String table) {
    	this.table = table;
    }

	public void setColumn(String column) {
    	this.column = column;
    }
	
	public void setDatabase(DBSystem db) {
		this.database = db;
	}

	public void setSelector(String selector) {
	    this.selector = selector;
    }

	// Generator interface implementation ------------------------------------------------------------------------------
	
    @SuppressWarnings("unchecked")
    public Class<E> getGeneratedType() {
        return (Class<E>) Number.class;
    }

	@Override
    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
        // check preconditions
        assertNotInitialized();
        if (database == null)
        	throw new InvalidGeneratorSetupException("db is null");
        
        // initialize
        query = "select " + column + " from " + table;
        if (selector != null)
        	query = ScriptUtil.combineScriptableParts(query, " where ", selector);
    	incrementorStrategy = createIncrementor();
        super.init(context);
    }

	private IncrementorStrategy createIncrementor() {
        if (increment == null)
        	return null;
    	String incrementorSql = "update " + table + " set " + column + " = ?";
    	if (selector != null)
    		incrementorSql = ScriptUtil.combineScriptableParts(incrementorSql, " where ", selector);
    	if (selector == null || !ScriptUtil.isScript(selector)) 
    		return new PreparedStatementStrategy(incrementorSql, database);
    	else
    		return new StatementStrategy(incrementorSql, database);
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public E generate() {
		if (this.state == GeneratorState.CLOSED)
			return null;
		assertInitialized();
		DataSource<?> iterable = database.query(query, true, context);
		DataIterator<?> iterator = null;
		E result;
		try {
			iterator = iterable.iterator();
			DataContainer<?> container = iterator.next(new DataContainer());
			if (container == null) {
				close();
				return null;
			}
			result = (E) container.getData();
			incrementorStrategy.run(result.longValue(), (BeneratorContext) context);
		} finally {
			IOUtil.close(iterator);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "cast" })
	public E generateWithParams(Object... params) {
		if (this.state == GeneratorState.CLOSED)
			return null;
		ResultSet resultSet = null;
		E result = null;
		try {
			if (parameterizedAccessorStatement == null) {
				String queryText = String.valueOf(ScriptUtil.parseUnspecificText(query).evaluate(context));
				parameterizedAccessorStatement = database.getConnection().prepareStatement(queryText);
			}
			for (int i = 0; i < params.length; i++)
				parameterizedAccessorStatement.setObject(i + 1, params[i]);
			resultSet = parameterizedAccessorStatement.executeQuery();
			if (!resultSet.next()) {
				close();
				return null;
			}
			result = (E) resultSet.getObject(1);
			incrementorStrategy.run(result.longValue(), (BeneratorContext) context, params);
		} catch (SQLException e) {
			throw new RuntimeException("Error fetching value in " + getClass().getSimpleName(), e);
		} finally {
			DBUtil.close(resultSet);
		}
		return (E) result;
	}
	
	@Override
	public void close() {
		IOUtil.close(incrementorStrategy);
		DBUtil.close(parameterizedAccessorStatement);
		super.close();
	}
	
	@Override
    public String toString() {
        return getClass().getSimpleName() + "[" + selector + "]";
    }
	
	// IncrementorStrategy ---------------------------------------------------------------------------------------------

    interface IncrementorStrategy extends Closeable {
    	void run(long currentValue, BeneratorContext context, Object... params);
    	void close();
    }

    class PreparedStatementStrategy implements IncrementorStrategy {
    	
    	private PreparedStatement statement;

	    public PreparedStatementStrategy(String incrementorSql, DBSystem db) {
	    	try {
	            statement = db.getConnection().prepareStatement(incrementorSql);
            } catch (SQLException e) {
            	throw new RuntimeException(e);
            }
        }

		public void run(long currentValue, BeneratorContext context, Object... params) {
		    try {
		    	statement.setLong(1, currentValue + increment);
		    	for (int i = 0; i < params.length; i++)
		    		statement.setObject(2 + i, params[i]);
		    	statement.executeUpdate();
	        } catch (SQLException e) {
		        throw new RuntimeException(e);
	        }
	    }

		public void close() {
			DBUtil.close(statement);
		}
    }

    class StatementStrategy implements IncrementorStrategy {
    	
    	private Statement statement;
    	private String sql;

	    public StatementStrategy(String sql, DBSystem db) {
	        try {
	            this.statement = db.getConnection().createStatement();
	            this.sql = sql;
            } catch (SQLException e) {
            	throw new RuntimeException(e);
            }
        }

		public void run(long currentValue, BeneratorContext context, Object... params) {
			try {
	            String cmd = sql.replace("?", String.valueOf(currentValue + increment));
	            cmd = ScriptUtil.parseUnspecificText(cmd).evaluate(context).toString();
	            statement.executeUpdate(cmd);
            } catch (SQLException e) {
            	throw new RuntimeException(e);
            }
	    }

		public void close() {
			DBUtil.close(statement);
		}
    }

}
