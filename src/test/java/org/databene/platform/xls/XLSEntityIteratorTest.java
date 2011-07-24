/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.xls;

import java.util.Date;
import java.util.List;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.DescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataUtil;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link XLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIteratorTest extends XLSTest {
	
	private static final String PRODUCT_XLS = "org/databene/platform/xls/product-singlesheet.ent.xls";
	private static final String IMPORT_XLS = "org/databene/platform/xls/import-multisheet.ent.xls";

    @Before
	public void setUp() {
		DataModel.getDefaultInstance().clear();
	}
	
	@Test
	public void testImport() throws Exception {
		XLSEntityIterator iterator = new XLSEntityIterator(IMPORT_XLS);
		try {
			assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
			Entity next = DataUtil.nextNotNullData(iterator);
			assertProduct(PROD2, next);
			assertPerson(PERSON1, DataUtil.nextNotNullData(iterator));
			assertNull(iterator.next(new DataContainer<Entity>()));
		} finally {
			iterator.close();
		}
	}
	
	@Test
	public void testParseAll() throws Exception {
		List<Entity> entities = XLSEntityIterator.parseAll(IMPORT_XLS, null);
		assertEquals(3, entities.size());
		assertProduct(PROD1, entities.get(0));
		assertProduct(PROD2, entities.get(1));
		assertPerson(PERSON1, entities.get(2));
	}

	@Test
	public void testTypes() throws Exception {
		// Create descriptor
		final ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("Product");
		descriptor.addComponent(new PartDescriptor("ean", "string"));
		SimpleTypeDescriptor priceTypeDescriptor = new SimpleTypeDescriptor("priceType", "big_decimal");
		priceTypeDescriptor.setPrecision("0.01");
		descriptor.addComponent(new PartDescriptor("price", priceTypeDescriptor));
		descriptor.addComponent(new PartDescriptor("date", "date"));
		descriptor.addComponent(new PartDescriptor("available", "boolean"));
		descriptor.addComponent(new PartDescriptor("updated", "timestamp"));
		DescriptorProvider dp = new DefaultDescriptorProvider("test") {
			@Override
			public TypeDescriptor getTypeDescriptor(String typeName) {
			    return ("Product".equals(typeName) ? descriptor : null);
			}
		};
		DataModel.getDefaultInstance().addDescriptorProvider(dp);
		
		// test import
		XLSEntityIterator iterator = new XLSEntityIterator(PRODUCT_XLS);
		try {
			assertProduct(PROD1, DataUtil.nextNotNullData(iterator));
			assertProduct(PROD2, DataUtil.nextNotNullData(iterator));
			assertNull(iterator.next(new DataContainer<Entity>()));
		} finally {
			iterator.close();
			DataModel.getDefaultInstance().removeDescriptorProvider("test");
		}
	}

	@Test
	public void testTypeDef() throws Exception {
		XLSEntityIterator iterator = new XLSEntityIterator(IMPORT_XLS);
		try {
			while (iterator.next(new DataContainer<Entity>()) != null) {
				// only iterate
			}
		} finally {
			iterator.close();
		}
		DataModel dataModel = DataModel.getDefaultInstance();
		ComplexTypeDescriptor personDescriptor = (ComplexTypeDescriptor) dataModel.getTypeDescriptor("Person");
		assertNotNull(personDescriptor);
		assertComponent(personDescriptor, "name", "string");
		assertComponent(personDescriptor, "age",  "double");
		ComplexTypeDescriptor productDescriptor = (ComplexTypeDescriptor) dataModel.getTypeDescriptor("Product");
		assertNotNull(productDescriptor);
		assertComponent(productDescriptor, "ean",     "string");
		assertComponent(productDescriptor, "price",   "double");
		assertComponent(productDescriptor, "date",    "date");
		assertComponent(productDescriptor, "avail",   "boolean");
		assertComponent(productDescriptor, "updated", "date");
	}
	
	
	// private helpers -------------------------------------------------------------------------------------------------
	
    private void assertComponent(ComplexTypeDescriptor complexTypeDescriptor, String componentName, String componentType) {
	    ComponentDescriptor component = complexTypeDescriptor.getComponent(componentName);
	    assertNotNull(component);
	    assertEquals("Type of component " + componentName + " is wrong, ", componentType, component.getTypeDescriptor().getName());
    }

	private void assertProduct(Entity expected, Entity actual) {
		assertEquals("Product", actual.type());
		assertEquals(expected.getComponent("ean"), actual.getComponent("ean"));
		assertEquals(((Number) expected.getComponent("price")).doubleValue(), ((Number) actual.getComponent("price")).doubleValue(), 0.000001);
		assertEquals(expected.getComponent("date"), actual.getComponent("date"));
		assertEquals(expected.getComponent("avail"), actual.getComponent("avail"));
		assertEquals(((Date) expected.getComponent("updated")).getTime(), 
				((Date) actual.getComponent("updated")).getTime());
    }

    private void assertPerson(Entity expected, Entity actual) {
		assertEquals("Person", actual.type());
		assertEquals(expected.get("name"), actual.get("name"));
		assertEquals(expected.get("age"), ((Number) actual.get("age")).intValue());
    }

	public static void assertUnavailable(DataIterator<Entity> iterator) {
		assertNull(iterator.next(new DataContainer<Entity>()));
	}
    
}
