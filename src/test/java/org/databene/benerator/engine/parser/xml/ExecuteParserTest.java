/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.statement.EvaluateStatement;
import org.databene.commons.xml.XMLUtil;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the {@link EvaluateParser} with respect to the features used 
 * in the &lt;execute&gt; element.<br/><br/>
 * Created: 30.10.2009 08:11:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExecuteParserTest {

	@Test
	public void testBeanInvocation() throws Exception {
        String uri = "string://<execute>bean.invoke(2)</execute>";
		Document doc = XMLUtil.parse(uri);
		EvaluateParser parser = new EvaluateParser();
		EvaluateStatement statement = parser.parse(doc.getDocumentElement(), new ResourceManagerSupport());
		BeneratorContext context = new BeneratorContext();
		BeanMock bean = new BeanMock();
		bean.invocationCount = 0;
		context.set("bean", bean);
		statement.execute(context);
		assertEquals(1, bean.invocationCount);
		assertEquals(2, bean.lastValue);
	}
	
}
