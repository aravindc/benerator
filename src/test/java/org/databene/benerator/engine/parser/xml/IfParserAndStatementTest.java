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
import org.databene.benerator.engine.Statement;
import org.databene.commons.ParseException;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link IfParser}.<br/><br/>
 * Created: 19.02.2010 09:47:36
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IfParserAndStatementTest {

	@Test
	public void testPlainTrue() throws Exception {
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='1==1'><property name='executed' value='OK'/></if>");
		Statement statement = new IfParser().parse(element, null);
		statement.execute(context);
		assertEquals("OK", context.getProperty("executed"));
	}
	
	@Test
	public void testPlainFalse() throws Exception {
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='2==3'><property name='executed' value='OK'/></if>");
		Statement statement = new IfParser().parse(element, null);
		statement.execute(context);
		assertEquals(null, context.getProperty("executed"));
	}
	
	@Test
	public void testThenTrue() throws Exception {
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='1==1'><then><property name='executed' value='OK'/></then></if>");
		Statement statement = new IfParser().parse(element, null);
		statement.execute(context);
		assertEquals("OK", context.getProperty("executed"));
	}
	
	@Test
	public void testThenFalse() throws Exception {
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='2==3'><then><property name='executed' value='OK'/></then></if>");
		Statement statement = new IfParser().parse(element, null);
		statement.execute(context);
		assertEquals(null, context.getProperty("executed"));
	}
	
	@Test
	public void testThenElse() throws Exception {
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement(
			"<if test='x==3'>" +
			"	<then>" + 
			"		<property name='executed' value='OK'/>" +
			"	</then>" +
			"	<else>" + 
			"		<property name='executed' value='NOK'/>" +
			"	</else>" +
			"</if>"
		);
		Statement statement = new IfParser().parse(element, null);
		// test 'then' part
		context.set("x", 3);
		statement.execute(context);
		assertEquals("OK", context.getProperty("executed"));
		// test 'else' part
		context.set("x", 2);
		statement.execute(context);
		assertEquals("NOK", context.getProperty("executed"));
	}
	
	@Test(expected = ParseException.class)
	public void testElseWithoutIf() throws Exception {
		new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='2==3'><else/></if>");
		new IfParser().parse(element, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTwoThens() throws Exception {
		new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='2==3'><then/><then/></if>");
		new IfParser().parse(element, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTwoElses() throws Exception {
		new BeneratorContext(); // this first for setting the default script engine to benerator script
		Element element = XMLUtil.parseStringAsElement("<if test='2==3'><then/><else/><else/></if>");
		new IfParser().parse(element, null);
	}
	
}
