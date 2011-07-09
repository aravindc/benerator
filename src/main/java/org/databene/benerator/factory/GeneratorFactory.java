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
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.benerator.*;
import org.databene.benerator.primitive.datetime.DateGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.commons.converter.*;
import org.databene.commons.iterator.TextLineIterable;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.document.csv.CSVCellIterable;
import org.databene.document.csv.CSVLineIterable;
import org.databene.model.data.Uniqueness;
import org.databene.regex.CustomCharClass;
import org.databene.regex.Factor;
import org.databene.regex.Quantifier;
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
public abstract class GeneratorFactory { 
	
    // boolean generator -----------------------------------------------------------------------------------------------

	/**
     * Creates a generator for boolean values with a trueQuota [0-1]
     *
     * @param trueQuota a value from 0 to 1, indicating the quota of true values to generate among the non-null values
     * @return a Boolean generator of the desired characteristics
     */
	public Generator<Boolean> createBooleanGenerator(double trueQuota) {
    	SequenceGenerator<Boolean> generator = new SequenceGenerator<Boolean>(Boolean.class);
    	if (trueQuota < 1)
    		generator.addValue(false);
    	if (trueQuota > 0)
    		generator.addValue(true);
    	return generator;
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
    public <T extends Number> Generator<T> createNumberGenerator(
            Class<T> numberType, T min, T max, T precision,
            Distribution distribution, boolean unique) {
        int fractionDigits = Math.max(MathUtil.fractionDigits(min.doubleValue()), MathUtil.fractionDigits(precision.doubleValue()));
        int prefixDigits = (max != null ? MathUtil.prefixDigits(max.doubleValue()) : MathUtil.prefixDigits(min.doubleValue()));
		int totalDigits = prefixDigits + fractionDigits;
        return createNumberGenerator(numberType, min, max, totalDigits, fractionDigits, precision, distribution, unique);
    }
    
    /**
     * Creates a generator for numbers.
     *
     * @param numberType   the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param granularity    the resolution to use in number generation.
     * @return a Number generator of the desired characteristics
     */
    public <T extends Number> Generator<T> createNumberGenerator(
            Class<T> numberType, T min, T max, Integer totalDigits, Integer fractionDigits, T granularity,
            Distribution distribution, boolean unique) {
        Assert.notNull(numberType, "numberType");
        if (min != null && min.equals(max))
            return new ConstantGenerator<T>(min);
        if (min == null)
        	min = defaultMin(numberType);
        if (granularity == null)
        	granularity = defaultGranularity(numberType);
        if (distribution == null)
        	distribution = defaultDistribution(unique ? Uniqueness.SIMPLE : Uniqueness.NONE);
        return distribution.createGenerator(numberType, min, max, granularity, unique); 
        // TODO v0.7 define difference between precision and fractionDigits and implement it accordingly
    }

    // sample source ------------------------------------------------------------------------------------------------

	public abstract <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
            Distribution distribution, boolean unique);

    /**
     * Creates a generator that reads cell Strings from a CSV file and converts them into objects by a converter
     *
     * @param uri       The URI or filename to read the data from
     * @param converter the converter to use for representing the file entries
     * @return a Generator that creates instances of the parameterized type T.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> Generator<T> createSampleGenerator(String uri, String encoding, Converter<String, T> converter) {
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
    public <T> Generator<T> createSampleGenerator(Collection<T> values) {
    	Class<T> generatedType = (Class<T>) Object.class;
    	if (values.size() > 0) {
    		T first = values.iterator().next();
			generatedType = (Class<T>) first.getClass();
    	}
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
    }

    public abstract <T> Generator<T> createSampleGenerator(Collection<T> values, Class<T> generatedType, boolean unique);

    /**
     * Creates a Generator that chooses from an array of values with equal weights.
     *
     * @param values An array of values to choose from
     * @return a generator that selects from the listed sample values
     */
    @SuppressWarnings("unchecked")
    public <T> Generator<T> createSampleGenerator(T ... values) {
    	Class<T> generatedType = (values.length > 0 ? (Class<T>) values[0].getClass() : (Class<T>) Object.class);
    	return createSampleGenerator(generatedType, values);
    }

    /**
     * Creates a Generator that chooses from an array of values with equal weights.
     *
     * @param values An array of values to choose from
     * @return a generator that selects from the listed sample values
     */
    public <T> Generator<T> createSampleGenerator(Class<T> generatedType, T ... values) {
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
    }

    /**
     * Creates a generator that chooses from a set of samples, using an individual weight for each sample.
     *
     * @param samples A collection of sample values
     * @return a generator of the desired characteristics
     */
    public <T> Generator<T> createWeightedSampleGenerator(Collection<WeightedSample<T>> samples) {
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
    public <T> Generator<T> createWeightedSampleGenerator(WeightedSample<T> ... samples) {
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
    public Generator<Date> createDateGenerator(
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
    public Generator<Date> createDateGenerator(String uri, String encoding, String pattern) {
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
    public Generator<Character> createCharacterGenerator(String pattern, Locale locale) {
        Collection<Character> chars = charSet(pattern, locale);
        return new CharacterGenerator(chars);
    }

    private Collection<Character> charSet(String pattern, Locale locale) {
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

    public Generator<Character> createUniqueCharacterGenerator(String pattern, Locale locale) {
        Character[] chars = CollectionUtil.toArray(charSet(pattern, locale), Character.class);
        return new SequenceGenerator<Character>(Character.class, chars);
    }

    /**
     * Creates a character generator that creates values from a collection of characters
     *
     * @param characters the set of characters to choose from
     * @return a generator of the desired characteristics
     */
    public Generator<Character> createCharacterGenerator(Collection<Character> characters) {
        return new CharacterGenerator(characters);
    }

    /**
     * Creates a character generator that creates values from a set of characters
     *
     * @param characters the set of characters to choose from
     * @return a generator of the desired characteristics
     */
    public Generator<Character> createCharacterGenerator(Character ... characters) {
        return new CharacterGenerator(Arrays.asList(characters));
    }

	public Generator<String> createStringGenerator(String pattern,
			Integer minLength, Integer maxLength, Distribution lengthDistribution,
			Locale locale, boolean unique) {
        if (maxLength == null)
            maxLength = defaultMaxLength();
        if (minLength == null) {
        	if (pattern != null && pattern.length() == 0)
        		minLength = 0;
        	else {
	            int defaultMinLength = defaultMinLength();
	            minLength = Math.min(maxLength, defaultMinLength);
        	}
        }
        if (pattern == null)
            pattern = "[A-Z]{" + minLength + ',' + maxLength + '}';
        Object regex;
        try {
			regex = new RegexParser().parseRegex(pattern);
		} catch (ParseException e) {
			throw new ConfigurationError(e);
		}
        if (lengthDistribution != null) {
        	if (!(regex instanceof Factor))
        		throw new ConfigurationError("Illegal regular expression in the context of a length distribution: " + pattern);
        	Factor factor = (Factor) regex;
        	Object atom = factor.getAtom();
        	if (!(atom instanceof CustomCharClass))
        		throw new ConfigurationError("Illegal regex atom in the context of a length distribution: " + atom);
        	Set<Character> chars = ((CustomCharClass) atom).getCharSet().getSet();
        	Quantifier quantifier = factor.getQuantifier();
        	minLength = Math.max(minLength, quantifier.getMin());
        	if (quantifier.getMax() != null)
        		maxLength = Math.min(maxLength, quantifier.getMax());
			return createStringGenerator(chars, minLength, maxLength, lengthDistribution, unique);
        }
        if (locale == null)
            locale = defaultLocale();
		return createRegexStringGenerator(pattern, minLength, maxLength, unique); 
	}
    
	public abstract Generator<String> createStringGenerator(Collection<Character> chars,
			Integer minLength, Integer maxLength, Distribution lengthDistribution, boolean unique);

	public abstract Generator<String> createCompositeStringGenerator(
			GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, boolean unique);

	/**
     * Creates a generator that produces Strings which match a regular expression in a locale
     *
     * @param pattern   the regular expression
     * @param minLength the minimum length of the products
     * @param maxLength the maximum length of the products
     * @return a generator of the desired characteristics
     * @throws ConfigurationError 
     */
    public Generator<String> createRegexStringGenerator(String pattern, int minLength, Integer maxLength, boolean unique) 
            	throws ConfigurationError {
    	Generator<String> generator = RegexGeneratorFactory.create(pattern, minLength, maxLength, unique, this);
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
    public <S, T> Generator<T> createConvertingGenerator(Generator<S> source, Converter<S, T> converter) {
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
    public Generator<String> createMessageGenerator(
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
    public <C extends Collection<I>, I> Generator<C> createCollectionGenerator(
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
    public <T> Generator<T[]> createArrayGenerator(
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
    public <T> Generator<T[]> createArrayGenerator(Class<T> componentType, Generator<T> ... sources) {
        return new SimpleCompositeArrayGenerator<T>(componentType, sources);
    }

	public abstract <T> Generator<T[]> createArrayGenerator(Class<T> componentType, NullableGenerator<T>[] sources, boolean unique);

	public abstract <T> Generator<T[]> createArrayGenerator(Class<T> componentType, Generator<T>[] sources, boolean unique);

    // wrappers --------------------------------------------------------------------------------------------------------

    /**
     * Creates a generator that returns a single value.
     *
     * @param value the value to return
     * @return a generator that returns a constant value.
     */
    public abstract <T> Generator<T> createSingleValueGenerator(T value, boolean unique);

    // source generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator that iterates through the cells of a CSV file.
     *
     * @param uri         the uri of the CSV file
     * @param separator   the cell separator used in the CSV file
     * @param cyclic      indicates wether iteration should restart from the first line after it reaches the file end.
     * @return a generator of the desired characteristics
     */
    public Generator<String> createCSVCellGenerator(String uri, char separator, boolean cyclic) {
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
    public Generator<String[]> createCSVLineGenerator(String uri, char separator, boolean ignoreEmptyLines, boolean cyclic) {
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
    public Generator<String> createTextLineGenerator(String uri, boolean cyclic) {
        Generator<String> generator = new IteratingGenerator<String>(new TextLineIterable(uri));
        return DescriptorUtil.wrapWithProxy(generator, cyclic);
    }

	public <T> Generator<T> wrapNonClosing(Generator<T> generator) {
		return new NonClosingGeneratorProxy<T>(generator);
	}

	public abstract NullableGenerator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota);
	public abstract NullableGenerator<?> applyNullSettings(NullableGenerator<?> source, Boolean nullable, Double nullQuota);

	public abstract <T> NullableGenerator<T> createNullGenerator(Class<T> generatedType);
	
//    public abstract Generator<String> createRepetitiveStringGenerator(Generator<String> partGenerator, int minReps, int maxReps, boolean unique);
	
	// default setting providers ---------------------------------------------------------------------------------------

	public abstract boolean shouldNullifyEachNullable();

	protected abstract boolean defaultNullable();
	protected abstract boolean defaultUnique();
	protected abstract double defaultNullQuota();
	
	protected abstract double defaultTrueQuota() ;

	protected abstract <T extends Number> T defaultMin(Class<T> numberType) ;
	protected abstract <T extends Number> T defaultMax(Class<T> numberType);
	protected abstract <T extends Number> T defaultGranularity(Class<T> numberType);
	protected abstract <T extends Number> int defaultTotalDigits(Class<T> numberType);
	protected abstract <T extends Number> int defaultFractionDigits(Class<T> numberType);

	public abstract Distribution defaultDistribution(Uniqueness uniqueness);

	protected abstract int defaultMinLength();
	protected abstract Integer defaultMaxLength();
	protected abstract Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required);
	
	protected abstract Locale defaultLocale();

}
