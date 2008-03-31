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

package org.databene.platform.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.StringUtil;
import org.databene.commons.Context;
import org.databene.commons.xml.NamespaceAlias;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.ModelParser;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.FeatureDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.UnionSimpleTypeDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.databene.commons.xml.XMLUtil.*;

/**
 * Parses an XML schema file into a benerator metadata structure.<br/>
 * <br/>
 * Created: 27.02.2008 09:40:45
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLSchemaDescriptorProvider extends DefaultDescriptorProvider {
    
    public static final String XML_REPRESENTATION = "xml:style";

    private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    
    // attributes ------------------------------------------------------------------------------------------------------
    
    private ModelParser parser = new ModelParser();
    private Context context;
    private DataModel dataModel;
    private List<String> propertiesFiles;

    
    // constructors ----------------------------------------------------------------------------------------------------
    
    public XMLSchemaDescriptorProvider(String schemaUri, Context context) {
        this(schemaUri, context, DataModel.getDefaultInstance());
    }
    
    public XMLSchemaDescriptorProvider(String schemaUri, Context context, DataModel dataModel) {
        super(schemaUri);
        this.context = context;
        this.dataModel = dataModel;
        this.propertiesFiles = new ArrayList<String>();
        setSchemaUri(schemaUri);
    }
    
    // interface -------------------------------------------------------------------------------------------------------
    
    public void setSchemaUri(String schemaUri) {
        try {
            Document document = parse(schemaUri);
            mapDocument(document);
        } catch (IOException e) {
            throw new ConfigurationError("Error parsing schemaUri: " + schemaUri, e);
        }
    }
    
    public String[] getPropertiesFiles() {
        return CollectionUtil.toArray(propertiesFiles);
    }

    public Context getContext() {
        return context;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------
    
    private void mapDocument(Document document) {
        logger.debug("parseDocument()");
        NamespaceAlias schemaAlias = XMLUtil.namespaceAlias(document, SCHEMA_NAMESPACE);
        dataModel.addDescriptorProvider(new XMLSchemaNativeTypeProvider(schemaAlias.getAliasName()));
        dataModel.addDescriptorProvider(this);
        Element root = document.getDocumentElement();
        Element[] childElements = XMLUtil.getChildElements(root);
        for (Element element : childElements) {
            String nodeName = localName(element);
            if (ELEMENT.equals(nodeName))
                parseElement(element, null);
            else if (COMPLEX_TYPE.equals(nodeName))
                parseComplexType(element, null, null);
            else if (SIMPLE_TYPE.equals(nodeName))
                parseSimpleType(element, null);
            else if (ANNOTATION.equals(nodeName))
                parseDocumentAnnotation(element);
            else if ("group".equals(nodeName))
                parseGroup(element);
            else if ("import".equals(nodeName))
                parseImport(element);
            else if ("attributeGroup".equals(nodeName))
                parseAttributeGroup(element);
            else
                throw unsupportedElementType(element);
        }
    }

    private void parseDocumentAnnotation(Element element) {
        Annotation annotation = new Annotation(element);
        Element appInfo = annotation.getAppInfo();
        if (appInfo == null)
            return;
        for (Element child : XMLUtil.getChildElements(appInfo)) {
            String childName = XMLUtil.localName(child);
            if ("include".equals(childName)) {
                String filename = parser.parseInclude(child, context);
                propertiesFiles.add(filename);
            } else if ("bean".equals(childName)) {
                parser.parseBean(child, context);
            } else
                throw new UnsupportedOperationException("Document annotation type not supported: " + child.getNodeName());
        }
    }

    private ComplexTypeDescriptor parseComplexType(Element complexType, String parentName, ComplexTypeDescriptor parentDescriptor) {
        String name = (parentName != null ? parentName : complexType.getAttribute(NAME));
        if (logger.isDebugEnabled())
            logger.debug("parseComplexType(" + name + ')');
        if (name == null)
            throw new ConfigurationError("unnamed complex type");
        ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor(name);
        Annotation annotation = null;
        Element[] children = XMLUtil.getChildElements(complexType);
        for (Element child : children) {
            String nodeName = localName(child);
            if ("annotation".equals(nodeName))
                annotation = new Annotation(child);
            else if ("sequence".equals(nodeName))
                parseSequence(child, descriptor);
            else if ("complexContent".equals(nodeName))
                parseComplexContent(child, descriptor);
            else if ("attribute".equals(nodeName)) {
                ComponentDescriptor attrDesc = parseAttribute(child);
                if (attrDesc != null)
                    descriptor.addComponent(attrDesc);
            } else if ("attributeGroup".equals(nodeName)) {
                    ComplexTypeDescriptor group = parseAttributeGroup(child);
                    for (ComponentDescriptor component : group.getComponents())
                        descriptor.addComponent(component);
            } else
                throw unsupportedElementType(child);
        }
        descriptor = parseComplexTypeAppinfo(descriptor, annotation);
        addDescriptor(descriptor);
        return descriptor;
    }
    
    private ComplexTypeDescriptor parseComplexTypeAppinfo(
            ComplexTypeDescriptor descriptor, Annotation annotation) {
        if (annotation == null || annotation.getAppInfo() == null)
            return descriptor;
        
        Element appInfo = annotation.getAppInfo();
        Element[] infos = XMLUtil.getChildElements(appInfo);
        if (infos.length > 1)
            throw new ConfigurationError("Cannot handle more than one appinfo in a complex type");
        Element info = infos[0];

        parser.parseComplexTypeChild(info, descriptor, context);
        return descriptor;
    }

    private void parseComplexContent(Element complexContent, ComplexTypeDescriptor descriptor) {
        Element[] children = XMLUtil.getChildElements(complexContent);
        for (Element child : children) {
            String nodeName = localName(child);
            if ("extension".equals(nodeName))
                parseExtension(child, descriptor);
            else
                throw unsupportedElementType(child);
        }
    }

    private void parseExtension(Element extension, ComplexTypeDescriptor descriptor) {
        String base = extension.getAttribute("base");
        ComplexTypeDescriptor baseDescriptor = (ComplexTypeDescriptor) getTypeDescriptor(base);
        descriptor.setParentName(baseDescriptor.getName());
        Element[] children = XMLUtil.getChildElements(extension);
        for (Element child : children) {
            String nodeName = localName(child);
            if ("attribute".equals(nodeName)) {
                ComponentDescriptor attribute = parseAttribute(child);
                if (attribute != null)
                    descriptor.addComponent(attribute);
            } else
                throw unsupportedElementType(child);
        }
    }

    private FeatureDescriptor parseElement(Element element, ComplexTypeDescriptor parentDescriptor) {
        String name = element.getAttribute(NAME);
        if (logger.isDebugEnabled())
            logger.debug("parseElement(" + element.getAttribute(NAME) + ')');
        FeatureDescriptor descriptor = null;
        if (!StringUtil.isEmpty(element.getAttribute("ref")))
            descriptor = parseElementRef(element, parentDescriptor);
        Annotation annotation = null;
        Element[] children = XMLUtil.getChildElements(element);
        for (Element child : children) {
            String nodeName = localName(child);
            if (COMPLEX_TYPE.equals(nodeName))
                descriptor = parseComplexType(child, name, parentDescriptor);
            else if (SIMPLE_TYPE.equals(nodeName))
                descriptor = parseSimpleType(child, parentDescriptor);
            else if ("key".equals(nodeName))
                parseKey(child);
            else if ("keyref".equals(nodeName))
                parseKeyRef(child);
            else if (ANNOTATION .equals(nodeName))
                annotation = new Annotation(child);
            else
                throw unsupportedElementType(child);
        }
        
        if (descriptor == null) { 
            String type = element.getAttribute("type");
            if (!StringUtil.isEmpty(type))
                return parseElementDefByType(element, parentDescriptor);
        }
        descriptor = parseElementAppInfo(descriptor, annotation);
        if (descriptor == null)
            throw new UnsupportedOperationException("Don't know how to handle element: " + name);
        return descriptor;
    }

    private FeatureDescriptor parseElementAppInfo(
            FeatureDescriptor descriptor, Annotation annotation) {
        if (annotation == null || annotation.getAppInfo() == null)
            return descriptor;
        
        Element appInfo = annotation.getAppInfo();
        Element[] infos = XMLUtil.getChildElements(appInfo);
        
        for (Element info : infos) {
            String childName = XMLUtil.localName(info);
            if ("bean".equals(childName))
                parser.parseBean(info, context);
            else if ("variable".equals(childName))
                parser.parseVariable(info, (ComplexTypeDescriptor) descriptor, context);
            else if (descriptor instanceof PartDescriptor) {
                descriptor = parser.parsePart(info, null, true, (PartDescriptor) descriptor, context);
            } else if (descriptor instanceof ComplexTypeDescriptor) {
                descriptor = parser.parseComplexType(info, (ComplexTypeDescriptor) descriptor, context);
            } else if (descriptor instanceof SimpleTypeDescriptor) {
                descriptor = parser.parseSimpleType(info, (SimpleTypeDescriptor) descriptor, context);
            } else
                throw new UnsupportedOperationException("Unsupported type: " + descriptor.getClass().getName());
        }
        return descriptor;
    }

    /**
     * Parses code like
     * <pre>
     *   <xs:element name="variable" type="generator-setup"/>
     * </pre>
     * @param element
     */
    private TypeDescriptor parseElementDefByType(Element element, ComplexTypeDescriptor owner) {
        String name = element.getAttribute(NAME);
        String typeName = element.getAttribute("type");
        TypeDescriptor parent = dataModel.getTypeDescriptor(typeName);
        if (parent != null) {
            SimpleTypeDescriptor simpleType = new SimpleTypeDescriptor(name, typeName);
            //addDescriptor(simpleType);
            if (owner != null) {
                PartDescriptor refDesc = new PartDescriptor(name);
                refDesc.setLocalType(simpleType);
                parseOccurrences(element, refDesc);
                refDesc.setPSInfo(XML_REPRESENTATION, "element");
                Element anno = XMLUtil.getChildElement(element, false, false, "annotation");
                if (anno != null)
                    refDesc = parseAttributeAppinfo(new Annotation(anno), refDesc);
                owner.addComponent(refDesc);
            } else
                addDescriptor(new SimpleTypeDescriptor(name, typeName));
            return simpleType;
        } else {
            if (owner != null) {
                PartDescriptor partDescriptor = new PartDescriptor(name, typeName);
                parseOccurrences(element, partDescriptor);
                owner.addComponent(partDescriptor);
            } else
                addDescriptor(new ComplexTypeDescriptor(name, typeName));
            return parent;
        }
    }

    private PartDescriptor parseElementRef(Element element, ComplexTypeDescriptor parentDescriptor) {
        String refName = element.getAttribute("ref");
        if (StringUtil.isEmpty(refName))
            throw new ConfigurationError("no ref specified in element");
        PartDescriptor descriptor = new PartDescriptor(refName, refName);
        parseOccurrences(element, descriptor);
        parentDescriptor.addComponent(descriptor);
        return descriptor;
    }

    private void parseOccurrences(Element element, InstanceDescriptor descriptor) {
        Long minOccurs = XMLUtil.getLongAttribute(element, "minOccurs", 1L);
        String maxOccursString = element.getAttribute("maxOccurs");
        Long maxOccurs = 1L;
        if (!StringUtil.isEmpty(maxOccursString))
            maxOccurs = ("unbounded".equals(maxOccursString) ? null : Long.parseLong(maxOccursString));
        descriptor.setMinCount(minOccurs);
        descriptor.setMaxCount(maxOccurs);
    }

    private void parseKeyRef(Element child) {
        logger.warn("KeyRefs are not supported, yet. Ignoring keyRef: " + child.getAttribute("name")); // TODO v1.0 implement parseKeyRef
    }

    private void parseKey(Element child) {
        logger.warn("Keys are not supported, yet. Ignoring key: " + child.getAttribute("name")); // TODO v1.0 implement parseKey
    }

    private ComponentDescriptor parseAttribute(Element attributeElement) {
        String name = attributeElement.getAttribute(NAME);
        if (logger.isDebugEnabled())
            logger.debug("parseAttribute(" + name + ')');
        if (StringUtil.isEmpty(name))
            throw new ConfigurationError("Unnamed attribute");
        Element[] children = XMLUtil.getChildElements(attributeElement);
        String use = attributeElement.getAttribute("use");
        Boolean nullable = ("required".equals(use) ? Boolean.FALSE : null);
        Annotation annotation = null;
        ComponentDescriptor descriptor = null;
        for (Element child : children) {
            String nodeName = localName(child);
            if ("annotation".equals(nodeName))
                annotation = new Annotation(child);
            else if (SIMPLE_TYPE.equals(nodeName)) {
                descriptor = new PartDescriptor(name, parseSimpleType(child, null));
            } else
                throw unsupportedElementType(child);
        }
        String type = attributeElement.getAttribute("type");
        if (descriptor == null)
            if (type != null) {
                descriptor = new PartDescriptor(name, type);
                if (nullable != null && !nullable)
                    descriptor.setNullable(false);
            }
        if (annotation != null && annotation.getAppInfo() != null)
            descriptor = parseAttributeAppinfo(annotation, descriptor);
        String fixed = attributeElement.getAttribute("fixed");
        if (!StringUtil.isEmpty(fixed))
            descriptor.getLocalType(false).setValues(new String[] { fixed });
        else {
            String defaultValue = attributeElement.getAttribute("default");
            if (!StringUtil.isEmpty(defaultValue))
                descriptor.getLocalType(false).setValues(new String[] { defaultValue });
        }
        descriptor.setCount(1L);
        if ("prohibited".equals(attributeElement.getAttribute("use")))
            descriptor.setMode(Mode.ignored);
        if (descriptor == null) 
                throw new ConfigurationError("Unable to parse attribute " + name);
        return descriptor;
    }
    
    private <T extends ComponentDescriptor> T parseAttributeAppinfo(Annotation annotation, T descriptor) {
        Element appInfo = annotation.getAppInfo();
        Element[] infos = XMLUtil.getChildElements(appInfo);
        if (infos.length > 1)
            throw new ConfigurationError("Cannot handle more than one appinfo in a simple type");
        Element info = infos[0];
        return (T) parser.parseSimpleTypeComponent(info, null, descriptor, context);
    }

    private SimpleTypeDescriptor parseSimpleType(
            Element simpleType, ComplexTypeDescriptor parentDescriptor) {
        Annotation annotation = null;
        SimpleTypeDescriptor descriptor = null;
        String name = simpleType.getAttribute("name");
        if (logger.isDebugEnabled())
            logger.debug("parseSimpleType(" + name + ')');
        for (Element child : XMLUtil.getChildElements(simpleType)) {
            String localName = localName(child);
            if (ANNOTATION.equals(localName)) {
                annotation = new Annotation(child);
            } else if (UNION.equals(localName)) {
                descriptor = parseUnion(child, name);
            } else if (RESTRICTION.equals(localName)) {
                descriptor = parseRestriction(child, name);
            } else
                throw unsupportedElementType(child);
        }
        if (descriptor == null) {
            String type = simpleType.getAttribute(TYPE);
            descriptor = new SimpleTypeDescriptor(name, type);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            parseSimpleTypeAppinfo(annotation, descriptor);
        if (!StringUtil.isEmpty(name)) {
            addDescriptor(descriptor);
            if (parentDescriptor != null)
                parentDescriptor.addComponent(new PartDescriptor(name, descriptor.getName()));
        }
        return descriptor;
    }

    private SimpleTypeDescriptor parseSimpleTypeAppinfo(
            Annotation annotation, SimpleTypeDescriptor descriptor) {
        Element appInfo = annotation.getAppInfo();
        Element[] infos = XMLUtil.getChildElements(appInfo);
        if (infos.length > 1)
            throw new ConfigurationError("Cannot handle more than one appinfo in a simple type");
        parser.parseSimpleType(infos[0], descriptor, context);
        return descriptor;
    }

    private SimpleTypeDescriptor parseUnion(Element union, String name) {
        if (logger.isDebugEnabled())
            logger.debug("parseUnion(" + name + ')');
        UnionSimpleTypeDescriptor descriptor = new UnionSimpleTypeDescriptor(name);
        Element[] children = XMLUtil.getChildElements(union);
        for (Element child : children) {
            String nodeName = localName(child);
            if (SIMPLE_TYPE.equals(nodeName)) {
                descriptor.addAlternative(parseSimpleType(child, null));
            } else
                throw unsupportedElementType(child);
        }
        String memberTypes = union.getAttribute("memberTypes");
        if (!StringUtil.isEmpty(memberTypes)) {
            String[] tokens = StringUtil.tokenize(memberTypes, ' ');
            for (String token : tokens)
                if (!StringUtil.isEmpty(token))
                    descriptor.addAlternative(new SimpleTypeDescriptor("_local", token));
        }
        return descriptor;
    }

    private SimpleTypeDescriptor parseRestriction(Element restriction, String name) {
        String base = XMLUtil.localName(restriction.getAttribute(BASE));
        SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor(name, base);
        Element[] children = XMLUtil.getChildElements(restriction);
        for (Element child : children) {
            String nodeName = localName(child);
            String value = child.getAttribute(VALUE);
            if (ENUMERATION.equals(nodeName)) {
                descriptor.addValue(value);
            } else if (MIN_INCLUSIVE.equals(nodeName)) {
                descriptor.setMin(value);
            } else if (MAX_INCLUSIVE.equals(nodeName)) {
                descriptor.setMax(value);
            } else if (LENGTH.equals(nodeName)) {
                int length = Integer.parseInt(value);
                descriptor.setMinLength(length);
                descriptor.setMaxLength(length);
            } else if (BeanUtil.hasProperty(descriptor.getClass(), nodeName)) {
                BeanUtil.setPropertyValue(descriptor, nodeName, value, false);
            } else
                logger.warn("Ignoring restriction " + nodeName + ": " + value);
        }
        return descriptor;
    }

    private void parseImport(Element importElement) {
        logger.debug("parseImport()");
        throw unsupportedElementType(importElement); // TODO v0.5.1 implement parseImport()
    }

    private void parseGroup(Element group) {
        logger.debug("parseGroup()");
        throw unsupportedElementType(group); // TODO v0.5.1 implement parseGroup()
    }

    private ComplexTypeDescriptor parseAttributeGroup(Element group) {
        logger.debug("parseAttributeGroup()");
        // check if it's an attributeGroup reference
        String refName = normalizedAttributeValue(group, "ref");
        if (refName != null) {
            ComplexTypeDescriptor refdType = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(refName);
            if (refdType == null)
                throw new ConfigurationError("referenced attributeGroup not found: " + refName);
            return refdType;
        }
        // create a new attributeGroup
        String name = normalizedAttributeValue(group, "name");
        ComplexTypeDescriptor type = new ComplexTypeDescriptor(name);
        Annotation annotation = null;
        for (Element child : XMLUtil.getChildElements(group)) {
            String elType = XMLUtil.localName(child);
            if ("attribute".equals(elType)) 
                type.addComponent(parseAttribute(child));
            else if ("attributeGroup".equals(elType)) { // TODO v0.5.1 map as parent relationship (could be several ones)
                ComplexTypeDescriptor childGroup = parseAttributeGroup(child);
                for (ComponentDescriptor component : childGroup.getComponents())
                    type.addComponent(component);
            } else if ("annotation".equals(elType))
                annotation = new Annotation(child);
            else
                throw unsupportedElementType(child);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            logger.warn("ignoring appinfo of attributeGroup: " + name);
        addDescriptor(type);
        return type;
    }

    private void parseSequence(Element sequence, ComplexTypeDescriptor owner) {
        logger.debug("parseSequence()"); // TODO v0.5.1 evaluate minCount/maxCount for sequence
        Element[] children = XMLUtil.getChildElements(sequence);
        for (Element child : children) {
            String nodeName = localName(child);
            if (ELEMENT.equals(nodeName))
                parseElement(child, owner);
            else
                throw unsupportedElementType(child);
        }
    }

    private UnsupportedOperationException unsupportedElementType(Element element) {
        return new UnsupportedOperationException("Not a supported element type: " + element.getNodeName());
    }
    
    private static final String SIMPLE_TYPE = "simpleType";

    private static final String COMPLEX_TYPE = "complexType";

    public static final String ANNOTATION = "annotation";

    public static final String UNION = "union";
    
    public static final String ELEMENT = "element";
    public static final String NAME = "name";

    public static final String RESTRICTION = "restriction";
    public static final String BASE = "base";
    public static final String VALUE = "value";
    public static final String LENGTH = "length";
    public static final String MAX_INCLUSIVE = "maxInclusive";
    public static final String MIN_INCLUSIVE = "minInclusive";
    public static final String ENUMERATION = "enumeration";

    private static final String TYPE = "type";

    private static Log logger = LogFactory.getLog(XMLSchemaDescriptorProvider.class);

}
