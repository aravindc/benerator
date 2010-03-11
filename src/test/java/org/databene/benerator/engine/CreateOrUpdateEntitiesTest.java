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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.benerator.factory.ConsumerMock;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Integration test for {@link GenerateOrIterateParser} 
 * and {@link CreateOrUpdateEntitiesTest}.<br/><br/>
 * Created: 12.11.2009 16:05:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CreateOrUpdateEntitiesTest {

	@Test
	public void testAttributes() throws Exception {
		String uri = "string://<generate type='dummy' count='{c}' threads='{tc}' pageSize='{ps}'" +
				" consumer='cons'/>";
		Document doc = XMLUtil.parse(uri);
		GenerateOrIterateParser parser = new GenerateOrIterateParser();
		Statement statement = parser.parse(doc.getDocumentElement(), new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		ConsumerMock consumer = new ConsumerMock();
		context.set("cons", consumer);
		context.set("c", 100);
		context.set("tc", 10);
		context.set("ps", 20);
		statement.execute(context);
		assertEquals(100, consumer.invocationCount.get());
	}

	@Test
	public void testSubCreate() throws Exception {
        String consumerClassName = ConsumerMock.class.getName();
		String uri = "string://" +
        		"<generate type='top' count='3' consumer='cons1'>" +
        		"    <generate type='sub' count='2' consumer='new " + consumerClassName + "(2)'/>" +
        		"</generate>";
		Document doc = XMLUtil.parse(uri);
		GenerateOrIterateParser parser = new GenerateOrIterateParser();
		Statement statement = parser.parse(doc.getDocumentElement(), new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		ConsumerMock outerConsumer = new ConsumerMock(1);
		context.set("cons1", outerConsumer);
		statement.execute(context);
		assertEquals(3, outerConsumer.invocationCount.get());
		assertFalse(outerConsumer.closed);
		ConsumerMock innerConsumer = ConsumerMock.instances.get(2);
		assertEquals(6, innerConsumer.invocationCount.get());
		assertTrue(innerConsumer.closed);
	}

	@Test
	public void testSubCreateLoop() throws Exception {
        // TODO test if sub loop that retrieves its loop length from the availability of a source, is properly reset between top loops
	}

}
