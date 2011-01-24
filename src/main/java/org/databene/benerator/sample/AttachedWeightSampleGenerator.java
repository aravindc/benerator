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

package org.databene.benerator.sample;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.AbstractWeightFunction;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.distribution.WeightedLongGenerator;
import org.databene.benerator.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * Generates values from a weighted or non-weighted set of samples.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class AttachedWeightSampleGenerator<E> extends AbstractSampleGenerator<E> { 
	// TODO v0.7 possibly this class and WeightedSample are obsolete (replaceable by MappedSampleGenerator)
	
    /** Keeps the Sample information */
    List<WeightedSample<? extends E>> samples = new ArrayList<WeightedSample<? extends E>>();
    
    private IndividualWeight<E> individualWeight;

    /** Generator for choosing a List index of the sample list */
    private WeightedLongGenerator indexGenerator = new WeightedLongGenerator(0, 0, 1, new SampleWeightFunction());

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an empty sample list */
    @SuppressWarnings("unchecked")
    public AttachedWeightSampleGenerator() {
        this((Class<E>) Object.class);
    }

    /** Initializes the generator to an empty sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType) {
        this(generatedType, (E[]) null);
    }

    /** Initializes the generator to an unweighted sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType, E ... values) {
    	super(generatedType);
        setValues(values);
    }

    /** Initializes the generator to an unweighted sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType, Distribution distribution, E ... values) {
    	super(generatedType);
        setValues(values);
        setDistribution(distribution);
    }

    /** Initializes the generator to an unweighted sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
    }

    /** Initializes the generator to an unweighted sample list */
    public AttachedWeightSampleGenerator(Class<E> generatedType, Distribution distribution, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
        setDistribution(distribution);
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Distribution getDistribution() {
        return indexGenerator.getDistribution();
    }

    @SuppressWarnings("unchecked")
    public void setDistribution(Distribution distribution) {
    	if (distribution instanceof IndividualWeight)
    		this.individualWeight = (IndividualWeight<E>) distribution;
    	else
    		indexGenerator.setDistribution(distribution);
    }

    // samples property ------------------------------------------------------------------------------------------------

    /** returns the sample list */
    public List<WeightedSample<? extends E>> getSamples() {
        return samples;
    }

    /** Sets the sample list to the specified weighted values */
    public void setSamples(WeightedSample<? extends E> ... samples) {
        this.samples.clear();
        for (WeightedSample<? extends E> sample : samples)
            this.samples.add(sample);
    }

    /** Adds weighted values to the sample list */
    public void setSamples(Collection<WeightedSample<E>> samples) {
        this.samples.clear();
        if (samples != null)
            this.samples.addAll(samples);
    }

    /** Adds weighted values to the sample list */
    public <T extends E> void addSample(T value, double weight) {
        addSample(new WeightedSample<E>(value, weight));
    }

    /** Adds a weighted value to the sample list */
    public void addSample(WeightedSample<? extends E> sample) {
        samples.add(sample);
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds an unweighted value to the sample list */
    @Override
    public <T extends E> void addValue(T value) {
        samples.add(new WeightedSample<E>(value, 1));
    }

    @Override
    public void clear() {
    	this.samples.clear();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Class<E> getGeneratedType() {
    	if (samples.size() == 0)
    		return (Class<E>) String.class;
        return (Class<E>) samples.get(0).getClass();
    }

    /** Initializes all attributes */
    @Override
    public void init(GeneratorContext context) {
        normalize();
        if (samples.size() > 0) {
	        indexGenerator.setMax((long) (samples.size() - 1));
	        indexGenerator.init(context);
        }
        super.init(context);
    }

    /** @see org.databene.benerator.Generator#generate() */
    public E generate() {
    	assertInitialized();
        if (samples.size() == 0)
            return null;
        int index = indexGenerator.generate().intValue();
        WeightedSample<? extends E> sample = samples.get(index);
        return sample.getValue();
    }

    // static interface ------------------------------------------------------------------------------------------------

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    public static <T> T generate(T ... samples) {
        return samples[RandomUtil.randomInt(0, samples.length - 1)];
    }

    /** Convenience utility method that chooses one sample out of a list with uniform random distribution */
    public static <T> T generate(List<T> samples) {
        return samples.get(RandomUtil.randomInt(0, samples.size() - 1));
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** normalizes the sample weights to a sum of 1 */
    private void normalize() {
    	if (individualWeight != null) {
            for (WeightedSample<? extends E> sample : samples)
                sample.setWeight(individualWeight.weight(sample.getValue()));
    	}
        double totalWeight = totalWeight();
        for (WeightedSample<? extends E> sample : samples) {
            if (totalWeight == 0) {
                sample.setWeight(1. / samples.size());
            } else {
                sample.setWeight(sample.getWeight() / totalWeight);
            }
        }
    }

    /** Calculates the total weight of all samples */
    private double totalWeight() {
        double total = 0;
        for (WeightedSample<? extends E> sample : samples)
            total += sample.getWeight();
        return total;
    }

    /** Weight function that evaluates the weights that are stored in the sample list. */

    class SampleWeightFunction extends AbstractWeightFunction {

        /** @see org.databene.benerator.distribution.WeightFunction#value(double) */
        public double value(double param) {
            return samples.get((int) param).getWeight();
        }

        /** creates a String representation */
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}
