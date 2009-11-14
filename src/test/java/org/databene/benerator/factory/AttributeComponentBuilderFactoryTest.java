/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.ComponentBuilderFactory;
import org.databene.benerator.primitive.HibUUIDGenerator;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.expression.ConstantExpression;
import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link ComponentBuilderFactory} class for all useful attribute setups.<br/>
 * <br/>
 * Created: 10.08.2007 12:40:41
 * @author Volker Bergmann
 */
public class AttributeComponentBuilderFactoryTest extends GeneratorTest {
	
	// TODO v0.6 define tests for all syntax paths
	
	private String contextUri = "./";
/*
    private static Log logger = LogFactory.getLog(ComponentBuilderFactory.class);
    
    private static Set<String> componentFeatures = CollectionUtil.toSet(
            "type", "unique", "nullable", "minCount", "maxCount", "count", "nullQuota");
    
*/
	@SuppressWarnings("unchecked")
    @Test
	public void testSingleValuesAttribute() {
		String componentName = "name";
		PartDescriptor name = new PartDescriptor(componentName);
		SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
		type.setValues("'A'");
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
		for (int i = 0; i < 10; i++)
			assertEquals("A", helper.generate());
	}

	@SuppressWarnings("unchecked")
    @Test
	public void testMultiValuesAttribute() {
		String componentName = "name";
		PartDescriptor name = new PartDescriptor(componentName);
		SimpleTypeDescriptor type = (SimpleTypeDescriptor) name.getLocalType(false);
		type.setValues("'A','B'");
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<String> helper = new ComponentBuilderGenerator(builder, name.getName());
		for (int i = 0; i < 10; i++) {
			String s = helper.generate();
			assertTrue("A".equals(s) || "B".equals(s));
		}
	}

	// csv string source -----------------------------------------------------------------------------------------------
	
	private static final String NAMES_CSV = "org/databene/benerator/factory/names.csv";

	@Test
	public void testCSVStringAttribute() {
		PartDescriptor name = createCSVStringAttributeDescriptor();
		expectUniqueSequence(name, "Alice", "Bob", "Charly");
	}

	@Test
	public void testCSVStringAttributeStep() {
		PartDescriptor name = createCSVStringAttributeDescriptor();
		SimpleTypeDescriptor localType = (SimpleTypeDescriptor) name.getLocalType(false);
		localType.setDistribution("step");
		localType.setMin("0");
		expectUniqueSequence(name, "Alice", "Bob", "Charly");
	}
	
	@Test
	public void testCSVStringAttributeUnique() {
		PartDescriptor name = createCSVStringAttributeDescriptor();
		name.setUnique(true);
		expectUniqueSet(name, "Alice", "Bob", "Charly");
	}

	/** TODO v0.6 support random unique
	@Test
	public void testCSVStringAttributeRandomUnique() {
		PartDescriptor name = createCSVStringAttributeBuilder();
		name.getLocalType().setDistribution(Sequence.RANDOM);
		name.setUnique(true);
		expectGeneratedSet(name, "Alice", "Bob", "Charly");
	}
	*/

	private PartDescriptor createCSVStringAttributeDescriptor() {
		return createCSVStringAttributeDescriptor(NAMES_CSV, ",");
	}
	
	private PartDescriptor createCSVStringAttributeDescriptor(String uri, String separator) {
		String componentName = "name";
		PartDescriptor name = new PartDescriptor(componentName);
		name.setMinCount(new ConstantExpression<Long>(1L));
		name.setMaxCount(new ConstantExpression<Long>(1L));
		name.getLocalType(false).setSource(uri);
		name.getLocalType(false).setSeparator(separator);
		return name;
	}
	
	// nullQuota == 1 evaluation ---------------------------------------------------------------------------------------
	
	@Test
	@SuppressWarnings("unchecked")
    public void testNullQuotaOneReference() {
		String componentName = "id";
		ReferenceDescriptor reference = (ReferenceDescriptor) new ReferenceDescriptor(componentName).withNullQuota(1);
		ComponentBuilder builder = createComponentBuilder(reference);
		Generator<String> helper = new ComponentBuilderGenerator(builder, componentName);
		expectNulls(helper, 10);
	}

	@Test
	@SuppressWarnings("unchecked")
    public void testNullQuotaOneAttribute() {
		String componentName = "part";
		PartDescriptor attribute = (PartDescriptor) new PartDescriptor(componentName).withNullQuota(1);
		ComponentBuilder builder = createComponentBuilder(attribute);
		Generator<String> helper = new ComponentBuilderGenerator(builder, componentName);
		expectNulls(helper, 10);
	}

	// Id Descriptor tests ---------------------------------------------------------------------------------------------
	
	/**
	 * Tests UUID generation
	 * <id name="id" strategy="uuid"/>
	 */
	@Test
	@SuppressWarnings("unchecked")
    public void testUuid() {
		String componentName = "id";
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("idType", "string");
		type.setGenerator(HibUUIDGenerator.class.getName());
		IdDescriptor id = new IdDescriptor(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		Generator<String> helper = new ComponentBuilderGenerator(builder, componentName);
		expectUniqueGenerations(helper, 10);
	}
	
	/**
	 * Tests 'increment' id generation with unspecified type
	 * <id name="id" strategy="increment"/>
	 */
	@Test
	@SuppressWarnings("unchecked")
    public void testIncrementIdWithoutType() {
		String componentName = "id";
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("idType", "long");
		type.setGenerator(IncrementGenerator.class.getName());
		IdDescriptor id = new IdDescriptor(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		Generator<Long> helper = new ComponentBuilderGenerator(builder, componentName);
		expectGeneratedSequenceOnce(helper, 1L, 2L, 3L, 4L);
	}
	
	/**
	 * Tests id generation with unspecified type and strategy
	 * <id name="id"/>
	 */
	@Test
	@SuppressWarnings("unchecked")
    public void testDefaultIdGeneration() {
		String componentName = "id";
		IdDescriptor id = new IdDescriptor(componentName);
		ComponentBuilder builder = createComponentBuilder(id);
		Generator<Long> helper = new ComponentBuilderGenerator(builder, componentName);
		expectGeneratedSequenceOnce(helper, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
		assertTrue(helper.available());
	}
	
	/**
	 * Tests 'increment' id generation with 'byte' type
	 * <id name="id" type="byte" strategy="increment"/>
	 */
	@Test
	@SuppressWarnings("unchecked")
    public void testIncrementByteId() {
		String componentName = "id";
		SimpleTypeDescriptor type = new SimpleTypeDescriptor("idType", "byte");
		type.setGenerator(IncrementGenerator.class.getName());
		IdDescriptor id = new IdDescriptor(componentName, type);
		ComponentBuilder builder = createComponentBuilder(id);
		Generator<Byte> helper2 = new ComponentBuilderGenerator(builder, componentName);
		expectGeneratedSequenceOnce(helper2, (byte) 1, (byte) 2, (byte) 3, (byte) 4);
	}
	
	@Test
    @SuppressWarnings("cast")
    public void testAlternative() {
    	AlternativeGroupDescriptor alternativeType = new AlternativeGroupDescriptor(null);
    	SimpleTypeDescriptor typeA = (SimpleTypeDescriptor) new SimpleTypeDescriptor("A", "string").withValues("'1'");
		alternativeType.addComponent(new PartDescriptor("a", typeA));
    	SimpleTypeDescriptor typeB = (SimpleTypeDescriptor) new SimpleTypeDescriptor("B", "string").withValues("'2'");
		alternativeType.addComponent(new PartDescriptor("b", typeB));
		BeneratorContext context = new BeneratorContext(null);
		PartDescriptor part = new PartDescriptor(null, alternativeType);
		ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, context);
		Entity entity = new Entity("Entity");
		builder.buildComponentFor(entity);
    }
	
	@Test
	public void testMap() {
		String componentName = "flag";
		PartDescriptor part = new PartDescriptor(componentName);
		part.setMinCount(new ConstantExpression<Long>(1L));
		part.setMaxCount(new ConstantExpression<Long>(1L));
		((SimpleTypeDescriptor) part.getLocalType(false)).setMap("1->'A',2->'B'");
		part.getLocalType(false).setGenerator("org.databene.benerator.primitive.IncrementGenerator");
		BeneratorContext context = new BeneratorContext(null);
		ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, context);
		Entity entity = new Entity("Entity");
		builder.buildComponentFor(entity);
		assertEquals("A", entity.get("flag"));
		builder.buildComponentFor(entity);
		assertEquals("B", entity.get("flag"));
	}
    
    // test construction alternatives ----------------------------------------------------------------------------------
    
/*
	@Test
    public void testGenerator() {
        createGenerator("test", "generator", BooleanGenerator.class.getName());
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "nullQuota", "0.5");
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "type", "string");
        createGenerator("test",
                "generator", BooleanGenerator.class.getName(),
                "type", "string",
                "nullQuota", "0.5");
    }

	@Test
    public void testSamples() {
        createGenerator("test",
                "values", "1,2,3");
        createGenerator("test",
                "values", "1,2,3",
                "type", "char");
        createGenerator("test",
                "values", "1,2,3",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "type", "int",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "2000-01-01,2000-01-02,2000-01-03",
                "type", "date",
                "pattern", "yyyy-MM-dd",
                "nullQuota", "0.5");
        // sequence
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", "cumulated",
                "type", "int",
                "nullQuota", "0.5");
        // weight function
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName());
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "nullQuota", "0.5");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "type", "int");
        createGenerator("test",
                "values", "1,2,3",
                "distribution", ConstantFunction.class.getName(),
                "type", "int",
                "nullQuota", "0.5");
    }

    @Test
	public void testNumbers() {
        checkNumberType("int");
        checkNumberType("byte");
        checkNumberType("short");
        checkNumberType("long");
        checkNumberType("double");
        checkNumberType("float");
        checkNumberType("big_integer");
        checkNumberType("big_decimal");
    }

	@Test
    public void testStrings() {
        createGenerator("test",
                "type", "string");
        createGenerator("test",
                "type", "string",
                "maxLength", "10");
        createGenerator("test",
                "type", "string",
                "minLength", "5",
                "maxLength", "10");
        createGenerator("test",
                "type", "string",
                "minLength", "5",
                "maxLength", "5");
        createGenerator("test",
                "type", "string",
                "pattern", "[0-9]{5}",
                "minLength", "5",
                "maxLength", "5");
        createGenerator("test",
                "type", "string",
                "pattern", "[0-9]{5}",
                "minLength", "4",
                "maxLength", "6");
    }

	@Test
    public void testBoolean() {
        createGenerator("test",
                "type", "boolean");
        createGenerator("test",
                "type", "boolean",
                "trueQuota", "0.5");
        createGenerator("test",
                "type", "boolean",
                "nullQuota", "0");
        createGenerator("test",
                "type", "boolean",
                "trueQuota", "0.5",
                "nullQuota", "0");
    }

	@Test
    public void testDate() {
        createGenerator("test", "type", "date");
        createGenerator("test",
                "type", "date",
                "min", "2000-01-01",
                "max", "2000-12-31",
                "pattern", "yyyy-MM-dd"
        );
        createGenerator("test",
                "type", "date",
                "min", "01/01/2000",
                "max", "01/03/2000"
        );
        createGenerator("test",
                "type", "date",
                "min", "01.01.2000",
                "max", "03.01.2000",
                "locale", "de"
        );
        createGenerator("test",
                "type", "date",
                "min", "2000-01-01",
                "max", "2000-12-31",
                "precision", "0000-00-01",
                "distribution", "cumulated",
                "pattern", "yyyy-MM-dd",
                "nullQuota", "0.1"
        );
    }

	@Test
    public void testCharacter() {
        createGenerator("test", "type", "char");
        createGenerator("test",
                "type", "char",
                "pattern", "\\w",
                "locale", "de",
                "nullQuota", "0.5");
    }

	@Test
    public void testImportToType() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "type", "string");
        createGenerator("test",
                "source", "org/databene/benerator/composite/booleans.txt",
                "type", "boolean");
        createGenerator("test",
                "source", "org/databene/benerator/composite/chars.txt",
                "type", "char");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "byte");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "short");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "int");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "long");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "float");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "double");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "big_integer");
        createGenerator("test",
                "source", "org/databene/benerator/composite/numbers.txt",
                "type", "big_decimal");
    }

	@Test
    public void testDateImport() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "pattern", "yyyy-MM-dd",
                "type", "date");
    }

	@Test
    public void testConvertingImport() {
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "converter", "org.databene.commons.converter.NoOpConverter");
        createGenerator("test",
                "source", "org/databene/benerator/composite/dates.txt",
                "type", "date",
                "pattern", "yyyy-MM-dd");
    }

    // private helpers -------------------------------------------------------------------------------------------------

	@Test
    private void checkNumberType(String type) {
        createGenerator("test",
                "type", type);
        createGenerator("test",
                "type", type,
                "min", "1");
        createGenerator("test",
                "type", type,
                "max", "2");
        createGenerator("test",
                "type", type,
                "distribution", "cumulated");
        createGenerator("test",
                "type", type,
                "min", "1",
                "max", "2");
        createGenerator("test",
                "type", type,
                "min", "-2",
                "max", "1");
        createGenerator("test",
                "type", type,
                "min", "1",
                "max", "2",
                "distribution", "cumulated");
        createGenerator("test",
                "type", type,
                "min", "-2",
                "max", "1",
                "distribution", "cumulated");
    }
*/
    // private helpers -------------------------------------------------------------------------------------------------
/*
	@Test
    private ComponentBuilder createGenerator(String name, String ... featureDetails) {
        GenerationSetup setup = new SimpleGenerationSetup();
        logger.debug("Test #" + (++testCount));
        if (featureDetails.length % 2 != 0)
            throw new ConfigurationError("Illegal setup: need an even number of parameters (name/value pairs)");
        SimpleTypeDescriptor type = new SimpleTypeDescriptor(name, (String) null);
        PartDescriptor part = new PartDescriptor(name, type);
        for (int i = 0; i < featureDetails.length; i += 2)
            if (componentFeatures.contains(featureDetails[i]))
                if ("type".equals(featureDetails[i]))
                    part.setTypeName(featureDetails[i + 1]);
                else
                    part.setDetailValue(featureDetails[i], featureDetails[i + 1]);
            else
                type.setDetailValue(featureDetails[i], featureDetails[i + 1]);
        ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(part, new DefaultContext(), setup);
        Entity entity = new Entity("Entity");
        for (int i = 0; i < 10; i++) {
            builder.buildComponentFor(entity);
            logger.debug(entity.getComponent(name));
        }
        return builder;
    }
*/
	// private helpers -------------------------------------------------------------------------------------------------
	
	private ComponentBuilder createComponentBuilder(ComponentDescriptor component) {
		return createComponentBuilder(component, new BeneratorContext(contextUri));
	}
	
	private ComponentBuilder createComponentBuilder(ComponentDescriptor component, BeneratorContext context) {
		return ComponentBuilderFactory.createComponentBuilder(component, context);
	}
	
	public static final class ComponentBuilderGenerator<E> extends LightweightGenerator<E> {
		
		private ComponentBuilder builder;
		private String componentName;

		public ComponentBuilderGenerator(ComponentBuilder builder, String componentName) {
			this.builder = builder;
			this.componentName = componentName;
		}

        @SuppressWarnings("unchecked")
        public Class<E> getGeneratedType() {
	        return (Class<E>) Object.class;
        }

        @Override
		public boolean available() {
			return builder.available();
		}
		
		@SuppressWarnings("unchecked")
        public E generate() {
			Entity entity = new Entity("Test");
			builder.buildComponentFor(entity);
			return (E) entity.get(componentName);
		}
		
		@Override
		public void reset() {
			builder.reset();
		}
		
		@Override
		public void close() {
			builder.close();
		}

	}

	@SuppressWarnings("unchecked")
    private <T> void expectUniqueSequence(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		expectGeneratedSequence(helper, products).withCeasedAvailability();
	}

	@SuppressWarnings("unchecked")
    private <T> void expectUniqueSet(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		expectGeneratedSet(helper, products).withCeasedAvailability();
	}
	/*
	private <T> void expectSequence(PartDescriptor name, T... products) {
		ComponentBuilder builder = createComponentBuilder(name);
		Generator<T> helper = new ComponentBuilderGenerator(builder, name.getName());
		expectGeneratedSet(helper, products).withContinuedAvailability();
	}
	*/
}
