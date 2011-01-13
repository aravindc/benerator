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

import java.io.Closeable;
import java.util.Iterator;

import org.databene.commons.TypedIterable;
import org.databene.model.data.Entity;
import org.databene.platform.db.DBSystem;
import org.junit.Test;

/**
 * Integration test for Benerator's transcoding feature.<br/><br/>
 * Created: 12.01.2011 17:06:25
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingIntegrationTest {

	private static final String DESCRIPTOR_FILE_NAME = "src/test/resources/org/databene/benerator/engine/transcode/TranscodingIntegrationTest.ben.xml";
	
	@Test
	public void testSimpleCase() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR_FILE_NAME);
		runner.run();
		BeneratorContext context = runner.getContext();
		DBSystem t = (DBSystem) context.get("t");
		// check countries
		TypedIterable<Entity> iterable = t.queryEntities("country", null, context);
		Iterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1, "United States", iterator);
		assertNextCountry(2, "Germany", iterator);
		assertFalse(iterator.hasNext());
		((Closeable) iterator).close();
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(3, 1, "California", iterator);
		assertNextState(4, 1, "Florida", iterator);
		assertNextState(5, 2, "Bayern", iterator);
		assertNextState(6, 2, "Hamburg", iterator);
		assertFalse(iterator.hasNext());
		((Closeable) iterator).close();
	}

	private void assertNextCountry(int id, String name, Iterator<Entity> iterator) {
		assertTrue(iterator.hasNext());
		assertEquals(new Entity("COUNTRY", "ID", id, "NAME", name), iterator.next());
	}
	
	private void assertNextState(int id, int countryId, String name, Iterator<Entity> iterator) {
		assertTrue(iterator.hasNext());
		assertEquals(new Entity("STATE", "ID", id, "COUNTRY_FK", countryId, "NAME", name), iterator.next());
	}
	
}