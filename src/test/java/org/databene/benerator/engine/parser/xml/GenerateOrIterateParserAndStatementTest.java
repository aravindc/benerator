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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.test.ConsumerMock;
import org.databene.benerator.test.PersonIterable;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.HeavyweightTypedIterable;
import org.databene.commons.db.hsql.HSQLUtil;
import org.databene.commons.iterator.IteratorTestCase;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.platform.db.DBSystem;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link GenerateOrIterateParser}.<br/><br/>
 * Created: 10.11.2009 15:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateParserAndStatementTest extends ParserTest {
	
	@Override
	@Before
	public void setUp() {
	    super.setUp();
		parser = new GenerateOrIterateParser();
	}
	
	@Test
	public void testAttributes() throws Exception {
		Statement statement = parse(
				"<generate type='dummy' count='{c}' threads='{tc}' pageSize='{ps}' consumer='cons'/>");
		ConsumerMock<Entity> consumer = new ConsumerMock<Entity>(false);
		context.set("cons", consumer);
		context.set("c", 100);
		context.set("tc", 10);
		context.set("ps", 20);
		statement.execute(context);
		assertEquals(100, consumer.startConsumingCount.get());
		assertEquals(100, consumer.finishConsumingCount.get());
		assertEquals(100L, context.getTotalGenerationCount());
	}

	@SuppressWarnings("unchecked")
    @Test
	public void testSimpleSubGenerate() throws Exception {
		Statement statement = parse(
				"<generate type='top' count='3' consumer='cons1'>" +
        		"    <generate type='sub' count='2' consumer='new " + ConsumerMock.class.getName() + "(false, 2)'/>" +
        		"</generate>"
        );
		ConsumerMock<Entity> outerConsumer = new ConsumerMock<Entity>(false, 1);
		context.set("cons1", outerConsumer);
		statement.execute(context);
		assertEquals(3, outerConsumer.startConsumingCount.get());
		assertTrue(outerConsumer.closeCount.get() == 0);
		ConsumerMock<Entity> innerConsumer = (ConsumerMock<Entity>) ConsumerMock.instances.get(2);
		assertEquals(6, innerConsumer.startConsumingCount.get());
		assertTrue(innerConsumer.closeCount.get() > 0);
		assertEquals(9L, context.getTotalGenerationCount());
	}

    /** Tests a sub loop that derives its loop length from a parent attribute. */
	@Test
	public void testSubGenerateParentRef() throws Exception {
		Statement statement = parse(
				"<generate name='pName' type='outer' count='3' consumer='cons'>" +
				"    <attribute name='n' type='int' distribution='step' />" +
				"    <generate type='inner' count='{pName.n}' consumer='cons'/>" + // TODO v0.6.1 make the brackets unnecessary
        		"</generate>");
		ConsumerMock<Entity> consumer = new ConsumerMock<Entity>(true, 1);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(9, consumer.startConsumingCount.get());
		assertEquals(new Entity("outer", "n", 1), consumer.products.get(0));
		assertEquals(new Entity("inner"), consumer.products.get(1));
		assertEquals(new Entity("outer", "n", 2), consumer.products.get(2));
		assertEquals(new Entity("inner"), consumer.products.get(3));
		assertEquals(new Entity("inner"), consumer.products.get(4));
		assertEquals(new Entity("outer", "n", 3), consumer.products.get(5));
		assertEquals(new Entity("inner"), consumer.products.get(6));
		assertEquals(new Entity("inner"), consumer.products.get(7));
		assertEquals(new Entity("inner"), consumer.products.get(8));
	}

	/** Tests the nesting of an &lt;execute&gt; element within a &lt;generate&gt; element */
	@Test
	public void testSubExecute() throws Exception {
		Statement statement = parse(
				"<generate type='dummy' count='3'>" +
        		"	<execute>bean.invoke(2)</execute>" +
        		"</generate>");
		BeanMock bean = new BeanMock();
		bean.invocationCount = 0;
		context.set("bean", bean);
		statement.execute(context);
		assertEquals(3, bean.invocationCount);
		assertEquals(2, bean.lastValue);
	}
	
	/** Tests iterating an {@link EntitySource} */
	@Test
	public void testIterate() throws Exception {
		Statement statement = parse("<iterate type='Person' source='personSource' consumer='cons' />");
		context.set("personSource", new PersonIterable());
		ConsumerMock<Entity> consumer = new ConsumerMock<Entity>(true);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(2, consumer.products.size());
		assertEquals(PersonIterable.PERSONS, consumer.products);
	}
	
	/** Tests pure {@link Entity} generation */
	@Test
	public void testGenerate() throws Exception {
		Statement statement = parse("<generate type='Person' count='2' consumer='cons' />");
		ConsumerMock<Entity> consumer = new ConsumerMock<Entity>(false);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(2, consumer.startConsumingCount.get());
		assertEquals(2, consumer.finishConsumingCount.get());
	}
	
	/** Tests DB update */
	@Test
	public void testDBUpdate() throws Exception {
		// create DB
        DBSystem db = new DBSystem("db", HSQLUtil.getInMemoryURL("benetest"), HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
        try {
    		// prepare DB
        	db.execute(
        		"create table GOIPAST (" +
        		"	ID int," +
        		"	N  int," +
        		"	primary key (ID)" +
        		")");
        	db.execute("insert into GOIPAST (id, n) values (1, 3)");
        	db.execute("insert into GOIPAST (id, n) values (2, 4)");
	        // parse and run statement
	        Statement statement = parse(
	        	"<iterate type='GOIPAST' source='db' consumer='db.updater()'>" +
	        	"	<attribute name='n' constant='2' />" +
	        	"</iterate>"
	        );
	        context.set("db", db);
			statement.execute(context);
			HeavyweightTypedIterable<Object> check = db.query("select N from GOIPAST", context);
			HeavyweightIterator<Object> iterator = check.iterator();
			IteratorTestCase.expectNextElements(iterator, 2, 2).withNoNext();
			iterator.close();
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        } finally {
        	// clean up
        	db.execute("drop table GOIPAST");
        	db.close();
        }
	}

}
