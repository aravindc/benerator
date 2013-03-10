/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
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

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.databene.script.expression.ExpressionUtil.*;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManager;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.parser.xml.BeanParser;
import org.databene.benerator.engine.parser.xml.BeneratorParseContext;
import org.databene.benerator.engine.parser.xml.IncludeParser;
import org.databene.benerator.engine.statement.BeanStatement;
import org.databene.benerator.engine.statement.IncludeStatement;
import org.databene.benerator.parser.ModelParser;
import org.databene.commons.Assert;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.context.ContextAware;
import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.AlternativeGroupDescriptor;
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
import org.databene.model.data.UnresolvedTypeDescriptor;
import org.databene.script.Expression;
import org.databene.script.expression.ConstantExpression;
import org.databene.script.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class XMLSchemaDescriptorProvider extends DefaultDescriptorProvider implements ContextAware, ResourceManager {
    
    private static Logger LOGGER = LoggerFactory.getLogger(XMLSchemaDescriptorProvider.class);

	private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	public static final String INCLUDE = "include";
	public static final String IMPORT = "import";
	public static final String SIMPLE_TYPE = "simpleType";
	public static final String COMPLEX_TYPE = "complexType";

    public static final String ANNOTATION = "annotation";

    public static final String SEQUENCE = "sequence";
    public static final String CHOICE = "choice";
    public static final String EXTENSION = "extension";
    public static final String UNION = "union";
    
    public static final String ELEMENT = "element";
    public static final String GROUP = "group";
    public static final String ATTRIBUTE_GROUP = "attributeGroup";
    public static final String NAME = "name";

    public static final String RESTRICTION = "restriction";
    public static final String BASE = "base";
    public static final String VALUE = "value";
    public static final String LENGTH = "length";
    public static final String MIN_INCLUSIVE = "minInclusive";
    public static final String MIN_EXCLUSIVE = "minExclusive";
    public static final String MAX_EXCLUSIVE = "maxExclusive";
    public static final String MAX_INCLUSIVE = "maxInclusive";
    public static final String ENUMERATION = "enumeration";

    private static final String TYPE = "type";
    private static final String REF = "ref";
	private static final String KEY = "key";
	private static final String KEYREF = "keyref";
	private static final String UNIQUE = "unique";
	private static final String ALL = "all";
	private static final String COMPLEX_CONTENT = "complexContent";
	private static final String SIMPLE_CONTENT = "simpleContent";
	private static final String ATTRIBUTE = "attribute";
	
    // attributes ------------------------------------------------------------------------------------------------------
    
    private BeneratorContext context;
    private String schemaUri;

    private ModelParser parser;
	private Map<String, String> namespaces;
	private ResourceManager resourceManager = new ResourceManagerSupport();
	
	
	
	// constructors ----------------------------------------------------------------------------------------------------
    
    public XMLSchemaDescriptorProvider(String schemaUri, BeneratorContext context) {
        super(schemaUri, context.getDataModel(), true);
        new XMLNativeTypeDescriptorProvider(SCHEMA_NAMESPACE, dataModel);
        this.namespaces = new HashMap<String, String>();
        parser = new ModelParser(context);
        setContext(context);
        setSchemaUri(schemaUri);
    }
    
    // interface -------------------------------------------------------------------------------------------------------
    
    public void setSchemaUri(String schemaUri) {
    	this.schemaUri = schemaUri;
    	checkSchema();
    }
    
    @Override
	public void setContext(Context context) {
    	this.context = (BeneratorContext) context;
    	checkSchema();
    }
    
    public BeneratorContext getContext() {
        return context;
    }
    
    // ResourceManager interface implementation ------------------------------------------------------------------------

	@Override
	public boolean addResource(Closeable resource) {
		return resourceManager.addResource(resource);
	}
	
	@Override
	public void close() {
		resourceManager.close();
	}

    // private helpers -------------------------------------------------------------------------------------------------
    
    private void checkSchema() {
    	if (!StringUtil.isEmpty(schemaUri) && context != null) {
	        try {
	    		Document document = parse(schemaUri);
	    		this.namespaces = getNamespaces(document);
	            this.id = getTargetNamespace(document);
	            dataModel.addDescriptorProvider(this);
	            parseStructure(document);
	            parseDetails(document);
	        } catch (IOException e) {
	            throw new ConfigurationError("Error parsing schemaUri: " + schemaUri, e);
	        }
    	}
    }

	private void parseStructure(Document document) throws IOException {
        LOGGER.debug("parseStructure()");
        Element root = document.getDocumentElement();
        Element[] childElements = XMLUtil.getChildElements(root);
        for (Element element : childElements) {
            String nodeName = localName(element);
            String nameAttribute = element.getAttribute("name");
            Set<String> COMPLEX_ELEMENTS = CollectionUtil.toSet(COMPLEX_TYPE, GROUP, ATTRIBUTE_GROUP);
            if (COMPLEX_ELEMENTS.contains(nodeName))
            	addTypeDescriptor(new ComplexTypeDescriptor(nameAttribute, this));
            else if (SIMPLE_TYPE.equals(nodeName))
            	addTypeDescriptor(new SimpleTypeDescriptor(nameAttribute, this));
            else if (ELEMENT.equals(nodeName)) {
            	String typeName = element.getAttribute("type");
				if (!StringUtil.isEmpty(typeName)) {
					TypeDescriptor elementType = dataModel.getTypeDescriptor(typeName);
					if (elementType instanceof SimpleTypeDescriptor)
						addTypeDescriptor(new SimpleTypeDescriptor(nameAttribute, this));
					else if (elementType instanceof SimpleTypeDescriptor)
	                	addTypeDescriptor(new ComplexTypeDescriptor(nameAttribute, this));
					else
						addTypeDescriptor(new UnresolvedTypeDescriptor(nameAttribute, this, typeName));
				} else if (XMLUtil.getChildElements(element, false, "complexType").length > 0) {
                	addTypeDescriptor(new ComplexTypeDescriptor(nameAttribute, this));
            	} else if (XMLUtil.getChildElements(element, false, SIMPLE_TYPE).length > 0) {
                	addTypeDescriptor(new SimpleTypeDescriptor(nameAttribute, this));
            	} else
            		addTypeDescriptor(new ComplexTypeDescriptor(nameAttribute, this));
            } else if (ANNOTATION.equals(nodeName))
                parseDocumentAnnotation(element);
            else if (IMPORT.equals(nodeName))
                parseImport(element);
            else if (INCLUDE.equals(nodeName))
                parseStructureOfInclude(element);
        }
        resolveTypes();
	}

	private void resolveTypes() {
		boolean unresolved = false;
		do {
			for (TypeDescriptor type : typeMap.values())
				if (type instanceof UnresolvedTypeDescriptor) {
					TypeDescriptor parent = type.getParent();
					if (parent instanceof SimpleTypeDescriptor)
						addTypeDescriptor(new SimpleTypeDescriptor(type.getName(), this, type.getParentName()));
					else if (parent instanceof ComplexTypeDescriptor)
						addTypeDescriptor(new ComplexTypeDescriptor(type.getName(), this, type.getParentName()));
					else if (parent == null)
						throw new ConfigurationError("parentType " + type.getParentName() + " not found for " + type.getName());
					else
						unresolved = true;
					
				}
		} while (unresolved);	
	}

	private void parseDetails(Document document) throws IOException {
        LOGGER.debug("parseDetails()");
        Element root = document.getDocumentElement();
        Element[] childElements = XMLUtil.getChildElements(root);
        for (Element element : childElements) {
            String nodeName = localName(element);
            if (ELEMENT.equals(nodeName))
                parseTopLevelElement(element);
            else if (COMPLEX_TYPE.equals(nodeName))
                parseComplexType(element, null, null, true);
            else if (SIMPLE_TYPE.equals(nodeName))
                addTypeDescriptor(parseSimpleType(null, element));
            else if (GROUP.equals(nodeName))
                parseGroup(element);
            else if (ATTRIBUTE_GROUP.equals(nodeName))
                parseAttributeGroup(element);
            else if (IMPORT.equals(nodeName))
                parseImport(element);
            else if (INCLUDE.equals(nodeName))
                parseDetailsOfInclude(element);
            else if (!ANNOTATION.equals(nodeName))
                throw unsupportedElementType(element, root);
        }
	}

    private void parseDocumentAnnotation(Element element) {
        Annotation annotation = new Annotation(element);
        Element appInfo = annotation.getAppInfo();
        if (appInfo == null)
            return;
        for (Element child : XMLUtil.getChildElements(appInfo)) {
            String childName = XMLUtil.localName(child);
            if (INCLUDE.equals(childName)) {
                IncludeStatement statement = (IncludeStatement) new IncludeParser().parse(child, null, new BeneratorParseContext(this));
                statement.execute(context);
            } else if ("bean".equals(childName)) {
                Expression<?> constructionExpression = BeanParser.parseBeanExpression(child);
                String id = child.getAttribute("id");
				BeanStatement beanStatement = new BeanStatement(id, constructionExpression, this);
				try {
					beanStatement.execute(context);
				} finally {
					IOUtil.close(beanStatement);
				}
            } else
                throw new UnsupportedOperationException("Document annotation type not supported: " 
                		+ child.getNodeName());
        }
    }

    private ComplexTypeDescriptor parseComplexType(Element complexTypeElement, String parentName, Annotation annotationBefore, boolean global) {
        String name = (parentName != null ? parentName : complexTypeElement.getAttribute(NAME));
        LOGGER.debug("parseComplexType({})", name);
        if (name == null)
            throw new ConfigurationError("unnamed complex type");
        ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor(name, this);
        if (annotationBefore != null)
        	descriptor = parseElementAppInfo(descriptor, annotationBefore);
        Annotation annotation = null;
        Element[] children = XMLUtil.getChildElements(complexTypeElement);
        for (Element child : children) {
            String nodeName = localName(child);
            if ("annotation".equals(nodeName))
                annotation = new Annotation(child);
            else if (SEQUENCE.equals(nodeName))
                parseSequence(child, descriptor);
            else if (COMPLEX_CONTENT.equals(nodeName))
                parseComplexContent(child, descriptor);
            else if (ALL.equals(nodeName))
                parseAll(child, descriptor);
            else if (SIMPLE_CONTENT.equals(nodeName))
                parseSimpleContent(child, descriptor);
            else if (ATTRIBUTE.equals(nodeName)) {
                parseAttribute(child, descriptor);
            } else if (ATTRIBUTE_GROUP.equals(nodeName)) {
                    ComplexTypeDescriptor group = parseAttributeGroup(child);
                    for (InstanceDescriptor component : group.getParts())
                        descriptor.addPart(component);
            } else
                throw unsupportedElementType(child, complexTypeElement);
        }
        descriptor = parseComplexTypeAppinfo(descriptor, annotation);
        if (global)
        	addTypeDescriptor(descriptor);
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

        parser.parseComplexTypeChild(info, descriptor);
        return descriptor;
    }

    private void parseComplexContent(Element complexContent, ComplexTypeDescriptor owner) {
        Element[] children = XMLUtil.getChildElements(complexContent);
        for (Element child : children) {
            String nodeName = localName(child);
            if (EXTENSION.equals(nodeName))
                parseExtension(child, owner);
            else if (RESTRICTION.equals(nodeName))
                parseComplexRestriction(child, owner);
            else
                throw unsupportedElementType(child, complexContent);
        }
    }

    private void parseComplexRestriction(Element restrictionElement, ComplexTypeDescriptor owner) {
    	// TODO v0.8 test this
        Element[] children = XMLUtil.getChildElements(restrictionElement);
        for (Element child : children) {
            String nodeName = localName(child);
            if (ATTRIBUTE.equals(nodeName))
                parseAttribute(child, owner);
            else
                throw unsupportedElementType(child, restrictionElement);
        }
	}

	private ComplexTypeDescriptor parseSimpleContent(Element simpleContentElement, ComplexTypeDescriptor complexType) {
        Annotation annotation = null;
        LOGGER.debug("parseSimpleContent()");
        for (Element child : XMLUtil.getChildElements(simpleContentElement)) {
            String localName = localName(child);
            if (ANNOTATION.equals(localName))
                annotation = new Annotation(child);
            else if (RESTRICTION.equals(localName))
            	parseSimpleContentRestriction(child, complexType);
            else if (EXTENSION.equals(localName))
            	parseSimpleContentExtension(child, complexType);
            else
                throw unsupportedElementType(child, simpleContentElement);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            complexType = parseComplexTypeAppinfo(complexType, annotation);
        return complexType;
    }

    private void parseSimpleContentRestriction(Element restriction, ComplexTypeDescriptor complexType) {
		String baseName = restriction.getAttribute("base");
		Assert.notNull(baseName, "base attribute");
		TypeDescriptor base = dataModel.getTypeDescriptor(baseName);
		Assert.notNull(base, "base type");
		if (!(base instanceof ComplexTypeDescriptor))
			throw new ConfigurationError("Expected ComplexTypeDescriptor for " + baseName + ", found: " + 
					base.getClass().getSimpleName());
		complexType.setParent(base);
		SimpleTypeDescriptor content = (SimpleTypeDescriptor) complexType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getLocalType(false);
		Assert.notNull(content, "content");
		parseRestrictionChildren(restriction, content);
	}

	private void parseSimpleContentExtension(Element extension, ComplexTypeDescriptor complexType) {
		String baseName = extension.getAttribute("base");
		Assert.notNull(baseName, "base attribute");
		TypeDescriptor base = dataModel.getTypeDescriptor(baseName);
		Assert.notNull(base, "base type");
		if (base instanceof SimpleTypeDescriptor) {
			complexType.addComponent(new PartDescriptor(ComplexTypeDescriptor.__SIMPLE_CONTENT, this, baseName, null, 
					new ConstantExpression<Long>(1L), new ConstantExpression<Long>(1L)));
		} else if (base instanceof ComplexTypeDescriptor)
			complexType.setParentName(baseName);
		else
			throw new UnsupportedOperationException("not a supported type: " + base.getClass());
		parseAttributes(extension, complexType);
	}

	private void parseExtension(Element extension, ComplexTypeDescriptor descriptor) {
        String base = extension.getAttribute(BASE);
        descriptor.setParentName(base);
        parseAttributes(extension, descriptor);
    }

	private void parseAttributes(Element extension, ComplexTypeDescriptor owner) {
		Element[] children = XMLUtil.getChildElements(extension);
        for (Element child : children) {
            String nodeName = localName(child);
            if (ATTRIBUTE.equals(nodeName))
                parseAttribute(child, owner);
            else
                throw unsupportedElementType(child, extension);
        }
	}

    private TypeDescriptor parseTopLevelElement(Element element) {
        String name = element.getAttribute(NAME);
        LOGGER.debug("parseTopLevelElement({})", name);
        TypeDescriptor descriptor = null;
        Annotation annotation = null;
        Element[] children = XMLUtil.getChildElements(element);
        for (Element child : children) {
            String nodeName = localName(child);
            if (COMPLEX_TYPE.equals(nodeName)) {
                descriptor = parseComplexType(child, name, annotation, false);
                annotation = null;
            } else if (SIMPLE_TYPE.equals(nodeName))
                descriptor = parseSimpleType(name, child);
            else if (KEY.equals(nodeName))
                parseKey(child);
            else if (KEYREF.equals(nodeName))
                parseKeyRef(child);
            else if (ANNOTATION .equals(nodeName))
                annotation = new Annotation(child);
            else
                throw unsupportedElementType(child, element);
        }
        
        if (descriptor == null) { 
            String type = element.getAttribute("type");
            if (!StringUtil.isEmpty(type))
                descriptor = parseTopLevelElementWithType(element);
        }
    	descriptor = parseElementAppInfo(descriptor, annotation);
        if (descriptor == null)
            descriptor = new ComplexTypeDescriptor(name, this);
        addTypeDescriptor(descriptor);
        return descriptor;
    }

    private ComponentDescriptor parseContainedElement(Element element, ComplexTypeDescriptor owner) {
        String name = element.getAttribute(NAME);
        LOGGER.debug("parseElement({})", element.getAttribute(NAME));
        Assert.notNull(owner, "owner");
        PartDescriptor descriptor = null;
        if (!StringUtil.isEmpty(element.getAttribute(REF)))
            descriptor = parseElementRef(element);
        Annotation annotation = null;
        Element[] children = XMLUtil.getChildElements(element);
        for (Element child : children) {
            String nodeName = localName(child);
            if (COMPLEX_TYPE.equals(nodeName)) {
                ComplexTypeDescriptor type = parseComplexType(child, name, annotation, false);
				descriptor = new PartDescriptor(name, this, type);
				annotation = null;
            } else if (SIMPLE_TYPE.equals(nodeName)) {
        		SimpleTypeDescriptor simpleType = parseSimpleType(name, child);
            	ComplexTypeDescriptor complexType = wrapSimpleTypeWithComplexType(simpleType);
            	descriptor = new PartDescriptor(name, this, complexType);
            } else if (KEY.equals(nodeName))
                parseKey(child);
            else if (KEYREF.equals(nodeName))
                parseKeyRef(child);
            else if (UNIQUE.equals(nodeName))
                parseUnique(child);
            else if (ANNOTATION .equals(nodeName))
                annotation = new Annotation(child);
            else
                throw unsupportedElementType(child, element);
        }
        
        if (descriptor == null) { 
            String type = element.getAttribute("type");
            if (!StringUtil.isEmpty(type)) {
            	descriptor = parseElementWithType(element);
            }
        } else
        	parseElementAppInfo(descriptor, annotation);
        if (descriptor == null)
            descriptor = new PartDescriptor(name, this, "string"); // possibly there i a more useful default type
        parseOccurrences(element, descriptor);
        if ("false".equals(element.getAttribute("nillable")))
        	descriptor.setNullable(false);
        owner.addComponent(descriptor);
        return descriptor;
    }

	private ComplexTypeDescriptor wrapSimpleTypeWithComplexType(SimpleTypeDescriptor simpleType) {
		ComplexTypeDescriptor complexType = new ComplexTypeDescriptor(simpleType.getName(), this);
		complexType.addComponent(new PartDescriptor(ComplexTypeDescriptor.__SIMPLE_CONTENT, this, simpleType));
		return complexType;
	}

    private void parseUnique(Element child) {
    	// TODO v1.0 automatically support uniqueness
        LOGGER.warn("<unique> is not supported. Please define own annotations or setup for uniqueness assurance"); 
	}

	private PartDescriptor parseElementRef(Element element) {
        String refName = element.getAttribute(REF);
        if (StringUtil.isEmpty(refName))
            throw new ConfigurationError("no ref specified in element");
        TypeDescriptor type = dataModel.getTypeDescriptor(refName);
        PartDescriptor descriptor;
        if (type instanceof SimpleTypeDescriptor) {
            ComplexTypeDescriptor complexType = new ComplexTypeDescriptor(refName, this);
    		complexType.addComponent(new PartDescriptor(ComplexTypeDescriptor.__SIMPLE_CONTENT, this, refName));
            descriptor = new PartDescriptor(refName, this, complexType);
        } else {
        	descriptor = new PartDescriptor(refName, this, type);
        }
        return descriptor;
    }

    @SuppressWarnings("unchecked")
    private <T extends FeatureDescriptor> T parseElementAppInfo(T descriptor, Annotation annotation) {
        if (annotation == null || annotation.getAppInfo() == null)
            return descriptor;
        
        Element appInfo = annotation.getAppInfo();
        Element[] infos = XMLUtil.getChildElements(appInfo);
        
        for (Element info : infos) {
            String childName = XMLUtil.localName(info);
            if ("bean".equals(childName))
                BeanParser.parseBeanExpression(info);
            else if ("variable".equals(childName))
                parser.parseVariable(info, (ComplexTypeDescriptor) descriptor);
            else if (ATTRIBUTE.equals(childName))
                descriptor = (T) parser.parseAttribute(info, null, (PartDescriptor) descriptor);
            else if ("part".equals(childName))
                descriptor = (T) parser.parsePart(info, null, (PartDescriptor) descriptor);
            else if (descriptor instanceof ComplexTypeDescriptor)
                descriptor = (T) parser.parseComplexType(info, (ComplexTypeDescriptor) descriptor);
            else if (descriptor instanceof SimpleTypeDescriptor)
                descriptor = (T) parser.parseSimpleType(info, (SimpleTypeDescriptor) descriptor);
            else if ("type".equals(childName)) {
            	TypeDescriptor typeDescriptor = (descriptor instanceof InstanceDescriptor ? ((InstanceDescriptor) descriptor).getTypeDescriptor() : (TypeDescriptor) descriptor);
            	if (typeDescriptor instanceof SimpleTypeDescriptor)
            		descriptor = (T) parser.parseSimpleType(info, (SimpleTypeDescriptor) typeDescriptor);
            	else
            		descriptor = (T) parser.parseComplexType(info, (ComplexTypeDescriptor) typeDescriptor);
            } else
                throw new UnsupportedOperationException("Unsupported element (" + childName + ") " +
                		"or descriptor type: " + descriptor.getClass().getName());
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
    private TypeDescriptor parseTopLevelElementWithType(Element element) {
        String name = element.getAttribute(NAME);
        String typeName = element.getAttribute("type");
        TypeDescriptor type = getType(typeName);
        if (type == null)
        	type = getType(name);
        if (type != null) {
        	if (type instanceof SimpleTypeDescriptor)
        		return new SimpleTypeDescriptor(name, this, typeName);
        	else if (type instanceof ComplexTypeDescriptor)
        		return new ComplexTypeDescriptor(name, this, typeName);
//        	else if (parentType instanceof UnresolvedTypeDescriptor)
//        		return new UnresolvedTypeDescriptor(name, typeName);
        	else
        		throw new UnsupportedOperationException("Unsupported descriptor: " + type);
        } else
        	throw new UnsupportedOperationException("Unsupported type: " + typeName);
    }

    private PartDescriptor parseElementWithType(Element element) {
        String name = element.getAttribute(NAME);
        String typeName = element.getAttribute("type");
        TypeDescriptor type = getType(typeName);
        if (type == null)
        	throw new ConfigurationError("Undefined type: " + typeName);
        PartDescriptor refDesc;
    	if (type instanceof SimpleTypeDescriptor) {
    		// the element wraps a simple type
            SimpleTypeDescriptor localType = new SimpleTypeDescriptor(name, this, typeName); 
            ComplexTypeDescriptor contentType = wrapSimpleTypeWithComplexType(localType);
            refDesc = new PartDescriptor(name, this, contentType);
            Element anno = XMLUtil.getChildElement(element, false, false, "annotation");
            if (anno != null)
                parseSimpleTypeAppinfo(new Annotation(anno), localType);
    	} else {
            ComplexTypeDescriptor localType = new ComplexTypeDescriptor(name, this, typeName);
            refDesc = new PartDescriptor(name, this, localType);
            Element anno = XMLUtil.getChildElement(element, false, false, "annotation");
            if (anno != null)
                refDesc = parseAttributeAppinfo(new Annotation(anno), refDesc);
    	}
        parseOccurrences(element, refDesc);
        return refDesc;
    }

	private TypeDescriptor getType(String typeName) {
		int sep = typeName.indexOf(':');
		if (sep < 0)
			return dataModel.getTypeDescriptor(typeName);
		String nsAlias = typeName.substring(0, sep);
		String namespace = getNamespaceForAlias(nsAlias);
		String typeInNs = typeName.substring(sep + 1);
		TypeDescriptor type = dataModel.getTypeDescriptor(namespace, typeInNs);
		return type;
	}

    private String getNamespaceForAlias(String nsAlias) {
		return namespaces.get(nsAlias);
	}

	private void parseOccurrences(Element element, InstanceDescriptor descriptor) {
        Long minOccurs = XMLUtil.getLongAttribute(element, "minOccurs", 1L);
        String maxOccursString = element.getAttribute("maxOccurs");
        Long maxOccurs = 1L;
        if (!StringUtil.isEmpty(maxOccursString))
            maxOccurs = ("unbounded".equals(maxOccursString) ? null : Long.parseLong(maxOccursString));
        if (minOccurs.equals(maxOccurs) && descriptor.getCount() != null) {
        	descriptor.setCount(constant(maxOccurs));
        	descriptor.setMinCount(null);
        	descriptor.setMaxCount(null);
        } else {
        	descriptor.setCount(null);
        	if (descriptor.getMinCount() == null)
        		descriptor.setMinCount(constant(minOccurs));
        	if (descriptor.getMaxCount() == null)
        		descriptor.setMaxCount(constant(maxOccurs));
        }
    }

    private void parseKeyRef(Element child) {
    	// TODO v1.0 implement parseKeyRef
        LOGGER.warn("KeyRefs are not supported, yet. Ignoring keyRef: " + child.getAttribute("name")); 
    }

    private void parseKey(Element child) {
    	// TODO v1.0 implement parseKey
        LOGGER.warn("Keys are not supported, yet. Ignoring key: " + child.getAttribute("name")); 
    }

    @SuppressWarnings("null")
    private ComponentDescriptor parseAttribute(Element attributeElement, ComplexTypeDescriptor owner) {
        String name = attributeElement.getAttribute(NAME);
        LOGGER.debug("parseAttribute({})", name);
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
                descriptor = new PartDescriptor(name, this, parseSimpleType(null, child));
            } else
                throw unsupportedElementType(child, attributeElement);
        }
        String type = attributeElement.getAttribute("type");
        if (descriptor == null && type != null) {
                descriptor = new PartDescriptor(name, this, type);
                if (nullable != null && !nullable)
                    descriptor.setNullable(false);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            descriptor = parseAttributeAppinfo(annotation, descriptor);
        String fixed = attributeElement.getAttribute("fixed");
        if (!StringUtil.isEmpty(fixed))
            ((SimpleTypeDescriptor) descriptor.getLocalType(false)).setValues(fixed);
        else {
            String defaultValue = attributeElement.getAttribute("default");
            if (!StringUtil.isEmpty(defaultValue))
                ((SimpleTypeDescriptor) descriptor.getLocalType(false)).setValues(defaultValue);
        }
        descriptor.setCount(new ConstantExpression<Long>(1L));
        if ("prohibited".equals(attributeElement.getAttribute("use")))
            descriptor.setMode(Mode.ignored);
        owner.addComponent(descriptor);
        return descriptor;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends ComponentDescriptor> T parseAttributeAppinfo(Annotation annotation, T descriptor) {
        Element appInfo = annotation.getAppInfo();
        if (appInfo == null)
        	return descriptor;
        Element[] infos = XMLUtil.getChildElements(appInfo);
        if (infos.length > 1)
            throw new ConfigurationError("Cannot handle more than one appinfo in a simple type");
        if (infos.length == 0)
        	return descriptor;
        Element info = infos[0];
        return (T) parser.parseSimpleTypeComponent(info, null, descriptor);
    }

    private SimpleTypeDescriptor parseSimpleType(String name, Element simpleType) {
        Annotation annotation = null;
        SimpleTypeDescriptor descriptor = null;
        if (name == null)
        	name = simpleType.getAttribute("name");
        LOGGER.debug("parseSimpleType({})", name);
        for (Element child : XMLUtil.getChildElements(simpleType)) {
            String localName = localName(child);
            if (ANNOTATION.equals(localName)) {
                annotation = new Annotation(child);
            } else if (UNION.equals(localName)) {
                descriptor = parseUnion(child, name);
            } else if (RESTRICTION.equals(localName)) {
                descriptor = parseSimpleTypeRestriction(child, name);
            } else
                throw unsupportedElementType(child, simpleType);
        }
        if (descriptor == null) {
            String type = simpleType.getAttribute(TYPE);
            descriptor = new SimpleTypeDescriptor(name, this, type);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            descriptor = parseSimpleTypeAppinfo(annotation, descriptor);
        return descriptor;
    }

    private SimpleTypeDescriptor parseSimpleTypeAppinfo(
            Annotation annotation, SimpleTypeDescriptor descriptor) {
        Element appInfo = annotation.getAppInfo();
        if (appInfo != null) {
	        Element[] infos = XMLUtil.getChildElements(appInfo);
	        if (infos.length > 1)
	            throw new ConfigurationError("Cannot handle more than one appinfo in a simple type");
	        parser.parseSimpleType(infos[0], descriptor);
        }
        return descriptor;
    }

    private SimpleTypeDescriptor parseUnion(Element union, String name) {
        LOGGER.debug("parseUnion({})", name);
        UnionSimpleTypeDescriptor descriptor = new UnionSimpleTypeDescriptor(name, this);
        Element[] children = XMLUtil.getChildElements(union);
        for (Element child : children) {
            String nodeName = localName(child);
            if (SIMPLE_TYPE.equals(nodeName)) {
                descriptor.addAlternative(parseSimpleType(null, child));
            } else
                throw unsupportedElementType(child, union);
        }
        String memberTypes = union.getAttribute("memberTypes");
        if (!StringUtil.isEmpty(memberTypes)) {
            String[] tokens = StringUtil.tokenize(memberTypes, ' ');
            for (String token : tokens)
                if (!StringUtil.isEmpty(token))
                    descriptor.addAlternative(new SimpleTypeDescriptor("_local", this, token));
        }
        return descriptor;
    }

    private SimpleTypeDescriptor parseSimpleTypeRestriction(Element restriction, String name) {
        String base = XMLUtil.localName(restriction.getAttribute(BASE));
        SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor(name, this, base);
        parseRestrictionChildren(restriction, descriptor);
        return descriptor;
    }

	private void parseRestrictionChildren(Element restriction,
			SimpleTypeDescriptor descriptor) {
		Element[] children = XMLUtil.getChildElements(restriction);
        for (Element child : children) {
            String nodeName = localName(child);
            String value = child.getAttribute(VALUE);
            if (ENUMERATION.equals(nodeName)) {
            	if (PrimitiveType.STRING.equals(descriptor.getPrimitiveType()))
            		descriptor.addValue("'" + value + "'");
            	else
            		descriptor.addValue(value);
            } else if (MIN_INCLUSIVE.equals(nodeName)) {
                descriptor.setMin(value);
                descriptor.setMinInclusive(true);
            } else if (MIN_EXCLUSIVE.equals(nodeName)) {
                descriptor.setMin(value);
                descriptor.setMinInclusive(false);
            } else if (MAX_INCLUSIVE.equals(nodeName)) {
                descriptor.setMax(value);
                descriptor.setMaxInclusive(true);
            } else if (MAX_EXCLUSIVE.equals(nodeName)) {
                descriptor.setMax(value);
                descriptor.setMaxInclusive(false);
            } else if (LENGTH.equals(nodeName)) {
                int length = Integer.parseInt(value);
                descriptor.setMinLength(length);
                descriptor.setMaxLength(length);
            } else if (BeanUtil.hasProperty(descriptor.getClass(), nodeName)) {
                BeanUtil.setPropertyValue(descriptor, nodeName, value, false);
            } else
                LOGGER.warn("Ignoring restriction " + nodeName + ": " + value);
        }
	}

    private void parseImport(Element importElement) {
        LOGGER.debug("parseImport()");
        throw unsupportedElementType(importElement, null); // TODO v0.8 implement parseImport()
    }

    /** parses an XML Schema inclusion and adds its types to the {@link DataModel} */
    private void parseStructureOfInclude(Element includeElement) throws IOException {
        LOGGER.debug("parseStructureOfInclude()");
    	assert "include".equals(localName(includeElement));
        String location = includeElement.getAttribute("schemaLocation");
        parseStructure(parse(location));
    }

    private void parseDetailsOfInclude(Element includeElement) throws IOException {
        LOGGER.debug("parseDetailsOfInclude()");
    	assert "include".equals(localName(includeElement));
        String location = includeElement.getAttribute("schemaLocation");
        parseDetails(parse(location));
    }

    private void parseGroup(Element group) {
        LOGGER.debug("parseGroup()");
        throw unsupportedElementType(group, null); // TODO v0.8 implement parseGroup()
    }

    private ComplexTypeDescriptor parseAttributeGroup(Element group) {
        LOGGER.debug("parseAttributeGroup()");
        // check if it's an attributeGroup reference
        String refName = normalizedAttributeValue(group, REF);
        if (refName != null) {
            ComplexTypeDescriptor refdType = (ComplexTypeDescriptor) getType(refName);
            if (refdType == null)
                throw new ConfigurationError("referenced attributeGroup not found: " + refName);
            return refdType;
        }
        // create a new attributeGroup
        String name = normalizedAttributeValue(group, "name");
        ComplexTypeDescriptor type = new ComplexTypeDescriptor(name, this);
        Annotation annotation = null;
        for (Element child : XMLUtil.getChildElements(group)) {
            String elType = XMLUtil.localName(child);
            if (ATTRIBUTE.equals(elType)) 
                parseAttribute(child, type);
            else if ("attributeGroup".equals(elType)) { 
            	// TODO v0.8 map as parent relationship (could be several ones)
                ComplexTypeDescriptor childGroup = parseAttributeGroup(child);
                for (InstanceDescriptor component : childGroup.getParts())
                    type.addPart(component);
            } else if ("annotation".equals(elType))
                annotation = new Annotation(child);
            else
                throw unsupportedElementType(child, group);
        }
        if (annotation != null && annotation.getAppInfo() != null)
            LOGGER.warn("ignoring appinfo of attributeGroup: " + name);
        addTypeDescriptor(type);
        return type;
    }

    private void parseSequence(Element sequence, ComplexTypeDescriptor owner) {
        LOGGER.debug("parseSequence()"); // TODO v0.8 evaluate minCount/maxCount for sequence
        parseComponentGroupChildren(sequence, owner);
    }

    private void parseChoice(Element choice, ComplexTypeDescriptor owner) {
        LOGGER.debug("parseChoice()");
        AlternativeGroupDescriptor choiceDescriptor = new AlternativeGroupDescriptor(null, this);
        parseComponentGroupChildren(choice, choiceDescriptor);
        PartDescriptor partDescriptor = new PartDescriptor(null, this, choiceDescriptor);
        parseOccurrences(choice, partDescriptor);
		owner.addComponent(partDescriptor);
	}

    private void parseAll(Element all, ComplexTypeDescriptor owner) {
        LOGGER.debug("parseAll()"); // TODO v0.8 test
        parseComponentGroupChildren(all, owner);	
	}

	private void parseComponentGroupChildren(Element choice, ComplexTypeDescriptor groupDescriptor) {
        Element[] children = XMLUtil.getChildElements(choice);
		for (Element child : children) {
            String nodeName = localName(child);
            if (ELEMENT.equals(nodeName))
                parseContainedElement(child, groupDescriptor);
            else if (SEQUENCE.equals(nodeName))
                parseSequence(child, groupDescriptor);
            else if (CHOICE.equals(nodeName))
                parseChoice(child, groupDescriptor);
            else
                throw unsupportedElementType(child, choice);
        }
	}

	private UnsupportedOperationException unsupportedElementType(Element element, Element parent) {
        String message = "Element type " + element.getNodeName() + " not supported";
        if (parent != null)
        	message += " in " + parent.getNodeName();
		return new UnsupportedOperationException(message);
    }
    
}
