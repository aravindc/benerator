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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.AttachedWeight;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.FeatureWeight;
import org.databene.benerator.distribution.WeightFunction;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.test.ConverterMock;
import org.databene.benerator.test.GeneratorMock;
import org.databene.benerator.test.JSR303ConstraintValidatorMock;
import org.databene.benerator.test.ValidatorMock;
import org.databene.benerator.test.WeightFunctionMock;
import org.databene.commons.Converter;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;

import org.junit.After;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link DescriptorUtil} class.<br/>
 * <br/>
 * Created at 31.12.2008 09:29:38
 * @since 0.5.7
 * @author Volker Bergmann
 */
public class DescriptorUtilTest {
	
	@After
	public void tearDown() throws Exception {
		ConverterMock.latestInstance = null;
	}

	// instantiation tests ---------------------------------------------------------------------------------------------

	@Test
	public void testGetConverter() {
		
		// test bean reference
		checkGetConverter("c", new ConverterMock(2), "c", 3);
		
		// test class name specification
		checkGetConverter(null, null, ConverterMock.class.getName(), 2);
		
		// test constructor spec
		checkGetConverter(null, null, "new " + ConverterMock.class.getName() + "(2)", 3);
		
		// test property spec
		checkGetConverter(null, null, "new " + ConverterMock.class.getName() + "[increment=3]", 4);
		
		// test converter chaining
		checkGetConverter("c", new ConverterMock(3), "c, new " + ConverterMock.class.getName() + "(5)", 9);
	}

	@Test
	public void testGetValidator() {
		
		// test bean reference
		checkGetValidator("c", new ValidatorMock(2), "c", 2);
		
		// test class name specification
		checkGetValidator(null, null, ValidatorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetValidator(null, null, "new " + ValidatorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetValidator(null, null, "new " + ValidatorMock.class.getName() + "[value=3]", 3);
		
		// test converter chaining
		checkGetValidator("c", new ValidatorMock(3), "c, new " + ValidatorMock.class.getName() + "(5)", null);
		checkGetValidator("c", new ValidatorMock(3), "c, new " + ValidatorMock.class.getName() + "(3)", 3);
		
		// test JSR 303 constraint validator
		checkGetValidator(null, null, "new " + JSR303ConstraintValidatorMock.class.getName() + "(2)", 2);
	}
	
	@Test
	public void testGetGeneratorByName() {
		// test bean reference
		checkGetGeneratorByName("c", new GeneratorMock(2), "c", 2);
		
		// test class name specification
		checkGetGeneratorByName(null, null, GeneratorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetGeneratorByName(null, null, "new " + GeneratorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetGeneratorByName(null, null, "new " + GeneratorMock.class.getName() + "[value=3]", 3);
	}

	@Test
	public void testParseConsumers() {
		Entity entity = new Entity("Person", "name", "Alice");
		BeneratorContext context = new BeneratorContext(".");
		
		// test constructor syntax
		ConsumerChain<Entity> consumer = DescriptorUtil.parseConsumersSpec(ConsumerMock.class.getName(), context);
		consumer.startConsuming(entity);
		assertEquals(1, consumer.componentCount());
		ConsumerMock consumerMock = (ConsumerMock) consumer.getComponent(0);
		assertEquals(entity, consumerMock.lastEntity);
		assertEquals(1, consumerMock.id);
		
		// test constructor syntax
		consumer = DescriptorUtil.parseConsumersSpec("new " + ConsumerMock.class.getName() + "(2)", context);
		consumer.startConsuming(entity);
		assertEquals(1, consumer.componentCount());
		consumerMock = (ConsumerMock) consumer.getComponent(0);
		assertEquals(entity, consumerMock.lastEntity);
		assertEquals(2, consumerMock.id);
		
		// test reference
		context.set("myconsumer", new ConsumerMock());
		consumer = DescriptorUtil.parseConsumersSpec("myconsumer", context);
		consumer.startConsuming(entity);
		assertEquals(1, consumer.componentCount());
		assertEquals(entity, ((ConsumerMock) consumer.getComponent(0)).lastEntity);
		
		// test comma-separated combination
		context.set("myconsumer", new ConsumerMock());
		consumer = DescriptorUtil.parseConsumersSpec("myconsumer," + ConsumerMock.class.getName(), context);
		consumer.startConsuming(entity);
		assertEquals(2, consumer.componentCount());
		assertEquals(entity, ((ConsumerMock) consumer.getComponent(0)).lastEntity);
		assertEquals(entity, ((ConsumerMock) consumer.getComponent(1)).lastEntity);
	}
	
	// distribution tests ----------------------------------------------------------------------------------------------
	
	@Test
	public void testGetDistributionForWeightFunction() {
		// test bean reference
		checkGetWeightFunction("c", new WeightFunctionMock(2), "c", 2);
		
		// test class name specification
		checkGetWeightFunction(null, null, WeightFunctionMock.class.getName(), 1);
		
		// test constructor spec
		checkGetWeightFunction(null, null, "new " + WeightFunctionMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetWeightFunction(null, null, "new " + WeightFunctionMock.class.getName() + "[value=3]", 3);
	}

	@Test
    public void testGetDistributionWeighted() {
		BeneratorContext context = new BeneratorContext(null);
		
		// test 'weighted'
		SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor("myType").withDistribution("weighted");
		Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), false, true, context);
		assertTrue(distribution instanceof AttachedWeight);
		
		// test 'weighted[population]'
		SimpleTypeDescriptor descriptor2 = new SimpleTypeDescriptor("myType").withDistribution("weighted[population]");
		Distribution distribution2 = GeneratorFactoryUtil.getDistribution(descriptor2.getDistribution(), false, true, context);
		assertTrue(distribution2 instanceof FeatureWeight);
		assertEquals("population", ((FeatureWeight) distribution2).getWeightFeature());
	}

	@Test
	public void testIsWrappedSimpleType() {
		// test wrapped simple type
		PartDescriptor bodyDescriptor = new PartDescriptor(ComplexTypeDescriptor.__SIMPLE_CONTENT);
		ComplexTypeDescriptor wrappedSimpleType = new ComplexTypeDescriptor("Test").withComponent(bodyDescriptor);
		assertTrue(DescriptorUtil.isWrappedSimpleType(wrappedSimpleType));
		
		// test complex type
		PartDescriptor partDescriptor = new PartDescriptor("name");
		ComplexTypeDescriptor complexType = new ComplexTypeDescriptor("Test").withComponent(partDescriptor);
		assertFalse(DescriptorUtil.isWrappedSimpleType(complexType));
	}

	
	// configuration tests ---------------------------------------------------------------------------------------------
	
	@Test
	public void testGetPatternAsDateFormat() {
		Date date = TimeUtil.date(2000, 0, 2);
		// test default format
		DateFormat format = DescriptorUtil.getPatternAsDateFormat(new SimpleTypeDescriptor("test"));
		assertEquals("2000-01-02", format.format(date));
		// test custom format
		format = DescriptorUtil.getPatternAsDateFormat(new SimpleTypeDescriptor("test").withPattern("yy-MM-dd"));
		assertEquals("00-01-02", format.format(date));
	}

	@Test
	public void testGetLocale() {
		assertEquals(Locale.US, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test")));
		assertEquals(Locale.ENGLISH, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test").withLocaleId("en")));
		assertEquals(Locale.GERMANY, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test").withLocaleId("de_DE")));
	}

	@Test
	public void testIsUnique() {
		assertEquals(false, DescriptorUtil.isUnique(new InstanceDescriptor("test")));
		assertEquals(false, DescriptorUtil.isUnique(new InstanceDescriptor("test").withUnique(false)));
		assertEquals(true, DescriptorUtil.isUnique(new InstanceDescriptor("test").withUnique(true)));
	}

	@Test
	public void testGetNullQuota() {
		assertEquals(0., DescriptorUtil.getNullQuota(new InstanceDescriptor("test")));
		assertEquals(0., DescriptorUtil.getNullQuota(new InstanceDescriptor("test").withNullQuota(0)));
		assertEquals(1., DescriptorUtil.getNullQuota(new InstanceDescriptor("test").withNullQuota(1)));
	}
	
	@Test
	public void testGetSeparator() {
		BeneratorContext context = new BeneratorContext(null);
		assertEquals(',', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x"), context));
		context.setDefaultSeparator('|');
		assertEquals('|', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x"), context));
		assertEquals(';', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x").withSeparator(";"), context));
	}
	
	/* TODO adapt tests
	@Test
	public void testGetMinCount() {
		BeneratorContext context = new BeneratorContext(".");
		// default
		assertEquals(1, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context).evaluate(context).intValue());
		// set explicitly
		assertEquals(2, DescriptorUtil.getMinCount(new InstanceDescriptor("x").withMinCount(2), context).evaluate(context).intValue());
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3, DescriptorUtil.getMinCount(new InstanceDescriptor("x").withMinCount(4), context).evaluate(context).intValue());
		// ignore global maxCount in default case
		context.setMaxCount(5L);
		assertEquals(1, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context).evaluate(context).intValue());
		// global maxCount overrides default
		context.setMaxCount(0L);
		assertEquals(0, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context).evaluate(context).intValue());
	}
	
	@Test
	public void testGetMaxCount() {
		BeneratorContext context = new BeneratorContext(".");
		// default
		assertNull(DescriptorUtil.getMaxCount(new InstanceDescriptor("x"), context).evaluate(context));
		// explicit setting
		assertEquals(2L, DescriptorUtil.getMaxCount(new InstanceDescriptor("x").withMaxCount(2), context).evaluate(null).longValue());
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3L, DescriptorUtil.getMaxCount(new InstanceDescriptor("x").withMaxCount(4), context).evaluate(null).longValue());
		// global maxCount overrides default
		context.setMaxCount(null);
		assertEquals(null, DescriptorUtil.getMaxCount(new InstanceDescriptor("x"), context).evaluate(null));
	}
	*/
	
	// helpers ---------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void checkGetConverter(String contextKey, Converter contextValue, String converterSpec, int expectedValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setConverter(converterSpec);
		Converter converter = DescriptorUtil.getConverter(descriptor, context);
		assertNotNull(converter);
		assertEquals(expectedValue, converter.convert(1));
	}
	
	@SuppressWarnings("unchecked")
	private void checkGetValidator(String contextKey, Validator contextValue, String validatorSpec, Integer validValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setValidator(validatorSpec);
		Validator validator = DescriptorUtil.getValidator(descriptor, context);
		assertNotNull(validator);
		if (validValue != null)
			assertEquals(true, validator.valid(validValue));
	}
	
	@SuppressWarnings("unchecked")
	private void checkGetGeneratorByName(
			String contextKey, Generator contextValue, String generatorSpec, int expectedValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setGenerator(generatorSpec);
		Generator generator = DescriptorUtil.getGeneratorByName(descriptor, context);
		assertNotNull(generator);
		assertEquals(expectedValue, generator.generate());
	}

	private void checkGetWeightFunction(
			String contextKey, Distribution contextValue, String distributionSpec, double expectedValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setDistribution(distributionSpec);
		Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), false, true, context);
		assertNotNull(distribution);
		assertTrue(distribution instanceof WeightFunction);
		assertEquals(expectedValue, ((WeightFunction) distribution).value(0));
	}

}
