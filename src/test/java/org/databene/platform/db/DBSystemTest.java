/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.HeavyweightTypedIterable;
import org.databene.commons.TypedIterable;
import org.databene.jdbacl.DBUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.Entity;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;
import static org.databene.jdbacl.hsql.HSQLUtil.*;

/**
 * Tests {@link DBSystem}.<br/>
 * <br/>
 * Created at 26.12.2008 03:40:44
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class DBSystemTest {

	@Test
	public void testReadWrite() {
		db.setReadOnly(false);
		
		// test insert w/o readOnly
		db.store(new Entity("Test", "ID", 1, "NAME", "Alice"));
		
		// test update w/o readOnly
		db.update(new Entity("Test", "ID", 1, "NAME", "Bob"));
	}
	
	@Test
	public void testReadOnly() throws IOException {
		db.setReadOnly(true);

		// test select w/ readOnly
		HeavyweightTypedIterable<Object> result = db.query("select id from Test", null);
		result.iterator().close();

		// test insert w/ readOnly
		try {
			db.store(new Entity("Test", "ID", 2, "NAME", "Charly"));
			fail("Exception expected in store()");
		} catch (Exception e) {
			// That's the required behavior!
		}

		// test update w/ readOnly
		try {
			db.update(new Entity("Test", "ID", 2, "NAME", "Doris"));
			fail("Exception expected in update()");
		} catch (Exception e) {
			// That's the required behavior!
		}

		Connection connection = null;
		try {
			// test drop w/ readOnly in createStatement
			Statement statement = null;
			try {
				connection = db.createConnection();
				statement = connection.createStatement();
				statement.execute("drop table Test");
				fail("Exception expected in execute()");
			} catch (Exception e) {
				// That's the required behavior!
			} finally {
				DBUtil.close(statement);
			}
			
			// test drop w/ readOnly in prepareStatement
			try {
				connection = db.createConnection();
				connection.prepareStatement("drop table Test");
				fail("Exception expected in prepareStatement()");
			} catch (Exception e) {
				// That's the required behavior!
			}
		} finally {
			DBUtil.close(connection);
		}
	}

	@Test
	public void testSequence() throws Exception {
		String seq = getClass().getSimpleName();
		try { 
			db.createSequence(seq);
			assertEquals(1, db.nextSequenceValue(seq));
			assertEquals(2, db.nextSequenceValue(seq));
			db.setSequenceValue(seq, 5);
			assertEquals(5, db.nextSequenceValue(seq));
		} finally {
			db.dropSequence(seq);
		}
	}
	
	@Test
	public void testUpdater() throws Exception {
		db.execute("insert into TEST (ID, NAME) values (1, 'Alice')");
        TypedIterable<Entity> entities = db.queryEntities("TEST", "ID = 1", new BeneratorContext());
        Iterator<Entity> iterator = entities.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new Entity("TEST", "ID", 1, "NAME", "Alice"), iterator.next());
	}
	
	@Test
	public void testInserter() throws Exception {
        Consumer<Entity> inserter = db.inserter();
        Entity entity = new Entity("TEST", "ID", 1, "NAME", "Alice");
        inserter.startConsuming(entity);
        inserter.finishConsuming(entity);
        TypedIterable<Entity> entities = db.queryEntities("TEST", "ID = 1", new BeneratorContext());
        Iterator<Entity> iterator = entities.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new Entity("TEST", "ID", 1, "NAME", "Alice"), iterator.next());
	}
	
	@Test
	public void testInserter_table() throws Exception {
        Consumer<Entity> inserter = db.inserter("TEST");
        Entity entity = new Entity("Xyz", "ID", 1, "NAME", "Alice");
        inserter.startConsuming(entity);
        inserter.finishConsuming(entity);
        TypedIterable<Entity> entities = db.queryEntities("TEST", "ID = 1", new BeneratorContext());
        Iterator<Entity> iterator = entities.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new Entity("TEST", "ID", 1, "NAME", "Alice"), iterator.next());
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	private DBSystem db;
	
	@Before
	public void setUp() throws Exception {
		Connection connection = null;
		try {
			db = new DBSystem("db", IN_MEMORY_URL_PREFIX + "benerator", DRIVER, DEFAULT_USER, DEFAULT_PASSWORD);
			db.setSchema("public");
			connection = db.createConnection();
			try {
				DBUtil.executeUpdate("drop table Test", connection);
			} catch (SQLException e) {
				// ignore
			}
			DBUtil.executeUpdate("create table Test ( "
					+ "ID   int,"
					+ "NAME varchar(30) not null,"
					+ "constraint T1_PK primary key (ID)"
					+ ");", 
					connection);
			db.invalidate();
		} finally {
			DBUtil.close(connection);
		}
	}
	
}
