/*
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
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseBooleanAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseIntAttribute;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.parseStringAttribute;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.composite.ConfiguredEntityGenerator;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.factory.SimpleTypeGeneratorFactory;
import org.databene.benerator.parser.BasicParser;
import org.databene.benerator.parser.ModelParser;
import org.databene.benerator.primitive.number.adapter.SequenceFactory;
import org.databene.commons.Assert;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ConversionException;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Escalator;
import org.databene.commons.IOUtil;
import org.databene.commons.LogCategories;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.ReaderLineIterator;
import org.databene.commons.RoundedNumberFormat;
import org.databene.commons.ShellUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.ErrorHandler.Level;
import org.databene.commons.converter.LiteralParser;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.db.DBUtil;
import org.databene.commons.mutator.AnyMutator;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.consumer.FileExporter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemConsumer;
import org.databene.platform.db.DBSystem;
import org.databene.script.Script;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.databene.script.jsr227.Jsr223ScriptFactory;
import org.databene.task.PageListener;
import org.databene.task.Task;
import org.databene.task.TaskException;
import org.databene.task.TaskRunner;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses and executes a benerator descriptor file.<br/>
 * <br/>
 * Created at 26.02.2009 15:51:59
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class DescriptorRunner {

	// constants -------------------------------------------------------------------------------------------------------

	public static final String LOCALE_VM_PARAM = "benerator.locale";
	
	private static final String UPDATE_ENTITIES = "update-entities";
	private static final String CREATE_ENTITIES = "create-entities";
	private static final Collection<String> COMPONENT_TYPES = CollectionUtil
		.toSet("attribute", "part", "id", "reference");
	
	private static final Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil
		.toSet("pagesize", "threads", "consumer", "onError");

	private static final Log logger = LogFactory.getLog(DescriptorRunner.class);
	private static final Log commentLogger = LogFactory.getLog(LogCategories.COMMENT);

	// attributes ------------------------------------------------------------------------------------------------------

	private ModelParser parser;
	private String uri;

	private ExecutorService executor;
	private Escalator escalator;
	private Set<Closeable> resources = new HashSet<Closeable>();
	private BeneratorContext context;
	private Map<String, Object> beans;

	private DataModel dataModel = DataModel.getDefaultInstance();
	private BasicParser basicParser;
	private List<String> generatedFiles;
	
	// constructor -----------------------------------------------------------------------------------------------------
	
	public DescriptorRunner(String uri) {
		this.uri = uri;
		this.context = new BeneratorContext(".");
		this.executor = Executors.newCachedThreadPool();
		this.escalator = new LoggerEscalator();
		this.basicParser = new BasicParser();
		this.beans = new HashMap<String, Object>();
		this.generatedFiles = new ArrayList<String>();
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	public BeneratorContext getContext() {
		return context;
	}

    public void run() throws IOException {
		try {
			generatedFiles = new ArrayList<String>();
			context.setContextUri(IOUtil.getContextUri(uri));
			parser = new ModelParser(context);
			SequenceFactory.setClassProvider(context); // TODO find more robust model

			long startTime = java.lang.System.currentTimeMillis();
			Document document = XMLUtil.parse(uri, context.isValidate());
			Element root = document.getDocumentElement();
			XMLUtil.mapAttributesToProperties(root, context, false);
			// process sub elements
			NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (!(node instanceof Element))
					continue;
				parseRootChild((Element) node);
			}
			for (Closeable resource : resources)
				resource.close();
			long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
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

	private void parseRootChild(Element element) {
		String elementType = element.getNodeName();
		if ("bean".equals(elementType))
			parseBean(element);
		else if (CREATE_ENTITIES.equals(elementType) || UPDATE_ENTITIES.equals(elementType))
			parseAndRunEntityTask(element);
		else if ("run-task".equals(elementType))
			parseRunTask(element);
		else if ("property".equals(elementType))
			parseProperty(element);
		else if ("include".equals(elementType))
			parser.parseInclude(element);
		else if ("import".equals(elementType))
			parser.parseImport(element);
		else if ("echo".equals(elementType))
			parseEcho(element);
		else if ("database".equals(elementType))
			parseDatabase(element);
		else if ("execute".equals(elementType))
			parseExecute(element);
		else if ("evaluate".equals(elementType))
			parseEvaluate(element);
		else if ("defaultComponents".equals(elementType))
			parseDefaultComponents(element);
		else if ("comment".equals(elementType))
			parseComment(element);
		else
			throw new ConfigurationError("Unknown element: " + elementType);
	}

    private void parseComment(Element element) {
	    commentLogger.debug(XMLUtil.getText(element));
    }

	private void parseProperty(Element element) {
		String propertyName = element.getAttribute("name");
		Object propertyValue;
		if (element.hasAttribute("value"))
			propertyValue = LiteralParser.parse(parseStringAttribute(element, "value", context));
		else if (element.hasAttribute("ref"))
			propertyValue = context.get(parseStringAttribute(element, "ref", context));
		else if (element.hasAttribute("source")) {
			SimpleTypeDescriptor tmpDescriptor = new SimpleTypeDescriptor("tmp");
			tmpDescriptor.setSource(element.getAttribute("source"));
			tmpDescriptor.setSelector("selector");
			Generator<? extends Object> tmpGenerator = null;
			try {
				tmpGenerator = SimpleTypeGeneratorFactory.createSourceAttributeGenerator(tmpDescriptor, context);
				propertyValue = tmpGenerator.generate();
			} finally {
				if (tmpGenerator != null)
					tmpGenerator.close();
			}
		} else
			throw new ConfigurationError("Syntax error in property definition");
		if (propertyName.startsWith("benerator."))
			AnyMutator.setValue(context, propertyName, propertyValue, true);
		else
			context.setProperty(propertyName, propertyValue);
	}

	private void parseEcho(Element element) {
		String message = parseStringAttribute(element, "message", context);
		System.out.println(ScriptUtil.render(message, context));
	}

	private Object parseBean(Element element) {
		try {
			Object bean = parser.parseBean(element);
			if (bean instanceof DescriptorProvider)
				dataModel.addDescriptorProvider((DescriptorProvider) bean);
			if (bean instanceof Closeable)
				addResource((Closeable) bean);
			if (BeanUtil.hasProperty(bean.getClass(), "id")) {
				Object id = BeanUtil.getPropertyValue(bean, "id");
				beans.put(String.valueOf(id), bean);
			}
			return bean;
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	boolean addResource(Closeable resource) {
		if (resources.contains(resource))
			return false;
		if (resource instanceof FileExporter)
			generatedFiles.add(((FileExporter<?>) resource).getUri());
	    return resources.add(resource);
    }

	private void parseDatabase(Element element) {
		try {
			String id = parseStringAttribute(element, "id", context);
			if (id == null)
				throw new ConfigurationError();
			logger.debug("Instantiating database with id '" + id + "'");
			DBSystem db = new DBSystem(id, 
					parseStringAttribute(element, "url", context), 
					parseStringAttribute(element, "driver", context),
					parseStringAttribute(element, "user", context), 
					parseStringAttribute(element, "password", context)
				);
			db.setSchema(parseStringAttribute(element, "schema", context));
			db.setBatch(parseBooleanAttribute(element, "batch", context, false));
			db.setFetchSize(parseIntAttribute(element, "fetchSize", context, 100));
			db.setBatch(parseBooleanAttribute(element, "readOnly", context, false));
			context.set(id, db);
			beans.put(id, db);
			dataModel.addDescriptorProvider(db, context.isValidate());
			addResource(db);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	private void parseExecute(Element element) {
		parseEvaluate(element);
	}

	private Object parseEvaluate(Element element) {
		try {
			String uri = parseStringAttribute(element, "uri", context);
			String target = parseStringAttribute(element, "target", context);
			Object targetObject = context.get(target);
			String onError = parseStringAttribute(element, "onError", context);
			if (StringUtil.isEmpty(onError))
				onError = "fatal";
			String encoding = parseStringAttribute(element, "encoding", context);
			boolean optimize = parseBooleanAttribute(element, "optimize",
					context, false);
			String type = parseStringAttribute(element, "type", context);
			// if type is not defined, derive it from the file extension
			if (type == null && uri != null) {
				// check for SQL file URI
				String lcUri = uri.toLowerCase();
				// TODO v0.6 map generically and extendible (Using/Including
				// Java Scripting?)
				if (lcUri.endsWith(".sql"))
					type = "sql";
				// check for shell file URI
				if ((lcUri.endsWith(".bat") || lcUri.endsWith(".sh")))
					type = "shell";
				// check for jar file URI
				if (lcUri.endsWith(".jar"))
					type = "jar";
				// check for JavaScript file URI
				if (lcUri.endsWith(".js"))
					type = "js";
				uri = IOUtil.resolveLocalUri(uri, context.getContextUri());
			}
			if (type == null && targetObject instanceof DBSystem)
				type = "sql";
			// run
			ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
			Object result = null;
			String text = XMLUtil.getText(element);
			if ("sql".equals(type))
				result = runSqlTask(uri, targetObject, onError, encoding, text, optimize);
			// else if ("jar".equals(type)) // TODO v0.6 support .jar files
			// runJar(text);
			else if ("shell".equals(type)) {
				if (!StringUtil.isEmpty(uri))
					text = IOUtil.getContentOfURI(uri);
				text = String.valueOf(ScriptUtil.render(text, context));
				result = runShell(null, text, onError); // TODO v0.6 remove null uri parameter
			} else {
				if (StringUtil.isEmpty(type))
					throw new ConfigurationError("script type is not defined");
				if (!StringUtil.isEmpty(uri))
					text = IOUtil.getContentOfURI(uri);
				result = runScript(text, type, onError);
			}
			context.set("result", result);
			Object assertion = parseAttribute(element, "assert", context);
			String resultAsString = ToStringConverter.convert(result, null);
			if (assertion != null && !(assertion instanceof String && ((String) assertion).length() == 0)) {
				if (assertion instanceof Boolean) {
					if (!(Boolean)assertion)
						errorHandler.handleError("Assertion failed: '" + element.getAttribute("assert") + "'");
				} else {
					if (!assertion.equals(resultAsString))
						errorHandler.handleError("Assertion failed. Expected: '" + assertion + "', found: '" + resultAsString + "'");
				}
			}
			String id = parseStringAttribute(element, "id", context);
			if (id != null)
				context.set(id, result);
			return result;
				
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
	}

	private Object runScript(String text, String type, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
				Level.valueOf(onError));
		try {
			Script script = Jsr223ScriptFactory.parseText(text, type);
			return script.evaluate(context);
		} catch (Exception e) {
			errorHandler.handleError("Error in script evaluation", e);
			return null;
		}
	}

	private int runShell(String uri, String text, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(),
				Level.valueOf(onError));
		if (text != null)
			return ShellUtil.runShellCommands(new ReaderLineIterator(
					new StringReader(text)), errorHandler);
		else if (uri != null) {
			try {
				return ShellUtil.runShellCommands(new ReaderLineIterator(IOUtil
						.getReaderForURI(uri)), errorHandler);
			} catch (IOException e) {
				errorHandler.handleError("Error in shell invocation", e);
				return 1;
			}
		} else
			throw new ConfigurationError(
					"At least uri or text must be provided in <execute>");
	}

	private Object runSqlTask(String uri, Object targetObject, String onError,
			String encoding, String text, boolean optimize) {
		if (targetObject == null)
			throw new ConfigurationError("Please specify the 'target' database to execute the SQL script");
		Assert.instanceOf(targetObject, DBSystem.class, "target");
		DBSystem db = (DBSystem) targetObject;
		if (uri != null)
			logger.info("Executing script " + uri);
		else if (text != null)
			logger.info("Executing inline script");
		else
			throw new TaskException("No uri or content");
        Connection connection = null;
        Object result = null;
		ErrorHandler errorHandler = new ErrorHandler(LogCategories.SQL, Level.valueOf(onError));
        try {
            connection = db.createConnection();
            if (text != null)
            	result = DBUtil.runScript(text, connection, optimize, errorHandler);
            else
            	result = DBUtil.runScript(uri, encoding, connection, optimize, errorHandler);
            db.invalidate(); // possibly we changed the database structure
            connection.commit();
		} catch (Exception sqle) { 
            if (connection != null) {
            	try {
                    connection.rollback();
                } catch (SQLException e) {
                    // ignore this 2nd exception, we have other problems now (sqle)
                }
            }
            errorHandler.handleError("Error in SQL script execution", sqle);
		} finally {
            DBUtil.close(connection);
        }
		return result;
	}

	private void parseDefaultComponents(Element element) {
		for (Element child : XMLUtil.getChildElements(element)) {
			String childType = XMLUtil.localName(child);
			if (COMPONENT_TYPES.contains(childType)) {
				ComponentDescriptor component = parser.parseSimpleTypeComponent(child, null);
				context.setDefaultComponentConfig(component);
			} else
				throw new ConfigurationError("Unexpected element: " + childType);
		}
	}

	private void parseRunTask(Element element) {
		try {
			String beanName = parseStringAttribute(element, "name", context);
			logger.debug("Instantiating task '" + beanName + "'");
			ScriptConverter scriptConverter = new ScriptConverter(context);
			Task task = (Task) XMLElement2BeanConverter.convert(element,
					context, scriptConverter);
			int count = parseIntAttribute(element, "count", context, 1);
			int pageSize = parseIntAttribute(element, "pagesize", context,
					context.getDefaultPagesize());
			int threads = parseIntAttribute(element, "threads", context, 1);
			PageListener pager = parsePager(element);
			TaskRunner.run(task, context, count, pager, pageSize, threads,
					executor);
		} catch (ConversionException e) {
			throw new ConfigurationError(e);
		}
	}

	private PageListener parsePager(Element element) {
		String pagerSetup = parseStringAttribute(element, "pager", context);
		if (StringUtil.isEmpty(pagerSetup))
			return null;
		PageListener pager = null;
		try {
			pager = (PageListener) basicParser.resolveConstructionOrReference(pagerSetup, context, context);
		} catch (Exception e) {
			pager = (PageListener) context.get(pagerSetup);
		}
		if (pager == null)
			throw new ConfigurationError(
					"pager=\""
							+ pagerSetup
							+ "\" neither denotes a class nor an object in the context.");
		return pager;
	}

	private void parseAndRunEntityTask(Element element) {
		PagedCreateEntityTask task = parseCreateEntities(element, false);
		long t0 = System.currentTimeMillis();
		long count0 = ConfiguredEntityGenerator.entityCount();
		task.init(context);
		try {
			task.run();
		} finally {
			IOUtil.close(task);
		}
		long dc = ConfiguredEntityGenerator.entityCount() - count0;
		long dt = System.currentTimeMillis() - t0;
		String taskId = task.getTaskName();
		if (dc == 0)
			logger.info("No entities created from '"
					+ taskId + "' setup");
		else if (dt > 0)
			logger.info("Created " + dc + " entities from '"
					+ taskId + "' setup in " + dt + " ms ("
					+ (dc * 1000 / dt) + "/s)");
		else
			logger.info("Created " + dc + " entities from '"
					+ taskId);
	}

	@SuppressWarnings("unchecked")
    public PagedCreateEntityTask parseCreateEntities(Element element, boolean isSubTask) {
		InstanceDescriptor descriptor = mapEntityDescriptorElement(element, context);
		descriptor.setNullable(false);
		ErrorHandler errorHandler = parseOnError(element, getClass().getName());
		if (!isSubTask)
			logger.info(descriptor);
		else if (logger.isDebugEnabled())
			logger.debug(descriptor);
		
		// parse consumers
		ConsumerChain<Entity> consumerChain = parseConsumers(element, CREATE_ENTITIES.equals(element.getNodeName()));
		if (UPDATE_ENTITIES.equals(element.getNodeName())) {
			String sourceName = parseStringAttribute(element, "source", context);
			Object source = context.get(sourceName);
			if (!(source instanceof StorageSystem))
				throw new ConfigurationError("The source of an <" + UPDATE_ENTITIES + "> " +
						"element must be a StorageSystem. '" + sourceName + "' is not");
			consumerChain.addComponent(new StorageSystemConsumer((StorageSystem) source, false));
		}
		
		// create generator
		Generator<Entity> configuredGenerator = (Generator<Entity>) InstanceGeneratorFactory
				.createInstanceGenerator(descriptor, context);
		
		// handle sub-create-entities
		List<PagedCreateEntityTask> subs = new ArrayList<PagedCreateEntityTask>();
		for (Element child : XMLUtil.getChildElements(element)) {
			String nodeName = child.getNodeName();
			if (CREATE_ENTITIES.equals(nodeName) || UPDATE_ENTITIES.equals(nodeName))
				subs.add(parseCreateEntities(child, true));
		}
		
		// parse task properties
		long minCount = DescriptorUtil.getMinCount(descriptor, context); // TODO make use of minCount
		Long maxCount = DescriptorUtil.getMaxCount(descriptor, context);
		int pageSize = parseIntAttribute(element, "pagesize", context, context.getDefaultPagesize());
		int threads = parseIntAttribute(element, "threads", context, 1);
		
		// done
		String taskName = descriptor.getName();
		if (taskName == null)
			taskName = descriptor.getLocalType().getSource();
		long limit = (maxCount != null ? maxCount : -1);
		return new PagedCreateEntityTask(taskName, limit, pageSize, // TODO v0.6 support maxCount and countDistribution
				threads, subs, configuredGenerator, consumerChain, executor,
				isSubTask, errorHandler);
	}

	@SuppressWarnings("unchecked")
    private ConsumerChain<Entity> parseConsumers(Element parent, boolean consumersExpected) {
		String entityName = parseStringAttribute(parent, "name", context);
		ConsumerChain<Entity> consumerChain = new ConsumerChain<Entity>();
		if (parent.hasAttribute("consumer")) {
			String consumerSpec = parseStringAttribute(parent, "consumer", context);
			consumerChain = DescriptorUtil.parseConsumersSpec(consumerSpec, context);
		}
		Element[] consumerElements = XMLUtil.getChildElements(parent, true, "consumer");
		for (int i = 0; i < consumerElements.length; i++) {
			Element consumerElement = consumerElements[i];
			if (consumerElement.hasAttribute("ref")) {
				String consumerSpec = parseStringAttribute(consumerElement, "ref", context);
				consumerChain.addComponent(DescriptorUtil.parseConsumersSpec(consumerSpec, context));
			} else if (consumerElement.hasAttribute("class")) {
				consumerChain.addComponent((Consumer<Entity>) parseBean(consumerElement));
			} else
				throw new UnsupportedOperationException(
						"Don't know how to handle " + XMLUtil.format(consumerElement));
		}
		for (Consumer consumer : consumerChain.getComponents())
			addResource(consumer);
		if (consumerChain.componentCount() == 0 && consumersExpected)
			escalator.escalate("No consumers defined for " + entityName, this, null);
		return consumerChain;
	}

	private InstanceDescriptor mapEntityDescriptorElement(Element element,
			BeneratorContext context) {
		String entityName = parseStringAttribute(element, "name", context);
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
			if ("variable".equals(childType)) {
				parser.parseVariable(child, (ComplexTypeDescriptor) localType);
			} else if (COMPONENT_TYPES.contains(childType)) {
				ComponentDescriptor component = parser.parseSimpleTypeComponent(child, (ComplexTypeDescriptor) localType);
				((ComplexTypeDescriptor) instance.getType()).addComponent(component);
			} else if (!CREATE_ENTITIES.equals(childType)
					&& !"consumer".equals(childType)
					&& !"variable".equals(childType))
				throw new ConfigurationError("Unexpected element: " + childType);
		}
		return instance;
	}
	
	private ErrorHandler parseOnError(Element element, String category) {
		String levelName = parseStringAttribute(element, "onError", context);
		if (levelName == null)
			levelName = context.getDefaultErrorHandler();
		Level level = Level.valueOf(levelName);
		return new ErrorHandler(category, level);
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
