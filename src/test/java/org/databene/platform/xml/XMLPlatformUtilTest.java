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

import static org.junit.Assert.assertEquals;

import org.databene.commons.xml.XMLUtil;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link XMLPlatformUtil}.<br/><br/>
 * Created: 16.01.2014 13:57:18
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XMLPlatformUtilTest {

	@Test
	public void test() {
		Element element = XMLUtil.parseStringAsElement("<person age='23'><name>Alice</name></person>");
		DescriptorProvider provider = new DefaultDescriptorProvider("test", new DataModel());
		Entity entity = XMLPlatformUtil.convertElement2Entity(element, provider);
		assertEquals("person", entity.type());
		assertEquals("23", entity.get("age"));
		assertEquals("Alice", entity.get("name"));
	}
	
}
