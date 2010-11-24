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

package org.databene.benerator.engine;

import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.commons.xml.XMLUtil;
import org.junit.Before;
import org.w3c.dom.Element;

/**
 * Parent class for Benerator integration tests.<br/><br/>
 * Created: 10.08.2010 07:07:42
 * @since 0.6.4
 * @author Volker Bergmann
 */
public abstract class BeneratorIntegrationTest {
	
	protected BeneratorContext context;

	@Before
	public void setUpContext() throws Exception {
		context = new BeneratorContext();
	}

	protected void parseAndExecute(String xml) {
	    Element element = XMLUtil.parseStringAsElement(xml);
		ResourceManagerSupport resourceManager = new ResourceManagerSupport();
		GenerateOrIterateParser parser = new GenerateOrIterateParser();
		Statement statement = parser.parse(element, new Statement[0], resourceManager);
		statement.execute(context);
    }
	
}
