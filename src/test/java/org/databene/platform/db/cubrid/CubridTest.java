/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.cubrid;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.commons.Encodings;
import org.databene.commons.TimeUtil;
import org.databene.jdbacl.DBUtil;
import org.databene.model.data.Entity;
import org.databene.platform.db.DBSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CUBRID access.<br/><br/>
 * Created: 18.04.2012 06:20:56
 * @since 0.7.7
 * @author Volker Bergmann
 */
public class CubridTest extends BeneratorIntegrationTest {
	
	private static final String ENVIRONMENT = "cubrid";
	
	private static final String DIRECTORY = CubridTest.class.getPackage().getName().replace('.', File.separatorChar);
	private static final String SETUP_FILENAME = DIRECTORY + File.separatorChar + "create_tables.sql";
	private static final String TEARDOWN_FILENAME = DIRECTORY + File.separatorChar + "drop_tables.sql";
	
	private Connection connection;
	/*
	@Before
	public void setUpDB() throws Exception {
		connection = DBUtil.connect(ENVIRONMENT, false);
		DBUtil.executeScriptFile(SETUP_FILENAME, Encodings.UTF_8, connection, true, null);
		DBSystem db = new DBSystem("db", ENVIRONMENT, dataModel);
		context.getDataModel().addDescriptorProvider(db);
		context.set("db", db);
	}
	
	@After
	public void tearDownDB() throws Exception {
		DBUtil.executeScriptFile(TEARDOWN_FILENAME, Encodings.UTF_8, connection, true, null);
		DBUtil.close(connection);
	}
	
	@Test
	public void testStringCreation() throws Exception {
		parseAndExecute("<generate type='chartest' count='5' consumer='ConsoleExporter, db' />");
		List<Object[]> rows = DBUtil.query("select * from chartest", connection);
		assertEquals(5, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(4, row.length);
			for (int colnum = 0; colnum < row.length; colnum++) {
				assertTrue(row[colnum] instanceof String);
				assertTrue(((String) row[colnum]).length() > 0);
			}
		}
	}
	
	@Test
	public void testStringIteration() throws Exception {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("list", consumer);
		DBUtil.executeUpdate("insert into chartest (char_col, varchar_col, nchar_col, nchar_varying_col) values ('', '', '', '')", connection);
		DBUtil.executeUpdate("insert into chartest (char_col, varchar_col, nchar_col, nchar_varying_col) values ('a', 'b', 'c', 'd')", connection);
		DBUtil.executeUpdate("insert into chartest (char_col, varchar_col, nchar_col, nchar_varying_col) values ('aaa', 'bbb', 'ccc', 'ddd')", connection);
		parseAndExecute("<iterate type='chartest' consumer='list' />");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(3, products.size());
		assertComponents(products.get(0), "char_col", "", "varchar_col", "", "nchar_col", "", "nchar_varying_col", "");
		assertComponents(products.get(1), "char_col", "a", "varchar_col", "b", "nchar_col", "c", "nchar_varying_col", "d");
		assertComponents(products.get(2), "char_col", "aaa", "varchar_col", "bbb", "nchar_col", "ccc", "nchar_varying_col", "ddd");
	}
	
	@Test
	public void testNumberCreation() throws Exception {
		parseAndExecute("<generate type='numtest' count='5' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from numtest", connection);
		assertEquals(5, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(7, row.length);
			assertTrue(row[0] instanceof Short);
			assertTrue(row[1] instanceof Integer);
			assertTrue(row[2] instanceof BigInteger);
			assertTrue(row[3] instanceof BigDecimal);
			assertTrue(row[4] instanceof Float);
			assertTrue(row[5] instanceof Double);
			assertTrue(row[6] instanceof Double);
		}
	}
	
	@Test
	public void testNumberIteration() throws Exception {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("list", consumer);
		DBUtil.executeUpdate("insert into numtest (smallint_col, int_col, bigint_col, decimal_col, real_col, double_col, monetary_col) values ( 0,  0,  0,    0,    0,    0,     0)", connection);
		DBUtil.executeUpdate("insert into numtest (smallint_col, int_col, bigint_col, decimal_col, real_col, double_col, monetary_col) values ( 1,  1,  1,  1.5,  1.5,  1.5,  1.51)", connection);
		DBUtil.executeUpdate("insert into numtest (smallint_col, int_col, bigint_col, decimal_col, real_col, double_col, monetary_col) values (-1, -1, -1, -1.5, -1.5, -1.5, -1.51)", connection);
		parseAndExecute("<iterate type='chartest' consumer='list' />");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(3, products.size());
		assertComponents(products.get(0), "smallint_col", (short)  0, "int_col",  0, "bigint_col", new BigInteger( "0"), "decimal_col", new BigDecimal(   "0"), "real_col", (float)    0, "double_col",   0., "monetary_col",     0);
		assertComponents(products.get(0), "smallint_col", (short)  1, "int_col",  1, "bigint_col", new BigInteger( "1"), "decimal_col", new BigDecimal( "1.5"), "real_col", (float)  1.5, "double_col",  1.5, "monetary_col",  1.51);
		assertComponents(products.get(0), "smallint_col", (short) -1, "int_col", -1, "bigint_col", new BigInteger("-1"), "decimal_col", new BigDecimal("-1.5"), "real_col", (float) -1.5, "double_col", -1.5, "monetary_col", -1.51);
	}
	
	@Test
	public void testLOBCreation() throws Exception {
		parseAndExecute("<generate type='lobtest' count='5' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from chartest", connection);
		assertEquals(5, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(2, row.length);
			for (int colnum = 0; colnum < row.length; colnum++) {
				assertTrue(row[colnum] instanceof String);
				assertTrue(((String) row[colnum]).length() > 0);
			}
		}
	}
	
	@Test
	public void testLOBIteration() throws SQLException {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("list", consumer);
		DBUtil.executeUpdate("insert into lobtest (clob_col, blob_col) values ('', TODO)", connection);
		DBUtil.executeUpdate("insert into lobtest (clob_col, blob_col) values ('a', TODO)", connection);
		DBUtil.executeUpdate("insert into lobtest (clob_col, blob_col) values ('aaa', TODO)", connection);
		parseAndExecute("<iterate type='lobtest' consumer='list' />");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(3, products.size());
		assertComponents(products.get(0), "clob_col",    "", "blob_col", new byte[0]);
		assertComponents(products.get(1), "clob_col",   "a", "blob_col", new byte[] { 1 });
		assertComponents(products.get(2), "clob_col", "aaa", "blob_col", new byte[] { 1, 2, 3 });
	}
	
	@Test
	public void testDateTimeCreation() throws Exception {
		parseAndExecute("<generate type='datetimetest' count='5' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from chartest", connection);
		assertEquals(5, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(4, row.length);
			for (int colnum = 0; colnum < row.length; colnum++) {
				assertTrue(row[colnum] instanceof String);
				assertTrue(((String) row[colnum]).length() > 0);
			}
		}
	}
	
	@Test
	public void testDateTimeIteration() throws Exception {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("list", consumer);
		DBUtil.executeUpdate("insert into datetimetest (date_col, time_col, timestamp_col, datetime_col) values ('2012-04-18', '00:00:00', '2012-04-18 00:00:00' '2012-04-18 00:00:00')", connection);
		DBUtil.executeUpdate("insert into datetimetest (date_col, time_col, timestamp_col, datetime_col) values ('1970-01-01', '00:00:00', '1970-01-01 00:00:00' '1970-01-01 00:00:00')", connection);
		DBUtil.executeUpdate("insert into datetimetest (date_col, time_col, timestamp_col, datetime_col) values ('2012-02-29', '23:59:59', '2012-04-29 23:59:59', '2012-04-29 23:59:59')", connection);
		parseAndExecute("<iterate type='chartest' consumer='list' />");
		@SuppressWarnings("unchecked")
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(3, products.size());
		assertComponents(products.get(0), "date_col", TimeUtil.date(2012, 3, 18), "time_col", TimeUtil.time( 0,  0,  0), "timestamp_col", TimeUtil.timestamp(2012, 3, 18,  0,  0,  0, 0), "datetime_col", TimeUtil.date(2012, 3, 18,  0,  0,  0, 0));
		assertComponents(products.get(1), "date_col", TimeUtil.date(1970, 0,  1), "time_col", TimeUtil.time( 0,  0,  0), "timestamp_col", TimeUtil.timestamp(1970, 0,  1,  0,  0,  0, 0), "datetime_col", TimeUtil.date(1970, 0,  1,  0,  0,  0, 0));
		assertComponents(products.get(2), "date_col", TimeUtil.date(2012, 1, 29), "time_col", TimeUtil.time(23, 59, 59), "timestamp_col", TimeUtil.timestamp(2012, 1, 29, 23, 59, 59, 0), "datetime_col", TimeUtil.date(2012, 1, 29, 23, 59, 59, 0));
	}
	
	@Test
	public void testPKCreation() throws Exception {
		parseAndExecute("<generate type='pktest' count='100' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from pktest", connection);
		assertEquals(100, rows.size());
		HashSet<Integer> keys = new HashSet<Integer>();
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(1, row.length);
			assertTrue(keys.add((Integer) row[0]));
		}
	}
	
	@Test
	public void testUniqueCreation() throws Exception {
		parseAndExecute("<generate type='uktest' count='100' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from uktest", connection);
		assertEquals(100, rows.size());
		HashSet<Integer> keys = new HashSet<Integer>();
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(1, row.length);
			assertTrue(keys.add((Integer) row[0]));
		}
	}
	*/
	@Test
	public void testFKCreation() {
		// TODO implement
	}
	/*
	@Test
	public void testNullCreation() throws Exception {
		parseAndExecute("<generate type='nulltest' count='100' consumer='db' />");
		List<Object[]> rows = DBUtil.query("select * from nulltest", connection);
		assertEquals(100, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] row = rows.get(i);
			assertNotNull(row);
			assertEquals(2, row.length);
			assertNotNull(row[0]);
			assertNull(row[1]);
		}
	}
	*/
}
