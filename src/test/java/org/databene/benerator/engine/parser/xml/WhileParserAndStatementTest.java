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

import org.databene.benerator.engine.statement.WhileStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.junit.Test;

/**
 * Tests the {@link WhileParser} and the {@link WhileStatement}.<br/><br/>
 * Created: 19.02.2010 10:06:29
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class WhileParserAndStatementTest extends BeneratorIntegrationTest {

	@Test
	public void testNoLoop() throws Exception {
		context.set("count", 0);
		parseAndExecute(
				"<while test='2==3'>" +
				"	<evaluate id='count'>count + 1</evaluate>" +
				"</while>");
		assertEquals(0, context.get("count"));
	}
	
	@Test
	public void testThreeLoops() throws Exception {
		context.set("count", 0);
		parseAndExecute(
				"<while test='count &lt; 3'>" +
				"	<execute>count = count + 1</execute>" +
				"</while>");
		assertEquals(3, context.get("count"));
	}
	
}
