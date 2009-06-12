/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.GeneratorTest;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.measure.count.ObjectCounter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * Tests the ComplexTypeGeneratorFactory.<br/><br/>
 * Created at 27.04.2008 18:29:59
 * @since 0.5.2
 * @author Volker Bergmann
 *
 */
public class ComplexTypeGeneratorFactoryTest extends GeneratorTest {
	
	private Entity alice = new Entity("person", "name", "Alice", "age", "23");
	private Entity otto = new Entity("person", "name", "Otto", "age", "89");
	
	public void testGeneratorBean() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("LocaleGenerator");
		type.setDetailValue("generator", MyGenerator.class.getName());
		type.setDetailValue("locale", "de");
		Generator<Entity> generator = createGenerator(type);
		generator.validate();
		assertTrue(generator.available());
		assertEquals(Locale.GERMAN, generator.generate().get("locale"));
	}
	
	public static class MyGenerator extends LightweightGenerator<Entity> {
		
		private Locale locale;
		
		public Locale getLocale() {
			return locale;
		}

		public void setLocale(Locale locale) {
			this.locale = locale;
		}

		public Entity generate() {
			return new Entity("MyEntity", "locale", locale);
		}

        public Class<Entity> getGeneratedType() {
	        return Entity.class;
        }
	}

	public void testTabbedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("org/databene/benerator/factory/person_tab.csv");
		type.setSeparator("\t");
		Generator<Entity> generator = createGenerator(type);
		expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
	}

	public void testSimpleCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("org/databene/benerator/factory/person.csv");
		Generator<Entity> generator = createGenerator(type);
		expectGeneratedSequence(generator, alice, otto).withCeasedAvailability();
	}

	public void testCyclicCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("org/databene/benerator/factory/person.csv");
		type.setCyclic(true);
		Generator<Entity> generator = createGenerator(type);
		expectGeneratedSequence(generator, alice, otto,alice).withContinuedAvailability();
	}

	public void testWeightedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("org/databene/benerator/factory/person.csv");
		type.setDetailValue("distribution", "weighted[age]");
		Generator<Entity> generator = createGenerator(type);
		expectGeneratedSet(generator, alice, otto).withContinuedAvailability();
		ObjectCounter<Entity> counter = new ObjectCounter<Entity>(2);
		int n = 1000;
		for (int i = 0; i < n; i++)
			counter.count(generator.generate());
		assertEquals(n * 24. / (24. + 89.), counter.getCount(alice), n / 20);
	}

	public void testSequencedCSVImport() {
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("person");
		type.setSource("org/databene/benerator/factory/person.csv");
		type.setDistribution("step");
		type.setVariation1("-1");
		Generator<Entity> generator = createGenerator(type);
		expectGeneratedSequence(generator, otto, alice).withCeasedAvailability();
	}
	
/*
	public void testUniqueCSVImport() { // TODO v0.6.0 uniqueness
		ComplexTypeDescriptor type = new ComplexTypeDescriptor("Person");
		type.setSource("org/databene/benerator/factory/person.csv");
		InstanceDescriptor instance = new InstanceDescriptor("person", type);
		instance.setUnique(true);
		Generator<Entity> generator = createGenerator(type);
		Entity person1 = generator.generate();
		Entity person2 = generator.generate();
		assertTrue(alice.equals(person1) && otto.equals(person2) || otto.equals(person1) && alice.equals(person2));
		assertFalse(generator.available());
	}
*/
	// private helpers -------------------------------------------------------------------------------------------------
	
	private Generator<Entity> createGenerator(ComplexTypeDescriptor type) {
		BeneratorContext context = new BeneratorContext(null);
		Generator<Entity> generator = ComplexTypeGeneratorFactory.createComplexTypeGenerator(type, false, context);
		return generator;
	}
}
