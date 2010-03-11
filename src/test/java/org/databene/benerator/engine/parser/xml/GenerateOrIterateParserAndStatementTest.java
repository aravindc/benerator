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

import java.io.IOException;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.test.ConsumerMock;
import org.databene.benerator.test.PersonIterable;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.HeavyweightTypedIterable;
import org.databene.commons.db.hsql.HSQLUtil;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.platform.db.DBSystem;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link GenerateOrIterateParser}.<br/><br/>
 * Created: 10.11.2009 15:08:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class GenerateOrIterateParserAndStatementTest {
	
	BeneratorContext context;
	ResourceManager resourceManager;
	GenerateOrIterateParser parser;

	@Before
	public void setUp() {
		context = new BeneratorContext();
		resourceManager = new ResourceManagerSupport();
		parser = new GenerateOrIterateParser();
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
			assertTrue(iterator.hasNext());
			assertEquals(2, iterator.next());
			assertTrue(iterator.hasNext());
			assertEquals(2, iterator.next());
			assertFalse(iterator.hasNext());
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
	
	// helper methods --------------------------------------------------------------------------------------------------

	private Statement parse(String xml) throws IOException {
		Element element = XMLUtil.parseStringAsElement(xml);
		return parser.parse(element, resourceManager);
    }
	
}
