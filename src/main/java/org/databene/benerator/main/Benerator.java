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

import org.databene.platform.xml.XMLElement2BeanConverter;
import org.databene.platform.xml.XMLUtil;
import org.databene.model.Processor;
import org.databene.model.Heavyweight;
import org.databene.model.ConversionException;
import org.databene.model.system.*;
import org.databene.commons.*;
import org.databene.task.TaskContext;
import org.databene.task.TaskRunner;
import org.databene.task.Task;
import org.databene.task.PageListener;
import org.databene.benerator.factory.EntityGeneratorFactory;
import org.databene.benerator.factory.ComponentGeneratorFactory;
import org.databene.benerator.Generator;
import org.databene.model.data.*;
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

/**
 * Parses and executes a benerator setup file.<br/>
 * <br/>
 * Created: 14.08.2007 19:14:28
 */
public class Benerator {

    private static final Log logger = LogFactory.getLog(Benerator.class);

    private DataModel model;

    private int totalEntityCount = 0;

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
    }

    public void processFile(String uri) throws IOException {
        long startTime = java.lang.System.currentTimeMillis();
        TaskContext context = new TaskContext();
        Document document = IOUtil.parseXML(uri);
        Element root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (!(node instanceof Element))
                continue;
            Set<Heavyweight> resources = new HashSet<Heavyweight>();
            parseElement((Element)node, resources, context);
            for (Heavyweight resource : resources) {
                resource.close();
            }
        }
        java.lang.System.out.println("context: " + context);
        long elapsedTime = java.lang.System.currentTimeMillis() - startTime;
        logger.info("Created " + RoundedNumberFormat.format(totalEntityCount, 0) + " entities " +
                "in " + RoundedNumberFormat.format(elapsedTime, 0) + " ms " +
                "(" + RoundedNumberFormat.format(totalEntityCount * 3600000L / elapsedTime, 0) + " p.h.)");
    }

    private void parseElement(Element element, Set<Heavyweight> resources, TaskContext context) {
        String elementType = element.getNodeName();
        if ("bean".equals(elementType))
            parseBean(element, context);
//        else if ("use".equals(elementType))
//            parseUse(element, resources, context);
        else if ("create-entities".equals(elementType))
            parseCreateEntities(element, resources, context);
        else if ("run-task".equals(elementType))
            parseRunTask(element, context);
        else
            throw new ConfigurationError("Unknown element: " + elementType);
    }

    private Object parseBean(Element element, TaskContext context) {
        try {
            String beanId = element.getAttribute("id");
            if (beanId != null)
                logger.debug("Instantiating bean with id '" + beanId + "'");
            else
                logger.debug("Instantiating bean of class " + element.getAttribute("class"));
            Object bean = XMLElement2BeanConverter.convert(element, context);
            if (!StringUtil.isEmpty(beanId)) {
                BeanUtil.setPropertyValue(bean, "id", beanId, false);
                context.set(beanId, bean);
            }
            if (bean instanceof DescriptorProvider)
                model.addDescriptorProvider((DescriptorProvider) bean);
            return bean;
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private void parseRunTask(Element element, TaskContext context) {
        try {
            String beanName = element.getAttribute("name");
            logger.debug("Instantiating task '" + beanName + "'");
            Task task = (Task) XMLElement2BeanConverter.convert(element, context);
            int count = parseInt(element.getAttribute("count"), 1);
            int pageSize = parseInt(element.getAttribute("pagesize"), 1);
            int threads = parseInt(element.getAttribute("threads"), 1);
            PageListener pager = parsePager(element, context);
            TaskRunner.run(task, context, count, pager, pageSize, threads);
        } catch (ConversionException e) {
            throw new ConfigurationError(e);
        }
    }

    private PageListener parsePager(Element element, TaskContext context) {
        String pagerSetup = element.getAttribute("pager");
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

    private int parseInt(String source, int defaultValue) {
        if (StringUtil.isEmpty(source))
            return defaultValue;
        return Integer.parseInt(source);
    }
/*
    private void parseUse(Element useElement, Set<Heavyweight> resources, TaskContext context) {
        NodeList children = useElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!(child instanceof Element))
                continue;
            Element element = (Element) child;
            String elementType = element.getNodeName();
            if ("create-entities".equals(elementType))
                parseCreateEntities(element, resources, context);
            else
                throw new UnsupportedOperationException("Element type not supported; " + elementType);
        }
    }
*/
    private void parseCreateEntities(Element element, Set<Heavyweight> resources, TaskContext context) {
        EntityDescriptor descriptor = mapEntityDescriptorElement(element);
        logger.info("Setting up creation for " + descriptor);
        // parse processors
        Collection<Processor<Entity>> processors = parseProcessors(element, context);
        for (Processor<Entity> processor : processors)
            resources.add(processor);
        // parse variables
        Map<String, Generator> variables = parseVariables(element, context);
        // generate
        Generator<Entity> entityGenerator = EntityGeneratorFactory.createEntityGenerator(descriptor, context);
        int pageSize = 1;
        if (!StringUtil.isEmpty(element.getAttribute("pagesize")))
            pageSize = Integer.parseInt(element.getAttribute("pagesize"));
        logger.debug("Starting entity generation by " + entityGenerator + " with page size " + pageSize);
        int count = 0;
        while (allVariablesAvailable(variables.values())) {
            for (Map.Entry<String, Generator> variable : variables.entrySet()) {
                String name = variable.getKey();
                Object value = variable.getValue().generate();
                context.set(name, value);
            }
            if (!entityGenerator.available())
                break;
            Entity entity = entityGenerator.generate();
            totalEntityCount++;
            // TODO v0.4 process sub-create-entities here
            for (Processor<Entity> processor : processors)
                processor.process(entity);
            for (String variableName : variables.keySet())
                context.set(variableName, null);
            count++;
            if (count % pageSize == 0)
                for (Processor<Entity> processor : processors)
                    processor.flush();
        }
        for (String variableName : variables.keySet()) {
            context.set(variableName, null);
        }
        for (Processor<Entity> processor : processors)
            processor.flush();
    }

    private boolean allVariablesAvailable(Collection<Generator> generators) {
        for (Generator generator : generators)
            if (!generator.available()) {
                logger.debug("No more available: " + generator);
                return false;
            }
        return true;
    }

    private Map<String, Generator> parseVariables(Element element, TaskContext context) {
        HashMap<String, Generator> variables = new HashMap<String, Generator>();
        NodeList varElements = element.getElementsByTagName("variable");
        for (int i = 0; i < varElements.getLength(); i++) {
            Element varElement = (Element) varElements.item(i);
            ComponentDescriptor componentDescriptor = mapComponentDescriptor(varElement);
            Generator generator = ComponentGeneratorFactory.getComponentGenerator(componentDescriptor, context);
            String varName = varElement.getAttribute("name");
            variables.put(varName, generator);
        }
        return variables;
    }

    private Collection<Processor<Entity>> parseProcessors(Element parent, TaskContext context) {
        List<Processor<Entity>> processors = new ArrayList<Processor<Entity>>();
        Processor<Entity> processor;
        if (parent.hasAttribute("processor")) {
            processor = getProcessorFromContext(context, parent.getAttribute("processor"));
            processors.add(processor);
        }
        NodeList processorElements = parent.getElementsByTagName("processor");
        for (int i = 0; i < processorElements.getLength(); i++) {
            Element processorElement = (Element) processorElements.item(i);
            if (processorElement.hasAttribute("ref")) {
                processor = getProcessorFromContext(context, processorElement.getAttribute("ref"));
            } else if (processorElement.hasAttribute("class")) {
                processor = (Processor<Entity>) parseBean(processorElement, context);
            } else
                throw new UnsupportedOperationException("Don't know how to handle " + XMLUtil.format(processorElement));
            processors.add(processor);
        }
        return processors;
    }

    private Processor<Entity> getProcessorFromContext(TaskContext context, String processorId) {
        Processor<Entity> processor;
        Object tmp = context.get(processorId);
        if (tmp instanceof org.databene.model.system.System)
            processor = new SystemProcessor((org.databene.model.system.System) tmp);
        else if (tmp instanceof Processor)
            processor = (Processor<Entity>) tmp;
        else if (StringUtil.isEmpty(processorId))
            throw new ConfigurationError("Empty processor id");
        else if (tmp == null)
            throw new ConfigurationError("Processor not found: " + processorId);
        else
            throw new UnsupportedOperationException("Processor type not supported: " + tmp.getClass());
        return processor;
    }

    private EntityDescriptor mapEntityDescriptorElement(Element element) {
        String entityName = element.getAttribute("name");
        EntityDescriptor parentDescriptor = model.getTypeDescriptor(entityName);
        EntityDescriptor ctDescriptor = new EntityDescriptor(entityName, false, parentDescriptor); // TODO v0.4 how to handle case sensitivity here?
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            if (!"pagesize".equals(attribute.getName()) && !"processor".equals(attribute.getName()))
                ctDescriptor.setDetail(attribute.getName(), attribute.getValue());
        }
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node childNode = nodes.item(i);
            if (!(childNode instanceof Element))
                continue;
            Element childElement = (Element) childNode;
            String childType = childElement.getNodeName();
            if ("attribute".equals(childType)) {
                ComponentDescriptor ad = mapComponentDescriptor(childElement);
                ctDescriptor.setComponentDescriptor(ad);
            }
        }
        return ctDescriptor;
    }

    private ComponentDescriptor mapComponentDescriptor(Element element) {
        String entityName = element.getAttribute("name");
        ComponentDescriptor descriptor;
        String nodeName = element.getNodeName();
        if ("attribute".equals(nodeName) || "variable".equals(nodeName))
            descriptor = new AttributeDescriptor(entityName);
        else
            throw new UnsupportedOperationException("'attribute' or 'variable' element expected, found: " + nodeName);
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            descriptor.setDetail(attribute.getName(), attribute.getValue());
        }
        return descriptor;
    }
}
