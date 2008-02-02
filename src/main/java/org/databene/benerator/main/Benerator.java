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

import org.databene.platform.db.DBSystem;
import org.databene.platform.xml.XMLElement2BeanConverter;
import org.databene.platform.xml.XMLUtil;
import org.databene.model.Processor;
import org.databene.commons.*;
import org.databene.commons.context.ContextStack;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.context.PropertiesContext;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.databene.task.TaskRunner;
import org.databene.task.Task;
import org.databene.task.PageListener;
import org.databene.benerator.factory.EntityGeneratorFactory;
import org.databene.benerator.factory.ComponentGeneratorFactory;
import org.databene.benerator.factory.GenerationSetup;
import org.databene.benerator.Generator;
import org.databene.model.consumer.Consumer;
import org.databene.model.consumer.ProcessorToConsumerAdapter;
import org.databene.model.data.*;
import org.databene.model.storage.StorageSystem;
import org.databene.model.storage.StorageSystemConsumer;
import org.databene.model.system.SystemToStorageAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parses and executes a benerator setup file.<br/>
 * <br/>
 * Created: 14.08.2007 19:14:28
 * @author Volker Bergmann
 */
public class Benerator implements GenerationSetup {

    private static final Log logger = LogFactory.getLog(Benerator.class);
    
    public  static final String  DEFAULT_SCRIPT   = "ftl";
    public  static final boolean DEFAULT_NULL     = true;
    public  static final String  DEFAULT_ENCODING = SystemInfo.fileEncoding();
    public  static final int     DEFAULT_PAGESIZE = 1;
    
    private DataModel model;
    private ExecutorService executor;
    private Escalator escalator;
    
    private String defaultScript;
    private boolean defaultNull;
    private String defaultEncoding;
    private int defaultPagesize;
    
    //private int totalEntityCount = 0;

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
        this.model = new DataModel();
        this.executor = Executors.newCachedThreadPool();
        this.escalator = new LoggerEscalator();
        this.defaultScript = DEFAULT_SCRIPT;
        this.defaultNull = DEFAULT_NULL;
        this.defaultEncoding = DEFAULT_ENCODING;
        this.defaultPagesize = DEFAULT_PAGESIZE;
    }

    public void processFile(String uri) throws IOException {
        try {
            long startTime = java.lang.System.currentTimeMillis();
            ContextStack context = new ContextStack();
            context.push(new PropertiesContext(java.lang.System.getenv()));
            context.push(new PropertiesContext(java.lang.System.getProperties()));
            context.push(new DefaultContext());
            context.set("benerator", this);
            Document document = IOUtil.parseXML(uri);
            Element root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            Set<Heavyweight> resources = new HashSet<Heavyweight>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (!(node instanceof Element))
                    continue;
                parseRootChild((Element)node, resources, context);
            }
            for (Heavyweight resource : resources) {
                resource.close();
            }
            //java.lang.System.out.println("context: " + context);
            long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
            logger.info("Elapsed time: " + RoundedNumberFormat.format(elapsedTime, 0) + " ms");
            /*
            long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
            logger.info("Created " + RoundedNumberFormat.format(totalEntityCount, 0) + " entities " +
                    "in " + RoundedNumberFormat.format(elapsedTime, 0) + " ms " +
                    "(" + RoundedNumberFormat.format(totalEntityCount * 3600000L / elapsedTime, 0) + " p.h.)");
            */
        } finally {
            this.executor.shutdownNow();
        }
    }

    private void parseRootChild(Element element, Set<Heavyweight> resources, ContextStack context) {
        String elementType = element.getNodeName();
        if ("bean".equals(elementType))
            parseBean(element, resources, context);
        else if ("create-entities".equals(elementType))
            parseAndRunCreateEntities(element, resources, context);
        else if ("run-task".equals(elementType))
            parseRunTask(element, context);
        else if ("property".equals(elementType))
            parseProperty(element, context);
        else if ("include".equals(elementType))
            parseInclude(element, context);
        else if ("echo".equals(elementType))
            parseEcho(element, context);
        else if ("database".equals(elementType))
            parseDatabase(element, resources, context);
        else
            throw new ConfigurationError("Unknown element: " + elementType);
    }

    private void parseProperty(Element element, ContextStack context) {
        String propertyName = element.getAttribute("name");
        Object propertyValue;
        if (element.hasAttribute("value"))
            propertyValue = parseAttribute(element, "value", context);
        else if (element.hasAttribute("ref"))
            propertyValue = context.get(parseAttribute(element, "ref", context));
        else 
            throw new ConfigurationError("Syntax error");
        context.set(propertyName, propertyValue);
    }

    private void parseEcho(Element element, ContextStack context) {
        String message = parseAttribute(element, "message", context);
        System.out.println(ScriptUtil.render(message, context, defaultScript));
    }

    private void parseInclude(Element element, ContextStack context) {
        String uri = parseAttribute(element, "uri", context);
        try {
            ScriptConverter preprocessor = new ScriptConverter(context, defaultScript);
            DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
            IOUtil.readProperties(uri, converter);
        } catch (IOException e) {
            throw new ConfigurationError("Properties not found at uri: " + uri);
        }
    }

    private Object parseBean(Element element, Set<Heavyweight> resources, ContextStack context) {
        try {
            String beanId = parseAttribute(element, "id", context);
            if (beanId != null)
                logger.debug("Instantiating bean with id '" + beanId + "'");
            else
                logger.debug("Instantiating bean of class " + parseAttribute(element, "class", context));
            Object bean = XMLElement2BeanConverter.convert(element, context, new ScriptConverter(context, defaultScript));
            if (!StringUtil.isEmpty(beanId)) {
                BeanUtil.setPropertyValue(bean, "id", beanId, false);
                context.set(beanId, bean);
            }
            if (bean instanceof DescriptorProvider)
                model.addDescriptorProvider((DescriptorProvider) bean);
            if (bean instanceof Heavyweight)
                resources.add((Heavyweight)bean);
            return bean;
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseDatabase(Element element, Set<Heavyweight> resources, ContextStack context) {
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
            context.set(id, db);
            model.addDescriptorProvider(db);
            resources.add(db);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseRunTask(Element element, ContextStack context) {
        try {
            String beanName = parseAttribute(element, "name", context);
            logger.debug("Instantiating task '" + beanName + "'");
            ScriptConverter scriptConverter = new ScriptConverter(context, defaultScript);
            Task task = (Task) XMLElement2BeanConverter.convert(element, context, scriptConverter);
            int count = parseIntAttribute(element, "count", context, 1);
            int pageSize = parseIntAttribute(element, "pagesize", context, defaultPagesize);
            int threads = parseIntAttribute(element, "threads", context, 1);
            PageListener pager = parsePager(element, context);
            TaskRunner.run(task, context, count, pager, pageSize, threads, executor);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private PageListener parsePager(Element element, ContextStack context) {
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

    private void parseAndRunCreateEntities(Element element, Set<Heavyweight> resources, ContextStack context) {
        PagedCreateEntityTask task = parseCreateEntities(element, resources, context, false);
        task.init(context);
        try {
            task.run();
        } finally {
            task.destroy();
        }
    }

    private PagedCreateEntityTask parseCreateEntities(Element element, Set<Heavyweight> resources, ContextStack context, boolean isSubTask) {
        EntityDescriptor descriptor = mapEntityDescriptorElement(element, context);
        logger.info(descriptor);
        // parse consumers
        Collection<Consumer<Entity>> consumers = parseConsumers(element, resources, context);
        // parse variables
        Map<String, Generator<? extends Object>> variables = parseVariables(element, context);
        // generate
        Generator<Entity> entityGenerator = EntityGeneratorFactory.createEntityGenerator(descriptor, context, this);
        int count = parseIntAttribute(element, "count", context, -1);
        int pageSize = parseIntAttribute(element, "pagesize", context, defaultPagesize);
        int threads = parseIntAttribute(element, "threads", context, 1);
        Generator<Entity> configuredGenerator = new ConfiguredGenerator(entityGenerator, variables, context);
        // create sub create-entities
        NodeList subCreates = element.getElementsByTagName("create-entities");
        List<PagedCreateEntityTask> subs = new ArrayList<PagedCreateEntityTask>(subCreates.getLength());
        for (int i = 0; i < subCreates.getLength(); i++) {
            Element ceElement = (Element) subCreates.item(i);
            subs.add(parseCreateEntities(ceElement, resources, context, true));
        }
        // done
        return new PagedCreateEntityTask(
                descriptor.getName(), count, pageSize, threads, subs, 
                configuredGenerator, consumers, executor, isSubTask);
    }

    private Map<String, Generator<? extends Object>> parseVariables(Element parent, ContextStack context) {
        HashMap<String, Generator<? extends Object>> variables = new HashMap<String, Generator<? extends Object>>();
        NodeList varElements = parent.getElementsByTagName("variable");
        for (int i = 0; i < varElements.getLength(); i++) {
            Element varElement = (Element) varElements.item(i);
            ComponentDescriptor componentDescriptor = mapComponentDescriptor(varElement, context);
            Generator<? extends Object> generator = ComponentGeneratorFactory.getComponentGenerator(componentDescriptor, context, this);
            String varName = parseAttribute(varElement, "name", context);
            variables.put(varName, generator);
        }
        return variables;
    }

    private Collection<Consumer<Entity>> parseConsumers(Element parent, Set<Heavyweight> resources, ContextStack context) {
        String entityName = parseAttribute(parent, "name", context);
        List<Consumer<Entity>> consumers = new ArrayList<Consumer<Entity>>();
        Consumer<Entity> consumer;
        if (parent.hasAttribute("consumer")) {
            consumer = getConsumerFromContext(context, parseAttribute(parent, "consumer", context));
            consumers.add(consumer);
        }
        List<Element> consumerElements = getChildElements(parent, "consumer");
        for (int i = 0; i < consumerElements.size(); i++) {
            Element consumerElement = (Element) consumerElements.get(i);
            if (consumerElement.hasAttribute("ref")) {
                consumer = getConsumerFromContext(context, parseAttribute(consumerElement, "ref", context));
            } else if (consumerElement.hasAttribute("class")) {
                consumer = (Consumer<Entity>) parseBean(consumerElement, resources, context);
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

    private Consumer<Entity> getConsumerFromContext(Context context, String consumerId) {
        Consumer<Entity> consumer;
        Object tmp = context.get(consumerId);
        if (tmp instanceof StorageSystem)
            consumer = new StorageSystemConsumer((StorageSystem) tmp);
        else if (tmp instanceof org.databene.model.system.System) {
            StorageSystem storage = new SystemToStorageAdapter((org.databene.model.system.System) tmp);
            consumer = new StorageSystemConsumer(storage);
        } else if (tmp instanceof Consumer)
            consumer = (Consumer<Entity>) tmp;
        else if (tmp instanceof Processor)
            consumer = new ProcessorToConsumerAdapter((Processor<Entity>) tmp);
        else if (StringUtil.isEmpty(consumerId))
            throw new ConfigurationError("Empty consumer id");
        else if (tmp == null)
            throw new ConfigurationError("Consumer not found: " + consumerId);
        else
            throw new UnsupportedOperationException("Consumer type not supported: " + tmp.getClass());
        return consumer;
    }

    private EntityDescriptor mapEntityDescriptorElement(Element element, Context context) {
        String entityName = parseAttribute(element, "name", context);
        EntityDescriptor parentDescriptor = model.getTypeDescriptor(entityName);
        EntityDescriptor ctDescriptor = new EntityDescriptor(entityName, false, parentDescriptor); // TODO v0.5 how to handle case sensitivity here?
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            if (!"pagesize".equals(attribute.getName()) && !"threads".equals(attribute.getName()) && !"consumer".equals(attribute.getName()))
                ctDescriptor.setDetail(attribute.getName(), parseAttribute(attribute, context));
        }
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (!(childNode instanceof Element))
                continue;
            Element childElement = (Element) childNode;
            String childType = childElement.getNodeName();
            if ("attribute".equals(childType)) {
                ComponentDescriptor ad = mapComponentDescriptor(childElement, context);
                ctDescriptor.setComponentDescriptor(ad);
            } else if ("id".equals(childType)) {
                ComponentDescriptor ad = mapComponentDescriptor(childElement, context);
                ctDescriptor.setComponentDescriptor(ad);
            } else if (!"create-entities".equals(childType) 
                    && !"consumer".equals(childType) && !"variable".equals(childType))
                throw new ConfigurationError("Unexpected element: " + childType);
        }
        return ctDescriptor;
    }

    private ComponentDescriptor mapComponentDescriptor(Element element, Context context) {
        String attributeName = parseAttribute(element, "name", context);
        ComponentDescriptor descriptor;
        String nodeName = element.getNodeName();
        if ("attribute".equals(nodeName) || "variable".equals(nodeName))
            descriptor = new AttributeDescriptor(attributeName);
        else if ("id".equals(nodeName))
            descriptor = new IdDescriptor(attributeName);
        else
            throw new UnsupportedOperationException("'attribute' or 'variable' element expected, found: " + nodeName);
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            descriptor.setDetail(attribute.getName(), parseAttribute(attribute, context));
        }
        return descriptor;
    }
    
    // attribute parsing -----------------------------------------------------------------------------------------------
    
    private String parseAttribute(Attr attribute, Context context) {
        String name = attribute.getName();
        String value = attribute.getValue();
        return renderAttribute(name, value, context);
    }

    private String parseAttribute(Element element, String name, Context context) {
        String value = element.getAttribute(name);
        return renderAttribute(name, value, context);
    }

    private int parseIntAttribute(Element element, String name, Context context, int defaultValue) {
        String text = parseAttribute(element, name, context);
        if (StringUtil.isEmpty(text))
            return defaultValue;
        text = ScriptUtil.render(text, context, defaultScript);
        return Integer.parseInt(text);
    }
    
    private String renderAttribute(String name, String value, Context context) {
        if ("script".equals(name))
            return value;
        else
            return ScriptUtil.render(value, context, defaultScript);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    // properties ------------------------------------------------------------------------------------------------------
    
    /**
     * @return the defaultScript
     */
    public String getDefaultScript() {
        return defaultScript;
    }

    /**
     * @param defaultScript the defaultScript to set
     */
    public void setDefaultScript(String defaultScript) {
        this.defaultScript = defaultScript;
    }

    /**
     * @return the defaultNull
     */
    public boolean isDefaultNull() {
        return defaultNull;
    }

    /**
     * @param defaultNull the defaultNull to set
     */
    public void setDefaultNull(boolean defaultNull) {
        this.defaultNull = defaultNull;
    }

    /**
     * @return the defaultEncoding
     */
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     * @param defaultEncoding the defaultEncoding to set
     */
    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * @return the defaultPageSize
     */
    public int getDefaultPagesize() {
        return defaultPagesize;
    }

    /**
     * @param defaultPageSize the defaultPageSize to set
     */
    public void setDefaultPagesize(int defaultPageSize) {
        this.defaultPagesize = defaultPageSize;
    }
        
}
