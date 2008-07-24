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

package org.databene.benerator.factory;

import junit.framework.TestCase;

import org.databene.benerator.composite.ComponentBuilder;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.TypedIterable;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.iterator.DefaultTypedIterable;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.storage.StorageSystem;

/**
 * Tests the {@link ReferenceGeneratorFactory}.<br/><br/>
 * Created at 05.05.2008 17:08:45
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class ReferenceGeneratorFactoryTest extends TestCase {

	public void testMissingType() {
		try {
			ReferenceDescriptor ref = createDescriptor("ref", null, "Storage");
			createGenerator(ref);
			fail(ConfigurationError.class.getSimpleName() + " expected");
		} catch (ConfigurationError e) {
			// this is expected
		}
	}
	
	public void testMissingSource() {
		try {
			ReferenceDescriptor ref = createDescriptor("ref", "Referee", null);
			createGenerator(ref);
			fail(ConfigurationError.class.getSimpleName() + " expected");
		} catch (ConfigurationError e) {
			// this is expected
		}
	}

	public void testSingleRef() {
		ReferenceDescriptor ref = createDescriptor("ref", "Person", "Storage");
		ref.setCount(1L);
		ComponentBuilder generator = createGenerator(ref);
		assertTrue(generator != null);
		assertTrue(generator.available());
		Entity entity = new Entity("Person");
		generator.buildComponentFor(entity);
		assertEquals("Alice", entity.get("ref"));
	}

	public void testMultiRef() {
		ReferenceDescriptor ref = createDescriptor("ref", "Person", "Storage");
		ref.setCount(2L);
		ComponentBuilder generator = createGenerator(ref);
		assertTrue(generator != null);
		assertTrue(generator.available());
		Entity entity = new Entity("Person");
		generator.buildComponentFor(entity);
		String[] product = (String[]) entity.get("ref");
		assertEquals(2, product.length);
		assertEquals("Alice", product[0]);
		assertEquals("Bob", product[1]);
	}

	// private helpers -------------------------------------------------------------------------------------------------

	private ReferenceDescriptor createDescriptor(String refName, String targetType, String source) {
		DataModel.getDefaultInstance().clear();
		ReferenceDescriptor descriptor = new ReferenceDescriptor(refName, "string");
		descriptor.getLocalType(false).setSource(source);
		descriptor.setTargetTye(targetType);
		return descriptor;
	}

	private ComponentBuilder createGenerator(ReferenceDescriptor ref) {
		DefaultContext context = new DefaultContext();
		StorageSystemMock storageSystem = new StorageSystemMock();
		DataModel.getDefaultInstance().addDescriptorProvider(storageSystem);
		context.set(storageSystem.getId(), storageSystem);
		SimpleGenerationSetup setup = new SimpleGenerationSetup();
		return ComponentBuilderFactory.createReferenceBuilder(ref, context, setup);
	}
	
	public static class StorageSystemMock extends DefaultDescriptorProvider implements StorageSystem {
		
		public StorageSystemMock() {
			super("Storage");
			super.addDescriptor(new ComplexTypeDescriptor("Person"));
		}

		public void close() {
		}

		public void flush() {
		}

		public <T> TypedIterable<T> query(String selector, Context context) {
			throw new UnsupportedOperationException("query() not implemented");
		}

		public TypedIterable<Entity> queryEntities(String type, String selector, Context context) {
			throw new UnsupportedOperationException("queryEntities() not implemented");
		}

		public <T> TypedIterable<T> queryEntityIds(String entityName, String selector, Context context) {
			return (TypedIterable<T>) new DefaultTypedIterable<String>(String.class, CollectionUtil.toList("Alice", "Bob"));
		}

		public void store(Entity entity) {
			throw new UnsupportedOperationException("store() not implemented");
		}

	}
}
