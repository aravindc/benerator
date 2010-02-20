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

import static org.junit.Assert.assertEquals;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link WhileParser} and the {@link WhileStatement}.<br/><br/>
 * Created: 19.02.2010 10:06:29
 * @since TODO version
 * @author Volker Bergmann
 */
public class WhileParserAndStatementTest {

	@Test
	public void testNoLoop() throws Exception {
		Element element = XMLUtil.parseStringAsElement(
				"<while test='2==3'>" +
				"	<evaluate id='count'>count + 1</evaluate>" +
				"</while>");
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		context.set("count", 0);
		Statement statement = new WhileParser().parse(element, null);
		statement.execute(context);
		assertEquals(0, context.get("count"));
	}
	
	@Test
	public void testThreeLoops() throws Exception {
		Element element = XMLUtil.parseStringAsElement(
				"<while test='count &lt; 3'>" +
				"	<evaluate id='count'>count + 1</evaluate>" + // TODO support syntax: count = count + 1
				"</while>");
		BeneratorContext context = new BeneratorContext(); // this first for setting the default script engine to benerator script
		context.set("count", 0); // TODO how to define a global variable in descriptor file syntax?
		Statement statement = new WhileParser().parse(element, null);
		statement.execute(context);
		assertEquals(3, context.get("count"));
	}
	
}
