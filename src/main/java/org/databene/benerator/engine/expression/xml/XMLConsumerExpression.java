/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import static org.databene.benerator.engine.DescriptorConstants.*;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Escalator;
import org.databene.commons.Expression;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.context.ContextAware;
import org.databene.commons.expression.DynamicExpression;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.data.Entity;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemInserter;
import org.w3c.dom.Element;

/**
 * Parses a {@link Consumer} specification in an XML element in a descriptor file.<br/><br/>
 * Created at 24.07.2009 07:21:16
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class XMLConsumerExpression extends DynamicExpression<Consumer<?>> {
	
	private Escalator escalator;

	private Element entityElement;
	private boolean consumersExpected;
	private ResourceManager resourceManager;

    public XMLConsumerExpression(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
    	this.entityElement = entityElement;
    	this.consumersExpected = consumersExpected;
		this.escalator = new LoggerEscalator();
		this.resourceManager = resourceManager;
    }

	@SuppressWarnings("unchecked")
    public Consumer<Entity> evaluate(Context context) {
		BeneratorContext beneratorContext = (BeneratorContext) context;
		ConsumerChain<Entity> consumerChain = new ConsumerChain<Entity>();
		
		// parse consumer attribute
		if (entityElement.hasAttribute(ATT_CONSUMER)) {
			String consumerSpec = parseStringAttribute(entityElement, ATT_CONSUMER, context);
			Expression<?>[] beanExs = BeneratorScriptParser.parseBeanSpecList(consumerSpec);
			for (Expression<?> beanEx : beanExs) {
				Object bean = beanEx.evaluate(beneratorContext);
				addConsumer(bean, beneratorContext, consumerChain);
			}
		}
		
		// parse consumer sub elements
		Element[] consumerElements = XMLUtil.getChildElements(entityElement, true, EL_CONSUMER);
		for (int i = 0; i < consumerElements.length; i++) {
			Element consumerElement = consumerElements[i];
			Object bean;
			if (consumerElement.hasAttribute(ATT_REF)) {
				String ref = parseStringAttribute(consumerElement, ATT_REF, context);
				bean = beneratorContext.get(ref);
			} else if (consumerElement.hasAttribute(ATT_CLASS) || consumerElement.hasAttribute(ATT_SPEC)) {
				Expression<?> beanExpr = BeanParser.parseBeanExpression(consumerElement);
				bean = beanExpr.evaluate(context);
			} else
				throw new UnsupportedOperationException(
						"Can't handle " + XMLUtil.format(consumerElement));
			addConsumer(bean, beneratorContext, consumerChain);
		}
		
		if (consumerChain.componentCount() == 0 && consumersExpected) {
			String entityName = parseStringAttribute(entityElement, ATT_NAME, context, false);
			escalator.escalate("No consumers defined for " + entityName, this, null);
		}
		for (Consumer<Entity> consumer : consumerChain.getComponents())
			resourceManager.addResource(consumer); // TODO v0.7 add only local consumers
		return consumerChain;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static void addConsumer(Object bean, BeneratorContext context, ConsumerChain<?> chain) {
    	Consumer consumer;
    	// check consumer type
    	if (bean instanceof Consumer)
    		consumer = (Consumer<?>) bean;
    	else if (bean instanceof StorageSystem)
    		consumer = new StorageSystemInserter((StorageSystem) bean);
    	else
    		throw new UnsupportedOperationException("Consumer type not supported: " + BeanUtil.simpleClassName(bean));
    	if (bean instanceof ContextAware)
    		((ContextAware) bean).setContext(context);
    	chain.addComponent(consumer);
	}

	@Override
	public String toString() {
	    return getClass().getSimpleName() + '(' + XMLUtil.format(entityElement) + ')';
	}
	
}
