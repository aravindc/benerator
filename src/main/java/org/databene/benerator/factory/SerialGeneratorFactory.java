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
import java.util.Date;
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
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.sample.SequenceGenerator;
import org.databene.benerator.wrapper.CompositeStringGenerator;
import org.databene.benerator.wrapper.GeneratorChain;
import org.databene.benerator.wrapper.SimpleMultiSourceArrayGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.Assert;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Converter;
import org.databene.commons.NumberUtil;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterManager;
import org.databene.model.data.Uniqueness;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.WeightedSample;

/**
 * {@link GeneratorFactory} implementation which provides 
 * serial value generation and parallel combinations.<br/>
 * <br/>
 * Created: 22.07.2011 10:14:36
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class SerialGeneratorFactory extends GeneratorFactory {

	public SerialGeneratorFactory() {
		super(new MeanDefaultsProvider());
	}

	@Override
	public <T> Generator<T> createAlternativeGenerator(
			Class<T> targetType, Generator<T>[] sources, Uniqueness uniqueness) {
		return new GeneratorChain<T>(targetType, uniqueness.isUnique(), sources);
	}
	
	@Override
	public <T> Generator<T[]> createCompositeArrayGenerator(
			Class<T> componentType, Generator<T>[] sources, Uniqueness uniqueness) {
    	return new SimpleMultiSourceArrayGenerator<T>(componentType, sources);
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
	public Generator<Date> createDateGenerator(
            Date min, Date max, long granularity, Distribution distribution) {
    	if (distribution == null)
    		distribution = SequenceManager.STEP_SEQUENCE;
    	return super.createDateGenerator(min, max, granularity, distribution);
    }

	@Override
	public <T extends Number> NonNullGenerator<T> createNumberGenerator(
            Class<T> numberType, T min, Boolean minInclusive, T max, Boolean maxInclusive, 
            T granularity, Distribution distribution, Uniqueness uniqueness) {
        Assert.notNull(numberType, "numberType");
        if (distribution == null)
        	distribution = SequenceManager.STEP_SEQUENCE;
        if (min == null)
        	min = (NumberUtil.isLimited(numberType) ? NumberUtil.minValue(numberType) : defaultsProvider.defaultMin(numberType));
        if (max == null)
        	max = (NumberUtil.isLimited(numberType) ? NumberUtil.maxValue(numberType) : defaultsProvider.defaultMax(numberType));
        if (granularity == null)
        	granularity = defaultsProvider.defaultGranularity(numberType);
    	return super.createNumberGenerator(numberType, min, minInclusive, max, maxInclusive, 
    			granularity, distribution, uniqueness);
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
		GeneratorChain<String> result = new GeneratorChain<String>(String.class, true);
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
        return super.createCharacterGenerator(pattern, locale, true);
    }

    @Override
	public NonNullGenerator<Character> createCharacterGenerator(Set<Character> characters) {
        return WrapperFactory.asNonNullGenerator(
        		new SequenceGenerator<Character>(Character.class, characters));
    }

	protected Set<Integer> defaultCounts(int minCount, int maxCount, int countPrecision) {
		Set<Integer> result = new TreeSet<Integer>();
		for (int i = minCount; i <= maxCount; i += countPrecision)
			result.add(i);
		return result;
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
    	return characters;
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
		return SequenceManager.STEP_SEQUENCE;
	}

	@Override
	protected double defaultTrueQuota() {
		return 0.5;
	}

	@Override
	protected boolean defaultUnique() {
		return true;
	}

}
