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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEventListener;

import org.databene.commons.BeanUtil;
import org.databene.commons.LogCategories;
import org.databene.commons.db.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a connection for logging of JDBC connection handling.<br/>
 * Created: 24.08.2008 18:47:44<br/>
 * @author Volker Bergmann
 * @since 0.5.5
 */
public class PooledConnectionHandler implements InvocationHandler {
    
    private static final Logger jdbcLogger = LoggerFactory.getLogger(LogCategories.JDBC);
    
    private static long nextId = 0;

    private DBSystem db;
    private Connection realConnection;
    private long id;
    
    // construction ----------------------------------------------------------------------------------------------------
    
    public PooledConnectionHandler(DBSystem db, Connection realConnection) {
    	this.db = db;
        this.id = nextId();
        this.realConnection = realConnection;
        this.listeners = new ArrayList<ConnectionEventListener>();
        if (jdbcLogger.isDebugEnabled())
            jdbcLogger.debug("Created connection #" + id + ": " + realConnection);
    }

    // InvocationHandler implementation --------------------------------------------------------------------------------
    
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if ("close".equals(methodName))
			this.close();
		else if ("getConnection".equals(methodName) && args.length == 0)
			return this.getConnection();
		else if ("addConnectionEventListener".equals(methodName)) {
			this.addConnectionEventListener((ConnectionEventListener) args[0]);
			return null;
		} else if ("removeConnectionEventListener".equals(methodName)) {
			this.removeConnectionEventListener((ConnectionEventListener) args[0]);
			return null;
		} else if ("prepareStatement".equals(methodName)) {
			switch (args.length) {
				case 1: return DBUtil.prepareStatement(realConnection, (String) args[0], db.isReadOnly());
				case 2: 
					if (method.getParameterTypes()[1] == int.class)
						return DBUtil.prepareStatement(realConnection, (String) args[0], db.isReadOnly(),
								(Integer) args[1], ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
					else
						break;
				case 3: return DBUtil.prepareStatement(realConnection, (String) args[0], db.isReadOnly(),
						(Integer) args[1], (Integer) args[2], ResultSet.HOLD_CURSORS_OVER_COMMIT);
				case 4: return DBUtil.prepareStatement(realConnection, (String) args[0], db.isReadOnly(), 
						(Integer) args[1], (Integer) args[2], (Integer) args[3]);
			}
		} else if ("createStatement".equals(methodName))
			return createStatement(method, args);
		return BeanUtil.invoke(realConnection, method, args);
	}

	private Statement createStatement(Method method, Object[] args) {
		Statement realStatement = (Statement) BeanUtil.invoke(realConnection, method, args);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Statement proxy = (Statement) Proxy.newProxyInstance(classLoader, 
				new Class[] { Statement.class }, 
				new LoggingStatementHandler(realStatement, db.isReadOnly()));
		return proxy;
	}

	// PooledConnection implementation ---------------------------------------------------------------------------------
    
	public void close() throws SQLException {
        try {
            realConnection.close();
            listeners.clear();
            if (jdbcLogger.isDebugEnabled())
                jdbcLogger.debug("Closed connection #" + id + ": " + realConnection);
        } catch (SQLException e) {
            jdbcLogger.error("Error closing connection #" + id + ": " + realConnection, e);
            throw e;
        }
    }

    public Connection getConnection() {
        return realConnection;
    }

    private List<ConnectionEventListener> listeners;
    
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static synchronized long nextId() {
        return ++nextId;
    }
    
}
