/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.sample.*;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.primitive.BooleanGenerator;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.benerator.*;
import org.databene.benerator.primitive.datetime.DateGenerator;
import org.databene.benerator.primitive.regex.RegexGeneratorFactory;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.commons.converter.*;
import org.databene.commons.iterator.ArrayIterable;
import org.databene.commons.iterator.TextLineIterable;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.document.csv.CSVCellIterable;
import org.databene.document.csv.CSVLineIterable;
import org.databene.regex.RegexParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Provides factory methods for generators.<br/>
 * <br/>
 * Created: 23.08.2006 21:44:27
 * @since 0.1
 * @author Volker Bergmann
 */
public class GeneratorFactory {
	
    // Singleton related stuff -----------------------------------------------------------------------------------------

    /**
     * Private constructor that prevents the user from multiple instantiation
     */
    private GeneratorFactory() {
    }

    // boolean generator -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator for boolean values with a trueQuota [0-1]
     *
     * @param trueQuota a value from 0 to 1, indicating the quota of true values to generate among the non-null values
     * @return a Boolean generator of the desired characteristics
     */
    public static Generator<Boolean> getBooleanGenerator(double trueQuota) {
        return new BooleanGenerator(trueQuota);
    }

    // number generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator for numbers.
     *
     * @param numberType         the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param precision    the resolution to use in number generation.
     * @param distribution The Sequence of WeightFunction to use for generation
     * @return a Number generator of the desired characteristics
     */
    public static <T extends Number> Generator<T> getNumberGenerator(
            Class<T> numberType, T min, T max, T precision,
            Distribution distribution, boolean unique) {
        int fractionDigits = Math.max(MathUtil.fractionDigits(min.doubleValue()), MathUtil.fractionDigits(precision.doubleValue()));
        int prefixDigits = (max != null ? MathUtil.prefixDigits(max.doubleValue()) : MathUtil.prefixDigits(min.doubleValue()));
		int totalDigits = prefixDigits + fractionDigits;
        return getNumberGenerator(numberType, min, max, totalDigits, fractionDigits, precision, distribution, unique);
    }
    
    /**
     * Creates a generator for numbers.
     *
     * @param numberType   the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param precision    the resolution to use in number generation.
     * @return a Number generator of the desired characteristics
     */
    public static <T extends Number> Generator<T> getNumberGenerator(
            Class<T> numberType, T min, T max, int totalDigits, int fractionDigits, T precision,
            Distribution distribution, boolean unique) {
        Assert.notNull(numberType, "numberType");
        return distribution.createGenerator(numberType, min, max, precision, unique); 
        // TODO v0.7 define difference between precision and fractionDigits and implement it accordingly
    }

    // sample source ------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	public static <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
            Distribution distribution, boolean unique) {
	    WeightedSample<T>[] samples = (WeightedSample<T>[]) BeneratorScriptParser.parseWeightedLiteralList(valueSpec);
	    if (distribution == null && !unique && weightsUsed(samples)) {
	    	AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<T>(targetType);
	    	for (int i = 0; i < samples.length; i++) {
	    		WeightedSample<T> sample = samples[i];
	    		if (sample.getValue() == null)
	    			throw new ConfigurationError("null is not supported in values='...', drop it from the list and use a nullQuota instead");
	    		generator.addSample(sample);
	    	}
	    	return generator;
	    } else {
	    	String[] values = new String[samples.length];
	    	for (int i = 0; i < samples.length; i++) {
	    		String value = String.valueOf(samples[i].getValue());
	    		if (value == null)
	    			throw new ConfigurationError("null is not supported in values='...', drop it from the list and use a nullQuota instead");
	    		values[i] = value;
	    	}
	        IteratingGenerator<String> source = new IteratingGenerator<String>(new ArrayIterable<String>(values, String.class));
	        if (distribution == null)
	        	distribution = SequenceManager.RANDOM_SEQUENCE;
	        Generator<T> gen = GeneratorFactory.getConvertingGenerator(source, ConverterManager.getInstance().createConverter(String.class, targetType));
	    	return distribution.applyTo(gen, unique);
	    }
    }

    private static boolean weightsUsed(WeightedSample<?>[] samples) {
	    for (WeightedSample<?> sample : samples)
	    	if (sample.getWeight() != 1)
	    		return true;
	    return false;
    }


    /**
     * Creates a generator that reads cell Strings from a CSV file and converts them into objects by a converter
     *
     * @param uri       The URI or filename to read the data from
     * @param converter the converter to use for representing the file entries
     * @return a Generator that creates instances of the parameterized type T.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Generator<T> getSampleGenerator(String uri, String encoding, Converter<String, T> converter) {
        if (converter == null)
            converter = new NoOpConverter();
        return new WeightedCSVSampleGenerator<T>(uri, encoding, converter);
    }

    /**
     * Creates a Generator that chooses from a set of values with equal weights.
     *
     * @param values A collection of values to choose from
     * @return a generator that selects from the listed sample values
     */
    @SuppressWarnings("unchecked")
    public static <T> Generator<T> getSampleGenerator(Collection<T> values) {
    	Class<T> generatedType = (Class<T>) Object.class;
    	if (values.size() > 0) {
    		T first = values.iterator().next();
			generatedType = (Class<T>) first.getClass();
    	}
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
    }

    /**
     * Creates a Generator that chooses from a set of values with equal weights.
     *
     * @param values A collection of values to choose from
     * @return a generator that selects from the listed sample values
     */
    public static <T> Generator<T> getSampleGenerator(Class<T> generatedType, Collection<T> values) {
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
    }

    /**
     * Creates a Generator that chooses from an array of values with equal weights.
     *
     * @param values An array of values to choose from
     * @return a generator that selects from the listed sample values
     */
    @SuppressWarnings("unchecked")
    public static <T> Generator<T> getSampleGenerator(T ... values) {
    	Class<T> generatedType = (values.length > 0 ? (Class<T>) values[0].getClass() : (Class<T>) Object.class);
    	return getSampleGenerator(generatedType, values);
    }

    /**
     * Creates a Generator that chooses from an array of values with equal weights.
     *
     * @param values An array of values to choose from
     * @return a generator that selects from the listed sample values
     */
    public static <T> Generator<T> getSampleGenerator(Class<T> generatedType, T ... values) {
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
    }

    /**
     * Creates a generator that chooses from a set of samples, using an individual weight for each sample.
     *
     * @param samples A collection of sample values
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T> getWeightedSampleGenerator(Collection<WeightedSample<T>> samples) {
        AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<T>();
        generator.setSamples(samples);
        return generator;
    }

    /**
     * Creates a generator that chooses from a set of samples, using an individual weight for each sample.
     *
     * @param samples A collection of sample values
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T> getWeightedSampleGenerator(WeightedSample<T> ... samples) {
        AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<T>();
        generator.setSamples(samples);
        return generator;
    }

    // date source --------------------------------------------------------------------------------------------------

    /**
     * Creates a Date generator that generates random dates.
     *
     * @param min          The earliest Date to generate
     * @param max          The latest Date to generate
     * @param precision    the time resolution of dates in milliseconds
     * @param distribution the distribution to use
     * @return a generator of the desired characteristics
     */
    public static Generator<Date> getDateGenerator(
            Date min, Date max, long precision, Distribution distribution) {
    	if (min == null) {
    		if (max == null) {
        		min = TimeUtil.date(1970, 0, 1);
        		max = new Date();
    		} else
    			min = TimeUtil.add(max, Calendar.DATE, -365);
    	} else if (max == null)
    		max = TimeUtil.add(min, Calendar.DATE, 365);
        return new DateGenerator(min, max, precision, distribution);
    }

    /**
     * Creates a date generator that generates date entries from a CSV file.
     *
     * @param uri       the uri of the CSV file.
     * @param pattern   the pattern to use for parsing the CSV cells
     * @return a generator of the desired characteristics
     */
    public static Generator<Date> getDateGenerator(String uri, String encoding, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        Converter<String, Date> converter = new ParseFormatConverter<Date>(Date.class, format, false);
        return new WeightedCSVSampleGenerator<Date>(uri, encoding, converter);
    }

    // text generators -------------------------------------------------------------------------------------------------

    /**
     * Creates a Character generator that creates characters of a Locale which match a regular expression.
     *
     * @param pattern   the regular expression that indicates the available range of values.
     *                  If null, any letters of the specified locale will be used
     * @param locale    the locale to use for '\w' evaluation
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(String pattern, Locale locale) {
        Collection<Character> chars = charSet(pattern, locale);
        return new CharacterGenerator(chars);
    }

    private static Collection<Character> charSet(String pattern, Locale locale) {
        Collection<Character> chars;
        if (pattern != null) {
            try {
                chars = RegexParser.toCharSet(new RegexParser(locale).parseSingleChar(pattern)).getSet();
            } catch (ParseException e) {
                throw new ConfigurationError("Invalid regular expression.", e);
            }
        } else
            chars = LocaleUtil.letters(locale);
        return chars;
    }

    public static Generator<Character> getUniqueCharacterGenerator(String pattern, Locale locale) {
        Character[] chars = CollectionUtil.toArray(charSet(pattern, locale), Character.class);
        return new SequenceGenerator<Character>(Character.class, chars);
    }

    /**
     * Creates a character generator that creates values from a collection of characters
     *
     * @param characters the set of characters to choose from
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(Collection<Character> characters) {
        return new CharacterGenerator(characters);
    }

    /**
     * Creates a character generator that creates values from a set of characters
     *
     * @param characters the set of characters to choose from
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(Character ... characters) {
        return new CharacterGenerator(Arrays.asList(characters));
    }

    /**
     * Creates a generator that produces Strings which match a regular expression in a locale
     *
     * @param pattern   the regular expression
     * @param minLength the minimum length of the products
     * @param maxLength the maximum length of the products
     * @return a generator of the desired characteristics
     * @throws ConfigurationError 
     */
    public static Generator<String> getRegexStringGenerator(String pattern, int minLength, Integer maxLength, boolean unique) 
            	throws ConfigurationError {
        Generator<String> generator = RegexGeneratorFactory.create(pattern, maxLength, unique);
        return new ValidatingGeneratorProxy<String>(
                generator, new StringLengthValidator(minLength, maxLength));
    }

    // formatting generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that accepts products from a source generator
     * and converts them to target products by the converter
     *
     * @param source    the source generator
     * @param converter the converter to apply to the products of the source generator
     * @return a generator of the desired characteristics
     */
    public static <S, T> Generator<T> getConvertingGenerator(Generator<S> source, Converter<S, T> converter) {
        return new ConvertingGenerator<S, T>(source, converter);
    }

    /**
     * Creates a generator that generates messages by reading the products of several source generators and
     * combining them by a Java MessageFormat.
     *
     * @param pattern   the MessageFormat pattern
     * @param minLength the minimum length of the generated value
     * @param maxLength the maximum length of the generated value
     * @param sources   the source generators of which to assemble the products
     * @return a generator of the desired characteristics
     * @see java.text.MessageFormat
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Generator<String> getMessageGenerator(
            String pattern, int minLength, int maxLength, Generator ... sources) {
        Generator<String> generator = new ConvertingGenerator<Object[], String>(
                new SimpleCompositeArrayGenerator<Object>(Object.class, sources), (Converter) new MessageConverter(pattern, null));
        generator = new ValidatingGeneratorProxy<String>(generator, new StringLengthValidator(minLength, maxLength));
        return generator;
    }

    // collection generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param collectionType     the type of collection to create, e.g. java.util.List or java.util.TreeSet
     * @param source             the generator that provides the collection items
     * @param sizeDistribution      distribution for the collection size
     * @return a generator of the desired characteristics
     */
    public static <C extends Collection<I>, I> Generator<C> getCollectionGenerator(
            Class<C> collectionType, Generator<I> source, 
            int minSize, int maxSize, Distribution sizeDistribution) {
        return new CollectionGenerator<C, I>(collectionType, source, minSize, maxSize, sizeDistribution);
    }

    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param source             the generator that provides the array items
     * @param type               the type of the array
     * @param sizeDistribution   distribution for the array length
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T[]> getArrayGenerator(
            Generator<T> source, Class<T> type, 
            int minSize, int maxSize, Distribution sizeDistribution) {
        return new SimpleArrayGenerator<T>(source, type, minSize, maxSize, sizeDistribution);
    }

    /**
     * Creates a generator that reads products of an array of generators and combines them to an array.
     *
     * @param sources the source generators
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T[]> getArrayGenerator(Class<T> componentType, Generator<T> ... sources) {
        return new SimpleCompositeArrayGenerator<T>(componentType, sources);
    }

    // wrappers --------------------------------------------------------------------------------------------------------

    /**
     * Creates a generator that returns a constant value.
     *
     * @param value the value to return
     * @return a generator that returns a constant value.
     */
    public static <T> Generator<T> getConstantGenerator(T value) {
        return new ConstantGenerator<T>(value);
    }

    // source generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator that iterates through the cells of a CSV file.
     *
     * @param uri         the uri of the CSV file
     * @param separator   the cell separator used in the CSV file
     * @param cyclic      indicates wether iteration should restart from the first line after it reaches the file end.
     * @return a generator of the desired characteristics
     */
    public static Generator<String> getCSVCellGenerator(String uri, char separator, boolean cyclic) {
        Generator<String> generator = new IteratingGenerator<String>(new CSVCellIterable(uri, separator));
        return DescriptorUtil.wrapWithProxy(generator, cyclic);
    }

    /**
     * Creates a generator that creates lines from a CSV file as String arrays.
     *
     * @param uri              the uri of the CSV file
     * @param separator        the cell separator used in the CSV file
     * @param ignoreEmptyLines flag wether to leave out empty lines
     * @param cyclic           indicates wether iteration should restart from the first line after it reaches the file end.
     * @return a generator of the desired characteristics
     */
    public static Generator<String[]> getCSVLineGenerator(String uri, char separator, boolean ignoreEmptyLines, boolean cyclic) {
        Generator<String[]> generator = new IteratingGenerator<String[]>(new CSVLineIterable(uri, separator, ignoreEmptyLines, SystemInfo.getFileEncoding()));
        return DescriptorUtil.wrapWithProxy(generator, cyclic);
    }

    /**
     * Creates a generator that iterates through the lines of a text file.
     *
     * @param uri         the uri of the text file
     * @param cyclic      indicates whether iteration should restart from the first line after it reaches the file end.
     * @return a generator of the desired characteristics
     */
    public static Generator<String> getTextLineGenerator(String uri, boolean cyclic) {
        Generator<String> generator = new IteratingGenerator<String>(new TextLineIterable(uri));
        return DescriptorUtil.wrapWithProxy(generator, cyclic);
    }

	public static <T> Generator<T> wrapNonClosing(Generator<T> generator) {
		return new NonClosingGeneratorProxy<T>(generator);
	}
    
}
