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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.databene.commons.ConfigurationError;
import org.databene.commons.db.DBUtil;
import org.databene.id.IdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Unique keys efficiently by connecting a database, retrieving a (unique) sequence value 
 * and building sub keys of it.
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class SeqHiLoIdProvider implements IdProvider<Long> {
    
    protected static final int DEFAULT_MAX_LO = 100;

    private PreparedStatement statement;
    
    private String selector;
    private long maxLo;
    
    private int lo;
    private long hi;

    // constructors -----------------------------------------------------------------------------------

    public SeqHiLoIdProvider(Connection connection, String selector) {
        this(connection, selector, DEFAULT_MAX_LO);
    }
    
    public SeqHiLoIdProvider(Connection connection, String selector, long maxLo) {
        if (logger.isDebugEnabled())
            logger.debug("Instantiating " + getClass().getSimpleName() + '[' + selector + ']');
        try {
            this.selector = selector;
            this.maxLo = maxLo;
            this.statement = connection.prepareStatement(selector);
            this.hi = -1;
            this.lo = -1;
        } catch (SQLException e) {
            throw new ConfigurationError("Statement creation failed: " + selector, e);
        }
    }
    
    // IdProvider interface --------------------------------------------------------------------------------------------
    
    public Class<Long> getType() {
        return Long.class;
    }
    
    public boolean hasNext() {
        return (statement != null);
    }

    public Long next() {
        if (hi == -1 || lo >= maxLo) {
            hi = nextHi();
            lo = 0;
        } else
            lo++;
        return hi * (maxLo + 1) + lo;
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
        return getClass().getSimpleName() + '[' + selector + ',' + maxLo +']';
    }
    
    // implementation --------------------------------------------------------------------------------------------------
    
    private long nextHi() {
        if (sqlLogger.isDebugEnabled())
            sqlLogger.debug(selector);
        return Long.parseLong(DBUtil.queryString(statement));
    }

    private static final Logger logger = LoggerFactory.getLogger(SeqHiLoIdProvider.class);
    private static final Logger sqlLogger = LoggerFactory.getLogger("org.databene.SQL"); 
}
