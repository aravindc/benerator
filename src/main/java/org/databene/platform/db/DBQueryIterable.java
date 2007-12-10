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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.StringUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Iterator;

/**
 * Creates Iterators for stepping through query results.<br/>
 * <br/>
 * Created: 17.08.2007 18:48:20
 */
public class DBQueryIterable implements Iterable<ResultSet> {
    
    private static final Log sqlLogger = LogFactory.getLog("org.databene.benerator.SQL"); 

    private Connection connection;
    private String query;
    private int fetchSize;

    public DBQueryIterable(Connection connection) {
        this(connection, null, 100);
    }

    public DBQueryIterable(Connection connection, String query) {
        this(connection, query, 100);
    }

    public DBQueryIterable(Connection connection, String query, int fetchSize) {
        this.connection = connection;
        this.query = query;
        this.fetchSize = fetchSize;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Iterator<ResultSet> iterator() {
        if (connection == null)
            throw new IllegalStateException("'connection' is null");
        if (StringUtil.isEmpty(query))
            throw new IllegalStateException("'query' is empty or null");
        try {
            Statement statement = connection.createStatement();
            statement.setFetchSize(fetchSize);
            ResultSet resultSet = statement.executeQuery(query);
            if (sqlLogger.isDebugEnabled())
                sqlLogger.debug(query);
            return new ResultSetIterator(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Error in query: " + query, e);
        }
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + query + ']';
    }
}
