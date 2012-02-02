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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import org.databene.benerator.engine.statement.EvaluateStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.junit.Test;

/**
 * Integration test for the &lt;evaluate&gt; statement.<br/><br/>
 * Created: 24.03.2011 11:54:43
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class EvaluateIntegrationTest extends BeneratorIntegrationTest {

	@Test
	public void testBeneratorScriptStringLiteral() {
		parseAndExecute("<evaluate id='result'>'TEST'</evaluate>");
		assertEquals("TEST", context.get("result"));
	}

	@Test
	public void testBeneratorScriptStringLiteralWithQuotes() {
		EvaluateStatement statement = (EvaluateStatement) parse("<evaluate id='result'>'\\'TEST\\''</evaluate>");
		assertEquals("'\\'TEST\\''", statement.getTextEx().evaluate(context));
		statement.execute(context);
		assertEquals("'TEST'", context.get("result"));
	}
	
}
