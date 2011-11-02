/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorProvider;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.primitive.EquivalenceStringGenerator;
import org.databene.benerator.primitive.number.NumberQuantizer;
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.sample.SequenceGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.CompositeStringGenerator;
import org.databene.benerator.wrapper.GeneratorChain;
import org.databene.benerator.wrapper.UniqueMultiSourceArrayGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Assert;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ComparableComparator;
import org.databene.commons.Converter;
import org.databene.commons.NumberUtil;
import org.databene.commons.OrderedSet;
import org.databene.commons.Period;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.commons.math.Interval;
import org.databene.model.data.Uniqueness;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.WeightedSample;
import org.databene.script.math.ArithmeticEngine;

/**
 * {@link GeneratorFactory} implementation which creates minimal data sets for 
 * <a href="http://en.wikipedia.org/wiki/Equivalence_partitioning">Equivalence Partitioning</a>
 * and <a href="http://en.wikipedia.org/wiki/Boundary_value_analysis">Boundary-value analysis</a> Tests.<br/>
 * <br/>
 * Created: 04.07.2011 09:39:38
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class EquivalenceGeneratorFactory extends GeneratorFactory {

	public EquivalenceGeneratorFactory() {
		super(new MeanDefaultsProvider());
	}

	@Override
	public <T> Generator<T> createAlternativeGenerator(
			Class<T> targetType, Generator<T>[] sources, Uniqueness uniqueness) {
		return new GeneratorChain<T>(targetType, true, sources);
	}
	
	@Override
	public <T> Generator<T[]> createCompositeArrayGenerator(
			Class<T> componentType, Generator<T>[] sources, Uniqueness uniqueness) {
    	return new UniqueMultiSourceArrayGenerator<T>(componentType, sources);
	}

	@Override
	public <T> Generator<T> createSampleGenerator(Collection<T> values, Class<T> generatedType, boolean unique) {
        return new SequenceGenerator<T>(generatedType, values);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
            Distribution distribution, boolean unique) {
	    List<WeightedSample<?>> samples = CollectionUtil.toList(DatabeneScriptParser.parseWeightedLiteralList(valueSpec));
		List<?> values = FactoryUtil.extractValues((List) samples);
	    Converter<?, T> typeConverter = new AnyConverter<T>(targetType);
	    Collection<T> convertedValues = ConverterManager.convertAll((List) values, typeConverter);
	    return createSampleGenerator(convertedValues, targetType, true);
    }

    @Override
	public <T> Generator<T> createWeightedSampleGenerator(Collection<WeightedSample<T>> samples, Class<T> targetType) {
    	List<T> values = FactoryUtil.extractValues(samples);
	    return createSampleGenerator(values, targetType, true);
    }


    @Override
	public Generator<Date> createDateGenerator(Date min, Date max, long granularity, Distribution distribution) {
    	if (min == null)
    		min = defaultsProvider.defaultMinDate();
    	if (max == null)
    		max = defaultsProvider.defaultMaxDate();
    	TreeSet<Date> values = new TreeSet<Date>();
    	values.add(min);
    	values.add(midDate(min, max, granularity));
    	values.add(max);
    	return new SequenceGenerator<Date>(Date.class, values);
    }

    Date midDate(Date min, Date max, long granularity) {
    	int segmentNo = (int) ((max.getTime() - min.getTime()) / granularity / 2);
    	long millisOffset = segmentNo * granularity;
    	Calendar medianDay = new GregorianCalendar();
    	medianDay.setTime(min);
    	long daysOffset = millisOffset / Period.DAY.getMillis();
    	long subDayOffset = millisOffset - daysOffset * Period.DAY.getMillis();
    	medianDay.add(Calendar.DAY_OF_YEAR, (int) daysOffset);
    	medianDay.add(Calendar.MILLISECOND, (int) subDayOffset);
    	return medianDay.getTime();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> NonNullGenerator<T> createNumberGenerator(
            Class<T> numberType, T min, Boolean minInclusive, T max, Boolean maxInclusive, 
            T granularity, Distribution distribution, Uniqueness uniqueness) {
        Assert.notNull(numberType, "numberType");
        if (distribution != null)
        	return super.createNumberGenerator(numberType, min, minInclusive, max, maxInclusive, 
        			granularity, distribution, uniqueness);
        if (min == null)
        	min = (NumberUtil.isLimited(numberType) ? NumberUtil.minValue(numberType) : defaultsProvider.defaultMin(numberType));
        if (max == null)
        	max = (NumberUtil.isLimited(numberType) ? NumberUtil.maxValue(numberType) : defaultsProvider.defaultMax(numberType));
        if (granularity == null)
        	granularity = defaultsProvider.defaultGranularity(numberType);
        if (((Comparable<T>) min).compareTo(max) == 0) // if min==max then return min once
            return WrapperFactory.asNonNullGenerator(new OneShotGenerator<T>(min));
        if (minInclusive == null)
        	minInclusive = true;
        if (maxInclusive == null)
        	maxInclusive = true;

        NumberToNumberConverter<Number, T> converter = new NumberToNumberConverter<Number, T>(Number.class, numberType);
        ArithmeticEngine engine = ArithmeticEngine.defaultInstance();
        ValueSet<T> values = new ValueSet<T>(min, minInclusive, max, maxInclusive, granularity, numberType);

        // values to be tested for any range, duplicated are sieved out by ValueSet
        values.addIfViable(min);
        values.addIfViable((Number) engine.add(min, granularity));
        values.addIfViable((Number) engine.subtract(max, granularity));
        values.addIfViable(max);

        // Check the environment of zero
        T zeroExact = converter.convert(0);
        T zeroApprox = converter.convert(NumberQuantizer.quantize(zeroExact, min, granularity, numberType));
        int minVsZero = ((Comparable<T>) min).compareTo(zeroApprox);
        int maxVsZero = ((Comparable<T>) max).compareTo(zeroApprox);

        if (minVsZero <= 0 && maxVsZero >= 0) {
            // 0 is contained in the number range, so add values around it
            if (((Comparable<T>) zeroApprox).compareTo(zeroExact) == 0) {
            	// 0 is contained in the value set (min + N * granularity),
            	// so add -2*granularity, -granularity, 0, granularity, 2*granularity
            	Number minusGranularity = (Number) engine.subtract(zeroExact, granularity);
    			values.addIfViable((Number) engine.multiply(minusGranularity, 2));
    			values.addIfViable(minusGranularity);
            	values.addIfViable(zeroExact);
            	values.addIfViable(granularity);
            	values.addIfViable((Number) engine.multiply(granularity, 2));
            } else {
        		values.addIfViable(zeroApprox);
            	if (((Comparable<T>) zeroApprox).compareTo(zeroExact) > 0) {
                	// the zero approximation is larger than zero
            		values.addIfViable((Number) engine.subtract(zeroApprox, granularity));
            	} else {
                	// the zero approximation is less than zero
            		values.addIfViable((Number) engine.add(zeroApprox, granularity));
            	}
            			
            }
        }
        if (minVsZero >= 0 || maxVsZero <= 0) {
        	// 0 is not contained in the range (or it is a border value), so add a value in the middle of the range
        	values.addIfViable((Number) engine.divide(engine.add(min, max), 2));
        }
		return WrapperFactory.asNonNullGenerator(new SequenceGenerator<T>(numberType, values.getAll()));
    }
    
	@Override
	public NonNullGenerator<String> createStringGenerator(Set<Character> chars,
			Integer minLength, Integer maxLength, int lengthGranularity, Distribution lengthDistribution, 
			Uniqueness uniqueness) {
		Generator<Character> charGenerator = createCharacterGenerator(chars);
		Set<Integer> counts = defaultCounts(minLength, maxLength, lengthGranularity);
		NonNullGenerator<Integer> lengthGenerator = WrapperFactory.asNonNullGenerator(
				new SequenceGenerator<Integer>(Integer.class, counts));
		return new EquivalenceStringGenerator<Character>(charGenerator, lengthGenerator);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public NonNullGenerator<String> createCompositeStringGenerator(
			GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, Uniqueness uniqueness) {
		AlternativeGenerator<String> result = new AlternativeGenerator<String>(String.class);
		Set<Integer> partCounts = defaultCounts(minParts, maxParts, 1);
		for (int partCount : partCounts) {
			Generator<String>[] sources = new Generator[partCount];
			for (int i = 0; i < partCount; i++)
				sources[i] = WrapperFactory.asStringGenerator(partGeneratorProvider.create());
			result.addSource(new CompositeStringGenerator(true, sources));
		}
		return WrapperFactory.asNonNullGenerator(result);
	}
	
    @Override
	public Generator<Character> createCharacterGenerator(String pattern, Locale locale, boolean unique) {
        Character[] chars = CollectionUtil.toArray(defaultSubSet(FactoryUtil.fullLocaleCharSet(pattern, locale)), Character.class);
        return new SequenceGenerator<Character>(Character.class, chars);
    }

    @Override
	public NonNullGenerator<Character> createCharacterGenerator(Set<Character> characters) {
        return WrapperFactory.asNonNullGenerator(
        		new SequenceGenerator<Character>(Character.class, defaultSubSet(characters)));
    }

	protected Set<Integer> defaultCounts(int minParts, int maxParts, int lengthGranularity) {
		Set<Integer> lengths = new TreeSet<Integer>();
		lengths.add(minParts); 
		lengths.add(((minParts + maxParts) / 2 - minParts) / lengthGranularity * lengthGranularity + minParts); 
		lengths.add(maxParts);
		if (maxParts > minParts) {
			lengths.add(minParts + 1);
			lengths.add(maxParts - 1);
		}
		return lengths;
	}

    @Override
	public <T> Generator<T> createSingleValueGenerator(T value, boolean unique) {
		return new OneShotGenerator<T>(value);
    }

	@Override
	public <T> Generator<T> createNullGenerator(Class<T> generatedType) {
		return new OneShotGenerator<T>(null, generatedType);
	}

    @Override
	public Set<Character> defaultSubSet(Set<Character> characters) {
    	Set<Character> uppers = new TreeSet<Character>();
    	Set<Character> lowers = new TreeSet<Character>();
    	Set<Character> digits = new TreeSet<Character>();
    	Set<Character> spaces = new TreeSet<Character>();
    	Set<Character> others = new TreeSet<Character>();
    	for (char c : characters) {
    		if (Character.isUpperCase(c))
    			uppers.add(c);
    		else if (Character.isLowerCase(c))
    			lowers.add(c);
    		else if (Character.isDigit(c))
    			digits.add(c);
    		else if (Character.isWhitespace(c))
    			spaces.add(c);
    		else
    			others.add(c);
    	}
    	Set<Character> result = new OrderedSet<Character>();
        addSelection(uppers, result);
        addSelection(lowers, result);
        addSelection(digits, result);
        for (Character c : spaces)
        	result.add(c);
        for (Character c : others)
        	result.add(c);
        return result;
    }

    protected void addSelection(Set<Character> ofChars, Set<Character> toChars) {
		if (ofChars.size() == 0)
			return;
		Character[] array = CollectionUtil.toArray(ofChars);
		toChars.add(array[0]);
		if (array.length >= 3)
			toChars.add(array[array.length/2]);
		if (array.length >= 2)
			toChars.add(ArrayUtil.lastElementOf(array));
	}

    // defaults --------------------------------------------------------------------------------------------------------
    
    @Override
	public Generator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota)  {
		if (nullable == null || nullable || (nullQuota != null && nullQuota > 0))
			return WrapperFactory.prependNull(source);
		else
			return source;
	}

	@Override
	protected Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required) {
    	return (required ? SequenceManager.STEP_SEQUENCE : null);
	}

	@Override
	public Distribution defaultDistribution(Uniqueness uniqueness) {
    	switch (uniqueness) {
        	case NONE: 	return SequenceManager.RANDOM_SEQUENCE;
        	default: 	return SequenceManager.STEP_SEQUENCE;
    	}
	}

	@Override
	protected double defaultTrueQuota() {
		return 0.5;
	}

	static class ValueSet<T extends Number> {
		
		Interval<T> numberRange;
		T granularity;
		Class<T> numberType;
		private TreeSet<T> set;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ValueSet(T min, boolean minInclusive, T max, boolean maxInclusive, T granularity, Class<T> numberType) {
			this.set = new TreeSet<T>();
	        this.numberRange = new Interval<T>(min, minInclusive, max, maxInclusive, new ComparableComparator());
	        this.granularity = granularity;
	        this.numberType = numberType;
		}

		public Collection<T> getAll() {
			return set;
		}

		public void addIfViable(Number value) {
			if (numberRange.contains(NumberToNumberConverter.convert(value, numberType))) {
				T quantizedValue = NumberQuantizer.quantize(value, numberRange.getMin(), granularity, numberType);
				set.add(quantizedValue);
			}
		}
	}

	@Override
	protected boolean defaultUnique() {
		return true;
	}

}
