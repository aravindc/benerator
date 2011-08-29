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

import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.IndividualWeight;
import org.databene.commons.math.MutableDouble;

/**
 * {@link IndividualWeightGenerator} implementation that organizes 
 * sample data in a Map of value-to-weight associations.<br/>
 * <br/>
 * Created at 12.07.2009 00:29:58
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class MappedWeightSampleGenerator<E> extends IndividualWeightGenerator<E> { 
	
	Map<E, MutableDouble> weights;
	double defaultWeight;
	
	// construction ----------------------------------------------------------------------------------------------------

	public MappedWeightSampleGenerator(Class<E> generatedType, E... values) {
	    super(generatedType, null, values);
	    init();
    }

	public MappedWeightSampleGenerator(Class<E> generatedType, Iterable<E> values) {
	    super(generatedType, null, values);
	    init();
    }
	
	private void init() {
	    this.weights = new HashMap<E, MutableDouble>();
	    super.individualWeight = new MappedWeight();
	    this.defaultWeight = 1;
    }

	// interface -------------------------------------------------------------------------------------------------------
	
	public void addSample(E sample) {
		addSample(sample, 1.);
	}

	public void addSample(E sample, double weight) {
		MutableDouble sampleWeight = weights.get(sample);
		if (sampleWeight == null) {
			sampleWeight = new MutableDouble(weight);
			weights.put(sample, sampleWeight);
		} else
			sampleWeight.value += weight;
	}
	
	public boolean containsSample(E sample) {
		return weights.containsKey(sample);
	}
	
	@Override
	public void init(GeneratorContext context) {
		for (E value : weights.keySet())
			addValue(value);
		super.init(context);
	}

	class MappedWeight extends IndividualWeight<E> {
        @Override
        public double weight(E sample) {
	        MutableDouble weight = weights.get(sample);
	        return (weight != null ? weight.value : defaultWeight);
        }
	}

	@Override
	public String toString() {
	    return getClass().getSimpleName() + weights;
	}
	
}
