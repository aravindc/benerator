/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import java.util.Map;

import org.databene.commons.ConfigurationError;
import org.databene.commons.converter.ToStringConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.TypeDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Provides utility methods for Benerator's XML platform.<br/><br/>
 * Created: 15.01.2014 11:03:25
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XMLPlatformUtil {
	
	private XMLPlatformUtil() {}
	
	public static Entity convertElement2Entity(Element element, DescriptorProvider provider) {
		
		// Determine data type
		String typeName = normalizeName(element.getNodeName());
		TypeDescriptor typeDescriptor = provider.getTypeDescriptor(typeName);
		if (typeDescriptor == null)
			typeDescriptor = new ComplexTypeDescriptor(typeName, provider);
		else if (!(typeDescriptor instanceof ComplexTypeDescriptor))
			throw new ConfigurationError("Expected ComplexTypeDescriptor for type " + typeName + 
					", but found " + typeDescriptor.getClass().getSimpleName());
		
		// create entity
		XmlEntity entity = new XmlEntity((ComplexTypeDescriptor) typeDescriptor);
		entity.setSourceElement(element);
		
		// map attributes
		NamedNodeMap atts = element.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node att = atts.item(i);
			String name = XMLPlatformUtil.normalizeName(att.getNodeName());
			entity.setComponent(name, att.getNodeValue());
		}
		
		// map sub elements that contain only text
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				NodeList grandchildNodes = childElement.getChildNodes();
				if (grandchildNodes.getLength() == 1) {
					Node grandchild = grandchildNodes.item(0);
					if (grandchild instanceof Text) {
						String childElementName = childElement.getNodeName();
						String attributeName = XMLPlatformUtil.normalizeName(childElementName);
						entity.setComponent(attributeName, grandchild.getTextContent());
					}
				}
			}
		}
		return entity;
	}
	
	public static Element mapEntityToElement(Entity source, Element target) {
		for (Map.Entry<String, Object> component : source.getComponents().entrySet())
			mapComponent(component.getKey(), component.getValue(), target);
		return target;
	}
	
	public static void mapComponent(String componentName, Object componentValue, Element target) {
		if (componentValue instanceof Entity)
			return; // ignore sub entities
		
		// if the element has an attribute of an appropriate name, then set it and return...
		NamedNodeMap targetAttributes = target.getAttributes();
		for (int i = 0; i < targetAttributes.getLength(); i++) {
			Node attribute = targetAttributes.item(i);
			if (normalizeName(attribute.getNodeName()).equals(componentName)) {
				target.setAttribute(componentName, convertToString(componentValue));
				return;
			}
		}
		
		// ... otherwise search for a child element and set that one
		NodeList childNodes = target.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				if (componentName.equals(normalizeName(childElement.getNodeName())))
					childElement.setTextContent(convertToString(componentValue));
			}
		}
	}

	public static String convertToString(Object value) {
		return ToStringConverter.convert(value, null);
	}

	public static String normalizeName(String name) {
		return name.replace('-', '_').replace('.', '_');
	}
	
}
