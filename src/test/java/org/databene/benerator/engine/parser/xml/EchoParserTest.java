/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.statement.EchoStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.junit.Test;

/**
 * Tests the {@link EchoParser}.<br/><br/>
 * Created: 11.02.2010 15:16:48
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EchoParserTest extends BeneratorIntegrationTest {

	@Test
	public void testSimpleMessageAttribute() throws Exception {
		EchoStatement statement = (EchoStatement) parse("<echo message='Hello' />");
		assertEquals("Hello", statement.getExpression().evaluate(context));
	}
	
	@Test
	public void testSimpleElementText() throws Exception {
		EchoStatement statement = (EchoStatement) parse("<echo>Hello</echo>");
		assertEquals("Hello", statement.getExpression().evaluate(context));
	}
	
	@Test
	public void testEscapedMessageAttribute() throws Exception {
		EchoStatement statement = (EchoStatement) parse("<echo message=\"\\'Test\\'\" />");
		assertEquals("'Test'", statement.getExpression().evaluate(context));
	}
	
	@Test
	public void testEscapedElementText() throws Exception {
		EchoStatement statement = (EchoStatement) parse("<echo>\\'Test\\'</echo>");
		assertEquals("'Test'", statement.getExpression().evaluate(context));
	}
	
}
