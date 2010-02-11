/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.databene.commons.ConnectFailedException;
import org.databene.commons.DatabeneTestUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.db.DBUtil;
import org.databene.commons.db.JDBCConnectData;
import org.databene.platform.db.DatabaseDialect;
import org.junit.Test;

/**
 * Parent class for testing concrete {@link DatabaseDialect} implementations.<br/><br/>
 * Created: 10.11.2009 16:59:14
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class DatabaseDialectTest {
	
	protected DatabaseDialect dialect;
	
	public DatabaseDialectTest(DatabaseDialect dialect) {
	    this.dialect = dialect;
    }

	@Test
	public void testSequenceSetupConsistency() {
		boolean supported = dialect.isSequenceSupported();
		if (supported)
			assertSequenceSupported();
		else
			assertSequenceNotSupported();
	}

	private void assertSequenceSupported() {
	    String sequence = "DUMMY_SEQ";
	    dialect.renderFetchSequenceValue(sequence);
	    dialect.renderDropSequence(sequence);
    }
	
	private void assertSequenceNotSupported() {
	    String sequence = "DUMMY_SEQ";
	    try {
	    	dialect.renderFetchSequenceValue(sequence);
	    	fail("UnsupportedOperationException expected");
	    } catch (UnsupportedOperationException e) {
	    	// this is expected
	    }
	    try {
	    	dialect.renderDropSequence(sequence);
	    	fail("UnsupportedOperationException expected");
	    } catch (UnsupportedOperationException e) {
	    	// this is expected
	    }
    }
	
	protected void testSequencesOnline(String databaseId) throws SQLException, ConnectFailedException {
		JDBCConnectData data = DatabeneTestUtil.getConnectData(databaseId);
		if (data == null) {
			System.out.println("Skipping test: testSequencesOnline(" + databaseId + ")");
			return;
		}
		String sequenceName = getClass().getSimpleName();
		Connection connection = DBUtil.connect(data);
		try {
			dialect.createSequence(sequenceName, 23, connection);
			String[] sequences = dialect.querySequences(connection);
			assertTrue(StringUtil.containsIgnoreCase(sequenceName, sequences));
			assertEquals(23L, DBUtil.queryLong(dialect.renderFetchSequenceValue(sequenceName), connection));
		} finally {
			DBUtil.executeUpdate(dialect.renderDropSequence(sequenceName), connection);
			DBUtil.close(connection);
		}
	}
	
}
