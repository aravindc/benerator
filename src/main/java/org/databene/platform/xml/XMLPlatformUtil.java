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

import org.databene.commons.converter.ToStringConverter;
import org.databene.model.data.Entity;
import org.databene.script.PrimitiveType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides utility methods for Benerator's XML platform.<br/><br/>
 * Created: 15.01.2014 11:03:25
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XMLPlatformUtil {
	
	private XMLPlatformUtil() {}
	
	public static Element mapEntityToElement(Entity source, Element target) {
		for (Map.Entry<String, Object> component : source.getComponents().entrySet())
			mapComponent(component.getKey(), component.getValue(), target);
		return target;
	}
	
	public static void mapComponent(String name, Object value, Element target) {
		if (value != null && PrimitiveType.findByJavaType(value.getClass()) == null) // ignore complex types 
			return;
		if (target.hasAttribute(name)) {
			// if the element has an attribute of an appropriate name, then set it...
			target.setAttribute(name, convertToString(value));
		} else {
			// ... otherwise search for a child element and set that one
			NodeList childNodes = target.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode instanceof Element) {
					Element childElement = (Element) childNode;
					if (name.equals(childElement.getNodeName()))
						childElement.setTextContent(convertToString(value));
				}
			}
		}
	}

	public static String convertToString(Object value) {
		return ToStringConverter.convert(value, null);
	}
	
}
