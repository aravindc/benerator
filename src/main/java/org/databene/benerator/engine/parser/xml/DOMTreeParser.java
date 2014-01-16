/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseAttribute;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseBooleanExpressionAttribute;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.parseScriptableStringAttribute;

import java.util.Set;

import org.databene.benerator.engine.BeneratorRootStatement;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.DefineDOMTreeStatement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.script.Expression;
import org.w3c.dom.Element;

/**
 * Parses &lt;domtree&gt; elements in a Benerator descriptor file.<br/><br/>
 * Created: 16.01.2014 15:59:48
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class DOMTreeParser extends AbstractBeneratorDescriptorParser {
	
	private static final Set<String> REQUIRED_ATTRIBUTES = CollectionUtil.toSet(ATT_ID, ATT_INPUT_URI);

	private static final Set<String> OPTIONAL_ATTRIBUTES = CollectionUtil.toSet(ATT_OUTPUT_URI, ATT_NAMESPACE_AWARE);


	public DOMTreeParser() {
	    super(EL_DOMTREE, REQUIRED_ATTRIBUTES, OPTIONAL_ATTRIBUTES, BeneratorRootStatement.class, IfStatement.class);
    }

	@Override
    public DefineDOMTreeStatement doParse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		try {
			Expression<String>  id        = parseAttribute(ATT_ID, element);
			Expression<String>  inputUri  = parseScriptableStringAttribute(ATT_INPUT_URI,  element);
			Expression<String>  outputUri = parseScriptableStringAttribute(ATT_OUTPUT_URI, element);
			Expression<Boolean> namespaceAware = parseBooleanExpressionAttribute(ATT_NAMESPACE_AWARE, element);
			return new DefineDOMTreeStatement(id, inputUri, outputUri, namespaceAware, context.getResourceManager());
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
    }

}
