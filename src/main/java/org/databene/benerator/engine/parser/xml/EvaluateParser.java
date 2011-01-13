/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.engine.statement.EvaluateStatement;
import org.databene.commons.Expression;
import org.databene.commons.expression.FeatureAccessExpression;
import org.databene.commons.expression.TypeConvertingExpression;
import org.w3c.dom.Element;

/**
 * Parses an &lt;evaluate&gt; element in a Benerator descriptor file.<br/><br/>
 * Created: 25.10.2009 01:01:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EvaluateParser extends AbstractBeneratorDescriptorParser {

	public EvaluateParser() {
		super("");
	}

	@Override
	public boolean supports(Element element, Statement[] parentPath) {
		String name = element.getNodeName();
	    return DescriptorConstants.EL_EVALUATE.equals(name) 
	    	|| DescriptorConstants.EL_EXECUTE.equals(name);
    }

	@Override
	public EvaluateStatement parse(Element element, Statement[] parentPath, BeneratorParseContext context) {
		Expression<String> id           = parseAttribute(ATT_ID, element);
		Expression<String> text         = new TypeConvertingExpression<String>(parseScriptableElementText(element), String.class);
		Expression<String> uri          = parseScriptableStringAttribute(ATT_URI,  element);
		Expression<String> type         = parseAttribute(ATT_TYPE, element);
		Expression<?> targetObject      = new FeatureAccessExpression<Object>(element.getAttribute(ATT_TARGET));
		Expression<String> onError      = parseScriptableStringAttribute(ATT_ON_ERROR, element);
		Expression<String> encoding     = parseScriptableStringAttribute(ATT_ENCODING, element);
		Expression<Boolean> optimize    = parseBooleanExpressionAttribute(ATT_OPTIMIZE, element, false);
		Expression<?> assertion         = new ScriptExpression<Object>(element.getAttribute(ATT_ASSERT));
		return new EvaluateStatement(id, text, uri, type, targetObject, onError, encoding, optimize, assertion);
	}

}
