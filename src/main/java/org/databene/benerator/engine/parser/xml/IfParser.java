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

package org.databene.benerator.engine.parser.xml;

import java.util.List;
import java.util.Set;

import org.databene.benerator.engine.DescriptorParser;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.statement.IfStatement;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.ParseException;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.commons.xml.XMLUtil;

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.engine.parser.xml.DescriptorParserUtil.*;

import org.w3c.dom.Element;

/**
 * Parses an &lt;if&gt; element.<br/><br/>
 * Created: 19.02.2010 09:07:51
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IfParser implements DescriptorParser {
	
	private static final Set<String> LEGAL_PARENTS = CollectionUtil.toSet(
			EL_SETUP, EL_IF, EL_WHILE);
	private static final Set<String> STRICT_CHILDREN = CollectionUtil.toSet(
			EL_THEN, EL_ELSE, EL_COMMENT);

	public Statement parse(Element ifElement, Statement[] parentPath, ResourceManager resourceManager) {
		Expression<Boolean> condition = parseBooleanExpressionAttribute(ATT_TEST, ifElement);
		if (ExpressionUtil.isNull(condition))
			throw new ParseException("'test' attribute of 'if' statement is missing or empty", 
					XMLUtil.format(ifElement));
		Element thenElement = XMLUtil.getChildElement(ifElement, false, false, "then");
		Element elseElement = XMLUtil.getChildElement(ifElement, false, false, "else");
		List<Statement> thenStatements = null;
		List<Statement> elseStatements = null;
		if (elseElement != null) {
			// if there is an 'else' element, there must be an 'if' element too
			if (thenElement == null)
				throw new ParseException("'else' without 'then'", XMLUtil.format(ifElement));
			thenStatements = DescriptorParserUtil.parseChildren(thenElement, resourceManager);
			elseStatements = DescriptorParserUtil.parseChildren(elseElement, resourceManager);
			// check that no elements conflict with 'then' and 'else'
			assertThenElseChildren(ifElement);
		} else if (thenElement != null) {
			thenStatements = DescriptorParserUtil.parseChildren(thenElement, resourceManager);
		} else
			thenStatements = DescriptorParserUtil.parseChildren(ifElement, resourceManager);
		return new IfStatement(condition, thenStatements, elseStatements);
    }

    public boolean supports(String elementName, String parentName) {
	    return (EL_IF.equals(elementName) && LEGAL_PARENTS.contains(parentName));
    }

	private static void assertThenElseChildren(Element ifElement) {
	    for (Element child : XMLUtil.getChildElements(ifElement)) {
	    	if (!STRICT_CHILDREN.contains(child.getNodeName()))
	    		throw new ConfigurationError();
	    }
    }


}
