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

import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Escalator;
import org.databene.commons.Expression;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.data.Entity;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemConsumer;
import org.w3c.dom.Element;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses a {@link Consumer} specification in an XML element in a descriptor file.<br/>
 * <br/>
 * Created at 24.07.2009 07:21:16
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class XMLConsumerExpression implements Expression<Consumer<Entity>> {
	
	private Escalator escalator;

	private Element entityElement;
	private boolean consumersExpected;
	private BeanParser beanParser = new BeanParser();
	private ResourceManager resourceManager;

    public XMLConsumerExpression(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
    	this.entityElement = entityElement;
    	this.consumersExpected = consumersExpected;
		this.escalator = new LoggerEscalator();
		this.resourceManager = resourceManager;
    }

	@SuppressWarnings("unchecked")
    public Consumer<Entity> evaluate(Context context) { // TODO merge with BeanParser
		BeneratorContext beneratorContext = (BeneratorContext) context;
		String entityName = parseStringAttribute(entityElement, "name", context);
		ConsumerChain<Entity> consumerChain = new ConsumerChain<Entity>();
		if (entityElement.hasAttribute("consumer")) {
			String consumerSpec = parseStringAttribute(entityElement, "consumer", context);
			consumerChain = DescriptorUtil.parseConsumersSpec(consumerSpec, beneratorContext);
		}
		Element[] consumerElements = XMLUtil.getChildElements(entityElement, true, "consumer");
		for (int i = 0; i < consumerElements.length; i++) {
			Element consumerElement = consumerElements[i];
			if (consumerElement.hasAttribute("ref")) {
				String consumerSpec = parseStringAttribute(consumerElement, "ref", context);
				consumerChain.addComponent(DescriptorUtil.parseConsumersSpec(consumerSpec, beneratorContext));
			} else if (consumerElement.hasAttribute("class")) {
				Expression beanExpr = beanParser.parseBeanExpression(consumerElement);
				consumerChain.addComponent((Consumer<Entity>) beanExpr.evaluate(context));
			} else if (consumerElement.hasAttribute("spec")) {
				Expression beanExpr = beanParser.parseBeanExpression(consumerElement);
				consumerChain.addComponent((Consumer<Entity>) beanExpr.evaluate(context));
			} else
				throw new UnsupportedOperationException(
						"Can't handle " + XMLUtil.format(consumerElement));
		}
		if (consumerChain.componentCount() == 0 && consumersExpected)
			escalator.escalate("No consumers defined for " + entityName, this, null);
		for (Consumer<Entity> consumer : consumerChain.getComponents())
			resourceManager.addResource(consumer);
		if (EL_UPDATE_ENTITIES.equals(entityElement.getNodeName())) {
			String sourceName = parseStringAttribute(entityElement, "source", context);
			Object source = context.get(sourceName);
			if (!(source instanceof StorageSystem))
				throw new ConfigurationError("The source of an <" + EL_UPDATE_ENTITIES + "> " +
						"element must be a StorageSystem. '" + sourceName + "' is not");
			consumerChain.addComponent(new StorageSystemConsumer((StorageSystem) source, false));
		}
		return consumerChain;
	}

}
