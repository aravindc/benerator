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

import java.text.ParseException;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.ScriptedLiteral;
import org.databene.benerator.engine.expression.StringScriptExpression;
import org.databene.benerator.engine.task.SetGlobalPropertyTask;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.expression.FeatureAccessExpression;
import org.w3c.dom.Element;

/**
 * TODO Document class.<br/><br/>
 * Created: 25.10.2009 00:58:53
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PropertyParser extends AbstractDescriptorParser {

	public PropertyParser() {
	    super(DescriptorConstants.EL_PROPERTY);
    }

	public SetGlobalPropertyTask parse(Element element, ResourceManager resourceManager) {
		String propertyName = element.getAttribute(ATT_NAME);
		Expression valueExpression;
		if (element.hasAttribute(ATT_VALUE))
			valueExpression = new ScriptedLiteral(element.getAttribute(ATT_VALUE));
		else if (element.hasAttribute(ATT_REF))
			valueExpression = new FeatureAccessExpression(element.getAttribute(ATT_REF));
		else if (element.hasAttribute(ATT_SOURCE)) // TODO test (like samba.ben.xml)
			valueExpression = parseSource(element.getAttribute(ATT_SOURCE));
		else
			throw new ConfigurationError("Syntax error in property definition");
		return new SetGlobalPropertyTask(propertyName, valueExpression);
	}

    private Expression parseSource(String source) {
		try {
			return new SourceExpression(BeneratorScriptParser.parseBeanSpec(source));
        } catch (ParseException e) {
            throw new ConfigurationError("Error parsing property source expression: " + source, e);
        }
    }

	/**
     * TODO Document class.<br/><br/>
     * Created: 26.10.2009 08:38:44
     * @since 0.6.0
     * @author Volker Bergmann
     */
    public class SourceExpression implements Expression {
    	
    	Expression source;
    	
		public SourceExpression(Expression source) {
	        this.source = source;
        }

		public Object evaluate(Context context) {
			Generator<?> generator = (Generator<?>) source.evaluate(context);
			return generator.generate();
		}

    }

}
