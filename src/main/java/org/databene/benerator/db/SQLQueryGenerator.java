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

package org.databene.benerator.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.platform.db.DBUtil;
import org.databene.platform.db.adapter.DBSystem;

/**
 * Creates long values from DB queries.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class SQLQueryGenerator implements Generator<Long> {
    
    private static final Log sqlLogger = LogFactory.getLog("org.databene.benerator.SQL"); 

    private String selector;
    
    private DBSystem source;
    
    private boolean dirty;
    
    private Connection connection;
    
    private PreparedStatement statement;
    
    public SQLQueryGenerator() {
        this(null, null);
    }
    
    public SQLQueryGenerator(DBSystem db, String selector) {
        this.source = db;
        this.selector = selector;
        this.dirty = true;
    }
    
    // properties -----------------------------------------------------------------------------------
    
    /**
     * @return the sequenceName
     */
    public String getSelector() {
        return selector;
    }

    /**
     * @param selector the sequenceName to set
     */
    public void setSelector(String selector) {
        this.selector = selector;
        this.dirty = true;
    }

    public DBSystem getSource() {
        return source;
    }

    public void setSource(DBSystem source) {
        this.source = source;
        this.dirty = true;
    }

    // Generator interface ------------------------------------------------------------------------------

    public Class<Long> getGeneratedType() {
        return Long.class;
    }

    public void validate() {
        if (dirty) {
            if (source == null)
                throw new InvalidGeneratorSetupException("source", "is null");
            if (selector == null)
                throw new InvalidGeneratorSetupException("selector", "is null");
            try {
                connection = source.createConnection();
                statement = connection.prepareStatement(selector);
            } catch (SQLException e) {
                throw new RuntimeException("Error in accessing sequence " + selector, e);
            }
            dirty = false;
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        return statement != null;
    }

    public Long generate() {
        if (dirty)
            validate();
        try {
            ResultSet rs = statement.executeQuery();
            rs.next();
            long value = rs.getLong(1);
            if (sqlLogger.isDebugEnabled())
                sqlLogger.debug(selector + " -> " + value);
            return value;
        } catch (SQLException e) {
            if (sqlLogger.isDebugEnabled())
                sqlLogger.debug(selector);
            throw new RuntimeException("Error in query: " + selector, e);
        }
    }

    public void close() {
        if (statement == null)
            return;
        DBUtil.close(statement);
        statement = null;
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error in commit", e);
        }
        DBUtil.close(connection);
        connection = null;
    }

    public void reset() {
    }

    // java.lang.Object overrides ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + selector + ']';
    }
}
