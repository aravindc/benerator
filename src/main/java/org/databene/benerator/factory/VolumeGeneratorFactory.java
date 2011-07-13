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
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorProvider;
import org.databene.benerator.composite.StochasticArrayGenerator;
import org.databene.benerator.composite.UniqueArrayGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.nullable.ConstantNullableGenerator;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableGeneratorFactory;
import org.databene.benerator.primitive.BooleanGenerator;
import org.databene.benerator.primitive.DistributedLengthStringGenerator;
import org.databene.benerator.primitive.UniqueStringGenerator;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.OneShotGenerator;
import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.CompositeStringGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.benerator.wrapper.UniqueCompositeArrayGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.commons.iterator.ArrayIterable;
import org.databene.model.data.Uniqueness;

/**
 * {@link GeneratorFactory} implementation that generates docile data in order to avoid functional failures 
 * and combines them randomly and repetitively for generating large data volumes. Its primary purpose is 
 * data generation for performance tests.<br/>
 * <br/>
 * Created: 04.07.2011 09:34:34
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class VolumeGeneratorFactory extends GeneratorFactory {

	public VolumeGeneratorFactory() {
		super();
	}

	@Override
	public <T> Generator<T[]> createCompositeArrayGenerator(Class<T> componentType, NullableGenerator<T>[] sources, boolean unique) {
        if (unique)
        	return new UniqueArrayGenerator<T>(componentType, sources);
        else
        	return new StochasticArrayGenerator<T>(componentType, sources);
	}

	@Override
	public <T> Generator<T[]> createCompositeArrayGenerator(Class<T> componentType, Generator<T>[] sources, boolean unique) {
        if (unique)
        	return new UniqueCompositeArrayGenerator<T>(componentType, sources);
        else
        	return new StochasticArrayGenerator<T>(componentType, NullableGeneratorFactory.wrapAll(sources));
	}

	@Override
	public <T> Generator<T> createSampleGenerator(Collection<T> values,
			Class<T> generatedType, boolean unique) {
		// TODO uniqueness
        return new AttachedWeightSampleGenerator<T>(generatedType, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Generator<T> createFromWeightedLiteralList(String valueSpec, Class<T> targetType,
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
	    		T rawValue = samples[i].getValue();
	    		if (rawValue == null)
	    			throw new ConfigurationError("null is not supported in values='...', drop it from the list and use a nullQuota instead");
				String value = String.valueOf(rawValue);
	    		values[i] = value;
	    	}
	        IteratingGenerator<String> source = new IteratingGenerator<String>(new ArrayIterable<String>(values, String.class));
	        if (distribution == null)
	        	distribution = SequenceManager.RANDOM_SEQUENCE;
	        Generator<T> gen = GeneratorFactoryUtil.createConvertingGenerator(source, ConverterManager.getInstance().createConverter(String.class, targetType));
	    	return distribution.applyTo(gen, unique);
	    }
    }
	
    @Override
	public Generator<Boolean> createBooleanGenerator(double trueQuota) {
        return new BooleanGenerator(trueQuota);
    }

    private boolean weightsUsed(WeightedSample<?>[] samples) {
	    for (WeightedSample<?> sample : samples)
	    	if (sample.getWeight() != 1)
	    		return true;
	    return false;
    }

	@Override
	public Generator<String> createStringGenerator(Set<Character> chars,
			Integer minLength, Integer maxLength, Distribution lengthDistribution, boolean unique) {
        if (unique) {
            return new UniqueStringGenerator(minLength, maxLength, chars);
        } else {
    		Generator<Character> charGenerator = createCharacterGenerator(chars);
    		if (lengthDistribution == null)
    			lengthDistribution = SequenceManager.RANDOM_SEQUENCE;
    		Generator<Integer> lengthGenerator = lengthDistribution.createGenerator(Integer.class, minLength, maxLength, 1, false);
    		return new DistributedLengthStringGenerator(charGenerator, lengthGenerator);
        }
	}

	@Override
	public NullableGenerator<?> applyNullSettings(Generator<?> source, Boolean nullable, Double nullQuota)  {
    	if (nullQuota == null) {
    		if (nullable == null)
    			nullable = defaultNullable();
    		nullQuota = (nullable ?  defaultNullQuota() : 0);
    	}
		return NullableGeneratorFactory.injectNulls(source, nullQuota);
	}

    @Override
	public NullableGenerator<?> applyNullSettings(NullableGenerator<?> source, Boolean nullable, Double nullQuota)  {
    	if (nullQuota == null) {
    		if (nullable == null)
    			nullable = defaultNullable();
    		nullQuota = (nullable ?  defaultNullQuota() : 0);
    	}
		return NullableGeneratorFactory.injectNulls(source, nullQuota);
	}

    @Override
	public <T> Generator<T> createSingleValueGenerator(T value, boolean unique) {
    	if (unique)
    		return new OneShotGenerator<T>(value);
    	else
    		return new ConstantGenerator<T>(value);
    }

    @Override
	public boolean shouldNullifyEachNullable() {
		return true;
	}
	
	@Override
	protected double defaultTrueQuota() {
		return 0.5;
	}

	@Override
	protected int defaultMinLength() {
		return 1;
	}

	@Override
	protected Integer defaultMaxLength() {
		return 30;
	}

	@Override
	protected Distribution defaultLengthDistribution(Uniqueness uniqueness, boolean required) {
    	switch (uniqueness) {
	    	case ORDERED: 	return SequenceManager.STEP_SEQUENCE;
	    	case SIMPLE: 	return SequenceManager.EXPAND_SEQUENCE;
	    	default: 		if (required)
	    						return SequenceManager.RANDOM_SEQUENCE;
	    					else
	    						return null;
		}
	}

	@Override
	protected <T extends Number> T defaultMin(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}

	@Override
	protected <T extends Number> T defaultMax(Class<T> numberType) {
		return NumberToNumberConverter.convert(9, numberType);
	}

	@Override
	protected <T extends Number> T defaultGranularity(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}

	@Override
	protected <T extends Number> int defaultTotalDigits(Class<T> numberType) {
		return 10; // TODO
	}

	@Override
	protected <T extends Number> int defaultFractionDigits(Class<T> numberType) {
		return 0; // TODO
	}

	@Override
	public Distribution defaultDistribution(Uniqueness uniqueness) {
		if (uniqueness == null)
			return SequenceManager.STEP_SEQUENCE;
    	switch (uniqueness) {
        	case ORDERED: 	return SequenceManager.STEP_SEQUENCE;
        	case SIMPLE: 	return SequenceManager.EXPAND_SEQUENCE;
        	default: 		return SequenceManager.RANDOM_SEQUENCE;
    	}
	}

	@Override
	protected boolean defaultNullable() {
		return false;
	}

	@Override
	protected boolean defaultUnique() {
		return false;
	}

	@Override
	protected double defaultNullQuota() {
		return 1.;
	}

	@Override
	public <T> NullableGenerator<T> createNullGenerator(Class<T> generatedType) {
		return new ConstantNullableGenerator<T>(null, generatedType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Generator<String> createCompositeStringGenerator(
			GeneratorProvider<?> partGeneratorProvider, int minParts, int maxParts, boolean unique) {
		AlternativeGenerator<String> result = new AlternativeGenerator<String>(String.class);
		for (int partCount = minParts; partCount <= maxParts; partCount++) {
			Generator<String>[] sources = new Generator[partCount];
			for (int i = 0; i < partCount; i++)
				sources[i] = GeneratorFactoryUtil.stringGenerator(partGeneratorProvider.create());
			result.addSource(new CompositeStringGenerator(unique, sources));
		}
		return result;
	}

}
