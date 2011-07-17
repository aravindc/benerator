/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.distribution.WeightFunction;
import org.databene.benerator.distribution.function.ConstantFunction;
import org.databene.benerator.distribution.function.GaussianFunction;
import org.databene.benerator.distribution.sequence.HeadSequence;
import org.databene.benerator.distribution.sequence.RandomDoubleGenerator;
import org.databene.benerator.distribution.sequence.RandomIntegerGenerator;
import org.databene.benerator.Generator;
import org.databene.commons.*;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.model.data.Uniqueness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link GeneratorFactory}.<br/><br/>
 * Created: 24.08.2006 07:03:03
 * @since 0.1
 * @author Volker Bergmann
 */
public class VolumeGeneratorFactoryTest extends GeneratorTest {

    private static Logger logger = LoggerFactory.getLogger(VolumeGeneratorFactoryTest.class);
    
    private GeneratorFactory generatorFactory = new VolumeGeneratorFactory();

    // boolean source -----------------------------------------------------------------------------------------------

    public void testGetBooleanGenerator() {
        initAndUseGenerator(generatorFactory.createBooleanGenerator(0.5f));
    }

    // number generators -----------------------------------------------------------------------------------------------

    @Test
    public void testGetNumberGenerator() {
        checkNumberGenerator(Byte.class, (byte) -10, (byte)10, (byte)1);
        checkNumberGenerator(Short.class, (short)-10, (short)10, (short)1);
        checkNumberGenerator(Integer.class, -10, 10, 1);
        checkNumberGenerator(Long.class, (long) -10, (long)10, (long)1);
        checkNumberGenerator(BigInteger.class, new BigInteger("-10"), new BigInteger("10"), new BigInteger("1"));
        checkNumberGenerator(Double.class, (double) -10, (double)10, (double)1);
        checkNumberGenerator(Float.class, (float) -10, (float)10, (float)1);
        checkNumberGenerator(BigDecimal.class, new BigDecimal(-10), new BigDecimal(10), new BigDecimal(1));
    }

    @Test
    public void testGetNumberGeneratorWithoutMax() {
        checkNumberGenerator(Byte.class, (byte) -10, null, (byte) 1);
        checkNumberGenerator(Short.class, (short) -10, null, (short) 1);
        checkNumberGenerator(Integer.class, -10, null, 1);
        checkNumberGenerator(Long.class, (long) -10, null, (long) 1);
        checkNumberGenerator(BigInteger.class, new BigInteger("-10"), null, new BigInteger("1"));
        checkNumberGenerator(Double.class, (double) -10, null, (double) 1);
        checkNumberGenerator(Float.class, (float) -10, null, (float) 1);
        checkNumberGenerator(BigDecimal.class, new BigDecimal(-10), null, new BigDecimal(1));
    }


    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T precision) {
        for (Sequence sequence : SequenceManager.registeredSequences())
        	if (!(sequence instanceof HeadSequence))
        		checkNumberGenerator(type, min, max, precision, sequence);
        if (max != null)
	        for (WeightFunction function : getDistributionFunctions(min.doubleValue(), max.doubleValue()))
	            checkNumberGenerator(type, min, max, precision, function);
    }
    
    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T precision, Sequence sequence) {
        Generator<T> generator = generatorFactory.createNumberGenerator(type, min, max, precision, sequence, Uniqueness.NONE);
        generator.init(context);
        for (int i = 0; i < 5; i++) {
            T n = generator.generate();
            assertNotNull("Generator not available: " + generator, n);
            if (min != null)
            	assertTrue("Generated value (" + n + ") is smaller than min (" + min + ") using sequence '" + sequence + "'", 
            		n.doubleValue() >= min.doubleValue());
            if (max != null)
            	assertTrue(n.doubleValue() <= max.doubleValue());
        }
    }

    private <T extends Number> void checkNumberGenerator(Class<T> type, T min, T max, T precision, WeightFunction weightFunction) {
        Generator<T> generator = generatorFactory.createNumberGenerator(type, min, max, precision, weightFunction, Uniqueness.NONE);
        generator.init(context);
        int range = (int)((max.doubleValue() - min.doubleValue() + precision.doubleValue()) / precision.doubleValue());
        int[] count = new int[range];
        for (int i = 0; i < 1000; i++) {
            T n = generator.generate();
            double d = n.doubleValue();
            assertTrue(d >= min.doubleValue());
            assertTrue(d <= max.doubleValue());
            int index = (int)((d - min.doubleValue()) / precision.doubleValue());
            count[index]++;
        }
        logger.debug(weightFunction + ": " + ArrayFormat.formatInts(", ", count));
    }

    // sample source ------------------------------------------------------------------------------------------------

    @Test
    public void testGetSampleGenerator() {
        List<Integer> samples = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++)
            samples.add(i);
        Generator<Integer> generator = generatorFactory.createSampleGenerator(samples, Integer.class, false);
        initAndUseGenerator(generator);
    }

    public void testGetEmptySampleGenerator() {
        Generator<Integer> generator = generatorFactory.createSampleGenerator(new HashSet<Integer>(), Integer.class, false);
		generator.init(context);
		assertNull(generator.generate());
    }

    // date source --------------------------------------------------------------------------------------------------

    @Test
    public void testGetDateGeneratorByDistributionType() {
        for (Sequence sequence : SequenceManager.registeredSequences())
            generatorFactory.createDateGenerator(date(2006, 0, 1), date(2006, 11, 31), Period.DAY.getMillis(), sequence);
    }

    @Test
    public void testGetDateGeneratorByDistributionFunction() {
        Date min = date(2006, 0, 1);
        Date max = date(2006, 11, 31);
        for (WeightFunction distributionFunction : getDistributionFunctions(min.getTime(), max.getTime())) {
            Generator<Date> generator = generatorFactory.createDateGenerator(min, max, Period.DAY.getMillis(), distributionFunction);
            initAndUseGenerator(generator);
        }
    }

    private Date date(int year, int nullBasedMonth, int day) {
        return new GregorianCalendar(year, nullBasedMonth, day).getTime();
    }

    // text source --------------------------------------------------------------------------------------------------

    @Test
    public void testGetCharacterGeneratorByLocale() {
        checkCharacterGeneratorOfLocale(Locale.GERMANY);
        checkCharacterGeneratorOfLocale(Locale.UK);
        checkCharacterGeneratorOfLocale(Locale.US);
        checkCharacterGeneratorOfLocale(new Locale("de", "ch"));
        checkCharacterGeneratorOfLocale(new Locale("de", "at"));
        checkCharacterGeneratorOfLocale(new Locale("fr", "ch"));
        checkCharacterGeneratorOfLocale(new Locale("it", "ch"));
        checkCharacterGeneratorOfLocale(Locale.GERMANY);
    }

    private void checkCharacterGeneratorOfLocale(Locale locale) {
        Generator<Character> generator = generatorFactory.createCharacterGenerator(null, locale, false);
        generator.init(context);
        List<Character> specialChars;
        specialChars = new ArrayList<Character>(LocaleUtil.letters(locale));
        int[] specialCount = new int[specialChars.size()];
        for (int i = 0; i < 1000; i++) {
            Character c = generator.generate();
            int index = specialChars.indexOf(c);
            if (index >= 0)
                specialCount[index]++;
        }
        for (int i = 0; i < specialCount.length; i++)
            assertTrue("Character '" + specialChars.get(i) + "' not found in products for " + locale,
                    specialCount[i] > 0);
    }

    @Test
    public void testGetCharacterGeneratorByRegex() {
        String pattern = "[A-Za-z0-1]";
        Generator<Character> generator = generatorFactory.createCharacterGenerator(pattern, Locale.GERMAN, false);
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetCharacterGeneratorBySet() {
        Set<Character> set = new CharSet('A', 'Z').getSet();
        Generator<Character> generator = generatorFactory.createCharacterGenerator(set);
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetRegexGenerator() {
//      checkRegexGenerator(null, 0, 0, true);
//      checkRegexGenerator("", 0, 0, false);
        checkRegexGeneration("[1-9]\\d{0,3}", 1, 4, false);
        checkRegexGeneration("12345678901234567890123456789012", 1, null, false);
  }

    @Test
    public void testGetUniqueRegexGenerator() {
      Generator<String> generator = generatorFactory.createRegexStringGenerator("[0-9]{3}", 3, 3, true);
      generator.init(context);
      expectUniqueGenerations(generator, 1000).withCeasedAvailability();
  }

    private void checkRegexGeneration(String pattern, int minLength, Integer maxLength, boolean nullable) {
        Generator<String> generator = generatorFactory.createRegexStringGenerator(pattern, minLength, maxLength, false);
        generator.init(context);
        RegexStringGeneratorFactory_volumeTest.checkRegexGeneration(generator, pattern, minLength, maxLength, nullable);
    }

    // weighted sample source ---------------------------------------------------------------------------------------

    @Test
    public void testGetWeightedSampleGeneratorByValues() {
        List<WeightedSample<Integer>> samples = new ArrayList<WeightedSample<Integer>>();
        int n = 10;
        for (int i = 0; i < n; i++) {
            WeightedSample<Integer> sample = new WeightedSample<Integer>(i, i * 2. / (n * (n + 1)));
            samples.add(sample);
        }
        Generator<Integer> generator = generatorFactory.createWeightedSampleGenerator(samples);
        initAndUseGenerator(generator);
    }
    
    
    
    // formatting generators -------------------------------------------------------------------------------------------

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetConvertingGenerator() {
        Generator<Double> source = new RandomDoubleGenerator(0, 9);
        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        Generator<String> generator = GeneratorFactoryUtil.createConvertingGenerator(
                source, new FormatFormatConverter(Object.class, format, false));
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetMessageGenerator() {
        List<String> salutations = Arrays.asList("Hello", "Hi");
        AttachedWeightSampleGenerator<String> salutationGenerator = new AttachedWeightSampleGenerator<String>(String.class, salutations);
        List<String> names = Arrays.asList("Alice", "Bob", "Charly");
        AttachedWeightSampleGenerator<String> nameGenerator = new AttachedWeightSampleGenerator<String>(String.class, names);
        String pattern = "{0} {1}";
        Generator<String> generator = GeneratorFactoryUtil.createMessageGenerator(pattern, 0, 12, salutationGenerator, nameGenerator);
        generator.init(context);
        for (int i = 0; i < 10; i++) {
            String message = generator.generate();
            StringTokenizer tokenizer = new StringTokenizer(message, " ");
            assertEquals(2, tokenizer.countTokens());
            assertTrue(salutations.contains(tokenizer.nextToken()));
            assertTrue(names.contains(tokenizer.nextToken()));
        }
    }

    // collection generators -------------------------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCollectionGeneratorByCardinalityDistributionType() {
        Generator<Integer> source = new RandomIntegerGenerator(0, 9);
        for (Sequence sequence : SequenceManager.registeredSequences()) {
        	if (sequence instanceof HeadSequence)
        		continue;
            Generator<List<?>> generator = GeneratorFactoryUtil.createCollectionGeneratorOfVariableSize(
                    List.class, source, 0, 5, sequence);
            initAndUseGenerator(generator);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCollectionGeneratorByCardinalityDistributionFunction() {
        Generator<Integer> source = new RandomIntegerGenerator(0, 9);
        int minSize = 0;
        int maxSize = 5;
        for (WeightFunction distributionFunction : getDistributionFunctions(minSize, maxSize)) {
            Generator<List<?>> generator = GeneratorFactoryUtil.createCollectionGeneratorOfVariableSize(
                    List.class, source, minSize, maxSize, distributionFunction);
            initAndUseGenerator(generator);
        }
    }

    // array generators ------------------------------------------------------------------------------------------------

    @Test
    public void testGetArrayGeneratorByCardinalityDistributionType() {
        Generator<Integer> source = new RandomIntegerGenerator(0, 9);
        for (Sequence sequence : SequenceManager.registeredSequences()) {
        	if (sequence instanceof HeadSequence)
        		continue;
            Generator<Integer[]> generator = GeneratorFactoryUtil.createArrayGeneratorOfVariableLength(source, Integer.class, 0, 5, sequence);
            initAndUseGenerator(generator);
        }
    }

    @Test
    public void testGetArrayGeneratorByCardinalityDistributionFunction() {
        int minLength = 0;
        int maxLength = 5;
        Generator<Integer> source = new RandomIntegerGenerator(0, 9);
        for (WeightFunction distributionFunction : getDistributionFunctions(minLength, maxLength)) {
            Generator<Integer[]> generator = GeneratorFactoryUtil.createArrayGeneratorOfVariableLength(source, Integer.class, minLength, maxLength, distributionFunction);
            initAndUseGenerator(generator);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetHeterogenousArrayGenerator() {
        List<String> salutations = Arrays.asList("Hello", "Hi");
        AttachedWeightSampleGenerator<String> salutationGenerator = new AttachedWeightSampleGenerator<String>(String.class, salutations);
        List<String> names = Arrays.asList("Alice", "Bob", "Charly");
        AttachedWeightSampleGenerator<String> nameGenerator = new AttachedWeightSampleGenerator<String>(String.class, names);
        Generator[] sources = new Generator[] { salutationGenerator, nameGenerator };
        Generator<Object[]> generator = generatorFactory.createCompositeArrayGenerator(Object.class, sources, false);
        generator.init(context);
        for (int i = 0; i < 10; i++) {
            Object[] array = generator.generate();
            assertEquals(2, array.length);
            assertTrue(salutations.contains(array[0]));
            assertTrue(names.contains(array[1]));
        }
    }

    // source generators -----------------------------------------------------------------------------------------------

    @Test
    public void testGetCSVCellGenerator() {
        Generator<String> generator = GeneratorFactoryUtil.createCSVCellGenerator("file://org/databene/csv/names-abc.csv", ',', Encodings.UTF_8);
        generator.init(context);
        assertEquals("Alice", generator.generate());
        assertEquals("Bob", generator.generate());
        assertEquals("Charly", generator.generate());
        assertNull(generator.generate());
    }

    @Test
    public void testGetArraySourceGenerator() {
        Generator<String[]> generator = GeneratorFactoryUtil.createCSVLineGenerator(
                "file://org/databene/csv/names-abc.csv", ',', true, Encodings.UTF_8);
        generator.init(context);
        assertArrayEquals(new String[] { "Alice", "Bob" }, generator.generate());
        assertArrayEquals(new String[] { "Charly"}, generator.generate());
        assertNull(generator.generate());
    }

    private void assertArrayEquals(String[] expected, String[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++)
            if (!NullSafeComparator.equals(expected[i], actual[i])) {
                fail("Expected: [" + ArrayFormat.format(expected) + "] Actual: [" + ArrayFormat.format(actual) + "]");
            }
    }

    // helpers ---------------------------------------------------------------------------------------------------------
/*
    private <T> void checkGenerator(Generator<T> generator, boolean unique, float nullQuota) {
        Set<T> products = new HashSet<T>();
        for (int i = 0; i < 10; i++) {
            T product = generator.generate();
            if (nullQuota == 0)
                assertNotNull(product);
            if (unique) {
                assertFalse(products.contains(product));
                products.add(product);
            }
        }
        checkNullQuota(generator, nullQuota);
    }

    private <T> void checkNullQuota(Generator<T> generator, double nullQuota) {
        int totalCount = 1000;
        int nullCount = 0;
        for (int i = 0; i < totalCount; i++) {
            T product = generator.generate();
            if (product == null)
                nullCount++;
            if (nullQuota == 0)
                assertNotNull(product);
            else if (nullQuota == 1)
                assertNull(product);
        }
        double measuredQuota = (double)nullCount / totalCount;
        assertEquals(nullQuota, measuredQuota, 0.05);
    }
*/
    private <T> void initAndUseGenerator(Generator<T> generator) {
    	generator.init(context);
        for (int i = 0; i < 5; i++) {
            T product = generator.generate();
        	assertNotNull("Generator unexpectedly invalid: " + generator.toString(), product);
        }
    }

    private WeightFunction[] getDistributionFunctions(double min, double max) {
        return new WeightFunction[] {
            new ConstantFunction(1. / (max - min)),
            new GaussianFunction((min + max) / 2, (max - min) / 4),
        };
    }

}
