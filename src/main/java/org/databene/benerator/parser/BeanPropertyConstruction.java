/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.BeanUtil;
import org.databene.commons.OrderedMap;
import org.databene.commons.bean.ClassProvider;

/**
 * Represents the construction of a JavaBean using a public default constructor and property mutator methods.<br/>
 * <br/>
 * Created at 02.01.2009 07:32:51
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class BeanPropertyConstruction extends Construction {

	protected OrderedMap<String, Object> properties;

	public BeanPropertyConstruction(String className, ClassProvider classProvider) {
		super(className, classProvider);
		properties = new OrderedMap<String, Object>();
	}
	
	public void addProperty(String name, Object value) {
		properties.put(name, value);
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public Object evaluate() {
		Object bean = super.evaluate();
		for (Map.Entry<String, Object> property : properties.entrySet())
			BeanUtil.setPropertyValue(bean, property.getKey(), property.getValue(), true, true);
		return bean;
	}
}
