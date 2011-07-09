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

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorProvider;
import org.databene.benerator.composite.UniqueArrayGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableGeneratorFactory;
import org.databene.benerator.nullable.OneShotNullableGenerator;
import org.databene.benerator.primitive.EquivalenceStringGenerator;
import org.databene.benerator.primitive.number.NumberQuantizer;
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.sample.SequenceGenerator;
import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.CompositeStringGenerator;
import org.databene.benerator.wrapper.UniqueCompositeArrayGenerator;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Assert;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ComparableComparator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.NumberUtil;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.commons.math.ArithmeticEngine;
import org.databene.commons.math.Interval;
import org.databene.model.data.Uniqueness;

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
		super();
	}

	@Override
	public <T> Generator<T[]> createArrayGenerator(Class<T> componentType, NullableGenerator<T>[] sources, boolean unique) {
    	return new UniqueArrayGenerator<T>(componentType, sources);
	}

	@Override
	public <T> Generator<T[]> createArrayGenerator(Class<T> componentType, Generator<T>[] sources, boolean unique) {
    	return new UniqueCompositeArrayGenerator<T>(componentType, sources);
	}

	@Override
	public <T> Generator<T> createSampleGenerator(Collection<T> values,
			Class<T> generatedType, boolean unique) {
        return new SequenceGenerator<T>(generatedType, CollectionUtil.toArray(values));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
            Distribution distribution, boolean unique) {
	    WeightedSample<T>[] samples = (WeightedSample<T>[]) BeneratorScriptParser.parseWeightedLiteralList(valueSpec);
    	String[] values = new String[samples.length];
    	for (int i = 0; i < samples.length; i++) {
    		T rawValue = samples[i].getValue();
    		if (rawValue == null)
    			throw new ConfigurationError("null is not supported in values='...', drop it from the list and use a nullQuota instead");
			String value = String.valueOf(rawValue);
    		values[i] = value;
    	}
	    SequenceGenerator<String> source = new SequenceGenerator<String>(String.class, values);
	    return createConvertingGenerator(source, ConverterManager.getInstance().createConverter(String.class, targetType));
    }

    @SuppressWarnings("unchecked")
	@Override
	public <T extends Number> Generator<T> createNumberGenerator(
            Class<T> numberType, T min, T max, Integer totalDigits, Integer fractionDigits, T granularity,
            Distribution distribution, boolean unique) {
        // TODO v0.7 define difference between precision and fractionDigits and implement it accordingly
        Assert.notNull(numberType, "numberType");
        if (distribution != null)
        	return super.createNumberGenerator(numberType, min, max, totalDigits, fractionDigits, granularity,
                    distribution, unique);
        if (min == null)
        	min = (NumberUtil.isLimited(numberType) ? NumberUtil.minValue(numberType) : defaultMin(numberType));
        if (max == null)
        	max = (NumberUtil.isLimited(numberType) ? NumberUtil.maxValue(numberType) : defaultMax(numberType));
        if (granularity == null)
        	granularity = defaultGranularity(numberType);
        if (((Comparable<T>) min).compareTo(max) == 0) // if min==max then return min once
            return new OneShotGenerator<T>(min);

        NumberToNumberConverter<Number, T> converter = new NumberToNumberConverter<Number, T>(Number.class, numberType);
        ArithmeticEngine engine = ArithmeticEngine.defaultInstance();
        boolean minInclusive = true; // TODO
        boolean maxInclusive = true; // TODO
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
		return new SequenceGenerator<T>(numberType, CollectionUtil.toArray(values.getAll(), numberType));
    }
    
	@Override
	public Generator<String> createStringGenerator(Collection<Character> chars,
			Integer minLength, Integer maxLength, Distribution lengthDistribution, boolean unique) {
		Generator<Character> charGenerator = createCharacterGenerator(chars);
		Set<Integer> counts = defaultCounts(minLength, maxLength);
		Generator<Integer> lengthGenerator = new SequenceGenerator<Integer>(Integer.class, counts);
		return new EquivalenceStringGenerator<Character>(charGenerator, lengthGenerator);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Generator<String> createCompositeStringGenerator(
			GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, boolean unique) {
		AlternativeGenerator<String> result = new AlternativeGenerator<String>(String.class);
		Set<Integer> partCounts = defaultCounts(minParts, maxParts);
		for (int partCount : partCounts) {
			Generator<String>[] sources = new Generator[partCount];
			for (int i = 0; i < partCount; i++)
				sources[i] = GeneratorFactoryUtil.stringGenerator(partGeneratorProvider.create());
			result.addSource(new CompositeStringGenerator(true, sources));
		}
		return result;
	}
	
    @Override
	public Generator<Character> createCharacterGenerator(Collection<Character> characters) {
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
        SequenceGenerator<Character> generator = new SequenceGenerator<Character>(Character.class);
        add(uppers, generator);
        add(lowers, generator);
        add(digits, generator);
        for (Character c : spaces)
        	generator.addValue(c);
        for (Character c : others)
        	generator.addValue(c);
        return generator;
    }

    private void add(Set<Character> chars, SequenceGenerator<Character> generator) {
		if (chars.size() == 0)
			return;
		Character[] array = CollectionUtil.toArray(chars);
		generator.addValue(array[0]);
		if (array.length >= 3)
			generator.addValue(array[array.length/2]);
		if (array.length >= 2)
			generator.addValue(ArrayUtil.lastElementOf(array));
			
	}

	protected Set<Integer> defaultCounts(int minParts, int maxParts) {
		Set<Integer> lengths = new TreeSet<Integer>();
		lengths.add(minParts); 
		lengths.add((minParts + maxParts) / 2); 
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
	public <T> NullableGenerator<T> createNullGenerator(Class<T> generatedType) {
		return new OneShotNullableGenerator<T>(null, generatedType);
	}

    // defaults --------------------------------------------------------------------------------------------------------
    
    @Override
	protected <T extends Number> T defaultMax(Class<T> numberType) {
		return NumberToNumberConverter.convert(10000, numberType);
	}

	@Override
	protected <T extends Number> T defaultMin(Class<T> numberType) {
		return NumberToNumberConverter.convert(-10000, numberType);
	}

	@Override
	protected <T extends Number> T defaultGranularity(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}

    @Override
	public NullableGenerator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota)  {
		if (nullable == null || nullable || (nullQuota != null && nullQuota > 0))
			return NullableGeneratorFactory.createNullStartingGenerator(source);
		else
			return NullableGeneratorFactory.wrap(source);
	}

    @Override
	public NullableGenerator<?> applyNullSettings(NullableGenerator<?> source, Boolean nullable, Double nullQuota)  {
		if (nullable == null || nullable || (nullQuota != null && nullQuota > 0))
			return NullableGeneratorFactory.createNullStartingGenerator(source);
		else
			return source;
	}

	@Override
	public boolean shouldNullifyEachNullable() {
		return false;
	}

	@Override
	protected int defaultMinLength() {
		return 0;
	}

	@Override
	protected Integer defaultMaxLength() {
		return 1000;
	}

	@Override
	protected Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required) {
    	return (required ? SequenceManager.STEP_SEQUENCE : null);
	}

	@Override
	protected Locale defaultLocale() {
		return Locale.getDefault();
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

	@Override
	protected <T extends Number> int defaultTotalDigits(Class<T> numberType) {
		return 10; // TODO
	}

	@Override
	protected <T extends Number> int defaultFractionDigits(Class<T> numberType) {
		return 0; // TODO
	}

	static class ValueSet<T extends Number> {
		
		Interval<T> numberRange;
		T granularity;
		Class<T> numberType;
		private HashSet<T> set;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ValueSet(T min, boolean minInclusive, T max, boolean maxInclusive, T granularity, Class<T> numberType) {
			this.set = new HashSet<T>();
	        this.numberRange = new Interval<T>(min, minInclusive, max, maxInclusive, new ComparableComparator()); // TODO handle min/MaxExclusive
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
	protected boolean defaultNullable() {
		return true;
	}

	@Override
	protected boolean defaultUnique() {
		return true;
	}

	@Override
	protected double defaultNullQuota() {
		return 0.5;
	}

}
