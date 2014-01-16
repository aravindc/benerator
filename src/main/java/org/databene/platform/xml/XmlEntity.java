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

import org.databene.commons.anno.Nullable;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.w3c.dom.Element;

/**
 * Benerator {@link Entity} which represents an XML element 
 * and holds a reference to the source element, allowing its update 
 * in the {@link DOMTree}.<br/><br/>
 * Created: 15.01.2014 10:52:37
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XmlEntity extends Entity {
	
	/** The original XML Element as it was parsed from the XML source. It may be null. */
	@Nullable
	private Element sourceElement;

	public XmlEntity(ComplexTypeDescriptor descriptor, Object... componentKeyValuePairs) {
		super(descriptor, componentKeyValuePairs);
		this.sourceElement = null;
	}

	public XmlEntity(Entity prototype) {
		super(prototype);
		if (prototype instanceof XmlEntity)
			this.sourceElement = ((XmlEntity) prototype).sourceElement;
		else
			this.sourceElement = null;
	}

	public XmlEntity(String name, DescriptorProvider descriptorProvider, Object... componentKeyValuePairs) {
		super(name, descriptorProvider, componentKeyValuePairs);
		this.sourceElement = null;
	}

	public XmlEntity(String name, DescriptorProvider descriptorProvider) {
		super(name, descriptorProvider);
		this.sourceElement = null;
	}
	
	public Element getSourceElement() {
		return sourceElement;
	}
	
	public void setSourceElement(Element sourceElement) {
		this.sourceElement = sourceElement;
	}
	
}
