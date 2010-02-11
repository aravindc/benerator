/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.db.DBUtil;
import org.databene.platform.db.DatabaseDialect;

/**
 * Implements generic database concepts for HSQL<br/><br/>
 * Created: 26.01.2008 07:04:45
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class HSQLDialect extends DatabaseDialect {
    
    public HSQLDialect() {
	    super("HSQL", false, true);
    }

	@Override
    public String[] querySequences(Connection connection) throws SQLException {
        String query = "select sequence_name from information_schema.system_sequences";
        return DBUtil.queryScalarArray(query, String.class, connection);
	}

	@Override
    public String renderFetchSequenceValue(String sequenceName) {
        return "call next value for " + sequenceName;
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
