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
import java.util.List;

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

import junit.framework.TestCase;

/**
 * Tests the {@link XLSEntityIterator} class.<br/>
 * <br/>
 * Created at 29.01.2009 11:06:33
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIteratorTest extends TestCase {
	
	private static final String XLS_FILENAME = "org/databene/platform/xls/person_entities.xls";

	public void testIteration() throws FileNotFoundException {
		// test default sheet
		XLSEntityIterator iterator = new XLSEntityIterator(XLS_FILENAME, 0, "Person");
		try {
			// check normal row
			expectNext(iterator, "Alice", 23.0);
			// test formula
			expectNext(iterator, "Bob", 34.0);
			// check end of sheet
			assertFalse(iterator.hasNext());
		} finally {
			iterator.close();
		}
	}

	public void testParseAll() throws FileNotFoundException {
		List<Entity> entities = XLSEntityIterator.parseAll(XLS_FILENAME, 0, new ComplexTypeDescriptor("Person"), null);
		assertEquals(2, entities.size());
		assertEquals(new Entity("Person", "name", "Alice", "age", 23.0), entities.get(0));
		assertEquals(new Entity("Person", "name", "Bob", "age", 34.0), entities.get(1));
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
	private void expectNext(XLSEntityIterator iterator, String name, double age) {
		assertTrue(iterator.hasNext());
		Entity expected = new Entity("Person", "name", name, "age", age);
		assertEquals(expected, iterator.next());
	}
	
}
