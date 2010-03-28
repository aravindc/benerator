/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import org.databene.commons.db.DBUtil;
import org.databene.platform.db.DatabaseDialect;

/**
 * {@link DatabaseDialect} implementation for the H2 database.
 * See <a href="http://www.h2database.com/html/grammar.html">H2 SQL grammar</a><br/><br/>
 * Created: 28.03.2010 07:54:19
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class H2Dialect extends DatabaseDialect {
    
	private static final String DATE_PATTERN = "''yyyy-MM-dd''";
	private static final String TIME_PATTERN = "''HH:mm:ss''";

    public H2Dialect() {
	    super("HSQL", false, true, DATE_PATTERN, TIME_PATTERN);
    }

	@Override
    public String[] querySequences(Connection connection) throws SQLException {
        String query = "select SEQUENCE_NAME from INFORMATION_SCHEMA.SYSTEM_SEQUENCES";
        // TODO restrict to catalog and schema, see http://www.h2database.com/html/grammar.html
        return DBUtil.queryScalarArray(query, String.class, connection);
	}

	@Override
    public String renderFetchSequenceValue(String sequenceName) {
        return "select next value for " + sequenceName;
    }

	@Override
	public void setSequenceValue(String sequenceName, long value, Connection connection) throws SQLException {
	    DBUtil.executeUpdate(setSequenceValue(sequenceName, value), connection);
	}

	public String setSequenceValue(String sequenceName, long value) {
	    return "alter sequence " + sequenceName + " restart with " + value;
    }
	
	@Override
	public String renderDropSequence(String name) {
		return "drop sequence " + name;
	}

}
