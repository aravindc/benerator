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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import org.databene.benerator.engine.Statement;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.commons.SyntaxError;
import org.junit.Test;

/**
 * Tests the {@link SettingParser}.<br/><br/>
 * Created: 18.02.2010 22:46:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SettingParserAndStatementTest extends BeneratorIntegrationTest {
	
	@Test
	public void testValue() throws Exception {
		parseAndExecute("<setting name='globalProp' value='XYZ' />");
		assertEquals("XYZ", context.get("globalProp"));
	}
	
	@Test
	public void testEscapedValue() throws Exception {
		parseAndExecute("<setting name='globalProp' value=\"\\\'\\t\\'\" />");
		assertEquals("'\t'", context.get("globalProp"));
	}
	
	@Test
	public void testDefault_undefined() throws Exception {
		parseAndExecute("<setting name='globalProp' default='XYZ' />");
		assertEquals("XYZ", context.get("globalProp"));
	}
	
	@Test
	public void testDefault_predefined() throws Exception {
		Statement statement = parse("<setting name='globalProp' default='XYZ' />");
		context.set("globalProp", "ZZZ");
		statement.execute(context);
		assertEquals("ZZZ", context.get("globalProp"));
	}
	
	@Test
	public void testRef() throws Exception {
		context.set("setting", "cfg");
		parseAndExecute("<setting name='globalProp' ref='setting' />");
		assertEquals("cfg", context.get("globalProp"));
	}
	
	@Test
	public void testSource() throws Exception {
		context.set("myGen", new ConstantGenerator<String>("myProd"));
		parseAndExecute("<setting name='globalProp' source='myGen' />");
		assertEquals("myProd", context.get("globalProp"));
	}
	
	@Test
	public void testNestedBean() throws Exception {
		parseAndExecute(
			"<setting name='globalProp'>" +
			"	<bean spec='new org.databene.benerator.engine.parser.xml.BeanMock(123)'/>" +
			"</setting>");
		assertEquals(123, ((BeanMock) context.get("globalProp")).lastValue);
	}
	
	@Test
	public void testNestedBeanArray() throws Exception {
		parseAndExecute(
				"<setting name='globalProp'>" +
				"	<bean spec='new org.databene.benerator.engine.parser.xml.BeanMock(1)'/>" +
				"	<bean spec='new org.databene.benerator.engine.parser.xml.BeanMock(2)'/>" +
				"</setting>");
		Object[] beans = (Object[]) context.get("globalProp");
		assertEquals(2, beans.length);
		assertEquals(1, ((BeanMock) beans[0]).lastValue);
		assertEquals(2, ((BeanMock) beans[1]).lastValue);
	}
	
	@Test(expected = SyntaxError.class)
	public void testInvalid() throws Exception {
		parseAndExecute("<setting name='globalProp' xyz='XYZ' />");
	}
	
	@Test
	public void testBeneratorProperty() throws Exception {
		assertTrue(context.getDefaultPageSize() != 123);
		parseAndExecute("<setting name='context.defaultPageSize' value='123' />");
		assertEquals(123, context.getDefaultPageSize());
	}
	
}
