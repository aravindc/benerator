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
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DescriptorConstants;
import org.databene.benerator.engine.DescriptorParser;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.expression.ErrorHandlerExpression;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.engine.expression.StringScriptExpression;
import org.databene.benerator.engine.expression.TypedScriptExpression;
import org.databene.benerator.engine.expression.context.DefaultPageSizeExpression;
import org.databene.benerator.engine.expression.xml.XMLConsumerExpression;
import org.databene.benerator.engine.statement.CreateOrUpdateEntityStatement;
import org.databene.benerator.engine.statement.GenerateAndConsumeEntityTask;
import org.databene.benerator.engine.statement.LazyStatement;
import org.databene.benerator.engine.statement.TimedEntityStatement;
import org.databene.benerator.factory.GeneratorFactoryUtil;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.TypeDescriptor;
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
	
	public Statement parse(final Element element, final ResourceManager resourceManager) {
		Expression expression = new Expression() {
			public Statement evaluate(Context context) {
				final CreateOrUpdateEntityStatement creator = parseCreateEntities(element, false, resourceManager, (BeneratorContext) context);
				return new Statement() {
					public void execute(BeneratorContext context) {
						creator.execute(context);
                    }
				};
            }
		};
		return new TimedEntityStatement(new LazyStatement(expression));
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

    public CreateOrUpdateEntityStatement parseCreateEntities(Element element, boolean isSubTask, 
    		ResourceManager resourceManager, BeneratorContext context) {
	    InstanceDescriptor descriptor = mapEntityDescriptorElement(element, context);
		GenerateAndConsumeEntityTask task = parseTask(element, descriptor, isSubTask, resourceManager, context);
		
		Expression countExpression = GeneratorFactoryUtil.getCountExpression(descriptor);
		String localPageSize = element.getAttribute(ATT_PAGESIZE);
		Expression pageSize = new ScriptExpression(localPageSize, new DefaultPageSizeExpression());
		Expression threads = new TypedScriptExpression(element.getAttribute(ATT_THREADS), Integer.class, 1);
		Expression pager = new ScriptExpression(element.getAttribute(ATT_PAGER), null);
		CreateOrUpdateEntityStatement creator = new CreateOrUpdateEntityStatement(task, countExpression, pageSize, pager, threads);
		return creator;
	}

	@SuppressWarnings("unchecked")
    private GenerateAndConsumeEntityTask parseTask(Element element, InstanceDescriptor descriptor, boolean isSubTask, ResourceManager resourceManager, BeneratorContext context) {
		descriptor.setNullable(false);
		StringScriptExpression onErrorExpr = new StringScriptExpression(element.getAttribute(ATT_ON_ERROR));
		Expression errorHandler = new ErrorHandlerExpression(onErrorExpr, getClass().getName());
		if (!isSubTask)
			logger.info(descriptor.toString());
		else if (logger.isDebugEnabled())
			logger.debug(descriptor.toString());
		
		// create generator
		Generator<Entity> generator = (Generator<Entity>) InstanceGeneratorFactory
				.createSingleInstanceGenerator(descriptor, context);
		
		// parse consumers
		Expression consumer = parseConsumers(element, EL_CREATE_ENTITIES.equals(element.getNodeName()), resourceManager);
		
		String taskName = descriptor.getName();
		if (taskName == null)
			taskName = descriptor.getLocalType().getSource();
		
		GenerateAndConsumeEntityTask task = new GenerateAndConsumeEntityTask(taskName, generator, consumer, isSubTask, errorHandler);

		// handle sub-create-entities
		for (Element child : XMLUtil.getChildElements(element)) {
			String nodeName = child.getNodeName();
			if (EL_CREATE_ENTITIES.equals(nodeName) || EL_UPDATE_ENTITIES.equals(nodeName)) {
			    InstanceDescriptor subDescriptor = mapEntityDescriptorElement(child, context);
				task.addSubTask(parseTask(child, subDescriptor, true, resourceManager, context));
			}
		}
		return task;
    }

	private Expression parseConsumers(Element entityElement, boolean consumersExpected, ResourceManager resourceManager) {
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
