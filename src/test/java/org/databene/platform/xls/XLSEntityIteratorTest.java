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

package org.databene.platform.xls;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.databene.commons.TimeUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;

import junit.framework.TestCase;

/**
 * Tests the {@link XLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIteratorTest extends TestCase {
	
	private static final String PERSON_XLS = "org/databene/platform/xls/person.ent.xls";
	private static final String PRODUCT_XLS = "org/databene/platform/xls/product.ent.xls";

	public void testIteration() throws FileNotFoundException {
		// test default sheet
		XLSEntityIterator iterator = new XLSEntityIterator(PERSON_XLS, 0, "Person");
		try {
			// check normal row
			expectNextPerson(iterator, "Alice", 23.0);
			// test formula
			expectNextPerson(iterator, "Bob", 34.0);
			// check end of sheet
			assertFalse(iterator.hasNext());
		} finally {
			iterator.close();
		}
	}

	public void testParseAll() throws FileNotFoundException {
		List<Entity> entities = XLSEntityIterator.parseAll(PERSON_XLS, 0, new ComplexTypeDescriptor("Person"), null);
		assertEquals(2, entities.size());
		assertEquals(new Entity("Person", "name", "Alice", "age", 23.0), entities.get(0));
		assertEquals(new Entity("Person", "name", "Bob", "age", 34.0), entities.get(1));
	}
	
	public void testTypes() throws Exception {
		ComplexTypeDescriptor descriptor = new ComplexTypeDescriptor("Product");
		descriptor.addComponent(new PartDescriptor("ean", "string"));
		SimpleTypeDescriptor priceTypeDescriptor = new SimpleTypeDescriptor("priceType", "big_decimal");
		priceTypeDescriptor.setPrecision("0.01");
		descriptor.addComponent(new PartDescriptor("price", priceTypeDescriptor));
		descriptor.addComponent(new PartDescriptor("date", "date"));
		descriptor.addComponent(new PartDescriptor("available", "boolean"));
		TimeZone timeZone = TimeZone.getDefault();
		// TODO v0.6 in which timezone should parsed dates be served by the XLSLineIterator?
		// TODO v0.6 1.95 is parsed as 9500000000000002
		TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
		// test default sheet
		XLSEntityIterator iterator = new XLSEntityIterator(PRODUCT_XLS, 0, descriptor, null);
		try {
			expectNextProduct(iterator, "8000353006386", new BigDecimal("2.0"), TimeUtil.date(0), true); // TODO v0.6 should be 2.00
			expectNextProduct(iterator, "3068320018430", new BigDecimal("0.01"), TimeUtil.date(0), false);
			// check end of sheet
			assertFalse(iterator.hasNext());
		} finally {
			TimeZone.setDefault(timeZone);
			iterator.close();
		}
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
    private void expectNextProduct(XLSEntityIterator iterator, 
    		String ean, BigDecimal price, Date date, boolean available) {
		assertTrue(iterator.hasNext());
		Entity expected = new Entity("Product", 
				"ean", ean, 
				"price", price, 
				"date", date,
				"available", available);
		Entity actual = iterator.next();
		assertEquals(expected, actual);
    }

	private void expectNextPerson(XLSEntityIterator iterator, String name, double age) {
		assertTrue(iterator.hasNext());
		Entity expected = new Entity("Person", "name", name, "age", age);
		assertEquals(expected, iterator.next());
	}
	
}
