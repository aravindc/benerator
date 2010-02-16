/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.expression.xml;

import static org.junit.Assert.*;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.factory.ConsumerMock;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.ConsumerChain;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the {@link XMLConsumerExpression}.<br/><br/>
 * Created: 16.02.2010 11:41:13
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class XMLConsumerExpressionTest {
	
	@SuppressWarnings("unchecked")
    @Test
	public void testInlineConsumerClass() throws Exception {
		Document doc = XMLUtil.parseString("<create-entities " +
				"consumer='org.databene.benerator.factory.ConsumerMock'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerChain consumerChain = (ConsumerChain) expression.evaluate(new BeneratorContext());
		ConsumerMock consumerMock = (ConsumerMock) consumerChain.getComponent(0);
		assertNotNull("Context not set", consumerMock.context);
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void testInlineConsumerSpec() throws Exception {
		Document doc = XMLUtil.parseString("<create-entities " +
				"consumer='new org.databene.benerator.factory.ConsumerMock(2)'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerChain consumerChain = (ConsumerChain) expression.evaluate(new BeneratorContext());
		ConsumerMock consumerMock = (ConsumerMock) consumerChain.getComponent(0);
		assertNotNull("Context not set", consumerMock.context);
		assertEquals(2, consumerMock.id);
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void testConsumerBean() throws Exception {
		Document doc = XMLUtil.parseString("<create-entities>" +
				"    <consumer spec='new org.databene.benerator.factory.ConsumerMock()'/>" +
				"</create-entities>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		ConsumerChain consumerChain = (ConsumerChain) expression.evaluate(new BeneratorContext());
		ConsumerMock consumerMock = (ConsumerMock) consumerChain.getComponent(0);
		assertNotNull("Context not set", consumerMock.context);
	}
	
    @Test
	public void testConsumerBeanRef() throws Exception {
		Document doc = XMLUtil.parseString("<create-entities>" +
				"    <consumer ref='myc'/>" +
				"</create-entities>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		context.set("myc", new ConsumerMock(3));
		ConsumerChain<?> consumerChain = (ConsumerChain<?>) expression.evaluate(context);
		ConsumerMock consumerMock = (ConsumerMock) consumerChain.getComponent(0);
		assertEquals(3, consumerMock.id);
	}
	
	@Test
	public void testInlineConsumerList() throws Exception {
		Document doc = XMLUtil.parseString("<create-entities " +
			"consumer='myc,new org.databene.benerator.factory.ConsumerMock(5)'/>");
		XMLConsumerExpression expression = new XMLConsumerExpression(doc.getDocumentElement(), true, 
				new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		context.set("myc", new ConsumerMock());
		ConsumerChain<?> consumerChain = (ConsumerChain<?>) expression.evaluate(context);
		ConsumerMock consumerMock = (ConsumerMock) consumerChain.getComponent(0);
		assertEquals(2, consumerChain.componentCount());
		assertEquals(consumerMock, consumerChain.getComponent(0));
		ConsumerMock component2 = (ConsumerMock) consumerChain.getComponent(1);
		assertEquals(5, component2.id);
	}
	
}
