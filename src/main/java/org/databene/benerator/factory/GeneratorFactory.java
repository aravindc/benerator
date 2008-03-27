/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.primitive.regex.RegexStringGenerator;
import org.databene.benerator.primitive.BooleanGenerator;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.benerator.*;
import org.databene.benerator.primitive.datetime.DateGenerator;
import org.databene.benerator.primitive.number.AbstractDoubleGenerator;
import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.benerator.primitive.number.DoubleFromLongGenerator;
import org.databene.benerator.primitive.number.LongFromDoubleGenerator;
import org.databene.benerator.primitive.number.NumberGenerator;
import org.databene.benerator.primitive.number.adapter.IntegralNumberGenerator;
import org.databene.benerator.primitive.number.adapter.BigDecimalGenerator;
import org.databene.benerator.primitive.number.adapter.FloatingPointNumberGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.commons.converter.*;
import org.databene.commons.iterator.TextLineIterable;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.model.*;
import org.databene.model.data.Iteration;
import org.databene.model.function.Distribution;
import org.databene.document.csv.CSVLineIterable;
import org.databene.platform.csv.CSVCellIterable;
import org.databene.regex.RegexParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Provides factory methods for generators.<br/>
 * <br/>
 * Created: 23.08.2006 21:44:27
 */
public class GeneratorFactory {

    // Singleton related stuff -----------------------------------------------------------------------------------------

    /**
     * Private constructor that prevents the user from multiple instantiantion
     */
    private GeneratorFactory() {
    }

    /**
     * The static attribute for keeping the only instance of this class
     */
    private static GeneratorFactory instance;


    /**
     * The instance factory and accessor method
     *
     * @deprecated use the static methods of the class itself
     */
    public static GeneratorFactory getInstance() {
        if (instance == null)
            instance = new GeneratorFactory();
        return instance;
    }

    // boolean generator -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator for boolean values of a trueQuots [0-1] and a nullQuota [0-1]
     *
     * @param trueQuota a value from 0 to 1, indicating the quota of true values to generate among the non-null values
     * @param nullQuota a value from 0 to 1, indicating the quota of true values to generate
     * @return a Boolean generator of the desired characteristics
     */
    public static Generator<Boolean> getBooleanGenerator(double trueQuota, double nullQuota) {
        BooleanGenerator realGenerator = new BooleanGenerator(trueQuota);
        return wrapNullQuota(realGenerator, nullQuota);
    }

    // number generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator for numbers.
     *
     * @param type         the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param precision    the resolution to use in number generation.
     * @param distribution The Sequence of WeightFunction to use for generation
     * @param nullQuota    the quota of null values to generate [0..1].
     * @return a Number generator of the desired characteristics
     */
    public static <T extends Number> Generator<T> getNumberGenerator(
            Class<T> type, T min, T max, T precision,
            Distribution distribution, double nullQuota) {
        return getNumberGenerator(type, min, max, precision, distribution,
                NumberToNumberConverter.convert(1, type), NumberToNumberConverter.convert(1, type),
                nullQuota);
    }

    public static <T extends Number> Generator<T> getNumberGenerator(
            Class<T> type, T min, T max, T precision,
            Distribution distribution, T variation1, T variation2,
            double nullQuota) {
        int fractionDigits = Math.max(MathUtil.fractionDigits(min.doubleValue()), MathUtil.fractionDigits(precision.doubleValue()));
        int totalDigits = MathUtil.prefixDigits(max.doubleValue()) + fractionDigits;
        return getNumberGenerator(type, min, max, totalDigits, fractionDigits, precision, distribution, variation1, variation2, nullQuota);
    }
    
    /**
     * Creates a generator for numbers.
     *
     * @param type         the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param precision    the resolution to use in number generation.
     * @param distribution The Sequence of WeightFunction to use for generation
     * @param variation1   parameter #1 for Sequence setup which is individual to the Sequence type
     * @param variation2   parameter #2 for Sequence setup which is individual to the Sequence type
     * @param nullQuota    the quota of null values to generate [0..1].
     * @return a Number generator of the desired characteristics
     */
    public static <T extends Number> Generator<T> getNumberGenerator(
            Class<T> type, T min, T max, int totalDigits, int fractionDigits, T precision,
            Distribution distribution, T variation1, T variation2,
            double nullQuota) {
        if (type == null)
            throw new IllegalArgumentException("Number type is null");
        Generator<T> source;
        if (Integer.class.equals(type) || Long.class.equals(type) || Byte.class.equals(type) || Short.class.equals(type) || BigInteger.class.equals(type))
            source = new IntegralNumberGenerator<T>(type, min, max, precision, distribution, variation1, variation2);
        else if (Double.class.equals(type) || Float.class.equals(type))
            source = (Generator<T>) new FloatingPointNumberGenerator(
                    type, 
                    min, 
                    max(type, max, totalDigits, fractionDigits), 
                    precision(type, precision, fractionDigits), 
                    distribution, 
                    variation1, 
                    variation2);
        else if (BigDecimal.class.equals(type))
            source = (Generator<T>) new BigDecimalGenerator(
                    (BigDecimal) min, 
                    (BigDecimal) max(type, max, totalDigits, fractionDigits), 
                    (BigDecimal) precision(type, precision, fractionDigits), 
                    distribution,
                    (BigDecimal) variation1, 
                    (BigDecimal) variation2);
        else
            throw new UnsupportedOperationException("Number type not supported: " + type.getName());
        return wrapNullQuota(source, nullQuota);
    }

    private static <T> T max(Class<T> type, T max, int totalDigits, int fractionDigits) {
        if (max != null)
            return max;
        return NumberConverter.convert(Math.pow(10, totalDigits - fractionDigits), type);
    }

    private static <T> T precision(Class<T> type, T precision, int fractionDigits) {
        if (precision != null)
            return precision;
        return NumberConverter.convert(Math.pow(10, -fractionDigits), type);
    }

    // sample source ------------------------------------------------------------------------------------------------

    /**
     * Creates a generator that reads cell Strings from a CSV file and converts them into objects by a converter
     *
     * @param uri       The URI or filename to read the data from
     * @param converter the converter to use for representing the file entries
     * @return a Generator that creates instances of the parameterized type T.
     */
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
    public static <T> Generator<T> getSampleGenerator(Collection<T> values) {
        return new WeightedSampleGenerator<T>(values);
    }

    /**
     * Creates a Generator that chooses from an array of values with equal weights.
     *
     * @param values An array of values to choose from
     * @return a generator that selects from the listed sample values
     */
    public static <T> Generator<T> getSampleGenerator(T ... values) {
        return new WeightedSampleGenerator<T>(values);
    }

    /**
     * Creates a generator that chooses from a set of samples, using an individual weight for each sample.
     *
     * @param samples A collection of sample values
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T> getWeightedSampleGenerator(Collection<WeightedSample<T>> samples) {
        WeightedSampleGenerator<T> generator = new WeightedSampleGenerator<T>();
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
        WeightedSampleGenerator<T> generator = new WeightedSampleGenerator<T>();
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
     * @param distribution the Sequence or WeightFunction to use
     * @param nullQuota    the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<Date> getDateGenerator(
            Date min, Date max, long precision, Distribution distribution, double nullQuota) {
        DateGenerator generator = new DateGenerator(min, max, precision, distribution);
        return wrapNullQuota(generator, nullQuota);
    }

    /**
     * Creates a date generator that generates date entries from a CSV file.
     *
     * @param uri       the uri of the CSV file.
     * @param pattern   the pattern to use for parsing the CSV cells
     * @param nullQuota the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<Date> getDateGenerator(String uri, String encoding, String pattern, double nullQuota) {
        DateFormat format = new SimpleDateFormat(pattern);
        Converter<String, Date> converter = new ParseFormatConverter<Date>(Date.class, format);
        WeightedCSVSampleGenerator<Date> generator = new WeightedCSVSampleGenerator<Date>(uri, encoding, converter);
        return wrapNullQuota(generator, nullQuota);
    }

    // text generators -------------------------------------------------------------------------------------------------

    /**
     * Creates a generator that produces characters of a locale.
     *
     * @param locale    the locale to choose the characters from
     * @param nullQuota the quota of null values to generate
     * @return a generator of the desired characteristics
     * @deprecated use getCharacterGenerator(String pattern, Locale locale, double nullQuota)
     */
    public static Generator<Character> getCharacterGenerator(Locale locale, double nullQuota) {
        return getCharacterGenerator(null, locale, nullQuota);
    }

    /**
     * Creates a Character generator that creates characters of a locale which match a regular expresseion.
     *
     * @param pattern   the regular expression that indicates the available range of values.
     *                  If null, any letters of the specified locale will be used
     * @param locale    the locale to use for '\w' evaluation
     * @param nullQuota the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(String pattern, Locale locale, double nullQuota) {
        CharacterGenerator generator;
        Collection<Character> chars = charSet(pattern, locale);
        generator = new CharacterGenerator(chars);
        return wrapNullQuota(generator, nullQuota);
    }

    private static Collection<Character> charSet(String pattern, Locale locale) {
        Collection<Character> chars;
        if (pattern != null) {
            try {
                chars = new RegexParser(locale).parseCharSet(pattern);
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
     * Creates a character generator that creates values from a collection of charcters
     *
     * @param characters the set of characters to choose from
     * @param nullQuota  the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(Collection<Character> characters, double nullQuota) {
        return wrapNullQuota(new CharacterGenerator(characters), nullQuota);
    }

    /**
     * Creates a character generator that creates values from a set of charcters
     *
     * @param characters the set of characters to choose from
     * @param nullQuota  the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<Character> getCharacterGenerator(double nullQuota, Character ... characters) {
        return wrapNullQuota(new CharacterGenerator(Arrays.asList(characters)), nullQuota);
    }

    /**
     * Creates a generator that produces Strings which match a regular expression in a locale
     *
     * @param pattern   the regular expression
     * @param minLength the minimum length of the products
     * @param maxLength the maximum length of the products
     * @param locale    the locale to use for '\w' expressions
     * @param nullQuota the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    public static Generator<String> getRegexStringGenerator(
            String pattern, int minLength, Integer maxLength, Locale locale, double nullQuota) {
        Generator<String> generator = new RegexStringGenerator(pattern, locale, maxLength, false);
        generator = new ValidatingGeneratorProxy<String>(
                generator, new StringLengthValidator(minLength, maxLength));
        return wrapNullQuota(generator, nullQuota);
    }

    public static Generator<String> getUniqueRegexStringGenerator(
            String pattern, int minLength, Integer maxLength, Locale locale) {
        Generator<String> generator = new RegexStringGenerator(pattern, locale, maxLength, true);
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
    public static Generator<String> getMessageGenerator(
            String pattern, int minLength, int maxLength, Generator ... sources) {
        Generator<String> generator = new ConvertingGenerator<Object[], String>(
                new CompositeArrayGenerator<Object>(Object.class, sources), new MessageConverter<Object[]>(pattern, null));
        generator = new ValidatingGeneratorProxy<String>(generator, new StringLengthValidator(minLength, maxLength));
        return generator;
    }

    // collection generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param collectionType     the type of collection to create, e.g. java.util.List or java.util.TreeSet
     * @param source             the generator that provides the collection items
     * @param minLength          the minimum collection length
     * @param maxLength          the maximum collection length
     * @param lengthDistribution a Sequence or WeightFunction for the distribution of collection lengths
     * @return a generator of the desired characteristics
     */
    public static <C extends Collection<I>, I> Generator<C> getCollectionGenerator(
            Class<C> collectionType, Generator<I> source, int minLength, int maxLength,
            Distribution lengthDistribution) {
        return new CollectionGenerator<C, I>(collectionType, source, minLength, maxLength, lengthDistribution);
    }

    /**
     * Creates a generator that combines several products of a source generator to a collection.
     *
     * @param source             the generator that provides the array items
     * @param type               the type of the array
     * @param minLength          the minimum array length
     * @param maxLength          the maximum array length
     * @param lengthDistribution a Sequence or WeightFunction for the distribution of array lengths
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T[]> getArrayGenerator(
            Generator<T> source, Class<T> type, int minLength, int maxLength, Distribution lengthDistribution) {
        return new SimpleArrayGenerator<T>(source, type, minLength, maxLength, lengthDistribution);
    }

    /**
     * Creates a generator that reads products of an array of generators and combines them to an array.
     *
     * @param sources the source generators
     * @return a generator of the desired characteristics
     */
    public static <T> Generator<T[]> getArrayGenerator(Class<T> componentType, Generator<T> ... sources) {
        return new CompositeArrayGenerator<T>(componentType, sources);
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
     * @param iteration   Chooses a proxy if not null
     * @param proxyParam1 first proxy parameter
     * @param proxyParam2 2nd proxy parameter
     * @return a generator of the desired characteristics
     */
    public static Generator<String> getCSVCellGenerator(String uri, char separator, boolean cyclic,
                                                        Iteration iteration, Long proxyParam1, Long proxyParam2) {
        Generator<String> generator = new IteratingGenerator<String>(new CSVCellIterable(uri, separator));
        return createProxy(generator, cyclic, iteration, proxyParam1, proxyParam2);
    }

    /**
     * Creates a generator that creates lines from a CSV file as String arrays.
     *
     * @param uri              the uri of the CSV file
     * @param separator        the cell separator used in the CSV file
     * @param ignoreEmptyLines flag wether to leave out empty lines
     * @param cyclic           indicates wether iteration should restart from the first line after it reaches the file end.
     * @param iteration        Chooses a proxy if not null
     * @param proxyParam1      first proxy parameter
     * @param proxyParam2      2nd proxy parameter
     * @return a generator of the desired characteristics
     */
    public static Generator<String[]> getCSVLineGenerator(String uri, char separator, boolean ignoreEmptyLines,
                                                          boolean cyclic, Iteration iteration, Long proxyParam1, Long proxyParam2) {
        Generator<String[]> generator = new IteratingGenerator<String[]>(new CSVLineIterable(uri, separator, ignoreEmptyLines, SystemInfo.fileEncoding()));
        return createProxy(generator, cyclic, iteration, proxyParam1, proxyParam2);
    }

    /**
     * Creates a generator that iterates through the lines of a text file.
     *
     * @param uri         the uri of the text file
     * @param cyclic      indicates wether iteration should restart from the first line after it reaches the file end.
     * @param iteration   Chooses a proxy if not null
     * @param proxyParam1 first proxy parameter
     * @param proxyParam2 2nd proxy parameter
     * @return a generator of the desired characteristics
     */
    public static Generator<String> getTextLineGenerator(String uri, boolean cyclic,
                                                         Iteration iteration, Long proxyParam1, Long proxyParam2) {
        Generator<String> generator = new IteratingGenerator<String>(new TextLineIterable(uri));
        return createProxy(generator, cyclic, iteration, proxyParam1, proxyParam2);
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------

    /**
     * Wraps a generator and forwards its products on generate(), but inserts a quota of null values.
     *
     * @param source    the generator to use for input
     * @param nullQuota the quota of null values to generate
     * @return a generator of the desired characteristics
     */
    static <T> Generator<T> wrapNullQuota(final Generator<T> source, double nullQuota) {
        Generator<T> generator = source;
        if (nullQuota > 0)
            generator = new NullableGenerator<T>(source, (float) nullQuota);
        return generator;
    }

    static <T> Generator<T> createProxy(Generator<T> generator, boolean cyclic,
                                                Iteration iteration, Long proxyParam1, Long proxyParam2) {
        if (cyclic)
            generator = new CyclicGeneratorProxy<T>(generator);
        if (iteration == Iteration.repeat)
            generator = new RepeatGeneratorProxy<T>(generator, proxyParam1, proxyParam2);
        else if (iteration == Iteration.skip)
            generator = new SkipGeneratorProxy<T>(generator, proxyParam1, proxyParam2);
        return generator;
    }

}
