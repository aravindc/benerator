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

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.model.data.Entity;
import org.databene.platform.db.DBSystem;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.DataSource;
import org.junit.After;
import org.junit.Test;

/**
 * Integration test for Benerator's transcoding feature.<br/><br/>
 * Created: 12.01.2011 17:06:25
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class TranscodingIntegrationTest extends BeneratorIntegrationTest {

	private static final String PARENT_FOLDER = "src/test/resources/org/databene/benerator/engine/transcode";
	private static final String DESCRIPTOR1_FILE_NAME = PARENT_FOLDER + "/transcode_to_empty_target.ben.xml";
	private static final String DESCRIPTOR2_FILE_NAME = PARENT_FOLDER + "/transcode_to_target_with_countries.ben.xml";
	private static final String DESCRIPTOR3_FILE_NAME = PARENT_FOLDER + "/transcode_partially.ben.xml";
	private static final String DESCRIPTOR4_FILE_NAME = PARENT_FOLDER + "/transcode_partially_to_non_empty_target.ben.xml";
	private static final String DESCRIPTOR5_FILE_NAME = PARENT_FOLDER + "/transcode_partially_with_cascade.ben.xml";
	
	@After
	public void clearDB() {
		DBSystem s = (DBSystem) context.get("s");
		s.execute("drop table user");
		s.execute("drop table role");
		s.execute("drop table city");
		s.execute("drop table state");
		s.execute("drop table country");
		DBSystem t = (DBSystem) context.get("t");
		t.execute("drop table user");
		t.execute("drop table role");
		t.execute("drop table city");
		t.execute("drop table state");
		t.execute("drop table country");
	}
	
	
	
	// tests -----------------------------------------------------------------------------------------------------------

	@Test
	public void testEmptyTarget() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR1_FILE_NAME, context);
		runner.run();
		DBSystem t = (DBSystem) context.get("t");
		// check countries
		DataSource<Entity> iterable = t.queryEntities("country", null, context);
		DataIterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1, "United States", iterator);
		assertNextCountry(2, "Germany", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(3, 1, "California", iterator);
		assertNextState(4, 1, "Florida", iterator);
		assertNextState(5, 2, "Bayern", iterator);
		assertNextState(6, 2, "Hamburg", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
	}

	@Test
	public void testTargetWithCountries() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR2_FILE_NAME, context);
		runner.setClosingResources(false);
		runner.run();
		DBSystem t = (DBSystem) context.get("t");
		// check countries
		DataSource<Entity> iterable = t.queryEntities("country", null, context);
		DataIterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1000, "United States", iterator);
		assertNextCountry(2000, "Germany", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(1, 1000, "California", iterator);
		assertNextState(2, 1000, "Florida", iterator);
		assertNextState(3, 2000, "Bayern", iterator);
		assertNextState(4, 2000, "Hamburg", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
	}
	
	@Test
	public void testPartialTranscode() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR3_FILE_NAME, context);
		runner.run();
		DBSystem t = (DBSystem) context.get("t");
		// check countries
		DataSource<Entity> iterable = t.queryEntities("country", null, context);
		DataIterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1, "Germany", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(2, 1, "Bayern", iterator);
		assertNextState(3, 1, "Hamburg", iterator);
		assertNextState(4, null, "No State", iterator); // checking transcoding of 'null' refs
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
	}

	@Test
	public void testPartialTranscodeToNonEmptyTarget() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR4_FILE_NAME, context);
		runner.run();
		DBSystem t = (DBSystem) context.get("t");
		// check countries
		DataSource<Entity> iterable = t.queryEntities("country", null, context);
		DataIterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1, "Germany", iterator);
		assertNextCountry(10, "United States", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(2, 1, "Bayern", iterator);
		assertNextState(3, 1, "Hamburg", iterator);
		assertNextState(110, 10, "California", iterator);
		assertNextState(120, 10, "Florida", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
	}

	@Test
	public void testPartialTranscodeWithCascade() throws Exception {
		// run descriptor file
		DescriptorRunner runner = new DescriptorRunner(DESCRIPTOR5_FILE_NAME, context);
		runner.run();
		DBSystem t = (DBSystem) context.get("t");
		
		// check countries
		DataSource<Entity> iterable = t.queryEntities("country", null, context);
		DataIterator<Entity> iterator = iterable.iterator();
		assertNextCountry(1, "Germany", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		
		// check states
		iterable = t.queryEntities("state", null, context);
		iterator = iterable.iterator();
		assertNextState(2, 1, "Bayern", iterator);
		assertNextState(5, 1, "Hamburg", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
		
		// check cities
		iterable = t.queryEntities("city", null, context);
		iterator = iterable.iterator();
		assertNextCity(3, 2, "München", iterator);
		assertNextCity(4, 2, "Ingolstadt", iterator);
		assertNull(iterator.next(new DataContainer<Entity>()));
		((Closeable) iterator).close();
	}

	
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void assertNextCountry(int id, String name, DataIterator<Entity> iterator) {
		Entity expectedCountry = createEntity("COUNTRY", "ID", id, "NAME", name);
		Entity actualCountry = iterator.next(new DataContainer<Entity>()).getData();
		assertEquals(expectedCountry, actualCountry);
	}
	
	private void assertNextState(int id, Integer countryId, String name, DataIterator<Entity> iterator) {
		Entity expectedState = createEntity("STATE", "ID", id, "COUNTRY_FK", countryId, "NAME", name);
		assertEquals(expectedState, iterator.next(new DataContainer<Entity>()).getData());
	}
	
	private void assertNextCity(int id, Integer stateId, String name, DataIterator<Entity> iterator) {
		Entity expectedCity = createEntity("CITY", "ID", id, "STATE_FK", stateId, "NAME", name);
		assertEquals(expectedCity, iterator.next(new DataContainer<Entity>()).getData());
	}
	
}