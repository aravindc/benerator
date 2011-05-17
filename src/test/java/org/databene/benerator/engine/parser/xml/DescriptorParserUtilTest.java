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

package org.databene.benerator.engine.parser.xml;

import static org.junit.Assert.*;

import org.databene.commons.Expression;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link DescriptorParserUtil}.<br/><br/>
 * Created: 11.04.2011 13:10:30
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class DescriptorParserUtilTest {

	@Test
	public void testParseScriptableElementText() {
		Element element = XMLUtil.parseStringAsElement("<text>'\\'Test\\''</text>");
		
		Expression<String> asIsExpression = DescriptorParserUtil.parseScriptableElementText(element, false);
		System.out.println(asIsExpression);
		assertEquals("'\\'Test\\''", asIsExpression.evaluate(new DefaultContext()));
		
		Expression<String> unescapingExpression = DescriptorParserUtil.parseScriptableElementText(element, true);
		System.out.println(unescapingExpression);
		assertEquals("''Test''", unescapingExpression.evaluate(new DefaultContext()));
	}
}
