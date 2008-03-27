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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Tests the DBHiLoIterator.<br/><br/>
 * Created: 27.01.2008 10:47:39
 * @since 0.4.0
 * @author Volker Bergmann
 */
public class SeqHiLoProviderTest extends TestCase {

    private static final String SELECTOR = "select nextval from seq";

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet[] resultSets;
    
    // test methods ----------------------------------------------------------------------------------------------------
    
    public void testMaxLo2() throws SQLException {
        prepare(SELECTOR, 1, 2);
        SeqHiLoIdProvider iterator = new SeqHiLoIdProvider(connection, SELECTOR, 2);
        expectSequence(iterator, 3, 4, 5, 6);
        iterator.close();
        assertFalse(iterator.hasNext());
    }
    
    public void testMaxLo100() throws SQLException {
        prepare(SELECTOR, 1);
        SeqHiLoIdProvider iterator = new SeqHiLoIdProvider(connection, SELECTOR, 100);
        expectSequence(iterator, 101, 102, 103, 104);
        iterator.close();
        assertFalse(iterator.hasNext());
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void prepare(String sqlQuery, long ... queryResults) throws SQLException {
        connection = EasyMock.createStrictMock(Connection.class);
        statement = EasyMock.createStrictMock(PreparedStatement.class);
        EasyMock.expect(connection.prepareStatement(sqlQuery)).andReturn(statement);
        connection.close();
        EasyMock.replay(connection);

        resultSets = new ResultSet[queryResults.length];
        for (int i = 0; i < resultSets.length; i++) {
            resultSets[i] = EasyMock.createStrictMock(ResultSet.class);
            EasyMock.expect(statement.executeQuery()).andReturn(resultSets[i]);
            EasyMock.expect(resultSets[i].next()).andReturn(true);
            EasyMock.expect(resultSets[i].getString(1)).andReturn(String.valueOf(queryResults[i]));
            EasyMock.expect(resultSets[i].next()).andReturn(false);
            resultSets[i].close();
            EasyMock.replay(resultSets[i]);
        }
        statement.close();
        EasyMock.replay(statement);
    }

    private void expectSequence(Iterator<Long> iterator, long ... values) {
        for (long expectedValue : values) {
            assertTrue(iterator.hasNext());
            assertEquals(expectedValue, iterator.next().longValue());
        }
    }
}
