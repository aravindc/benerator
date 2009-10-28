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

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.engine.parser.xml.AbstractDescriptorParser.*;

import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.DescriptorParser;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.engine.expression.TypedScriptExpression;
import org.databene.benerator.engine.statement.EvaluateStatement;
import org.databene.commons.Expression;
import org.databene.commons.expression.FeatureAccessExpression;
import org.databene.commons.expression.StringExpression;
import org.w3c.dom.Element;

/**
 * TODO Document class.<br/><br/>
 * Created: 25.10.2009 01:01:02
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class EvaluateParser implements DescriptorParser {

	public boolean supports(String elementName, String parentName) {
	    return DescriptorConstants.EL_EVALUATE.equals(elementName) 
	    	|| DescriptorConstants.EL_EXECUTE.equals(elementName);
    }

	public EvaluateStatement parse(Element element, ResourceManager resourceManager) {
		StringExpression id           = parseStringAttr(ATT_ID, element);
		StringExpression text         = parseTextElem(element);
		StringExpression uri          = parseStringAttr(ATT_URI,  element);
		StringExpression type         = parseStringAttr(ATT_TYPE, element);
		Expression targetObject       = new FeatureAccessExpression(element.getAttribute(ATT_TARGET));
		StringExpression onError      = parseStringAttr(ATT_ON_ERROR, element);
		StringExpression encoding     = parseStringAttr(ATT_ENCODING, element);
		Expression optimize           = new TypedScriptExpression(
											element.getAttribute(ATT_OPTIMIZE), Boolean.class, false);
		Expression assertion    = new ScriptExpression(element.getAttribute(ATT_ASSERT));
		return new EvaluateStatement(id, text, uri, type, targetObject, onError, encoding, optimize, assertion);
	}

}
