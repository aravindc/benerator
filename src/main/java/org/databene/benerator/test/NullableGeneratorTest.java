/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.primitive.number.AbstractNumberGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Validator;
import org.databene.commons.converter.ToStringConverter;
import org.databene.commons.validator.UniqueValidator;
import org.databene.measure.count.ObjectCounter;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the {@link NullableGenerator}.<br/><br/>
 * Created: 26.01.2010 19:12:43
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class NullableGeneratorTest {

    public final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected BeneratorContext context;

    @Before
    public void setUp() throws Exception {
        context = new BeneratorContext();
        context.importDefaults();
    }

    // helper methods for this and child classes -----------------------------------------------------------------------

    protected static <T> Helper expectGeneratedSequence(NullableGenerator<T> generator, T ... products) {
        expectGeneratedSequenceOnce(generator, products);
        generator.reset();
        expectGeneratedSequenceOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectGeneratedSet(NullableGenerator<T> generator, T ... products) {
        expectGeneratedSetOnce(generator, products);
        generator.reset();
        expectGeneratedSetOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectUniqueFromSet(NullableGenerator<T> generator, T ... products) {
        expectUniqueFromSetOnce(generator, products);
        generator.reset();
        expectUniqueFromSetOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectUniqueProducts(NullableGenerator<T> generator, int n) {
        expectUniqueProductsOnce(generator, n);
        generator.reset();
        expectUniqueProductsOnce(generator, n);
        return new Helper(generator);
    }

    protected <T> Helper expectGenerations(NullableGenerator<T> generator, int n, Validator<T> ... validators) {
        expectGenerationsOnce(generator, n, validators);
        generator.reset();
        expectGenerationsOnce(generator, n, validators);
        return new Helper(generator);
    }

    protected <T> Helper expectUniqueGenerations(NullableGenerator<T> generator, int n) {
        expectUniqueGenerationsOnce(generator, n);
        generator.reset();
        expectUniqueGenerationsOnce(generator, n);
        return new Helper(generator);
    }

    protected <T>String format(T product) {
        return ToStringConverter.convert(product, "[null]");
    }
    
    public static <T> void assertUnavailable(NullableGenerator<T> generator) {
        assertNull("Generator " + generator + " is expected to be unavailable", 
        		generator.generate(new ProductWrapper<T>()));
    }

    public static <T> void assertAvailable(NullableGenerator<T> generator) {
        assertAvailable("Generator " + generator + " is expected to be available", generator);
    }

	public static <T> void assertAvailable(String message, NullableGenerator<T> generator) {
        assertNotNull(message, generator.generate(new ProductWrapper<T>()));
    }

    private static <T> T generateBy(NullableGenerator<T> generator) {
	    ProductWrapper<T> wrapper = generator.generate(new ProductWrapper<T>());
		return (wrapper != null ? wrapper.product : null);
    }

    // Number generator tests ------------------------------------------------------------------------------------------

    public static <T extends Number> void checkEqualDistribution(
            Class<? extends AbstractNumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, double tolerance, T ... expectedValues) {
        Set<T> expectedSet = CollectionUtil.toSet(expectedValues);
        checkDistribution(generatorClass, min, max, precision, iterations, true, tolerance, expectedSet);
    }

    public static <T extends Number> void checkEqualDistribution(
            Class<? extends AbstractNumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, double tolerance, Set<T> expectedSet) {
        checkDistribution(generatorClass, min, max, precision, iterations, true, tolerance, expectedSet);
    }

    private static <T extends Number> void checkDistribution(
            Class<? extends AbstractNumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, boolean equalDistribution, double tolerance, Set<T> expectedSet) {
    	AbstractNumberGenerator<T> generator = BeanUtil.newInstance(generatorClass);
        generator.setMin(min);
        generator.setMax(max);
        generator.setPrecision(precision);
        ObjectCounter<T> counter = new ObjectCounter<T>(expectedSet != null ? expectedSet.size() : 10);
        for (int i = 0; i < iterations; i++)
            counter.count(generator.generate());
        checkDistribution(counter, equalDistribution, tolerance, expectedSet);
    }

    // unspecific generator tests --------------------------------------------------------------------------------------

    public static <E> void checkEqualDistribution(
            Generator<E> generator, int iterations, double tolerance, Set<E> expectedSet) {
        checkDistribution(generator, iterations, true, tolerance, expectedSet);
    }

    public static <T> void checkProductSet(Generator<T> generator, int iterations, Set<T> expectedSet) {
        checkDistribution(generator, iterations, false, 0, expectedSet);
    }

    private static <E> void checkDistribution(Generator<E> generator,
            int iterations, boolean equalDistribution, double tolerance, Set<E> expectedSet) {
        ObjectCounter<E> counter = new ObjectCounter<E>(expectedSet != null ? expectedSet.size() : 10);
        for (int i = 0; i < iterations; i++)
            counter.count(generator.generate());
        checkDistribution(counter, equalDistribution, tolerance, expectedSet);
    }

    protected static void expectRelativeWeights(Generator<?> generator, int iterations, Object... expectedValueWeightPairs) {
	    ObjectCounter<Object> counter = new ObjectCounter<Object>(expectedValueWeightPairs.length / 2);
	    for (int i = 0; i < iterations; i++)
	    	counter.count(generator.generate());
	    Set<Object> productSet = counter.objectSet();
	    double totalExpectedWeight = 0;
	    for (int i = 1; i < expectedValueWeightPairs.length; i += 2)
	    	totalExpectedWeight += ((Number) expectedValueWeightPairs[i]).doubleValue();

	    for (int i = 0; i < expectedValueWeightPairs.length; i += 2) {
            Object value = expectedValueWeightPairs[i];
	    	double expectedWeight = ((Number) expectedValueWeightPairs[i + 1]).doubleValue() / totalExpectedWeight;
			if (expectedWeight > 0) {
	            assertTrue("Generated set does not contain value " + value, productSet.contains(value));
				double measuredWeight = counter.getRelativeCount(value);
				assertTrue(Math.abs(measuredWeight - expectedWeight) / expectedWeight < 0.15);
			} else
	    		assertFalse("Generated contains value " + value + " though it has zero weight", productSet.contains(value));
	    }
    }

    // collection checks -----------------------------------------------------------------------------------------------

    public static <E> void checkEqualDistribution(Collection<E> collection, double tolerance, Set<E> expectedSet) {
        checkDistribution(collection, true, tolerance, expectedSet);
    }

    private static <E> void checkDistribution(Collection<E> collection,
                                              boolean equalDistribution, double tolerance, Set<E> expectedSet) {
        ObjectCounter<E> counter = new ObjectCounter<E>(expectedSet != null ? expectedSet.size() : 10);
        for (E object : collection)
            counter.count(object);
        checkDistribution(counter, equalDistribution, tolerance, expectedSet);
    }

    // counter checks --------------------------------------------------------------------------------------------------

    public static <E> void checkEqualDistribution(
            ObjectCounter<E> counter, double tolerance, Set<E> expectedSet) {
        checkDistribution(counter, true, tolerance, expectedSet);
    }

    private static <E> void checkDistribution(
            ObjectCounter<E> counter, boolean equalDistribution, double tolerance, Set<E> expectedSet) {
        if (equalDistribution)
            assertTrue("Distribution is not equal: " + counter, counter.equalDistribution(tolerance));
        if (expectedSet != null)
        	assertEquals(expectedSet, counter.objectSet());
    }

    public static class Helper {
    	
        private NullableGenerator<?> generator;

        public Helper(NullableGenerator<?> generator) {
            this.generator = generator;
        }

        public void withCeasedAvailability() {
        	try {
        		assertUnavailable(generator);
            } finally {
                generator.close();
            }
        }

        public void withContinuedAvailability() {
            assertAvailable(generator);
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    protected static <T> void expectGeneratedSequenceOnce(NullableGenerator<T> generator, T... products) {
        for (T expectedProduct : products) {
            T generatedProduct = generateBy(generator);
            assertNotNull("Generator is unexpectedly unavailable: " + generator, generatedProduct);
			assertEquals(expectedProduct, generatedProduct);
        }
    }

    private <T>void expectGeneratedSetOnce(NullableGenerator<T> generator, T... products) {
        Set<T> expectedSet = CollectionUtil.toSet(products);
        for (int i = 0; i < products.length; i++) {
        	T generation = generateBy(generator);
            assertNotNull("Generator has gone unavailable before creating a number of products " +
                    "that matches the expected set.", generation);
            logger.debug("created " + format(generation));
            assertTrue("The generated value '" + format(generation) + "' was not in the expected set: " + expectedSet,
                    expectedSet.contains(generation));
        }
    }

    private <T>void expectUniqueFromSetOnce(NullableGenerator<T> generator, T... products) {
        Set<T> expectedSet = CollectionUtil.toSet(products);
        UniqueValidator<Object> validator = new UniqueValidator<Object>();
        for (int i = 0; i < products.length; i++) {
        	T product = generateBy(generator);
            assertNotNull("Generator has gone unavailable before creating the expected number of products. ", 
            		product);
            logger.debug("created " + format(product));
            assertTrue("Product is not unique: " + product, validator.valid(product));
            assertTrue("The generated value '" + format(product) + "' was not in the expected set: " 
            		+ format(expectedSet), expectedSet.contains(product));
        }
    }

    private <T>void expectUniqueProductsOnce(NullableGenerator<T> generator, int n) {
        UniqueValidator<T> validator = new UniqueValidator<T>();
        for (int i = 0; i < n; i++) {
        	T product = generateBy(generator);
            assertNotNull("Generator is not available: " + generator, product);
            logger.debug("created: " + format(product));
            assertTrue("Product is not unique: " + product, validator.valid(product));
        }
    }

    private <T> void expectGenerationsOnce(NullableGenerator<T> generator, int n, Validator<T> ... validators) {
        for (int i = 0; i < n; i++) {
        	T product = generateBy(generator);
            assertNotNull("Generator has gone unavailable before creating the required number of products ",
                    product);
            logger.debug("created " + format(product));
            for (Validator<T> validator : validators) {
                assertTrue("The generated value '" + format(product) + "' is not valid according to " + validator,
                        validator.valid(product));
            }
        }
    }

    private <T> void expectUniqueGenerationsOnce(NullableGenerator<T> generator, int n, Validator<T> ... validators) {
        UniqueValidator<T> validator = new UniqueValidator<T>();
        for (int i = 0; i < n; i++) {
        	T product = generateBy(generator);
            assertNotNull("Generator has gone unavailable before creating the required number of products ",
                    product);
            logger.debug("created " + format(product));
            assertTrue("The generated value '" + format(product) + "' is not unique. Generator is " + generator, 
                    validator.valid(product));
        }
    }

}
