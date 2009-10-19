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

package org.databene.benerator.engine;

import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseIntAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.databene.benerator.Generator;
import org.databene.benerator.composite.ConfiguredEntityGenerator;
import org.databene.benerator.engine.expression.ErrorHandlerExpression;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.engine.expression.ScriptedLiteral;
import org.databene.benerator.engine.expression.StringScriptExpression;
import org.databene.benerator.engine.expression.xml.XMLConsumerExpression;
import org.databene.benerator.engine.expression.xml.XMLDefaultComponentsTask;
import org.databene.benerator.engine.task.CommentTask;
import org.databene.benerator.engine.task.CreateBeanTask;
import org.databene.benerator.engine.task.DefineDatabaseTask;
import org.databene.benerator.engine.task.EchoTask;
import org.databene.benerator.engine.task.EvaluateTask;
import org.databene.benerator.engine.task.LazyTask;
import org.databene.benerator.engine.task.PagedCreateEntityTask;
import org.databene.benerator.engine.task.SerialTask;
import org.databene.benerator.engine.task.SetGlobalPropertyTask;
import org.databene.benerator.engine.task.TimedEntityTask;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.factory.SimpleTypeGeneratorFactory;
import org.databene.benerator.parser.ModelParser;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Expression;
import org.databene.commons.IOUtil;
import org.databene.commons.RoundedNumberFormat;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ConstantExpression;
import org.databene.commons.expression.FeatureAccessExpression;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.FileExporter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptConverter;
import org.databene.task.PageListener;
import org.databene.task.Task;
import org.databene.task.TaskRunnerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import static org.databene.benerator.engine.DescriptorConstants.*;

/**
 * Parses and executes a benerator descriptor file.<br/>
 * <br/>
 * Created at 26.02.2009 15:51:59
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class DescriptorRunner implements ResourceManager {

    public static final String LOCALE_VM_PARAM = "benerator.locale";
	
	private static final Logger logger = LoggerFactory.getLogger(DescriptorRunner.class);

	// attributes ------------------------------------------------------------------------------------------------------

	private ModelParser parser;
	private String uri;

	private ExecutorService executor;
	private Set<Closeable> resources = new HashSet<Closeable>();
	private BeneratorContext context;

	DataModel dataModel = DataModel.getDefaultInstance();
	private List<String> generatedFiles;
	
	private ResourceManagerSupport resourceManager = new ResourceManagerSupport();
	
	// constructor -----------------------------------------------------------------------------------------------------
	
	public DescriptorRunner(String uri) {
		this.uri = uri;
		this.context = new BeneratorContext(".");
		this.executor = Executors.newCachedThreadPool();
		this.generatedFiles = new ArrayList<String>();
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	public BeneratorContext getContext() {
		return context;
	}

    public void run() throws IOException {
		try {
			// prepare system
			generatedFiles = new ArrayList<String>();
			context.setContextUri(IOUtil.getContextUri(uri));
			parser = new ModelParser(context);
			long startTime = 0;
			
			// parse benerator descriptor into an AST
			Document document = XMLUtil.parse(uri, context.isValidate());
			Element root = document.getDocumentElement();
			XMLUtil.mapAttributesToProperties(root, context, false, true);
			SerialTask beneratorTask = new SerialTask();
			// process sub elements
			for (Element element : XMLUtil.getChildElements(root)) {
				if (startTime == 0 && EL_CREATE_ENTITIES.equals(element.getNodeName()))
					startTime = System.currentTimeMillis();
				parseRootChild(element, beneratorTask);
			}
			
			// run AST
			beneratorTask.run(context);
			
			// calculate and print statistics
			long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
			for (Closeable resource : resources)
				resource.close();
			if (startTime != 0)
				logger.info("Created a total of " + ConfiguredEntityGenerator.entityCount() + " entities "
					+ "in " + elapsedTime + " ms " + "(~" 
					+ RoundedNumberFormat.format(ConfiguredEntityGenerator.entityCount() * 3600000L / elapsedTime, 0) + " p.h.)");
			List<String> generations = getGeneratedFiles();
			if (generations.size() > 0)
				logger.info("Generated file(s): " + generations);
		} finally {
			this.executor.shutdownNow();
		}
	}
	
	public List<String> getGeneratedFiles() {
		return generatedFiles;
	}

	// private helpers -------------------------------------------------------------------------------------------------

	private void parseRootChild(Element element, SerialTask parentTask) {
		String elementType = element.getNodeName();
		Task task = null;
		if (EL_BEAN.equals(elementType))
			task = parseBean(element);
		else if (EL_CREATE_ENTITIES.equals(elementType) || EL_UPDATE_ENTITIES.equals(elementType))
			task = parseEntityTask(element);
		else if (EL_RUN_TASK.equals(elementType))
			task = parseRunTask(element);
		else if (EL_PROPERTY.equals(elementType))
			task = parseProperty(element);
		else if (EL_INCLUDE.equals(elementType))
			task = parser.parseInclude(element);
		else if (EL_IMPORT.equals(elementType))
			task = parser.parseImport(element);
		else if (EL_ECHO.equals(elementType))
			task = parseEcho(element);
		else if (EL_DATABASE.equals(elementType))
			task = parseDatabase(element);
		else if (EL_EXECUTE.equals(elementType))
			task = parseExecute(element);
		else if (EL_EVALUATE.equals(elementType))
			task = parseEvaluate(element);
		else if (EL_DEFAULT_COMPONENTS.equals(elementType))
			task = parseDefaultComponents(element);
		else if (EL_COMMENT.equals(elementType))
			task = parseComment(element);
		else
			throw new ConfigurationError("Unknown element: " + elementType);
		parentTask.addSubTask(task);
	}

    private Task parseComment(Element element) {
    	return new CommentTask(XMLUtil.getText(element));
    }

	private Task parseProperty(Element element) {
		String propertyName = element.getAttribute(ATT_NAME);
		Expression<?> valueExpression;
		if (element.hasAttribute(ATT_VALUE))
			valueExpression = new ScriptedLiteral(element.getAttribute(ATT_VALUE));
		else if (element.hasAttribute(ATT_REF))
			valueExpression = new FeatureAccessExpression(new StringScriptExpression(element.getAttribute(ATT_REF)));
		else if (element.hasAttribute(ATT_SOURCE)) {
			SimpleTypeDescriptor tmpDescriptor = new SimpleTypeDescriptor("tmp");
			tmpDescriptor.setSource(element.getAttribute(ATT_SOURCE));
			tmpDescriptor.setSelector(ATT_SELECTOR);
			Generator<? extends Object> tmpGenerator = null;
			try {
				tmpGenerator = SimpleTypeGeneratorFactory.createSourceAttributeGenerator(tmpDescriptor, context);
				valueExpression = new ConstantExpression<Object>(tmpGenerator.generate()); // TODO evaluate when running, not when parsing
			} finally {
				if (tmpGenerator != null)
					tmpGenerator.close();
			}
		} else
			throw new ConfigurationError("Syntax error in property definition");
		return new SetGlobalPropertyTask(propertyName, valueExpression);
	}

	private Task parseEcho(Element element) {
		// TODO support text like <echo>DB: {${dbUrl}}</echo>
		return new EchoTask(new StringScriptExpression(element.getAttribute(ATT_MESSAGE)));
	}
	
	// ResourceManager interface implementation ------------------------------------------------------------------------

	private Task parseBean(Element element) {
		try {
			Expression<Object> bean = parser.parseBean(element);
			Expression<String> id = new StringScriptExpression(element.getAttribute(ATT_ID));
			return new CreateBeanTask(id, bean, this);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	public boolean addResource(Closeable resource) {
		if (!resourceManager.addResource(resource))
			return false;
		else if (resource instanceof FileExporter)
			generatedFiles.add(((FileExporter<?>) resource).getUri());
	    return true;
    }

    public void close() {
	    resourceManager.close();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

	private Task parseDatabase(Element element) {
		try {
			Expression<String>  id        = parseStringAttr(ATT_ID,       element);
			Expression<String>  url       = parseStringAttr(ATT_URL,      element);
			Expression<String>  driver    = parseStringAttr(ATT_DRIVER,   element);
			Expression<String>  user      = parseStringAttr(ATT_USER,     element);
			Expression<String>  password  = parseStringAttr(ATT_PASSWORD, element);
			Expression<String>  schema    = parseStringAttr(ATT_SCHEMA,   element);
			Expression<Boolean> batch     = new ScriptExpression<Boolean>(element.getAttribute(ATT_BATCH), Boolean.class, false);
			Expression<Integer> fetchSize = new ScriptExpression<Integer>(element.getAttribute(ATT_FETCH_SIZE), Integer.class, 100);
			Expression<Boolean> readOnly  = new ScriptExpression<Boolean>(element.getAttribute(ATT_READ_ONLY), Boolean.class, false);
			return new DefineDatabaseTask(id, url, driver, user, password, schema, batch, fetchSize, readOnly, this);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	private Task parseExecute(Element element) {
		return parseEvaluate(element);
	}

	private Task parseEvaluate(Element element) {
		Expression<String> text         = parseTextElem(element);
		Expression<String> id           = parseStringAttr(ATT_ID, element);
		Expression<String> uri          = parseStringAttr(ATT_URI,    element);
		Expression<String> targetName   = parseStringAttr(ATT_TARGET, element);
		Expression<Object> targetObject = new FeatureAccessExpression(targetName);
		Expression<String> onError      = parseStringAttr(ATT_ON_ERROR, element);
		Expression<String> encoding     = parseStringAttr(ATT_ENCODING, element);
		Expression<Boolean> optimize    = new ScriptExpression<Boolean>(
											element.getAttribute(ATT_OPTIMIZE), Boolean.class, false);
		Expression<String> type         = parseStringAttr(ATT_TYPE, element);
		Expression<Object> assertion    = new ScriptExpression<Object>(element.getAttribute(ATT_ASSERT), Object.class);
		return new EvaluateTask(id, text, uri, type, targetObject, onError, encoding, optimize, assertion);
	}

	private Task parseDefaultComponents(Element element) {
		return new XMLDefaultComponentsTask(element);
	}

	@SuppressWarnings("unchecked")
    private Task parseRunTask(Element element) {
		try {
			String beanName = parseStringAttribute(element, ATT_NAME, context);
			logger.debug("Instantiating task '" + beanName + "'");
			ScriptConverter scriptConverter = new ScriptConverter(context);
			Task task    = (Task) XMLElement2BeanConverter.convert(element, context, scriptConverter);
			int count    = parseIntAttribute(element, ATT_COUNT, context, 1);
			int pageSize = parseIntAttribute(element, ATT_PAGESIZE, context, context.getDefaultPagesize());
			int threads  = parseIntAttribute(element, ATT_THREADS, context, 1);
			PageListener pager = parsePager(element);
			return new TaskRunnerTask(task, count, pageSize, threads, pager, executor);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	private PageListener parsePager(Element element) {
		String pagerSetup = parseStringAttribute(element, ATT_PAGER, context);
		if (StringUtil.isEmpty(pagerSetup))
			return null;
		PageListener pager = null;
		try {
			pager = (PageListener) BeneratorScriptParser.parseBeanSpec(pagerSetup).evaluate(context);
		} catch (Exception e) {
			pager = (PageListener) context.get(pagerSetup);
		}
		if (pager == null)
			throw new ConfigurationError("pager=\"" + pagerSetup
						+ "\" neither denotes a class nor an object in the context.");
		return pager;
	}

	private Task parseEntityTask(final Element element) {
		Expression<PagedCreateEntityTask> expression = new Expression<PagedCreateEntityTask>() {
			public PagedCreateEntityTask evaluate(Context context) {
	            return parseCreateEntities(element, false);
            }
		};
		return new TimedEntityTask(new LazyTask(expression));
	}

	@SuppressWarnings("unchecked")
    public PagedCreateEntityTask parseCreateEntities(Element element, boolean isSubTask) {
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
				.createInstanceGenerator(descriptor, context);
		
		// parse task properties
		long minCount = DescriptorUtil.getMinCount(descriptor, context).evaluate(context); // TODO make use of minCount
		Long maxCount = DescriptorUtil.getMaxCount(descriptor, context).evaluate(context);
		// TODO support count distribution
		int pageSize  = parseIntAttribute(element, ATT_PAGESIZE, context, context.getDefaultPagesize());
		int threads   = parseIntAttribute(element, ATT_THREADS, context, 1);
		
		// parse consumers
		Expression<Consumer<Entity>> consumer = parseConsumers(element, EL_CREATE_ENTITIES.equals(element.getNodeName()));
		
		// done
		String taskName = descriptor.getName();
		if (taskName == null)
			taskName = descriptor.getLocalType().getSource();
		long limit = (maxCount != null ? maxCount : -1);
		PagedCreateEntityTask task = new PagedCreateEntityTask(taskName, limit, pageSize, // TODO v0.6 support maxCount and countDistribution
				threads, configuredGenerator, consumer, executor, isSubTask, errorHandler);
		
		// handle sub-create-entities
		for (Element child : XMLUtil.getChildElements(element)) {
			String nodeName = child.getNodeName();
			if (EL_CREATE_ENTITIES.equals(nodeName) || EL_UPDATE_ENTITIES.equals(nodeName))
				task.addSubTask(parseCreateEntities(child, true));
		}
		
		return task;
	}

    private Expression<Consumer<Entity>> parseConsumers(Element entityElement, boolean consumersExpected) {
		return new XMLConsumerExpression(entityElement, consumersExpected, this);
	}

	private InstanceDescriptor mapEntityDescriptorElement(Element element,
			BeneratorContext context) {
		String entityName = parseStringAttribute(element, ATT_NAME, context);
		TypeDescriptor parentType = dataModel.getTypeDescriptor(entityName);
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
	
	// parsing support methods -----------------------------------------------------------------------------------------
	
	private static Expression<String> parseTextElem(Element element) {
	    return new ScriptExpression<String>(XMLUtil.getText(element), String.class);
    }

	private static Expression<String> parseStringAttr(String attId, Element element) {
	    return new StringScriptExpression(element.getAttribute(ATT_ID));
    }

	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
