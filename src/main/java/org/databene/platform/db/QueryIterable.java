/*
 * (c) Copyright 2007-2013 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterable;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.NoOpConverter;
import org.databene.jdbacl.QueryIterator;
import org.databene.script.ScriptConverterForStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Creates Iterators for stepping through query results.<br/>
 * <br/>
 * Created: 17.08.2007 18:48:20
 * @author Volker Bergmann
 */
public class QueryIterable implements HeavyweightIterable<ResultSet> {
    
    private static final Logger logger = LoggerFactory.getLogger(QueryIterable.class); 

    private final Connection connection;
    private final String query;
    private final int fetchSize;
    
    private Converter<String, ?> queryPreprocessor;
    private String renderedQuery;

    public QueryIterable(Connection connection, String query, int fetchSize, Context context) {
        if (connection == null)
            throw new IllegalStateException("'connection' is null");
        if (StringUtil.isEmpty(query))
            throw new IllegalStateException("'query' is empty or null");
        this.connection = connection;
        this.query = query;
        this.fetchSize = fetchSize;
        if (context != null)
        	this.queryPreprocessor = new ScriptConverterForStrings(context);
        else
        	this.queryPreprocessor = new NoOpConverter<String>();
        if (logger.isDebugEnabled())
        	logger.debug("Constructed QueryIterable: " + query);
    }

    public String getQuery() {
        return query;
    }

    @Override
	public HeavyweightIterator<ResultSet> iterator() {
        renderedQuery = queryPreprocessor.convert(query).toString();
        return new QueryIterator(renderedQuery, connection, fetchSize);
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + (renderedQuery != null ? renderedQuery : query) + ']';
    }
    
}
