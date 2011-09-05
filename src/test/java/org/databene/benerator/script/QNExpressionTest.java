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
import org.databene.commons.Context;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link QNExpression}.<br/><br/>
 * Created: 18.05.2011 16:17:22
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class QNExpressionTest {
	
	BeneratorContext context;
	
	@Before
	public void stUpContext() {
		context = new DefaultBeneratorContext();
	}

	@Test
	public void testClass() {
		check(ScriptTestUtil.class, "org", "databene", "benerator", "script", "ScriptTestUtil");
	}

	@Test
	public void testImportedClass() {
		context.importClass("org.databene.benerator.script.*");
		check(ScriptTestUtil.class, context, "ScriptTestUtil");
	}

	@Test
	public void testStaticField() {
		check("pubVarContent", "org", "databene", "benerator", "script", "ScriptTestUtil", "pubvar");
	}

	@Test
	public void testStaticFieldOfImportedClass() {
		BeneratorContext context = new DefaultBeneratorContext();
		context.importClass("org.databene.benerator.script.*");
		check("pubVarContent", context, "ScriptTestUtil", "pubvar");
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void check(Object expected, String... parts) {
		check(expected, context, parts);
	}
	
	private void check(Object expected, Context context, String... parts) {
		assertEquals(expected, new QNExpression(parts).evaluate(context));
	}
	
}
