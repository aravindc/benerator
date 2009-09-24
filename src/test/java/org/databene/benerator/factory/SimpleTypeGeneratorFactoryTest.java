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

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.databene.measure.count.ObjectCounter;
import org.databene.model.data.SimpleTypeDescriptor;

/**
 * Tests the {@link SimpleTypeGeneratorFactory}.<br/>
 * <br/>
 * Created at 29.04.2008 20:13:40
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class SimpleTypeGeneratorFactoryTest extends GeneratorTest {

	private static final String NAME_CSV = "org/databene/benerator/factory/name.csv";
	private static final String NAMES_TAB_CSV = "org/databene/benerator/factory/names_tab.csv";
	private static final String SCRIPTED_NAMES_CSV = "org/databene/benerator/factory/scripted_names.csv";
	private static final String SCRIPTED_NAMES_WGT_CSV = "org/databene/benerator/factory/scripted_names.wgt.csv";

	String contextUri = ".";
	
	// 'value' attribute tests -----------------------------------------------------------------------------------------

	public void testValues() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("string");
		type.setValues("A,B,C");
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSet(generator, "A", "B", "C").withContinuedAvailability();
	}
	
	public void testValuesWithCustomSeparator() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("string");
		type.setValues("A|B|C");
		type.setSeparator("|");
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSet(generator, "A", "B", "C").withContinuedAvailability();
	}
	
	public void testUniqueValues() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("string");
		type.setValues("A,B,C");
		Generator<String> generator = createGenerator(type, true);
		expectGeneratedSet(generator, "A", "B", "C").withCeasedAvailability();
	}
	
	public void testCreateSampleGeneratorNA() {
		assertNull(SimpleTypeGeneratorFactory.createSampleGenerator(new SimpleTypeDescriptor("test"), false, null));
	}
	
	public void testCreateSampleGeneratorUnweighted() {
		Generator<?> generator = SimpleTypeGeneratorFactory.createSampleGenerator(new SimpleTypeDescriptor("test").withValues("a,b"), false, null);
		expectRelativeWeights(generator, 100, "a", 1, "b", 1);
		generator = SimpleTypeGeneratorFactory.createSampleGenerator((SimpleTypeDescriptor) new SimpleTypeDescriptor("test").withValues("a|b").withSeparator("|"), false, null);
		expectRelativeWeights(generator, 1000, "a", 1, "b", 1);
		generator = SimpleTypeGeneratorFactory.createSampleGenerator((SimpleTypeDescriptor) new SimpleTypeDescriptor("test").withValues("a|b,c").withSeparator("|"), false, null);
		expectRelativeWeights(generator, 1000, "a", 1, "b,c", 1);
	}

	public void testCreateSampleGeneratorWeighted() {
		Generator<?> generator = SimpleTypeGeneratorFactory.createSampleGenerator(new SimpleTypeDescriptor("test").withValues("a[2],b"), false, null);
		expectRelativeWeights(generator, 1000, "a", 2, "b", 1);
	}
	
	// CSV tests -------------------------------------------------------------------------------------------------------

	public void testSimpleCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource(NAME_CSV);
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSequence(generator, "Alice", "Otto").withCeasedAvailability();
	}

	public void testTabSeparatedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("name");
		type.setSource(NAMES_TAB_CSV);
		type.setSeparator("\t");
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSequence(generator, "Alice", "Bob", "Charly").withCeasedAvailability();
	}

	public void testScriptedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("name");
		type.setSource(SCRIPTED_NAMES_CSV);
		BeneratorContext context = new BeneratorContext(".");
		context.set("some_user", "the_user");
		Generator<String> generator = createGenerator(type, false, context);
		expectGeneratedSequence(generator, "Alice", "the_user", "Otto").withCeasedAvailability();
	}

	public void testScriptedWgtCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("name");
		type.setSource(SCRIPTED_NAMES_WGT_CSV);
		BeneratorContext context = new BeneratorContext(".");
		context.set("some_user", "the_user");
		Generator<String> generator = createGenerator(type, false, context);
		expectGeneratedSet(generator, "Alice", "the_user", "Otto").withContinuedAvailability();
	}

	public void testWeightedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource(NAME_CSV);
		type.setDetailValue("distribution", "weighted");
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSet(generator, "Alice", "Otto").withContinuedAvailability();
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		int n = 1000;
		for (int i = 0; i < n; i++)
			counter.count(generator.generate());
		assertEquals(n * 24. / (24. + 89.), counter.getCount("Alice"), n / 20);
	}

	public void testSequencedCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource(NAME_CSV);
		type.setDistribution("step(-1)");
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSequence(generator, "Otto", "Alice").withCeasedAvailability();
	}

	public void testUniqueCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource(NAME_CSV);
		Generator<String> generator = createGenerator(type, true);
		ObjectCounter<String> counter = new ObjectCounter<String>(2);
		for (int i = 0; i < 2; i++)
			counter.count(generator.generate());
		assertEquals(1, counter.getCount("Alice"));
		assertEquals(1, counter.getCount("Otto"));
		assertFalse(generator.available());
	}
	
	public void testCyclicCSVImport() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("givenName");
		type.setSource(NAME_CSV);
		type.setCyclic(true);
		Generator<String> generator = createGenerator(type, false);
		expectGeneratedSequence(generator, "Alice", "Otto", "Alice").withContinuedAvailability();
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
    private Generator<String> createGenerator(SimpleTypeDescriptor type, boolean unique) {
		return createGenerator(type, unique, context);
	}

	@SuppressWarnings("unchecked")
    private Generator<String> createGenerator(SimpleTypeDescriptor type, boolean unique, BeneratorContext context) {
		return (Generator<String>) SimpleTypeGeneratorFactory.createSimpleTypeGenerator(type, false, unique, context);
	}

}
