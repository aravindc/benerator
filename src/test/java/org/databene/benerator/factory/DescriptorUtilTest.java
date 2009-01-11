/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.test.ConverterMock;
import org.databene.benerator.test.GeneratorMock;
import org.databene.benerator.test.GeneratorProxyMock;
import org.databene.benerator.test.ValidatorMock;
import org.databene.benerator.test.WeightFunctionMock;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.BeanUtil;
import org.databene.commons.Converter;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.id.IdProvider;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.function.FeatureWeight;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;

import junit.framework.TestCase;

/**
 * Tests the {@link DescriptorUtil} class.<br/>
 * <br/>
 * Created at 31.12.2008 09:29:38
 * @since 0.5.7
 * @author Volker Bergmann
 */
public class DescriptorUtilTest extends TestCase {
	
	@Override
	protected void tearDown() throws Exception {
		ConverterMock.latestInstance = null;
		super.tearDown();
	}
	
	
	// instantiation tests ---------------------------------------------------------------------------------------------

	
	public void testGetConverter() {
		
		// test bean reference
		checkGetConverter("c", new ConverterMock(2), "c", 3);
		
		// test class name specification
		checkGetConverter(null, null, ConverterMock.class.getName(), 2);
		
		// test constructor spec
		checkGetConverter(null, null, ConverterMock.class.getName() + "(2)", 3);
		
		// test property spec
		checkGetConverter(null, null, ConverterMock.class.getName() + "[increment=3]", 4);
		
		// test converter chaining
		checkGetConverter("c", new ConverterMock(3), "c," + ConverterMock.class.getName() + "(5)", 9);
	}

	public void testGetValidator() {
		
		// test bean reference
		checkGetValidator("c", new ValidatorMock(2), "c", 2);
		
		// test class name specification
		checkGetValidator(null, null, ValidatorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetValidator(null, null, ValidatorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetValidator(null, null, ValidatorMock.class.getName() + "[value=3]", 3);
		
		// test converter chaining
		checkGetValidator("c", new ValidatorMock(3), "c," + ValidatorMock.class.getName() + "(5)", null);
		checkGetValidator("c", new ValidatorMock(3), "c," + ValidatorMock.class.getName() + "(3)", 3);
	}
	
	public void testGetProxy() {
		String mockName = GeneratorProxyMock.class.getName();
		checkGetProxy(mockName, null, null, "param1", 1, "param2", 2);
		checkGetProxy(mockName + "(3, 4)", null, null, "param1", 3, "param2", 4);
		checkGetProxy(mockName + "[param1 = 5, param2 = 6]", null, null, "param1", 5, "param2", 6);
		checkGetProxy("repeat", null, null, "minRepetitions", 0L, "maxRepetitions", 3L);
		checkGetProxy("repeat", 4L, 5L, "minRepetitions", 4L, "maxRepetitions", 5L);
		checkGetProxy("repeat(6, 7)", null, null, "minRepetitions", 6L, "maxRepetitions", 7L);
		checkGetProxy("repeat[minRepetitions=8, maxRepetitions=9]", null, null, "minRepetitions", 8L, "maxRepetitions", 9L);
		checkGetProxy("skip", null, null, "minIncrement", 1L, "maxIncrement", 1L);
		checkGetProxy("skip", 3L, 4L, "minIncrement", 3L, "maxIncrement", 4L);
		checkGetProxy("skip(5, 6)", null, null, "minIncrement", 5L, "maxIncrement", 6L);
		checkGetProxy("skip[minIncrement=7, maxIncrement=8]", null, null, "minIncrement", 7L, "maxIncrement", 8L);
	}
	
	public void testGetGeneratorByName() {
		// test bean reference
		checkGetGeneratorByName("c", new GeneratorMock(2), "c", 2);
		
		// test class name specification
		checkGetGeneratorByName(null, null, GeneratorMock.class.getName(), 1);
		
		// test constructor spec
		checkGetGeneratorByName(null, null, GeneratorMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetGeneratorByName(null, null, GeneratorMock.class.getName() + "[value=3]", 3);
	}

	public void testGetIdProvider() {
		checkGetIdProvider("increment", null, 1L);
		checkGetIdProvider("increment", "10", 10L);
		checkGetIdProvider("increment(10)", null, 10L);
		checkGetIdProvider("uuid", null, null);
		checkGetIdProvider("IdProviderMock", null, 1);
		checkGetIdProvider("IdProviderMock(2)", null, 2);
		checkGetIdProvider("org.databene.benerator.test.IdProviderMock(2)", null, 2);
		checkGetIdProvider("org.databene.benerator.test.IdProviderMock[value = 3]", null, 3);
	}

	public void testParseConsumers() {
		//throw new UnsupportedOperationException("Not implemented yet"); // TODO v0.5.7 test
	}
	
	// distribution tests ----------------------------------------------------------------------------------------------
	
	
	public void testGetDistributionDefault() {
		SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor("myType");
		BeneratorContext context = new BeneratorContext(null);
		assertNull(DescriptorUtil.getDistribution(descriptor, false, context));
		assertEquals(Sequence.BIT_REVERSE, DescriptorUtil.getDistribution(descriptor, true, context));
	}

	public void testGetDistributionForWeightFunction() {
		// test bean reference
		checkGetWeightFunction("c", new WeightFunctionMock(2), "c", 2);
		
		// test class name specification
		checkGetWeightFunction(null, null, WeightFunctionMock.class.getName(), 1);
		
		// test constructor spec
		checkGetWeightFunction(null, null, WeightFunctionMock.class.getName() + "(2)", 2);
		
		// test property spec
		checkGetWeightFunction(null, null, WeightFunctionMock.class.getName() + "[value=3]", 3);
	}

	public void testGetDistributionForSequence() {
		checkGetSequence(null, null, "random", null, null, null);
		checkGetSequence(null, null, "random", "1", "1", 1);
		checkGetSequence("id", Sequence.STEP, "id", null, null, 1);
/* TODO v0.5.7 test custom sequences
		checkGetSequence(null, null, "org.databene.benerator.test.SequenceMock", null, null, 1);
		checkGetSequence(null, null, "SequenceMock", null, null, 1);
		checkGetSequence(null, null, "SequenceMock(2)", null, null, 2);
		checkGetSequence(null, null, "SequenceMock[value = 3]", null, null, 3);
*/
	}

	public void testGetDistributionWeighted() {
		BeneratorContext context = new BeneratorContext(null);
		
		// test 'weighted'
		SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor("myType").withDistribution("weighted");
		Distribution distribution = DescriptorUtil.getDistribution(descriptor, false, context);
		assertTrue(distribution instanceof FeatureWeight);
		
		// test 'weighted[population]'
		SimpleTypeDescriptor descriptor2 = new SimpleTypeDescriptor("myType").withDistribution("weighted[population]");
		Distribution distribution2 = DescriptorUtil.getDistribution(descriptor2, false, context);
		assertTrue(distribution2 instanceof FeatureWeight);
		assertEquals("population", ((FeatureWeight) distribution2).getWeightFeature());
	}

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
	
	
	public void testGetValues() {
		assertNull(DescriptorUtil.getValues(new SimpleTypeDescriptor("test"), null));
		assertTrue(Arrays.equals(new Object[] {"a", "b"}, 
				DescriptorUtil.getValues(new SimpleTypeDescriptor("test").withValues("a,b"), null)));
		assertTrue(Arrays.equals(new Object[] {"a", "b"}, 
				DescriptorUtil.getValues(new SimpleTypeDescriptor("test").withValues("a|b").withSeparator("|"), null)));
		assertTrue(Arrays.equals(new Object[] {"a", "b,c"}, 
				DescriptorUtil.getValues(new SimpleTypeDescriptor("test").withValues("a|b,c").withSeparator("|"), null)));
	}
	
	public void testGetPatternAsDateFormat() {
		Date date = TimeUtil.date(2000, 0, 2);
		// test default format
		DateFormat format = DescriptorUtil.getPatternAsDateFormat(new SimpleTypeDescriptor("test"));
		assertEquals("2000-01-02", format.format(date));
		// test custom format
		format = DescriptorUtil.getPatternAsDateFormat(new SimpleTypeDescriptor("test").withPattern("yy-MM-dd"));
		assertEquals("00-01-02", format.format(date));
	}

	public void testGetLocale() {
		assertEquals(Locale.US, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test")));
		assertEquals(Locale.ENGLISH, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test").withLocaleId("en")));
		assertEquals(Locale.GERMANY, DescriptorUtil.getLocale(new SimpleTypeDescriptor("test").withLocaleId("de_DE")));
	}

	public void testIsUnique() {
		assertEquals(false, DescriptorUtil.isUnique(new InstanceDescriptor("test")));
		assertEquals(false, DescriptorUtil.isUnique(new InstanceDescriptor("test").withUnique(false)));
		assertEquals(true, DescriptorUtil.isUnique(new InstanceDescriptor("test").withUnique(true)));
	}

	public void testGetNullQuota() {
		assertEquals(0., DescriptorUtil.getNullQuota(new InstanceDescriptor("test")));
		assertEquals(0., DescriptorUtil.getNullQuota(new InstanceDescriptor("test").withNullQuota(0)));
		assertEquals(1., DescriptorUtil.getNullQuota(new InstanceDescriptor("test").withNullQuota(1)));
	}
	
	public void testGetSeparator() {
		BeneratorContext context = new BeneratorContext(null);
		assertEquals(',', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x"), context));
		context.setDefaultSeparator('|');
		assertEquals('|', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x"), context));
		assertEquals(';', DescriptorUtil.getSeparator(new SimpleTypeDescriptor("x").withSeparator(";"), context));
	}
	
	public void testGetMinCount() {
		BeneratorContext context = new BeneratorContext(".");
		// default
		assertEquals(1, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context));
		// set explicitly
		assertEquals(2, DescriptorUtil.getMinCount(new InstanceDescriptor("x").withMinCount(2), context));
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3, DescriptorUtil.getMinCount(new InstanceDescriptor("x").withMinCount(4), context));
		// ignore global maxCount in default case
		context.setMaxCount(5L);
		assertEquals(1, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context));
		// global maxCount overrides default
		context.setMaxCount(0L);
		assertEquals(0, DescriptorUtil.getMinCount(new InstanceDescriptor("x"), context));
	}
	
	public void testGetMaxCount() {
		BeneratorContext context = new BeneratorContext(".");
		// default
		assertEquals(null, DescriptorUtil.getMaxCount(new InstanceDescriptor("x"), context));
		// explicit setting
		assertEquals(2L, DescriptorUtil.getMaxCount(new InstanceDescriptor("x").withMaxCount(2), context).longValue());
		// override by global maxCount
		context.setMaxCount(3L);
		assertEquals(3L, DescriptorUtil.getMaxCount(new InstanceDescriptor("x").withMaxCount(4), context).longValue());
		// global maxCount overrides default
		context.setMaxCount(0L);
		assertEquals(0L, DescriptorUtil.getMaxCount(new InstanceDescriptor("x"), context).longValue());
	}
	
	public void testGetCountDistribution() {
		//throw new UnsupportedOperationException("Not implemented yet"); // TODO v0.5.7 test
	}
	

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

	@SuppressWarnings("unchecked")
	private void checkGetWeightFunction(
			String contextKey, Distribution contextValue, String distributionSpec, double expectedValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setDistribution(distributionSpec);
		Distribution distribution = DescriptorUtil.getDistribution(descriptor, false, context); // TODO test unique=true?
		assertNotNull(distribution);
		assertTrue(distribution instanceof WeightFunction);
		assertEquals(expectedValue, ((WeightFunction) distribution).value(0));
	}

	@SuppressWarnings("unchecked")
	private void checkGetSequence(
			String contextKey, Distribution contextValue, 
			String distributionSpec, String variation1, String variation2, Integer expectedValue) {
		BeneratorContext context = new BeneratorContext(null);
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setDistribution(distributionSpec);
		Distribution distribution = DescriptorUtil.getDistribution(descriptor, false, context); // TODO test unique=true?
		assertNotNull(distribution);
		assertTrue(distribution instanceof Sequence);
		// TODO test sequence settings
	}

	@SuppressWarnings("unchecked")
	private void checkGetProxy(String spec, Long proxyParam1, Long proxyParam2,
			String prop1, Object expected1, String prop2, Object expected2) {
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setProxy(spec);
		descriptor.setProxyParam1(proxyParam1);
		descriptor.setProxyParam2(proxyParam2);
		Generator source = new GeneratorMock();
		BeneratorContext context = new BeneratorContext(null);
		context.importDefaults();
		GeneratorProxy proxy = (GeneratorProxy) DescriptorUtil.wrapWithProxy(source, descriptor, context);
		assertNotNull(proxy);
		assertEquals(source, proxy.getSource());
		assertEquals(expected1, BeanUtil.getPropertyValue(proxy, prop1));
		assertEquals(expected2, BeanUtil.getPropertyValue(proxy, prop2));
		assertTrue(proxy.available());
		assertNotNull(proxy.generate());
	}

	@SuppressWarnings("unchecked")
	private void checkGetIdProvider(String spec, String param, Object expected) {
		IdDescriptor descriptor = new IdDescriptor("id");
		descriptor.setStrategy(spec);
		descriptor.setParam(param);
		BeneratorContext context = new BeneratorContext(null);
		context.importPackage("org.databene.benerator.test");
		IdProvider idProvider = DescriptorUtil.getIdProvider(descriptor, context);
		assertNotNull(idProvider);
		assertTrue(idProvider.hasNext());
		if (expected != null)
			assertEquals(expected, idProvider.next());
	}

}
