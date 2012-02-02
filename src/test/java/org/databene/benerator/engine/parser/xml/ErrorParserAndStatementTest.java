/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.BeneratorError;
import org.databene.benerator.engine.statement.ErrorStatement;
import org.databene.benerator.test.BeneratorIntegrationTest;
import org.junit.Test;

/**
 * Tests the {@link ErrorParser} and the {@link ErrorStatement}.<br/><br/>
 * Created: 12.01.2011 08:58:34
 * @since o.6.4
 * @author Volker Bergmann
 */
public class ErrorParserAndStatementTest extends BeneratorIntegrationTest {

	@Test(expected = BeneratorError.class)
	public void testNoInfo() {
		ErrorStatement statement = (ErrorStatement) parse("<error/>");
		assertNull(statement.messageEx.evaluate(context));
		assertNull(statement.codeEx.evaluate(context));
		statement.execute(context);
	}
	
	@Test(expected = BeneratorError.class)
	public void testExecute() {
		ErrorStatement statement = (ErrorStatement) parse("<error>Something bad happened</error>");
		statement.execute(context);
	}
	
}
