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
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseIntAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import java.util.concurrent.ExecutorService;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.DescriptorParser;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.expression.ErrorHandlerExpression;
import org.databene.benerator.engine.expression.StringScriptExpression;
import org.databene.benerator.engine.expression.context.ExecutorServiceExpression;
import org.databene.benerator.engine.expression.xml.XMLConsumerExpression;
import org.databene.benerator.engine.task.LazyTask;
import org.databene.benerator.engine.task.PagedCreateEntityTask;
import org.databene.benerator.engine.task.TimedEntityTask;
import org.databene.benerator.factory.GeneratorFactoryUtil;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * TODO Document class.<br/><br/>
 * Created: 25.10.2009 01:05:18
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CreateOrUpdateEntityParser implements DescriptorParser {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateOrUpdateEntityParser.class);
	
	// DescriptorParser interface --------------------------------------------------------------------------------------
	
	public boolean supports(String elementName, String parentName) {
	    return DescriptorConstants.EL_CREATE_ENTITIES.equals(elementName)
	    		|| DescriptorConstants.EL_UPDATE_ENTITIES.equals(elementName);
    }
	
	public Task parse(final Element element, final ResourceManager resourceManager) {
		Expression<PagedCreateEntityTask> expression = new Expression<PagedCreateEntityTask>() {
			public PagedCreateEntityTask evaluate(Context context) {
	            return parseCreateEntities(element, false, resourceManager, (BeneratorContext) context);
            }
		};
		return new TimedEntityTask(new LazyTask(expression));
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
    public PagedCreateEntityTask parseCreateEntities(Element element, boolean isSubTask, 
    		ResourceManager resourceManager, BeneratorContext context) {
		InstanceDescriptor descriptor = mapEntityDescriptorElement(element, context);
		descriptor.setNullable(false);
		StringScriptExpression onErrorExpr = new StringScriptExpression(element.getAttribute(ATT_ON_ERROR));
		Expression<ErrorHandler> errorHandler = new ErrorHandlerExpression(onErrorExpr, getClass().getName());
		if (!isSubTask)
			logger.info(descriptor.toString());
		else if (logger.isDebugEnabled())
			logger.debug(descriptor.toString());
		
		// create generator
		Generator<Entity> configuredGenerator = (Generator<Entity>) InstanceGeneratorFactory
				.createSingleInstanceGenerator(descriptor, context);
		
		// parse task properties
		Expression<Long> countExpression = GeneratorFactoryUtil.getCountExpression(descriptor);
		int pageSize  = parseIntAttribute(element, ATT_PAGESIZE, context, context.getDefaultPagesize());
		int threads   = parseIntAttribute(element, ATT_THREADS, context, 1);
		
		// parse consumers
		Expression<Consumer<Entity>> consumer = parseConsumers(element, EL_CREATE_ENTITIES.equals(element.getNodeName()), resourceManager);
		
		// done
		String taskName = descriptor.getName();
		if (taskName == null)
			taskName = descriptor.getLocalType().getSource();
		Expression<ExecutorService> executor = new ExecutorServiceExpression();
		PagedCreateEntityTask task = new PagedCreateEntityTask(taskName, countExpression, pageSize,
				threads, configuredGenerator, consumer, executor, isSubTask, errorHandler);
		
		// handle sub-create-entities
		for (Element child : XMLUtil.getChildElements(element)) {
			String nodeName = child.getNodeName();
			if (EL_CREATE_ENTITIES.equals(nodeName) || EL_UPDATE_ENTITIES.equals(nodeName))
				task.addSubTask(parseCreateEntities(child, true, resourceManager, context));
		}
		
		return task;
	}

	private Expression<Consumer<Entity>> parseConsumers(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
		return new XMLConsumerExpression(entityElement, consumersExpected, resourceManager);
	}

	private InstanceDescriptor mapEntityDescriptorElement(Element element,
			BeneratorContext context) {
		ModelParser parser = new ModelParser(context);
		String entityName = parseStringAttribute(element, ATT_NAME, context);
		TypeDescriptor parentType = DataModel.getDefaultInstance().getTypeDescriptor(entityName);
		TypeDescriptor localType;
		if (parentType != null) {
			entityName = parentType.getName(); // take over capitalization of the parent
			localType = new ComplexTypeDescriptor(parentType.getName(), (ComplexTypeDescriptor) parentType);
		} else 
			localType = new ComplexTypeDescriptor(entityName, "entity");
		InstanceDescriptor instance = new InstanceDescriptor(entityName, entityName);
		instance.setLocalType(localType);
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attribute = (Attr) attributes.item(i);
			String attributeName = attribute.getName();
			if (!CREATE_ENTITIES_EXT_SETUP.contains(attributeName)) {
				Object attributeValue = parseAttribute(attribute, context);
				if (instance.supportsDetail(attributeName))
					instance.setDetailValue(attributeName, attributeValue);
				else if (localType != null)
					localType.setDetailValue(attributeName, attributeValue);
				// else we expect different types
			}
		}
		for (Element child : XMLUtil.getChildElements(element)) {
			String childType = XMLUtil.localName(child);
			if (EL_VARIABLE.equals(childType)) {
				parser.parseVariable(child, (ComplexTypeDescriptor) localType);
			} else if (COMPONENT_TYPES.contains(childType)) {
				ComponentDescriptor component = parser.parseSimpleTypeComponent(child, (ComplexTypeDescriptor) localType);
				((ComplexTypeDescriptor) instance.getType()).addComponent(component);
			} else if (!EL_CREATE_ENTITIES.equals(childType)
					&& !EL_CONSUMER.equals(childType)
					&& !EL_VARIABLE.equals(childType))
				throw new ConfigurationError("Unexpected element: " + childType);
		}
		return instance;
	}

}
