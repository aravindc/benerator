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
import org.databene.model.ModelParser;
import org.databene.model.Processor;
import org.databene.commons.*;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    
    private static final Collection<String> CREATE_ENTITIES_EXT_SETUP = CollectionUtil.toSet("pagesize", "threads", "consumer");

    private ModelParser parser = new ModelParser();
    
    private ExecutorService executor;
    private Escalator escalator;
    
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
        this.executor = Executors.newCachedThreadPool();
        this.escalator = new LoggerEscalator();
    }

    public void processFile(String uri) throws IOException {
        try {
            long startTime = java.lang.System.currentTimeMillis();
            BeneratorContext context = new BeneratorContext();
            context.set("benerator", this);
            Document document = XMLUtil.parse(uri);
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
            long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
            logger.info("Created a total of " + ConfiguredEntityGenerator.entityCount() + " entities " +
                    "in " + elapsedTime + " ms " +
                    "(~" + RoundedNumberFormat.format(ConfiguredEntityGenerator.entityCount() * 3600000L / elapsedTime, 0) + " p.h.)");
        } finally {
            this.executor.shutdownNow();
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void parseRootChild(Element element, Set<Heavyweight> resources, Context context) {
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
            parser.parseInclude(element, context);
        else if ("echo".equals(elementType))
            parseEcho(element, context);
        else if ("database".equals(elementType))
            parseDatabase(element, resources, context);
        else
            throw new ConfigurationError("Unknown element: " + elementType);
    }

    private void parseProperty(Element element, Context context) {
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

    private void parseEcho(Element element, Context context) {
        String message = parseAttribute(element, "message", context);
        System.out.println(ScriptUtil.render(message, context, defaultScript));
    }

    private Object parseBean(Element element, Set<Heavyweight> resources, Context context) {
        try {
            Object bean = parser.parseBean(element, context);
            if (bean instanceof DescriptorProvider)
                dataModel.addDescriptorProvider((DescriptorProvider) bean);
            if (bean instanceof Heavyweight)
                resources.add((Heavyweight)bean);
            return bean;
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseDatabase(Element element, Set<Heavyweight> resources, Context context) {
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
            dataModel.addDescriptorProvider(db);
            resources.add(db);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseRunTask(Element element, Context context) {
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

    private PageListener parsePager(Element element, Context context) {
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

    private void parseAndRunCreateEntities(Element element, Set<Heavyweight> resources, Context context) {
        PagedCreateEntityTask task = parseCreateEntities(element, resources, context, false);
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

    private PagedCreateEntityTask parseCreateEntities(Element element, Set<Heavyweight> resources, Context context, boolean isSubTask) {
        InstanceDescriptor descriptor = mapEntityDescriptorElement(element, context);
        descriptor.setNullable(false);
        logger.info(descriptor);
        // parse consumers
        Collection<Consumer<Entity>> consumers = parseConsumers(element, resources, context);
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
                subs.add(parseCreateEntities((Element)node, resources, context, true));
        }
        // parse task properties
        int count    = parseIntAttribute(element, "count", context, -1);
        int pageSize = parseIntAttribute(element, "pagesize", context, defaultPagesize);
        int threads  = parseIntAttribute(element, "threads", context, 1);
        // done
        return new PagedCreateEntityTask(
                descriptor.getName(), count, pageSize, threads, subs, 
                configuredGenerator, consumers, executor, isSubTask);
    }

    private Collection<Consumer<Entity>> parseConsumers(Element parent, Set<Heavyweight> resources, Context context) {
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
        else if (tmp instanceof Consumer)
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

}
