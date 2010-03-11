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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.test.ConsumerMock;
import org.databene.benerator.test.PersonIterable;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
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
        String xml = "<generate type='dummy' count='3'>" +
        		"<execute>bean.invoke(2)</execute>" +
        		"</generate>";
		Element element = XMLUtil.parseStringAsElement(xml);
		Statement statement = parser.parse(element, resourceManager);
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
        String xml = "<iterate type='Person' source='personSource' consumer='cons' />";
		Element element = XMLUtil.parseStringAsElement(xml);
		Statement statement = parser.parse(element, resourceManager);
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
        String xml = "<generate type='Person' count='2' consumer='cons' />";
		Element element = XMLUtil.parseStringAsElement(xml);
		Statement statement = parser.parse(element, resourceManager);
		ConsumerMock<Entity> consumer = new ConsumerMock<Entity>(false);
		context.set("cons", consumer);
		statement.execute(context);
		assertEquals(2, consumer.startConsumingCount.get());
		assertEquals(2, consumer.finishConsumingCount.get());
	}
	
}
