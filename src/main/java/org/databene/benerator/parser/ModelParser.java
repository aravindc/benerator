/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.parser;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.ArrayFormat;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptConverter;
import static org.databene.benerator.parser.xml.XmlDescriptorParser.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Parses databene model files.<br/><br/>
 * Created: 04.03.2008 16:43:09
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ModelParser {
	
    private static final Log logger = LogFactory.getLog(ModelParser.class);

    private BeneratorContext context;
    private BasicParser basicParser;
	
    public ModelParser(BeneratorContext context) {
		this.context = context;
		this.basicParser = new BasicParser();
	}

	public Object parseBean(Element element) {
        String beanId = parseStringAttribute(element, "id", context);
        String beanClass = parseStringAttribute(element, "class", context);
        String beanSpec = parseStringAttribute(element, "spec", context);
        Object bean = null;
        if (beanClass != null) {
	        logger.debug("Instantiating bean of class " + beanClass + " (id=" + beanId + ")");
	        bean = XMLElement2BeanConverter.convert(element, context, new ScriptConverter(context), context);
	        if (!StringUtil.isEmpty(beanId))
	            BeanUtil.setPropertyValue(bean, "id", beanId, false);
        } else if (beanSpec != null) {
	        logger.debug("Instantiating bean: " + beanSpec + " (id=" + beanId + ")");
	        Construction construction = basicParser.parseConstruction(beanSpec, context, context);
	        bean = construction.evaluate();
        } else
        	throw new ConfigurationError("Syntax error in definition of bean " + beanId);
        context.set(beanId, bean);
        return bean;
    }

    public ComponentDescriptor parseSimpleTypeComponent(Element element, ComplexTypeDescriptor owner) {
        return parseSimpleTypeComponent(element, owner, null);
    }

    public ComponentDescriptor parseSimpleTypeComponent(
    		Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor) {
        String name = XMLUtil.localName(element);
        if ("part".equals(name) || "attribute".equals(name))
            return parsePart(element, owner, false, descriptor);
        else if ("id".equals(name))
            return parseId(element, descriptor);
        else if ("reference".equals(name))
            return parseReference(element, descriptor);
        else
            throw new ConfigurationError("Expected one of these element names: " +
            		"'id', 'attribute', 'reference' or 'part'. Found: " + name);
    }

    public ComplexTypeDescriptor parseComplexType(Element ctElement, ComplexTypeDescriptor descriptor) {
        assertElementName(ctElement, "entity", "type");
        descriptor = new ComplexTypeDescriptor(descriptor.getName(), descriptor);
        mapTypeDetails(ctElement, descriptor);
        for (Element child : XMLUtil.getChildElements(ctElement))
            parseComplexTypeChild(child, descriptor);
        return descriptor;
    }

    public void parseComplexTypeChild(Element element, ComplexTypeDescriptor descriptor) {
        String childName = XMLUtil.localName(element);
        if ("variable".equals(childName))
            parseVariable(element, descriptor);
        else
            throw new UnsupportedOperationException("element type not supported here: " + childName);
    }

    public PartDescriptor parsePart(Element element, ComplexTypeDescriptor owner, 
            boolean complex, ComponentDescriptor descriptor) {
        assertElementName(element, "part", "attribute");
        PartDescriptor result;
        if (descriptor instanceof PartDescriptor)
            result = (PartDescriptor) descriptor;
        else if (descriptor != null)
            result = new PartDescriptor(descriptor.getName(), descriptor.getTypeName());
        else {
            String type = normalizeNull(element.getAttribute("type"));
            result = new PartDescriptor(element.getAttribute("name"), type);
        }
        mapInstanceDetails(element, complex, result);
        if (result.getDeclaredDetailValue("minCount") == null)
            result.setDetailValue("minCount", 1);
        if (result.getDeclaredDetailValue("maxCount") == null)
            result.setDetailValue("maxCount", 1);
        if (owner != null) {
            ComponentDescriptor parentComponent = owner.getComponent(result.getName());
            if (parentComponent != null) {
                TypeDescriptor parentType = parentComponent.getType();
                result.getLocalType(false).setParent(parentType);
            }
        }
        return result;
    }

    public SimpleTypeDescriptor parseSimpleType(Element element) {
        assertElementName(element, "type");
        return parseSimpleType(element, new SimpleTypeDescriptor(null, (String) null));
    }

    public SimpleTypeDescriptor parseSimpleType(Element element, SimpleTypeDescriptor descriptor) {
        assertElementName(element, "type");
        return mapTypeDetails(element, descriptor);
    }

    public InstanceDescriptor parseVariable(Element varElement, ComplexTypeDescriptor parent) {
        assertElementName(varElement, "variable");
        InstanceDescriptor descriptor = new InstanceDescriptor(varElement.getAttribute("name"));
        InstanceDescriptor variable = mapInstanceDetails(varElement, false, descriptor);
        parent.addVariable(variable);
        return variable;
    }

    public String parseInclude(Element element) {
        String uri = parseStringAttribute(element, "uri", context);
        uri = IOUtil.resolveLocalUri(uri, context.getContextUri());
        try {
            importProperties(uri);
            return uri;
        } catch (IOException e) {
            throw new ConfigurationError("Properties file not found for uri: " + uri);
        }
    }

    public void importProperties(String uri) throws IOException {
        logger.debug("reading properties from uri: " + uri);
        ScriptConverter preprocessor = new ScriptConverter(context);
        DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
        IOUtil.readProperties(uri, converter);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private <T extends TypeDescriptor> T mapTypeDetails(Element element, T descriptor) {
    	NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
        	Attr attr = (Attr) attributes.item(i);
            String detailValue = parseStringAttribute(attr, context);
            descriptor.setDetailValue(attr.getName(), detailValue);
        }
        return descriptor;
    }
  
    private <T extends InstanceDescriptor> T mapInstanceDetails(
    		Element element, boolean complexType, T descriptor) {
        TypeDescriptor localType = descriptor.getLocalType();
        Map<String, String> attributes = XMLUtil.getAttributes(element);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String detailName = entry.getKey();
            if (detailName.equals("type"))
                continue;
            Object tmp = renderAttribute(detailName, entry.getValue(), context);
			String detailString = ToStringConverter.convert(tmp, null);
            if (descriptor.supportsDetail(detailName))
                descriptor.setDetailValue(detailName, detailString);
            else {
                if (localType == null) {
                    String partType = attributes.get("type");
                    if (partType == null)
                    	partType = descriptor.getTypeName();
                    if (partType == null) {
                    	String sourceSpec = attributes.get("source");
                    	if (sourceSpec != null) {
                    		Object source = context.get(sourceSpec);
                    		if (source != null) {
                    			if (source instanceof Generator) {
                    				if (((Generator<?>) source).getGeneratedType() == Entity.class)
                    					partType = "entity";
                    			} else if (source instanceof EntitySource) {
                    				partType = "entity";
                    			} 
                    			// TODO v0.6 how to handle simple types and (DB)System sources?
                    		} else if (sourceSpec.endsWith(".ent.csv") || sourceSpec.endsWith(".flat.csv") 
                    				|| sourceSpec.endsWith(".dbunit.xml")) {
                    			partType = "entity";
                    		}
                    		// TODO v0.6 how to handle properties of beans in context?
                    	}
                    }
                    if (partType != null) {
                        TypeDescriptor localTypeParent = DataModel.getDefaultInstance().getTypeDescriptor(partType);
                        String name = attributes.get("name");
                        localType = (localTypeParent instanceof ComplexTypeDescriptor ? 
                        		new ComplexTypeDescriptor(name, partType) : 
                        			new SimpleTypeDescriptor(name, partType));
                    }
                    descriptor.setLocalType(localType);
                }
                if (localType == null)
                    localType = descriptor.getLocalType(complexType); // create new local type
                localType.setDetailValue(detailName, detailString);
            }
        }
        return descriptor;
    }
    
    private void assertElementName(Element element, String... expectedNames) {
        String elementName = XMLUtil.localName(element);
        for (String expectedName : expectedNames)
            if (elementName.equals(expectedName))
                return;
        String message;
        if (expectedNames.length == 1)
            message = "Expected element '" + expectedNames[0] + "', found: " + elementName;
        else
            message = "Expected one of these element names: '" + ArrayFormat.format(expectedNames) + "', " +
            		"found: " + elementName;
        throw new IllegalArgumentException(message);
    }
    private String normalizeNull(String text) {
        return ("".equals(text) ? null : text);
    }

    private IdDescriptor parseId(Element element, ComponentDescriptor descriptor) {
        assertElementName(element, "id");
        IdDescriptor result;
        if (descriptor instanceof IdDescriptor)
            result = (IdDescriptor) descriptor;
        else if (descriptor != null)
            result = new IdDescriptor(descriptor.getName(), descriptor.getTypeName());
        else
            result = new IdDescriptor(element.getAttribute("name"), element.getAttribute("type"));
        return mapInstanceDetails(element, false, result);
    }

    private ReferenceDescriptor parseReference(Element element, ComponentDescriptor descriptor) {
        assertElementName(element, "reference");
        ReferenceDescriptor result;
        if (descriptor instanceof ReferenceDescriptor)
            result = (ReferenceDescriptor) descriptor;
        else if (descriptor != null)
            result = new ReferenceDescriptor(descriptor.getName(), descriptor.getTypeName());
        else
            result = new ReferenceDescriptor(element.getAttribute("name"), element.getAttribute("type"));
        return mapInstanceDetails(element, false, result);
    }

	public void parseImport(Element element) {
		String attribute = element.getAttribute("class");
		if (!StringUtil.isEmpty(attribute))
			context.importClass(attribute);
		attribute = element.getAttribute("domain");
		if (!StringUtil.isEmpty(attribute))
			importDomain(attribute);
		if ("true".equals(element.getAttribute("defaults")))
			context.importDefaults();
	}

	public void importDomain(String domain) {
		if (domain.indexOf('.') < 0)
			context.importPackage("org.databene.domain." + domain);
		else
			context.importPackage(domain);
	}

}
