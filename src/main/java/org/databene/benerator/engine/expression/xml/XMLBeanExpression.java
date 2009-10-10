/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.benerator.engine.expression.xml;

import java.text.ParseException;

import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.task.CreateBeanTask;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.script.ScriptConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Parses a &lt;bean/&gt; definition in an XML descriptor file.<br/>
 * <br/>
 * Created at 24.07.2009 07:51:56
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class XMLBeanExpression implements Expression<Object> {

	private static Logger logger = LoggerFactory.getLogger(CreateBeanTask.class);

	private Element element;

    public XMLBeanExpression(Element element) {
        this.element = element;
    }

    public Object evaluate(Context context) {
		BeneratorContext beneratorContext = (BeneratorContext) context;
        String id = parseStringAttribute(element, "id", context);
        String beanClass = parseStringAttribute(element, "class", context);
        String beanSpec = parseStringAttribute(element, "spec", context);
        Object bean;
        if (beanClass != null) {
	        logger.debug("Instantiating bean of class " + beanClass + " (id=" + id + ")");
	        bean = XMLElement2BeanConverter.convert(element, context, new ScriptConverter(context), beneratorContext);
	        if (!StringUtil.isEmpty(id))
	            BeanUtil.setPropertyValue(bean, "id", id, false);
        } else if (beanSpec != null) {
        	try {
		        logger.debug("Instantiating bean: " + beanSpec + " (id=" + id + ")");
		        Expression<?> beanExpression = BeneratorScriptParser.parseBeanSpec(beanSpec);
		        bean = beanExpression.evaluate(context);
        	} catch (ParseException e) {
        		throw new ConfigurationError("Error parsing bean spec: " + beanSpec, e);
        	}
        } else
        	throw new ConfigurationError("Syntax error in definition of bean " + id);
        return bean;
    }

}
