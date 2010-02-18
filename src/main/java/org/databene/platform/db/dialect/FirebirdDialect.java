/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import org.databene.commons.StringUtil;
import org.databene.commons.db.DBUtil;
import org.databene.platform.db.DatabaseDialect;

/**
 * {@link DatabaseDialect} implementation for the Firebird database.<br/>
 * <br/>
 * Created at 09.03.2009 07:13:35
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class FirebirdDialect extends DatabaseDialect {

	private static final String DATE_PATTERN = "''yyyy-MM-dd''";
	private static final String TIME_PATTERN = "''HH:mm:ss''";

    public FirebirdDialect() {
	    super("Firebird", true, true, DATE_PATTERN, TIME_PATTERN);
    }

    public String getJDBCDriverClass() {
    	return "org.firebirdsql.jdbc.FBDriver";
    }
    
    @Override
    public void createSequence(String name, long initialValue, Connection connection) throws SQLException {
    	DBUtil.executeUpdate(renderCreateSequence(name), connection);
    	DBUtil.executeUpdate(renderSetSequenceValue(name, initialValue), connection);
    }
    
    public String renderCreateSequence(String name) {
        return "create generator " + name;
    }
    
    @Override
    public String renderDropSequence(String sequenceName) {
        return "drop generator " + sequenceName;
    }
    
    @Override
    public String renderFetchSequenceValue(String sequenceName) {
        return "select gen_id(" + sequenceName + ", 1) from RDB$DATABASE;";
    }
    
    @Override
    public String[] querySequences(Connection connection) throws SQLException {
        String query = "select * from RDB$GENERATORS where RDB$GENERATOR_NAME NOT LIKE '%$%'";
        String[] sequences = DBUtil.queryScalarArray(query, String.class, connection);
		return StringUtil.trimAll(sequences);
    }
    
    @Override
    public void setSequenceValue(String sequenceName, long value, Connection connection) throws SQLException {
    	DBUtil.executeUpdate("set generator " + sequenceName + " to " + value, connection);
    }
    
    public String renderSetSequenceValue(String sequenceName, long value) {
        return "set generator " + sequenceName + " to " + (value - 1);
    }

}
