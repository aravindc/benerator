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

package org.databene.platform.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.ConnectionEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.LogCategories;
import org.databene.commons.BeanUtil;

/**
 * Wraps a connection for logging of JDBC connection handling.<br/>
 * Created: 24.08.2008 18:47:44<br/>
 * @author Volker Bergmann
 * @since 0.5.5
 */
public class PooledConnectionHandler implements InvocationHandler {
    
    private static final Log jdbcLogger = LogFactory.getLog(LogCategories.JDBC);
    
    private static long nextId = 0;

    private Connection realConnection;
    private Class<? extends Connection> realConnectionClass;
    private long id;
    
    // construction ----------------------------------------------------------------------------------------------------
    
    public PooledConnectionHandler(Connection realConnection) {
        this.id = nextId();
        this.realConnection = realConnection;
        this.realConnectionClass = realConnection.getClass();
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
		else if ("getConnection".equals(methodName))
			return this.getConnection();
		else if ("addConnectionEventListener".equals(methodName))
			this.addConnectionEventListener((ConnectionEventListener) args[0]);
		else if ("removeConnectionEventListener".equals(methodName))
			this.removeConnectionEventListener((ConnectionEventListener) args[0]);
		else
			return BeanUtil.invoke(realConnection, method, args);
		return null;
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

    public Connection getConnection() throws SQLException {
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
