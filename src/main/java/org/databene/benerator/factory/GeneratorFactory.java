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
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.primitive.CharacterGenerator;
import org.databene.benerator.*;
import org.databene.benerator.primitive.datetime.DateGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.commons.validator.StringLengthValidator;
import org.databene.model.data.Uniqueness;
import org.databene.regex.RegexParser;
import org.databene.script.WeightedSample;

import java.util.*;

/**
 * Provides factory methods for generators.<br/>
 * <br/>
 * Created: 23.08.2006 21:44:27
 * @since 0.1
 * @author Volker Bergmann
 */
public abstract class GeneratorFactory {
	
	protected DefaultsProvider defaultsProvider;
	
	protected GeneratorFactory() {
		this(null);
	}

	protected GeneratorFactory(DefaultsProvider defaultsProvider) {
		this.defaultsProvider = defaultsProvider;
	}

	public DefaultsProvider getDefaultsProvider() {
		return defaultsProvider;
	}

	public void setDefaultsProvider(DefaultsProvider defaultsProvider) {
		this.defaultsProvider = defaultsProvider;
	}
	
	
	public abstract<T> Generator<T> createAlternativeGenerator(Class<T> targetType, Generator<T>[] sources, 
			Uniqueness uniqueness);

    // boolean generator -----------------------------------------------------------------------------------------------

	/**
     * Creates a generator for boolean values with a trueQuota [0-1]
     *
     * @param trueQuota a value from 0 to 1, indicating the quota of true values to generate among the non-null values
     * @return a Boolean generator of the desired characteristics
     */
	public Generator<Boolean> createBooleanGenerator(Double trueQuota) {
    	SequenceGenerator<Boolean> generator = new SequenceGenerator<Boolean>(Boolean.class);
    	if (trueQuota == null || trueQuota < 1)
    		generator.addValue(false);
    	if (trueQuota == null || trueQuota > 0)
    		generator.addValue(true);
    	return generator;
    }

    // number generators -----------------------------------------------------------------------------------------------

    /**
     * Creates a generator for numbers.
     *
     * @param numberType   the number type, e.g. java.lang.Integer
     * @param min          the minimum number to generate
     * @param max          the maximum number to generate
     * @param granularity  the resolution to use in number generation.
     * @param distribution The Sequence of WeightFunction to use for generation
     * @return a Number generator of the desired characteristics
     */
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
            Class<T> numberType, T min, Boolean minInclusive, T max, Boolean maxInclusive, T granularity,
            Distribution distribution, Uniqueness uniqueness) {
        Assert.notNull(numberType, "numberType");
        if (min != null && min.equals(max))
            return WrapperFactory.asNonNullGenerator(new ConstantGenerator<T>(min));
        if (min == null)
        	min = defaultsProvider.defaultMin(numberType);
        if (granularity == null)
        	granularity = defaultsProvider.defaultGranularity(numberType);
        if (distribution == null) {
            if (Uniqueness.ORDERED == uniqueness)
            	distribution = SequenceManager.STEP_SEQUENCE;
            else
            	distribution = defaultDistribution(uniqueness);
        }
        return distribution.createNumberGenerator(numberType, min, max, granularity, uniqueness.isUnique()); 
    }

    // sample source ------------------------------------------------------------------------------------------------

	public abstract <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
            Distribution distribution, boolean unique);

    public abstract <T> Generator<T> createSampleGenerator(Collection<T> values, Class<T> generatedType, boolean unique);

    /**
     * Creates a generator that chooses from a set of samples, using an individual weight for each sample.
     *
     * @param samples A collection of sample values
     * @return a generator of the desired characteristics
     */
    public <T> Generator<T> createWeightedSampleGenerator(Collection<WeightedSample<T>> samples, Class<T> targetType) {
    	AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<T>(targetType);
        for (WeightedSample<T> sample : samples)
        	generator.addSample(sample.getValue(), sample.getWeight());
        return generator;
    }

    // date source --------------------------------------------------------------------------------------------------

    /**
     * Creates a Date generator that generates random dates.
     *
     * @param min          the earliest Date to generate
     * @param max          the latest Date to generate
     * @param granularity  the time resolution of dates in milliseconds
     * @param distribution the distribution to use
     * @return a generator of the desired characteristics
     */
    public Generator<Date> createDateGenerator(
            Date min, Date max, long granularity, Distribution distribution) {
    	if (min == null) {
    		if (max == null) {
        		min = TimeUtil.date(1970, 0, 1);
        		max = new Date();
    		} else
    			min = TimeUtil.add(max, Calendar.DATE, -365);
    	} else if (max == null)
    		max = TimeUtil.add(min, Calendar.DATE, 365);
        return new DateGenerator(min, max, granularity, distribution);
    }

    // text generators -------------------------------------------------------------------------------------------------

    /**
     * Creates a Character generator that creates characters of a Locale which match a regular expression.
     *
     * @param pattern   the regular expression that indicates the available range of values.
     *                  If null, any letters of the specified locale will be used
     * @param locale    the locale to use for '\w' evaluation
     * @param unique    flag indicating if character generation should be unique
     * @return a generator of the desired characteristics
     */
    public Generator<Character> createCharacterGenerator(String pattern, Locale locale, boolean unique) {
        Set<Character> chars = FactoryUtil.fullLocaleCharSet(pattern, locale);
    	if (unique)
	        return new SequenceGenerator<Character>(Character.class, chars);
    	else
            return new CharacterGenerator(chars);
    }

    /**
     * Creates a character generator that creates values from a collection of characters
     *
     * @param characters the set of characters to choose from
     * @return a generator of the desired characteristics
     */
    public NonNullGenerator<Character> createCharacterGenerator(Set<Character> characters) {
        return new CharacterGenerator(defaultSubSet(characters));
    }

	public Generator<String> createStringGenerator(String pattern,
			Locale locale, Integer minLength, Integer maxLength, int lengthGranularity,
			Distribution lengthDistribution, Uniqueness uniqueness) {
        if (maxLength == null)
            maxLength = defaultsProvider.defaultMaxLength();
        if (minLength == null) {
        	if (pattern != null && pattern.length() == 0)
        		minLength = 0;
        	else {
	            int defaultMinLength = defaultsProvider.defaultMinLength();
	            minLength = Math.min(maxLength, defaultMinLength);
        	}
        }
        if (pattern == null)
            pattern = "[A-Z]*";
        if (lengthDistribution != null) {
        	Set<Character> chars = RegexParser.charsOfPattern(pattern, locale);
			return createStringGenerator(
					chars, minLength, maxLength, lengthGranularity, lengthDistribution, uniqueness);
        }
        if (locale == null)
            locale = FactoryUtil.defaultLocale();
		return createRegexStringGenerator(pattern, minLength, maxLength, uniqueness); 
	}
    
	public abstract NonNullGenerator<String> createStringGenerator(Set<Character> chars,
			Integer minLength, Integer maxLength, int lengthGranularity, Distribution lengthDistribution, 
			Uniqueness uniqueness);

	public abstract NonNullGenerator<String> createCompositeStringGenerator(
			GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, Uniqueness uniqueness);

	/**
     * Creates a generator that produces Strings which match a regular expression in a locale
     *
     * @param pattern   the regular expression
     * @param minLength the minimum length of the products
     * @param maxLength the maximum length of the products
     * @return a generator of the desired characteristics
     * @throws ConfigurationError 
     */
    public NonNullGenerator<String> createRegexStringGenerator(String pattern, int minLength, Integer maxLength, 
    		Uniqueness uniqueness) throws ConfigurationError {
    	NonNullGenerator<String> generator = RegexGeneratorFactory.create(pattern, minLength, maxLength, uniqueness, this);
        StringLengthValidator validator = new StringLengthValidator(minLength, maxLength);
		return WrapperFactory.asNonNullGenerator(WrapperFactory.applyValidator(validator, generator));
    }

    // collection generators -------------------------------------------------------------------------------------------

    /**
     * Creates a generator that reads products of an array of generators and combines them in an array.
     * @param sources the source generators
     * @return a generator of the desired characteristics
     */
	public abstract <T> Generator<T[]> createCompositeArrayGenerator(
			Class<T> componentType, Generator<T>[] sources, Uniqueness uniqueness);

    // wrappers --------------------------------------------------------------------------------------------------------

    /**
     * Creates a generator that returns a single value.
     *
     * @param value the value to return
     * @return a generator that returns a constant value.
     */
    public abstract <T> Generator<T> createSingleValueGenerator(T value, boolean unique);

	public abstract Generator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota);

	public abstract <T> Generator<T> createNullGenerator(Class<T> generatedType);
	
	
	
	// default setting providers ---------------------------------------------------------------------------------------

	public Set<Character> defaultSubSet(Set<Character> characters) {
		return characters;
	}

	protected abstract boolean defaultUnique();
	
	protected abstract double defaultTrueQuota() ;

	public abstract Distribution defaultDistribution(Uniqueness uniqueness);

	protected abstract Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required);

}