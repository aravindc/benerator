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

import org.databene.commons.ConfigurationError;
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
 * Converts XML {@link Element}s to {@link Entity}s.<br/><br/>
 * Created: 14.01.2014 17:17:05
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XmlElement2EntityConverter {
	
	public static Entity convert(Element element, DescriptorProvider provider) {
		
		// Determine data type
		String elementName = element.getNodeName();
		TypeDescriptor typeDescriptor = provider.getTypeDescriptor(elementName);
		if (typeDescriptor == null)
			typeDescriptor = new ComplexTypeDescriptor(elementName, provider);
		else if (!(typeDescriptor instanceof ComplexTypeDescriptor))
			throw new ConfigurationError("Expected ComplexTypeDescriptor for type " + elementName + 
					", but found " + typeDescriptor.getClass().getSimpleName());
		
		// create entity
		XmlEntity entity = new XmlEntity((ComplexTypeDescriptor) typeDescriptor);
		entity.setSourceElement(element);
		
		// map attributes
		NamedNodeMap atts = element.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node att = atts.item(i);
			entity.setComponent(att.getNodeName(), att.getNodeValue());
		}
		
		// map sub elements
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				NodeList grandchildNodes = childElement.getChildNodes();
				if (grandchildNodes.getLength() == 1) {
					Node grandchild = grandchildNodes.item(0);
					if (grandchild instanceof Text)
						entity.setComponent(childElement.getNodeName(), grandchild.getTextContent());
				}
			}
		}
		return entity;
	}
	
}
