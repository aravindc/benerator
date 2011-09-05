/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.script;

import static org.junit.Assert.*;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.Expression;
import org.databene.commons.expression.ExpressionUtil;
import org.junit.Before;
import org.junit.Test;

import freemarker.template.utility.StringUtil;

/**
 * Tests the {@link QNInvocationExpression}.<br/><br/>
 * Created: 18.05.2011 16:50:14
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class QNInvocationExpressionTest {

	private BeneratorContext context;
	
	@Before
	public void setup() {
		context = new DefaultBeneratorContext();
	}

	@Test
	public void testClass() {
		check("Hello Alice", "org.databene.benerator.script.ScriptTestUtil.sayHello", "Alice");
	}

	@Test
	public void testImportedClass() {
		context.importClass("org.databene.benerator.script.*");
		check("Hello Alice", "ScriptTestUtil.sayHello", "Alice");
	}

	private void check(Object expected, String qn, String arg) {
		QNInvocationExpression ex = new QNInvocationExpression(StringUtil.split(qn, '.'), new Expression<?>[] { ExpressionUtil.constant(arg) });
		Object actual = ex.evaluate(context);
		assertEquals(expected, actual);
	}

}
