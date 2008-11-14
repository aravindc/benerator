/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.main;

import org.databene.LogCategories;
import org.databene.platform.db.DBSystem;
import org.databene.platform.db.RunSqlScriptTask;
import org.databene.model.ModelParser;
import org.databene.model.Processor;
import org.databene.commons.*;
import org.databene.commons.ErrorHandler.Level;
import org.databene.commons.converter.LiteralParser;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.databene.task.TaskException;
import org.databene.task.TaskRunner;
import org.databene.task.Task;
import org.databene.task.PageListener;
import org.databene.benerator.composite.ConfiguredEntityGenerator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.factory.SimpleGenerationSetup;
import org.databene.benerator.Generator;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ProcessorToConsumerAdapter;
import org.databene.model.data.*;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Parses and executes a benerator setup file.<br/>
 * <br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator extends SimpleGenerationSetup {

    private static final Log logger = LogFactory.getLog(Benerator.class);
    
    private static final Collection<String> COMPONENT_TYPES 
        = CollectionUtil.toSet("attribute", "part", "id", "reference");
    
    private static final Collection<String> CREATE_ENTITIES_EXT_SETUP 
    	= CollectionUtil.toSet("pagesize", "threads", "consumer", "onError");

    private ModelParser parser;
    
    private ExecutorService executor;
    private Escalator escalator;
    private Set<Heavyweight> resources = new HashSet<Heavyweight>();
    private BeneratorContext context;
    private Map<String, Object> beans;
    
    //private int totalEntityCount = 0;
    
    private DataModel dataModel = DataModel.getDefaultInstance();

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
            java.lang.System.exit(-1);
        }
        new Benerator().processFile(args[0]);
    }

    private static void printHelp() {
        java.lang.System.out.println("Please specify a file name as command line parameter");
    }

    public Benerator() {
    	super(null);
        this.executor = Executors.newCachedThreadPool();
        this.escalator = new LoggerEscalator();
        beans = new HashMap<String, Object>();
    }
    
    public Context getContext() {
    	return context;
    }
    
    public void processFile(String uri) throws IOException {
        try {
        	contextUri = IOUtil.getContextUri(uri);
            context = new BeneratorContext(contextUri);
            context.set("benerator", this);
			parser = new ModelParser(contextUri);
            long startTime = java.lang.System.currentTimeMillis();
            Document document = XMLUtil.parse(uri);
            Element root = document.getDocumentElement();
            // TODO map root element attributes to benerator properties
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (!(node instanceof Element))
                    continue;
                parseRootChild((Element)node);
            }
            for (Heavyweight resource : resources) {
                resource.close();
            }
            long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
            logger.info("Created a total of " + ConfiguredEntityGenerator.entityCount() + " entities " +
                    "in " + elapsedTime + " ms " +
                    "(~" + RoundedNumberFormat.format(ConfiguredEntityGenerator.entityCount() * 3600000L / elapsedTime, 0) + " p.h.)");
        } finally {
            this.executor.shutdownNow();
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void parseRootChild(Element element) {
        String elementType = element.getNodeName();
        if ("bean".equals(elementType))
            parseBean(element);
        else if ("create-entities".equals(elementType))
            parseAndRunCreateEntities(element);
        else if ("run-task".equals(elementType))
            parseRunTask(element);
        else if ("property".equals(elementType))
            parseProperty(element);
        else if ("include".equals(elementType))
            parser.parseInclude(element, context);
        else if ("echo".equals(elementType))
            parseEcho(element);
        else if ("database".equals(elementType))
            parseDatabase(element);
        else if ("execute".equals(elementType))
            parseExecute(element);
        else if ("defaultComponents".equals(elementType))
            parseDefaultComponents(element);
        else
            throw new ConfigurationError("Unknown element: " + elementType);
    }

    private void parseProperty(Element element) {
        String propertyName = element.getAttribute("name");
        Object propertyValue;
        if (element.hasAttribute("value"))
            propertyValue = LiteralParser.parse(parseAttribute(element, "value", context));
        else if (element.hasAttribute("ref"))
            propertyValue = context.get(parseAttribute(element, "ref", context));
        else 
            throw new ConfigurationError("Syntax error");
        context.set(propertyName, propertyValue); // TODO set bean properties?
    }

    private void parseEcho(Element element) {
        String message = parseAttribute(element, "message", context);
        System.out.println(ScriptUtil.render(message, context));
    }

    private Object parseBean(Element element) {
        try {
            Object bean = parser.parseBean(element, context);
            if (bean instanceof DescriptorProvider)
                dataModel.addDescriptorProvider((DescriptorProvider) bean);
            if (bean instanceof Heavyweight)
                resources.add((Heavyweight)bean);
            if (BeanUtil.hasProperty(bean.getClass(), "id")) {
				Object id = BeanUtil.getPropertyValue(bean, "id");
				beans.put(String.valueOf(id), bean);
			}
            return bean;
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseDatabase(Element element) {
        try {
            String id = parseAttribute(element, "id", context);
            if (id == null)
                throw new ConfigurationError();
            logger.debug("Instantiating database with id '" + id + "'");
            DBSystem db = new DBSystem(
                id,
                parseAttribute(element, "url", context),
                parseAttribute(element, "driver", context),
                parseAttribute(element, "user", context),
                parseAttribute(element, "password", context)
            );
            db.setSchema(parseAttribute(element, "schema", context));
            db.setBatch(parseBooleanAttribute(element, "batch", context, false));
            db.setFetchSize(parseIntAttribute(element, "fetchSize", context, 100));
            context.set(id, db);
            beans.put(id, db);
            dataModel.addDescriptorProvider(db);
            resources.add(db);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseExecute(Element element) {
        try {
        	String uri = parseAttribute(element, "uri", context);
        	String target = parseAttribute(element, "target", context);
    		Object targetObject = context.get(target);
        	String onError = parseAttribute(element, "onError", context);
        	if (onError == null)
        		onError = "fatal";
        	String encoding = parseAttribute(element, "encoding", context);
        	boolean optimize = parseBooleanAttribute(element, "optimize", context, false);
        	String type = parseAttribute(element, "type", context);
        	// if type is not defined, derive it from the file extension
        	if (type == null && uri != null) {
            	// check for SQL file URI
	        	String lcUri = uri.toLowerCase();
	        	// TODO v0.6 map generically and extendible (Using/Including Java Scripting?)
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
	        	uri = IOUtil.resolveLocalUri(uri, contextUri);
        	}
        	if (type == null && targetObject instanceof DBSystem)
        		type="sql";
        	// run
        	String text = XMLUtil.getText(element);
        	if ("sql".equals(type))
        		runSqlTask(uri, targetObject, onError, encoding, text, optimize);
        	//else if ("jar".equals(type)) // TODO v0.6 support .jar files
        	//	runJar(text);
        	else if ("shell".equals(type))
        		runShell(text, type, onError);
        	else
        		runScript(text, type, onError);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void runScript(String text, String type, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
        try {
	        ScriptEngineManager factory = new ScriptEngineManager();
	        ScriptEngine engine = factory.getEngineByName(type);
	        engine.put("benerator", this);
	        for (Map.Entry<String, Object> entry : beans.entrySet()) {
	        	engine.put(entry.getKey(), entry.getValue());
	        }
	        // TODO v0.6.0 bind Context
			engine.eval(text);
		} catch (ScriptException e) {
			errorHandler.handleError("Error in script execution", e);
		}
	}
    
	private void runShell(String uri, String text, String onError) {
		ErrorHandler errorHandler = new ErrorHandler(getClass().getName(), Level.valueOf(onError));
        if (text != null)
        	ShellUtil.runShellTask(new ReaderLineIterator(new StringReader(text)), errorHandler);
        else if (uri != null) {
            try {
	        	ShellUtil.runShellTask(new ReaderLineIterator(IOUtil.getReaderForURI(uri)), errorHandler);
            }
            catch (IOException e) {
                errorHandler.handleError("Error in shell invocation", e);
            }
        } else
        	throw new ConfigurationError("At least uri or text must be provided in <execute>");
    }

	private void runSqlTask(String uri, Object targetObject, String onError, String encoding, String text, boolean optimize) {
		Assert.instanceOf(targetObject, DBSystem.class, "targetObject");
		RunSqlScriptTask task;
		DBSystem db = (DBSystem) targetObject;
		if (uri != null) {
			logger.info("Executing script " + uri);
			task = new RunSqlScriptTask(uri, encoding, db);
		} else if (text != null) {
			logger.info("Executing inline script");
			task = new RunSqlScriptTask(text, db);
		} else
			throw new TaskException("No uri or content");
		task.setIgnoreComments(optimize);
		task.setErrorHandler(new ErrorHandler(LogCategories.SQL, Level.valueOf(onError)));
		task.run();
	}

    private void parseDefaultComponents(Element element) {
        for (Element child : XMLUtil.getChildElements(element)) {
            String childType = XMLUtil.localName(child);
            if (COMPONENT_TYPES.contains(childType)) {
                ComponentDescriptor component = parser.parseSimpleTypeComponent(child, defaultComponent, context);
                defaultComponent.addComponent(component);
            } else
                 throw new ConfigurationError("Unexpected element: " + childType);
         }
    }

    private void parseRunTask(Element element) {
        try {
            String beanName = parseAttribute(element, "name", context);
            logger.debug("Instantiating task '" + beanName + "'");
            ScriptConverter scriptConverter = new ScriptConverter(context);
            Task task = (Task) XMLElement2BeanConverter.convert(element, context, scriptConverter);
            int count = parseIntAttribute(element, "count", context, 1);
            int pageSize = parseIntAttribute(element, "pagesize", context, defaultPagesize);
            int threads = parseIntAttribute(element, "threads", context, 1);
            PageListener pager = parsePager(element);
            TaskRunner.run(task, context, count, pager, pageSize, threads, executor);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private PageListener parsePager(Element element) {
        String pagerSetup = parseAttribute(element, "pager", context);
        if (StringUtil.isEmpty(pagerSetup))
            return null;
        PageListener pager = null;
        try {
            pager = (PageListener) BeanUtil.newInstance(pagerSetup);
        } catch (Exception e) {
            pager = (PageListener) context.get(pagerSetup);
        }
        if (pager == null)
            throw new ConfigurationError("pager=\"" + pagerSetup +
                    "\" neither denotes a class nor an object in the context.");
        return pager;
    }

    private void parseAndRunCreateEntities(Element element) {
        PagedCreateEntityTask task = parseCreateEntities(element, false);
        long t0 = System.currentTimeMillis();
        long count0 = ConfiguredEntityGenerator.entityCount();
        task.init(context);
        try {
            task.run();
        } finally {
            task.destroy();
        }
        long dc = ConfiguredEntityGenerator.entityCount() - count0;
        long dt = System.currentTimeMillis() - t0;
        if (dt > 0)
            logger.info("Created " + dc + " entities from '" + task.getEntityName() + "' setup in " + dt + " ms (" + (dc * 1000 / dt) + "/s)");
        else
            logger.info("Created " + dc + " entities from '" + task.getEntityName() + "' setup in no time");
    }

    public PagedCreateEntityTask parseCreateEntities(Element element, boolean isSubTask) {
        InstanceDescriptor descriptor = mapEntityDescriptorElement(element, context);
        descriptor.setNullable(false);
        ErrorHandler errorHandler = parseOnError(element, getClass().getName());
        if (!isSubTask)
        	logger.info(descriptor);
        else if (logger.isDebugEnabled())
        	logger.debug(descriptor);
        // parse consumers
        Collection<Consumer<Entity>> consumers = parseConsumers(element);
        // create generator
        Generator<Entity> configuredGenerator = (Generator<Entity>) InstanceGeneratorFactory.createInstanceGenerator(descriptor, context, this);
        // create sub create-entities
        List<PagedCreateEntityTask> subs = new ArrayList<PagedCreateEntityTask>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (!(node instanceof Element))
                continue;
            if ("create-entities".equals(node.getNodeName()))
                subs.add(parseCreateEntities((Element)node, true));
        }
        // parse task properties
        int count    = parseIntAttribute(element, "count", context, -1);
        int pageSize = parseIntAttribute(element, "pagesize", context, defaultPagesize);
        int threads  = parseIntAttribute(element, "threads", context, 1);
        // done
        return new PagedCreateEntityTask(
                descriptor.getName(), count, pageSize, threads, subs, 
                configuredGenerator, consumers, executor, isSubTask, errorHandler);
    }

    private Collection<Consumer<Entity>> parseConsumers(Element parent) {
        String entityName = parseAttribute(parent, "name", context);
        List<Consumer<Entity>> consumers = new ArrayList<Consumer<Entity>>();
        if (parent.hasAttribute("consumer")) {
        	String consumerConfig = parseAttribute(parent, "consumer", context);
        	String[] consumerNames = StringUtil.tokenize(consumerConfig, ',');
            Consumer<Entity> consumer;
        	for (String consumerName : consumerNames) {
	            consumer = getConsumer(consumerName);
	            consumers.add(consumer);
        	}
        }
        List<Element> consumerElements = getChildElements(parent, "consumer");
        for (int i = 0; i < consumerElements.size(); i++) {
            Element consumerElement = (Element) consumerElements.get(i);
            Consumer<Entity> consumer;
            if (consumerElement.hasAttribute("ref")) {
                consumer = getConsumer(parseAttribute(consumerElement, "ref", context));
            } else if (consumerElement.hasAttribute("class")) {
                consumer = (Consumer<Entity>) parseBean(consumerElement);
            } else
                throw new UnsupportedOperationException("Don't know how to handle " + XMLUtil.format(consumerElement));
            consumers.add(consumer);
        }
        if (consumers.size() == 0)
            escalator.escalate("No consumers defined for " + entityName, this, null);
        return consumers;
    }

    private List<Element> getChildElements(Element parent, String nodeName) {
        List<Element> children = new ArrayList<Element>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (!(childNode instanceof Element))
                continue;
            Element childElement = (Element) childNode;
            String childType = childElement.getNodeName();
            if (nodeName.equals(childType))
                children.add(childElement);
        }
        return children;
    }

    private Consumer<Entity> getConsumer(String consumerId) {
    	// look up or create consumer
        Object tmp = context.get(consumerId);
        if (tmp == null)
        	tmp = BeanUtil.newInstance(consumerId);
        if (tmp == null)
            throw new ConfigurationError("Consumer not found: " + consumerId);

        // check consumer type
        Consumer<Entity> consumer = null;
        if (StringUtil.isEmpty(consumerId))
            throw new ConfigurationError("Empty consumer id");
        else if (tmp instanceof StorageSystem)
            consumer = new StorageSystemConsumer((StorageSystem) tmp);
        else if (tmp instanceof Consumer)
            consumer = (Consumer<Entity>) tmp;
        else if (tmp instanceof Processor)
            consumer = new ProcessorToConsumerAdapter((Processor<Entity>) tmp);
        else 
            throw new UnsupportedOperationException("Consumer type not supported: " + tmp.getClass());
        return consumer;
    }

    private InstanceDescriptor mapEntityDescriptorElement(Element element, Context context) {
        String entityName = parseAttribute(element, "name", context);
        InstanceDescriptor instance = new InstanceDescriptor(entityName, entityName);
        TypeDescriptor localType = new ComplexTypeDescriptor(entityName);
        localType.setName(entityName);
        TypeDescriptor parentType = dataModel.getTypeDescriptor(entityName);
        if (parentType != null) 
            localType.setParent(parentType);
        else
            localType.setParentName("entity");
        instance.setLocalType(localType);
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            String attributeName = attribute.getName();
            if (!CREATE_ENTITIES_EXT_SETUP.contains(attributeName)) {
                String attributeValue = parseAttribute(attribute, context);
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
               parser.parseVariable(child, (ComplexTypeDescriptor) localType, context);
           } else if (COMPONENT_TYPES.contains(childType)) {
               ComponentDescriptor component = parser.parseSimpleTypeComponent(child, (ComplexTypeDescriptor) localType, context);
               ((ComplexTypeDescriptor) instance.getType()).addComponent(component);
           } else if (!"create-entities".equals(childType) 
                    && !"consumer".equals(childType) && !"variable".equals(childType))
                throw new ConfigurationError("Unexpected element: " + childType);
        }
        return instance;
    }
/*
    private Map<String, Generator<? extends Object>> parseVariables(Element parent, ComplexTypeDescriptor complexType, ContextStack context) {
        HashMap<String, Generator<? extends Object>> variables = new HashMap<String, Generator<? extends Object>>();
        Element[] varElements = XMLUtil.getChildElements(parent, false, "variable");
        for (Element varElement : varElements) {
            InstanceDescriptor varDescriptor = parser.parseVariable(varElement, complexType, context);
            Generator<? extends Object> generator = InstanceGeneratorFactory.createInstanceGenerator(varDescriptor, context, this);
            String varName = parseAttribute(varElement, "name", context);
            variables.put(varName, generator);
        }
        return variables;
    }
*/
    // attribute parsing -----------------------------------------------------------------------------------------------
    
    private String parseAttribute(Attr attribute, Context context) {
        String name = attribute.getName();
        String value = attribute.getValue();
        return ModelParser.renderAttribute(name, value, context);
    }

    private String parseAttribute(Element element, String name, Context context) {
        String value = element.getAttribute(name);
        if (value != null && value.length() == 0)
        	value = null;
        return ModelParser.renderAttribute(name, value, context);
    }

    private int parseIntAttribute(Element element, String name, Context context, int defaultValue) {
        String text = parseAttribute(element, name, context);
        if (StringUtil.isEmpty(text))
            return defaultValue;
        text = ScriptUtil.render(text, context);
        return Integer.parseInt(text);
    }
    
    private boolean parseBooleanAttribute(Element element, String name, Context context, boolean defaultValue) {
        String text = parseAttribute(element, name, context);
        if (StringUtil.isEmpty(text))
            return defaultValue;
        text = ScriptUtil.render(text, context);
        return Boolean.parseBoolean(text);
    }
    
    private ErrorHandler parseOnError(Element element, String category) {
    	String levelName = parseAttribute(element, "onError", context);
    	if (levelName == null)
    		levelName = defaultErrorHandler;
    	Level level = Level.valueOf(levelName);
    	return new ErrorHandler(category, level);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
