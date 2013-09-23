/*
 * (c) Copyright 2013 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.composite;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.junit.Test;

/**
 * Tests the {@link ComponentTypeConverter}.<br/><br/>
 * Created: 28.08.2013 17:19:08
 * @since 0.8.3
 * @author Volker Bergmann
 */

public class ComponentTypeConverterTest {
	
	@Test
	public void testRecursively() {
		DescriptorProvider provider = new DefaultDescriptorProvider("p", new DataModel());
		ComplexTypeDescriptor childType = new ComplexTypeDescriptor("childType", provider);
		childType.addComponent(new PartDescriptor("child", provider, "string"));
		ComplexTypeDescriptor parentType = new ComplexTypeDescriptor("parentType", provider);
		parentType.addComponent(new PartDescriptor("child", provider, childType));
		ComponentTypeConverter converter = new ComponentTypeConverter(parentType);
		Entity child = new Entity(childType, "child", "childChildValue");
		Entity parent = new Entity(parentType, "child", child);
		converter.convert(parent);
	}
	
}
