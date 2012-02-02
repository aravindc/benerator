/*
 * (c) Copyright 2009-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.assertEquals;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.commons.ConfigurationError;
import org.databene.jdbacl.dialect.HSQLUtil;
import org.databene.platform.db.DBSystem;
import org.junit.Test;

/**
 * Tests the {@link EvaluateParser} with respect to the features used 
 * in the &lt;execute&gt; element.<br/><br/>
 * Created: 30.10.2009 08:11:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExecuteParserAndStatementTest extends BeneratorIntegrationTest {

	@Test
	public void testBeanInvocation() throws Exception {
		Statement statement = parse("<execute>bean.invoke(2)</execute>");
		BeanMock bean = new BeanMock();
		context.set("bean", bean);
		statement.execute(context);
		assertEquals(1, bean.invocationCount);
		assertEquals(2, bean.lastValue);
	}

	@Test
	public void testSimpleTypeVariableDefinition() throws Exception {
		Statement statement = parse("<execute>x = 3</execute>");
		statement.execute(context);
		assertEquals(3, context.get("x"));
	}
	
	@Test
	public void testSimpleTypeVariableAccess() throws Exception {
		context.set("x", 3);
		Statement statement = parse("<execute>x = x + 2</execute>");
		statement.execute(context);
		assertEquals(5, context.get("x"));
	}
	
	@Test(expected = ConfigurationError.class)
	public void testSqlExecutionWithoutTarget() throws Exception {
		Statement statement = parse("<execute type='sql'>create sequence seq</execute>");
		statement.execute(context);
	}

	@Test
	public void testEmptyResultSet() throws Exception {
		String url = HSQLUtil.getInMemoryURL("benerator");
		DBSystem db = new DBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
		BeneratorContext context = new DefaultBeneratorContext();
		context.set("db", db);
		try {
			db.execute("create table epast_test (id int)");
			Statement statement = parse("<execute target='db'>select * from epast_test where 1 = 0</execute>");
			statement.execute(context);
		} finally {
			db.execute("drop table epast_test");
			db.close();
		}
	}

	@Test
	public void testDbInvalidationDefault() throws Exception {
		String url = HSQLUtil.getInMemoryURL("benerator");
		DBSystem db = new DBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
		BeneratorContext context = new DefaultBeneratorContext();
		context.set("db", db);
		assertEquals(0, db.invalidationCount());
		try {
			db.execute("create table epast_test (id int)");
			Statement statement = parse("<execute target='db'>select * from epast_test where 1 = 0</execute>");
			statement.execute(context);
			assertEquals(0, db.invalidationCount());
			Statement statement2 = parse("<execute target='db'>create table BBB (id int)</execute>");
			statement2.execute(context);
			assertEquals(1, db.invalidationCount());
		} finally {
			db.execute("drop table epast_test");
			db.close();
		}
	}

	@Test
	public void testDbInvalidationOverride() throws Exception {
		String url = HSQLUtil.getInMemoryURL("benerator");
		DBSystem db = new DBSystem("db", url, HSQLUtil.DRIVER, "sa", null, context.getDataModel());
		BeneratorContext context = new DefaultBeneratorContext();
		context.set("db", db);
		assertEquals(0, db.invalidationCount());
		try {
			db.execute("create table epast_test (id int)");
			Statement statement = parse("<execute target='db' invalidate='true'>select * from epast_test where 1 = 0</execute>");
			statement.execute(context);
			assertEquals(1, db.invalidationCount());
			Statement statement2 = parse("<execute target='db' invalidate='false'>create table AAA (id int)</execute>");
			statement2.execute(context);
			assertEquals(1, db.invalidationCount());
		} finally {
			db.execute("drop table epast_test");
			db.close();
		}
	}

}
