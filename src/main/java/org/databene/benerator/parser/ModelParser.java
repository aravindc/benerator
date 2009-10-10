/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.xml.XMLBeanExpression;
import org.databene.benerator.engine.task.ImportTask;
import org.databene.benerator.engine.task.IncludeTask;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.ArrayFormat;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.expression.ConstantExpression;
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
import org.databene.task.Task;

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
	
    private BeneratorContext context;
	
    public ModelParser(BeneratorContext context) {
		this.context = context;
	}

	public Expression<Object> parseBean(Element element) {
        return new XMLBeanExpression(element);
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
            result.setMinCount(new ConstantExpression<Long>(1L));
        if (result.getDeclaredDetailValue("maxCount") == null)
            result.setMaxCount(new ConstantExpression<Long>(1L));
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

    public IncludeTask parseInclude(Element element) {
        String uri = parseStringAttribute(element, "uri", context);
        return new IncludeTask(uri);
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
            Object tmp = resolveScript(detailName, entry.getValue(), context);
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

	public Task parseImport(Element element) {
		// TODO these are too many different attribs. Remove some!
		ArrayBuilder<String> classImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> packageImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> domainImports = new ArrayBuilder<String>(String.class); 
		ArrayBuilder<String> platformImports = new ArrayBuilder<String>(String.class); 
		
		// defaults import
		boolean defaults = ("true".equals(element.getAttribute("defaults")));
		
		// check class import
		String attribute = element.getAttribute("class");
		if (!StringUtil.isEmpty(attribute))
			classImports.add(attribute);
		
		// domain import
		attribute = element.getAttribute("domain");
		if (!StringUtil.isEmpty(attribute))
			domainImports.add(attribute);
		
		// multiple domain import
		attribute = element.getAttribute("domains");
		if (!StringUtil.isEmpty(attribute))
			domainImports.addAll(StringUtil.tokenize(attribute, ','));
		
		// package import
		attribute = element.getAttribute("package");
		if (!StringUtil.isEmpty(attribute))
			packageImports.add(attribute);
		
		// platform import
		attribute = element.getAttribute("platform");
		if (!StringUtil.isEmpty(attribute))
			platformImports.add(attribute);
		
		// multiple platform import
		attribute = element.getAttribute("platforms");
		if (!StringUtil.isEmpty(attribute))
			platformImports.addAll(StringUtil.tokenize(attribute, ','));
		
		return new ImportTask(defaults, classImports.toArray(), packageImports.toArray(), 
				domainImports.toArray(), platformImports.toArray());
	}

}
