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

import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.TypedScriptExpression;
import org.databene.benerator.engine.expression.context.ContextReference;
import org.databene.benerator.engine.statement.CreateBeanStatement;
import org.databene.benerator.script.Assignment;
import org.databene.benerator.script.BeanConstruction;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.script.DefaultConstruction;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.Expression;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ExpressionUtil;
import org.databene.commons.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 * Parses a &lt;bean&gt; element.<br/><br/>
 * Created: 25.10.2009 01:09:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class BeanParser extends AbstractDescriptorParser {
	
	public BeanParser() {
	    super(EL_BEAN);
    }

	public CreateBeanStatement parse(Element element, ResourceManager resourceManager) {
		try {
			String id = element.getAttribute(ATT_ID);
			Expression<?> bean = parseBeanExpression(element);
			return new CreateBeanStatement(id, bean, resourceManager);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	@SuppressWarnings("unchecked")
    public Expression<?> parseBeanExpression(Element element) {
		String id = element.getAttribute(ATT_ID);
        Expression<?> instantiation;
        String beanSpec = element.getAttribute(ATT_SPEC);
        String beanClass = element.getAttribute(ATT_CLASS);
        if (!StringUtil.isEmpty(beanSpec)) {
        	try {
		        instantiation = BeneratorScriptParser.parseBeanSpec(beanSpec);
        	} catch (ParseException e) {
        		throw new ConfigurationError("Error parsing bean spec: " + beanSpec, e);
        	}
        } else if (!StringUtil.isEmpty(beanClass)) {
	        logger.debug("Instantiating bean of class " + beanClass + " (id=" + id + ")");
	        instantiation = new DefaultConstruction(beanClass);
        } else
        	throw new ConfigurationError("Syntax error in definition of bean " + id);
        Element[] propertyElements = XMLUtil.getChildElements(element, false, EL_PROPERTY);
		Assignment[] propertyAssignments = parsePropertyAssignments(propertyElements);
        return new BeanConstruction(instantiation, propertyAssignments);
    }

	private Assignment[] parsePropertyAssignments(Element[] propertyElements) {
		Assignment[] assignments = new Assignment[propertyElements.length];
        for (int i = 0; i < propertyElements.length; i++) {
        	Element propertyElement = propertyElements[i];
            String propertyName = propertyElement.getAttribute("name");
            Expression<?> propertyValueExpression;
            if (propertyElement.hasAttribute("value")) {
            	// parse simple or script values
                propertyValueExpression = new TypedScriptExpression<Object>(propertyElement.getAttribute("value"));
            } else if (propertyElement.hasAttribute("ref")) {
            	// parse references
                String ref = propertyElement.getAttribute("ref");
                propertyValueExpression = new ContextReference(ref);
            } else { // map child elements to a collection or array
                Element[] childElements = XMLUtil.getChildElements(propertyElement);
                final Expression<?>[] subExpressions = new Expression[childElements.length];
                for (int j = 0; j < childElements.length; j++)
                	subExpressions[j] = parseBeanExpression(childElements[j]);
                if (subExpressions.length == 0)
                    throw new ConfigurationError("No valid property spec: " + XMLUtil.format(propertyElement));
                propertyValueExpression = new Expression<Object[]>() {
					public Object[] evaluate(Context context) {
	                    return ExpressionUtil.evaluateAll(subExpressions, context);
                    }
                };
            }
            assignments[i] = new Assignment(propertyName, propertyValueExpression);
        }
        return assignments;
    }

}
