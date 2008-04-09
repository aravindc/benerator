/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.model;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.main.Benerator;
import org.databene.commons.ArrayFormat;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.DefaultEntryConverter;
import org.databene.commons.xml.XMLElement2BeanConverter;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Parses databene model files.<br/><br/>
 * Created: 04.03.2008 16:43:09
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class ModelParser {
   
    private static final Log logger = LogFactory.getLog(ModelParser.class);
    
    private String defaultScript = Benerator.DEFAULT_SCRIPT;
    
    public String getDefaultScript() {
        return defaultScript;
    }

    public void setDefaultScript(String defaultScript) {
        this.defaultScript = defaultScript;
    }
    
    public Object parseBean(Element element, Context context) {
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
        return bean;
    }

    public ComponentDescriptor parseSimpleTypeComponent(Element element, ComplexTypeDescriptor owner, Context context) {
        return parseSimpleTypeComponent(element, owner, null, context);
    }

    public ComponentDescriptor parseSimpleTypeComponent(Element element, ComplexTypeDescriptor owner, ComponentDescriptor descriptor, Context context) {
        String name = XMLUtil.localName(element);
        if ("part".equals(name) || "attribute".equals(name))
            return parsePart(element, owner, false, descriptor, context);
        else if ("id".equals(name))
            return parseId(element, descriptor, context);
        else if ("reference".equals(name))
            return parseReference(element, descriptor, context);
        else
            throw new ConfigurationError("Expected one of these element names: " +
            		"'part', 'id', 'descriptor'. Found: " + name);
    }

    public ComplexTypeDescriptor parseComplexType(Element ctElement, ComplexTypeDescriptor descriptor, Context context) {
        assertElementName(ctElement, "entity");
        for (Element child : XMLUtil.getChildElements(ctElement))
            parseComplexTypeChild(child, descriptor, context);
        return descriptor;
    }

    public void parseComplexTypeChild(Element element, 
            ComplexTypeDescriptor descriptor, Context context) {
        String childName = XMLUtil.localName(element);
        if ("variable".equals(childName))
            parseVariable(element, descriptor, context);
        else
            throw new UnsupportedOperationException("element type not supported here: " + childName);
    }

    public PartDescriptor parsePart(Element element, ComplexTypeDescriptor owner, 
            boolean complex, ComponentDescriptor descriptor, Context context) {
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
        mapInstanceDetails(element, complex, result, context);
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

    private String normalizeNull(String text) {
        return ("".equals(text) ? null : text);
    }

    private IdDescriptor parseId(Element element, ComponentDescriptor descriptor, Context context) {
        assertElementName(element, "id");
        IdDescriptor result;
        if (descriptor instanceof IdDescriptor)
            result = (IdDescriptor) descriptor;
        else if (descriptor != null)
            result = new IdDescriptor(descriptor.getName(), descriptor.getTypeName());
        else
            result = new IdDescriptor(element.getAttribute("name"), element.getAttribute("type"));
        return mapInstanceDetails(element, false, result, context);
    }

    private ReferenceDescriptor parseReference(Element element,
            ComponentDescriptor descriptor, Context context) {
        assertElementName(element, "id");
        ReferenceDescriptor result;
        if (descriptor instanceof ReferenceDescriptor)
            result = (ReferenceDescriptor) descriptor;
        else if (descriptor != null)
            result = new ReferenceDescriptor(descriptor.getName(), descriptor.getTypeName());
        else
            result = new ReferenceDescriptor(element.getAttribute("name"), element.getAttribute("type"));
        return mapInstanceDetails(element, false, result, context);
    }

    public SimpleTypeDescriptor parseSimpleType(Element element, Context context) {
        assertElementName(element, "type");
        return parseSimpleType(element, new SimpleTypeDescriptor(null, null), context);
    }

    public SimpleTypeDescriptor parseSimpleType(
            Element element, SimpleTypeDescriptor descriptor, Context context) {
        assertElementName(element, "type");
        return mapTypeDetails(element, descriptor, context);
    }

    public InstanceDescriptor parseVariable(
            Element varElement, ComplexTypeDescriptor parent, Context context) {
        assertElementName(varElement, "variable");
        InstanceDescriptor descriptor = new InstanceDescriptor(varElement.getAttribute("name"));
        InstanceDescriptor variable = mapInstanceDetails(varElement, false, descriptor, context);
        parent.addVariable(variable);
        return variable;
    }

    public String parseAttribute(Element element, String name, Context context) {
        String value = element.getAttribute(name);
        return renderAttribute(name, value, context);
    }

    public String parseInclude(Element element, Context context) {
        String uri = parseAttribute(element, "uri", context);
        try {
            importProperties(uri, context);
            return uri;
        } catch (IOException e) {
            throw new ConfigurationError("Properties file not found for uri: " + uri);
        }
    }

    public void importProperties(String uri, Context context) throws IOException {
        logger.debug("reading properties: " + uri);
        ScriptConverter preprocessor = new ScriptConverter(context, defaultScript);
        DefaultEntryConverter converter = new DefaultEntryConverter(preprocessor, context, true);
        IOUtil.readProperties(uri, converter);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private <T extends TypeDescriptor> T mapTypeDetails(Element element, T descriptor, Context context) {
        for (Map.Entry<String, String> entry : XMLUtil.getAttributes(element).entrySet()) {
            String detailName = entry.getKey();
            String detailValue = renderAttribute(detailName, entry.getValue(), context);
            descriptor.setDetailValue(entry.getKey(), detailValue);
        }
        return descriptor;
    }
  
    private <T extends InstanceDescriptor> T mapInstanceDetails(Element element, boolean complexType, T descriptor, Context context) {
        TypeDescriptor localType = descriptor.getLocalType();
        Map<String, String> attributes = XMLUtil.getAttributes(element);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String detailName = entry.getKey();
            if (detailName.equals("type"))
                continue;
            String detailValue = renderAttribute(detailName, entry.getValue(), context);
            if (descriptor.supportsDetail(detailName))
                descriptor.setDetailValue(detailName, detailValue);
            else {
                if (localType == null) {
                    String partType = attributes.get("type");
                    if (partType == null)
                    	partType = descriptor.getTypeName();
                    if (partType != null) {
                        TypeDescriptor localTypeParent = DataModel.getDefaultInstance().getTypeDescriptor(partType);
                        String name = attributes.get("name");
                        localType = (localTypeParent instanceof ComplexTypeDescriptor ? new ComplexTypeDescriptor(name, partType) : new SimpleTypeDescriptor(name, partType));
                    }
                    descriptor.setLocalType(localType);
                }
                if (localType == null)
                    localType = descriptor.getLocalType(complexType); // create new local type
                localType.setDetailValue(detailName, detailValue);
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

    private String parseAttribute(Attr attribute, Context context) {
        String name = attribute.getName();
        String value = attribute.getValue();
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

}
