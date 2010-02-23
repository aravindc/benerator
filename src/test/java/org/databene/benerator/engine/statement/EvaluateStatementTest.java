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

package org.databene.benerator.engine.statement;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Encodings;
import org.junit.Test;

import static org.databene.commons.expression.ExpressionUtil.*;
import static org.junit.Assert.*;

/**
 * Tests the {@link EvaluateStatement}.<br/><br/>
 * Created: 12.02.2010 13:18:42
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EvaluateStatementTest {

	@Test
	public void testInlineJavaScript() {
		EvaluateStatement stmt = new EvaluateStatement(
			stringEx("message"),
			stringEx("'Hello World'"),
			null,
			null,
			null,
			stringEx("fatal"),
			stringEx(Encodings.UTF_8),
			booleanEx(false),
			null);
		BeneratorContext context = new BeneratorContext();
		stmt.execute(context);
		assertEquals("Hello World", context.get("message"));
	}
	
	@Test
	public void testUriMapping() {
		EvaluateStatement stmt = new EvaluateStatement(
			stringEx("message"),
			null,
			stringEx("/org/databene/benerator/engine/statement/HelloWorld.js"),
			null,
			null,
			stringEx("fatal"),
			stringEx(Encodings.UTF_8),
			booleanEx(false),
			null);
		BeneratorContext context = new BeneratorContext();
		stmt.execute(context);
		assertEquals("Hello World", context.get("message"));
	}
	
	@Test
	public void testShell() {
		EvaluateStatement stmt = new EvaluateStatement(
				stringEx("result"),
				stringEx("echo 42"),
				null,
				stringEx("shell"),
				null,
				stringEx("fatal"),
				stringEx(Encodings.UTF_8),
				booleanEx(false),
				null);
			BeneratorContext context = new BeneratorContext();
			stmt.execute(context);
			assertEquals(42, context.get("result"));
	}
	
}
