/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.databene.benerator.distribution.AbstractWeightFunction;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.distribution.WeightedLongGenerator;
import org.databene.benerator.util.RandomUtil;

/**
 * Generator for {@link IndividualWeight} distributions.<br/>
 * <br/>
 * Created at 01.07.2009 11:48:23
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class IndividualWeightGenerator<E> extends AbstractSampleGenerator<E> {
	
    /** Keeps the Sample information */
    List<E> samples = new ArrayList<E>();
    
    IndividualWeight<E> distribution;

    /** Generator for choosing a List index of the sample list */
    private WeightedLongGenerator indexGenerator;

    /** Flag that indicates if the generator needs to be initialized */
    protected boolean dirty = true;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an empty sample list */
    @SuppressWarnings("unchecked")
    public IndividualWeightGenerator() {
        this((Class<E>) Object.class, null);
    }

    /** Initializes the generator to an unweighted sample list */
    public IndividualWeightGenerator(Class<E> generatedType, IndividualWeight<E> distribution, E ... values) {
    	super(generatedType);
        setValues(values);
        this.distribution = distribution;
    }

    /** Initializes the generator to an unweighted sample list */
    public IndividualWeightGenerator(Class<E> generatedType, IndividualWeight<E> distribution, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
        this.distribution = distribution;
    }

    // samples property ------------------------------------------------------------------------------------------------

    /** returns the sample list */
    public List<E> getSamples() {
        return samples;
    }

    /** Sets the sample list to the specified weighted values */
    public void setSamples(E ... samples) {
        this.samples.clear();
        for (E sample : samples)
            this.samples.add(sample);
        this.dirty = true;
    }

    /** Adds weighted values to the sample list */
    public void setSamples(Collection<E> samples) {
        this.samples.clear();
        if (samples != null)
            this.samples.addAll(samples);
        this.dirty = true;
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds an unweighted value to the sample list */
    public void addValue(E value) {
        samples.add(value);
        this.dirty = true;
    }

    public void clear() {
    	this.samples.clear();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
        return (Class<E>) samples.get(0).getClass();
    }

    /** Initializes all attributes */
    public void validate() {
        if (dirty) {
            if (samples.size() > 0) {
                indexGenerator = new WeightedLongGenerator(0, samples.size() - 1, 1, new SampleWeightFunction());
                indexGenerator.validate();
            }
            this.dirty = false;
        }
    }

    /** @see org.databene.benerator.Generator#generate() */
    public E generate() {
        if (dirty)
            validate();
        if (samples.size() == 0)
            return null;
        int index = indexGenerator.generate().intValue();
        return samples.get(index);
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

    /** Weight function that evaluates the weights that are stored in the sample list. */
    private class SampleWeightFunction extends AbstractWeightFunction {
    	
		private double totalWeight;

    	public SampleWeightFunction() {
    		totalWeight = totalWeight();
        }

        /** @see org.databene.benerator.distribution.WeightFunction#value(double) */
        public double value(double param) {
            return distribution.weight(samples.get((int) param)) / totalWeight;
        }
        
        /** Calculates the total weight of all samples */
        private double totalWeight() {
            double total = 0;
            for (E sample : samples)
                total += distribution.weight(sample);
            return total;
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