/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.databene.commons.ConfigurationError;
import org.databene.id.IdProvider;

/**
 * Iterates the results of a SQL query.<br/><br/>
 * Created: 27.01.2008 18:40:36
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class QueryIdProvider implements IdProvider<String> {
    
    private PreparedStatement statement;
    
    private String query;

    // constructors ----------------------------------------------------------------------------------------------------

    public QueryIdProvider(Connection connection, String query) {
        try {
            this.query = query;
            this.statement = connection.prepareStatement(query);
        } catch (SQLException e) {
            throw new ConfigurationError("Statement creation failed: " + query, e);
        }
    }
    
    // HeavyweightIterator interface -----------------------------------------------------------------------------------
    
    public Class<String> getType() {
        return String.class;
    }
    
    public boolean hasNext() {
        return (statement != null);
    }

    public String next() {
        return DBUtil.queryWithOneCellResult(statement);
    }

    public void remove() {
        throw new UnsupportedOperationException("Removal is not supported.");
    }
    
    public void close() {
        DBUtil.close(statement);
        this.statement = null;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + query + ']';
    }

}
