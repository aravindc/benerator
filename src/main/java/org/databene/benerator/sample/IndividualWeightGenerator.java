/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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
import java.util.List;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.AbstractWeightFunction;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.distribution.WeightedLongGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Generator for {@link IndividualWeight} distributions.<br/>
 * <br/>
 * Created at 01.07.2009 11:48:23
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class IndividualWeightGenerator<E> extends AbstractSampleGenerator<E> { // TODO v0.7 test
	
    /** Keeps the Sample information */
    List<E> samples = new ArrayList<E>();
    
    IndividualWeight<E> individualWeight;
    
	double totalWeight;

    /** Generator for choosing a List index of the sample list */
    private WeightedLongGenerator indexGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to an unweighted sample list */
    public IndividualWeightGenerator(Class<E> generatedType, IndividualWeight<E> individualWeight, E ... values) {
    	super(generatedType);
        setValues(values);
        this.individualWeight = individualWeight;
    }

    /** Initializes the generator to an unweighted sample list */
    public IndividualWeightGenerator(Class<E> generatedType, IndividualWeight<E> distribution, Iterable<E> values) {
    	super(generatedType);
        setValues(values);
        this.individualWeight = distribution;
    }

    // samples property ------------------------------------------------------------------------------------------------

    /** Sets the sample list to the specified weighted values */
    public void setSamples(E ... samples) {
        this.samples.clear();
        for (E sample : samples)
            this.samples.add(sample);
    }

    // values property -------------------------------------------------------------------------------------------------

    /** Adds an unweighted value to the sample list */
    @Override
    public <T extends E> void addValue(T value) {
        samples.add(value);
    }

    /** Calculates the total weight of all samples */
    public double totalWeight() {
        double total = 0;
        for (E sample : samples)
            total += individualWeight.weight(sample);
        return total;
    }

    @Override
    public void clear() {
    	this.samples.clear();
    }
    
    // Generator implementation ----------------------------------------------------------------------------------------

    /** Initializes all attributes */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
		totalWeight = totalWeight();
        indexGenerator = new WeightedLongGenerator(0, samples.size() - 1, 1, new SampleWeightFunction());
        indexGenerator.init(context);
        super.init(context);
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        assertInitialized();
        if (samples.size() == 0)
            return null;
        int index = GeneratorUtil.generateNonNull(indexGenerator).intValue();
        return wrapper.wrap(samples.get(index));
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** Weight function that evaluates the weights that are stored in the sample list. */
    class SampleWeightFunction extends AbstractWeightFunction {
    	
        /** @see org.databene.benerator.distribution.WeightFunction#value(double) */
        public double value(double param) {
            return individualWeight.weight(samples.get((int) param));
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