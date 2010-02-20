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
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.engine.statement.BeanStatement;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link BeanParser}.<br/><br/>
 * Created: 30.10.2009 19:02:25
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeanParserAndStatementTest {

	@Test
    public void testParseBeanClass() throws Exception {
		String xml = "<bean id='id' class='" + BeanMock.class.getName() + "' />";
		BeneratorContext context = parseAndExecute(xml);
		Object bean = context.get("id");
		assertNotNull(bean);
		assertEquals(BeanMock.class, bean.getClass());
		assertEquals(0, ((BeanMock) bean).lastValue);
		assertNotNull(((BeanMock) bean).getContext());
	}

	@Test
	public void testParseBeanSpec() throws Exception {
		String xml = "<bean id='id' spec='new " + BeanMock.class.getName() + "(2)' />";
		BeneratorContext context = parseAndExecute(xml);
		Object bean = context.get("id");
		assertNotNull(bean);
		assertEquals(BeanMock.class, bean.getClass());
		assertEquals(2, ((BeanMock) bean).lastValue);
		assertNotNull(((BeanMock) bean).getContext());
	}
	
	// test helpers ----------------------------------------------------------------------------------------------------

	private BeneratorContext parseAndExecute(String xml) throws IOException {
	    Element element = XMLUtil.parseStringAsElement(xml);
        BeanParser parser = new BeanParser();
		BeanStatement statement = parser.parse(element, new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		statement.execute(context);
	    return context;
    }

}
