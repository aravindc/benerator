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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorTest;
import org.databene.commons.Context;
import org.databene.commons.context.DefaultContext;
import org.databene.measure.count.ObjectCounter;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.function.Sequence;

/**
 * Tests the {@link SimpleTypeGeneratorFactory}.<br/><br/>
 * Created at 29.04.2008 20:13:40
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactoryTest extends GeneratorTest {
	// TODO v0.5.x resolve TODOs from area_demo.ben.xml

	public void testSimpleCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource("org/databene/benerator/factory/name.csv");
		Generator<String> generator = (Generator<String>) createGenerator(type, false);
		expectGeneratedSequence(generator, "Alice", "23", "Otto", "89").withCeasedAvailability();
	}

	public void testCyclicCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource("org/databene/benerator/factory/name.csv");
		type.setCyclic(true);
		Generator<String> generator = (Generator<String>) createGenerator(type, false);
		expectGeneratedSequence(generator, "Alice", "23", "Otto", "89", "Alice").withContinuedAvailability();
	}

	public void testWeightedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource("org/databene/benerator/factory/name.csv");
		type.setDetailValue("distribution", "weighted");
		Generator<String> generator = (Generator<String>) createGenerator(type, false);
		expectGeneratedSet(generator, "Alice", "Otto").withContinuedAvailability();
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		int n = 1000;
		for (int i = 0; i < n; i++)
			counter.count(generator.generate());
		assertEquals(n * 24. / (24. + 89.), counter.getCount("Alice"), n / 20);
	}

	public void testSequencedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource("org/databene/benerator/factory/name.csv");
		type.setDistribution(Sequence.STEP);
		type.setVariation1("-1");
		Generator<String> generator = (Generator<String>) createGenerator(type, false);
		expectGeneratedSequence(generator, "89", "Otto", "23", "Alice").withCeasedAvailability();
	}

	public void testUniqueCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource("org/databene/benerator/factory/name.csv");
		Generator<String> generator = (Generator<String>) createGenerator(type, true);
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		for (int i = 0; i < 4; i++)
			counter.count(generator.generate());
		assertEquals(1, counter.getCount("Alice"));
		assertEquals(1, counter.getCount("23"));
		assertEquals(1, counter.getCount("Otto"));
		assertEquals(1, counter.getCount("89"));
		assertFalse(generator.available());
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
	private Generator<? extends Object> createGenerator(SimpleTypeDescriptor type, boolean unique) {
		Context context = new DefaultContext();
		GenerationSetup setup = new SimpleGenerationSetup();
		Generator<? extends Object> generator = SimpleTypeGeneratorFactory.createSimpleTypeGenerator(type, false, unique, context, setup);
		return generator;
	}
}
