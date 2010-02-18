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

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.statement.SetGlobalPropertyStatement;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link PropertyParser}.<br/><br/>
 * Created: 18.02.2010 22:46:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PropertyParserTest {
	
	private PropertyParser parser = new PropertyParser();

	@Test
	public void testValue() throws Exception {
		Element element = XMLUtil.parseStringAsElement("<property name='globalProp' value='XYZ' />");
		SetGlobalPropertyStatement statement = parser.parse(element, null);
		BeneratorContext context = new BeneratorContext();
		statement.execute(context);
		assertEquals("XYZ", context.get("globalProp"));
	}
	
	@Test
	public void testRef() throws Exception {
		Element element = XMLUtil.parseStringAsElement("<property name='globalProp' ref='setting' />");
		SetGlobalPropertyStatement statement = parser.parse(element, null);
		BeneratorContext context = new BeneratorContext();
		context.set("setting", "cfg");
		statement.execute(context);
		assertEquals("cfg", context.get("globalProp"));
	}
	
	@Test
	public void testSource() throws Exception {
		Element element = XMLUtil.parseStringAsElement("<property name='globalProp' source='myGen' />");
		SetGlobalPropertyStatement statement = parser.parse(element, null);
		BeneratorContext context = new BeneratorContext();
		context.set("myGen", new ConstantGenerator<String>("myProd"));
		statement.execute(context);
		assertEquals("myProd", context.get("globalProp"));
	}
	
	@Test(expected = ConfigurationError.class)
	public void testInvalid() throws Exception {
		Element element = XMLUtil.parseStringAsElement("<property name='globalProp' xyz='XYZ' />");
		parser.parse(element, null);
	}
	
}
