/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.databene.benerator.test.ConsumerMock;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.model.data.IdDescriptor;
import org.databene.platform.memstore.MemStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the {@link MemStore} class.<br/><br/>
 * Created: 08.03.2011 16:06:12
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class MemStoreIntegrationTest extends BeneratorIntegrationTest {

	private MemStore src; 
	private MemStore dst; 
	private ConsumerMock<Entity> consumer;
	
	@Before
	public void setUp() throws Exception {
		consumer = new ConsumerMock<Entity>(true);
		context.set("cons", consumer);

		// create source store and prefill it
		src = new MemStore("src");
		context.set("src", src);
		ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("product");
		descriptor.addComponent(new IdDescriptor("id", "int"));
		for (int i = 3; i < 6; i++)
			src.store(new Entity(descriptor, "id", i));
		DataModel.getDefaultInstance().addDescriptorProvider(src);

		// create dest store
		dst = new MemStore("dst");
		context.set("dst", dst);
	}

	
	
	// test methods ----------------------------------------------------------------------------------------------------

	@Test
	public void testStore() {
		parseAndExecute(
			"<generate type='product' count='3' consumer='dst'>" +
			"	<id name='id' type='int' />" +
			"</generate>"
		);
		Collection<Entity> products = dst.getEntities("product");
		assertEquals(3, products.size());
		int index = 1;
		for (Entity product : products) {
			assertNotNull(product);
			assertEquals(index, product.get("id"));
			index++;
		}
	}
	
	@Test
	public void testIterate() {
		parseAndExecute("<iterate source='src' type='product' consumer='cons'/>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		int index = 3;
		for (Entity product : products) {
			assertNotNull(product);
			assertEquals(index, product.get("id"));
			index++;
		}
	}
	
	@Test
	public void testIterateWithSelector() {
		parseAndExecute("<iterate source='src' type='product' selector='_candidate.id == 4' consumer='cons'/>");
		List<Entity> products = consumer.getProducts();
		assertEquals(1, products.size());
		assertEquals(4, products.get(0).get("id"));
	}
	
	@Test
	public void testVariable() {
		parseAndExecute(
			"<generate type='order' consumer='cons'>" +
			"	<variable name='p' source='src' type='product'/>" +
			"	<id name='id' type='int' />" +
			"	<attribute name='prod_id' type='int' script='p.id' />" +
			"</generate>"
		);
		List<Entity> orders = consumer.getProducts();
		assertEquals(3, orders.size());
		int index = 1;
		for (Entity order : orders) {
			assertNotNull(order);
			assertEquals(index, order.get("id"));
			assertEquals(index + 2, order.get("prod_id"));
			index++;
		}
	}
	
	@Test
	public void testAttribute() {
		parseAndExecute(
			"<generate type='order' consumer='cons'>" +
			"	<id name='id' type='int' />" +
			"	<attribute name='product' source='src' type='product' />" +
			"</generate>"
		);
		Collection<Entity> orders = consumer.getProducts();
		assertEquals(3, orders.size());
		int index = 1;
		for (Entity order : orders) {
			assertNotNull(order);
			assertEquals(index, order.get("id"));
			Entity product = (Entity) order.get("product");
			assertEquals(index + 2, product.get("id"));
			index++;
		}
	}
	
	@Test
	public void testIntegration() {
		parseAndExecute(
			"<setup>" +
			"	<memstore id='store'/>" +
			"	<generate type='product' count='100' consumer='store'>" +
			"		<id name='id' type='int' />" +
			"		<attribute name='name' pattern='[A-Z][a-z]{3,8}' />" +
			"	</generate>" + 
			"</setup>"
		);
		MemStore store = (MemStore) context.get("store");
		Collection<Entity> products = store.getEntities("product");
		assertEquals(100, products.size());
		int index = 1;
		for (Entity order : products) {
			assertNotNull(order);
			assertEquals(index, order.get("id"));
			index++;
		}
	}
	
}
