/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator;

import junit.framework.TestCase;

import java.util.Set;
import java.util.Collection;

import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.model.Validator;
import org.databene.model.validator.UniqueValidator;
import org.databene.model.converter.ToStringConverter;
import org.databene.measure.count.ObjectCounter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides methods for testing generators.<br/>
 * <br/>
 * Created: 15.11.2007 14:46:31
 */
public abstract class GeneratorTest extends TestCase {

    private static final Log logger = LogFactory.getLog(GeneratorTest.class);

    public GeneratorTest() {
    }

    public GeneratorTest(String uri) {
        super(uri);
    }

    // helper methods for this and child classes -----------------------------------------------------------------------

    protected static <T> Helper expectGeneratedSequence(Generator<T> generator, T ... products) {
        expectGeneratedSequenceOnce(generator, products);
        generator.reset();
        expectGeneratedSequenceOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectGeneratedSet(Generator<T> generator, T ... products) {
        expectGeneratedSetOnce(generator, products);
        generator.reset();
        expectGeneratedSetOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectUniqueFromSet(Generator<T> generator, T ... products) {
        expectUniqueFromSetOnce(generator, products);
        generator.reset();
        expectUniqueFromSetOnce(generator, products);
        return new Helper(generator);
    }

    protected <T> Helper expectUniqueProducts(Generator<T> generator, int n) {
        expectUniqueProductsOnce(generator, n);
        generator.reset();
        expectUniqueProductsOnce(generator, n);
        return new Helper(generator);
    }

    protected <T> Helper expectGenerations(Generator<T> generator, int n, Validator<T> ... validators) {
        expectGenerationsOnce(generator, n, validators);
        generator.reset();
        expectGenerationsOnce(generator, n, validators);
        return new Helper(generator);
    }

    protected <T>String format(T product) {
        return ToStringConverter.convert(product, "[null]");
    }

    // Number generator tests ------------------------------------------------------------------------------------------

    public static <T extends Number> void checkEqualDistribution(
            Class<? extends NumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, double tolerance, T ... expectedValues) {
        Set<T> expectedSet = ArrayUtil.toSet(expectedValues);
        checkDistribution(generatorClass, min, max, precision, iterations, true, tolerance, expectedSet);
    }

    public static <T extends Number> void checkEqualDistribution(
            Class<? extends NumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, double tolerance, Set<T> expectedSet) {
        checkDistribution(generatorClass, min, max, precision, iterations, true, tolerance, expectedSet);
    }

    private static <T extends Number> void checkDistribution(
            Class<? extends NumberGenerator<T>> generatorClass, T min, T max, T precision,
            int iterations, boolean equalDistribution, double tolerance, Set<T> expectedSet) {
        NumberGenerator<T> generator = BeanUtil.newInstance(generatorClass);
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
            TestCase.assertTrue("Distribution is not equal: " + counter, counter.equalDistribution(tolerance));
        if (expectedSet != null)
            TestCase.assertEquals(expectedSet, counter.objectSet());
    }

    public static class Helper {
        private Generator generator;

        public Helper(Generator generator) {
            this.generator = generator;
        }

        public void withCeasedAvailability() {
            assertFalse("Generator is expected to be unavailable: " + generator, generator.available());
            try {
                generator.generate();
                fail("When generator " + generator + " is unavailable, a generate() call is expected " +
                        "to cause an " + IllegalGeneratorStateException.class.getSimpleName());
            } catch (IllegalGeneratorStateException e) {
                // exception is required
            } catch (Throwable t) {
                fail("When generator " + generator + " is unavailable, a generate() call is expected " +
                        "to cause an " + IllegalGeneratorStateException.class.getSimpleName() + ". " +
                        "Instead this exception was thrown: " + t);
                t.printStackTrace();
            } finally {
                generator.close();
            }
        }

        public void withContinuedAvailability() {
            assertTrue(generator.available());
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static <T>void expectGeneratedSequenceOnce(Generator<T> generator, T... products) {
        generator.validate();
        for (T expectedProduct : products) {
            assertTrue("Generator is unexpectedly unavailable: " + generator, generator.available());
            assertEquals(expectedProduct, generator.generate());
        }
    }

    private <T>void expectGeneratedSetOnce(Generator<T> generator, T... products) {
        generator.validate();
        Set<T> expectedSet = ArrayUtil.toSet(products);
        for (int i = 0; i < products.length; i++) {
            assertTrue("Generator has gone unavailable before creating a number of products " +
                    "that matches the expected set.", generator.available());
            T product = generator.generate();
            logger.debug("created " + format(product));
            assertTrue("The generated value '" + format(product) + "' was not in the expected set: " + expectedSet,
                    expectedSet.contains(product));
        }
    }

    private <T>void expectUniqueFromSetOnce(Generator<T> generator, T... products) {
        generator.validate();
        Set<T> expectedSet = ArrayUtil.toSet(products);
        UniqueValidator<T> validator = new UniqueValidator<T>();
        for (int i = 0; i < products.length; i++) {
            assertTrue("Generator has gone unavailable before creating a number of products " +
                    "that matches the expected set: " + generator, generator.available());
            T product = generator.generate();
            logger.debug("created " + format(product));
            assertTrue("Product is not unique: " + product, validator.valid(product));
            assertTrue("The generated value '" + format(product) + "' was not in the expected set: " + expectedSet,
                    expectedSet.contains(product));
        }
    }

    private <T>void expectUniqueProductsOnce(Generator<T> generator, int n) {
        generator.validate();
        UniqueValidator<T> validator = new UniqueValidator<T>();
        for (int i = 0; i < n; i++) {
            assertTrue("Generator is not available: " + generator, generator.available());
            T product = generator.generate();
            logger.debug("created: " + format(product));
            assertTrue("Product is not unique: " + product, validator.valid(product));
        }
    }

    private <T> void expectGenerationsOnce(Generator<T> generator, int n, Validator<T> ... validators) {
        generator.validate();
        for (int i = 0; i < n; i++) {
            assertTrue("Generator has gone unavailable before creating the required number of products ",
                    generator.available());
            T product = generator.generate();
            logger.debug("created " + format(product));
            for (Validator<T> validator : validators) {
                assertTrue("The generated value '" + format(product) + "' is not valid accoring to " + validator,
                        validator.valid(product));
            }
        }
    }


}
