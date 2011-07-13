/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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

import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.test.PersonIterable;
import org.databene.measure.count.ObjectCounter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Uniqueness;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the ComplexTypeGeneratorFactory.<br/><br/>
 * Created at 27.04.2008 18:29:59
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class ComplexTypeGeneratorFactoryTest extends GeneratorTest {
	
    private static final String PERSON_TAB_CSV = "org/databene/benerator/factory/person_tab.csv";
    private static final String PERSON_CSV = "org/databene/benerator/factory/person.ent.csv";
    
	private Entity alice = new Entity("person", "name", "Alice", "age", "23");
	private Entity otto = new Entity("person", "name", "Otto", "age", "89");
	
	// testing generator feature ---------------------------------------------------------------------------------------

	@Test
	public void testGeneratorBean() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("LocaleGenerator");
		type.setDetailValue("generator", MyGenerator.class.getName());
		type.setDetailValue("locale", "de");
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		Entity product = generator.generate();
		assertNotNull(product);
		assertEquals(Locale.GERMAN, product.get("locale"));
	}
	
	// testing CSV file import -----------------------------------------------------------------------------------------
	
	@Test
	public void testSimpleCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_CSV);
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
	}

	@Test
	public void testSimpleCSVImport_scriptedSource() {
		context.set("filepath", PERSON_CSV);
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("{filepath}");
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
	}

	@Test
	public void testTabbedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_TAB_CSV);
		type.setSeparator("\t");
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
	}

	@Test
	public void testCyclicCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_CSV);
		type.setCyclic(true);
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSequence(generator, alice, otto, alice).withContinuedAvailability();
	}

	@Test
	public void testWeightedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_CSV);
		type.setDetailValue("distribution", "weighted[age]");
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSet(generator, 20, alice, otto).withContinuedAvailability();
		ObjectCounter<Entity> counter = new ObjectCounter<Entity>(2);
		int n = 1000;
		for (int i = 0; i < n; i++)
			counter.count(generator.generate());
		assertEquals(n * 24. / (24. + 89.), counter.getCount(alice), n / 20);
	}

	@Test
	public void testSequencedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_CSV);
		type.setDistribution("new StepSequence(-1)");
		Generator<Entity> generator = createGenerator(type);
		generator.init(context);
		expectGeneratedSequence(generator, otto, alice).withCeasedAvailability();
	}
	
	@Test
	public void testUniqueCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource(PERSON_CSV);
		InstanceDescriptor instance = new InstanceDescriptor("person", type);
		instance.setUnique(true);
		Generator<Entity> generator = createGenerator(instance);
		generator.init(context);
		Entity person1 = generator.generate();
		Entity person2 = generator.generate();
		assertTrue(alice.equals(person1) && otto.equals(person2) || otto.equals(person1) && alice.equals(person2));
		assertUnavailable(generator);
	}

	// other tests -----------------------------------------------------------------------------------------------------

	@Test
	public void testFilteredImport() {
		ComplexTypeDescriptor twenType = new ComplexTypeDescriptor("Twen");
		twenType.setSource("personSource");
		twenType.setFilter("_candidate.age < 30 && _candidate.age >= 20");
		InstanceDescriptor twen = new InstanceDescriptor("twen", twenType);
		context.set("personSource", new PersonIterable());
		Generator<Entity> generator = createGenerator(twen);
		generator.init(context);
		Entity person1 = generator.generate();
		assertEquals(PersonIterable.createAlice(), person1);
		assertUnavailable(generator);
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private Generator<Entity> createGenerator(InstanceDescriptor instance) {
		ComplexTypeDescriptor type = (ComplexTypeDescriptor) instance.getTypeDescriptor();
		return ComplexTypeGeneratorFactory.createComplexTypeGenerator(type.getName(), type, instance.getUniqueness(), context);
	}
	
	private Generator<Entity> createGenerator(ComplexTypeDescriptor type) {
		return ComplexTypeGeneratorFactory.createComplexTypeGenerator(type.getName(), type, Uniqueness.NONE, context);
	}
	
}
