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

import org.databene.commons.DBUtil;
import org.databene.commons.HeavyweightIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wraps a ResultSet into the semantic of a heavyweight iterator.
 * @see HeavyweightIterator<br/>
 * <br/>
 * Created: 15.08.2007 18:19:25
 */
public class ResultSetIterator implements HeavyweightIterator<ResultSet> {

    private static final Log logger = LogFactory.getLog(ResultSetIterator.class);

    private String query;

    private ResultSet resultSet;
    private Boolean hasNext;

    public ResultSetIterator(ResultSet resultSet, String query) {
        this.resultSet = resultSet;
        this.hasNext = null;
        this.query = query;
    }

    // interface -------------------------------------------------------------------------------------------------------

    public boolean hasNext() {
        if (logger.isDebugEnabled())
            logger.debug("hasNext() called on: " + this);
        if (hasNext != null)
            return hasNext;
        if (resultSet == null)
        	return false;
        try {
            hasNext = resultSet.next();
            if (!hasNext)
            	close();
            return hasNext;
        } catch (SQLException e) {
            throw new RuntimeException("Error in query: " + query, e);
        }
    }

    public ResultSet next() {
        if (logger.isDebugEnabled())
            logger.debug("next() called on: " + this);
        try {
            if (!hasNext())
                throw new IllegalStateException("No more row available");
            return resultSet;
        } finally {
            hasNext = null;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }

    public synchronized void close() {
        if (logger.isDebugEnabled())
            logger.debug("closing " + this);
        hasNext = false;
    	if (resultSet == null)
    		return;
        try {
            Statement statement = resultSet.getStatement();
            DBUtil.close(resultSet);
            resultSet = null;
            DBUtil.close(statement);
            statement = null;
        } catch (SQLException e) {
            logger.error(e, e);
        }
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + query + ']';
    }
}
