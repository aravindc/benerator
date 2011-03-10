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

package org.databene.benerator.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;

/**
 * TODO Document class.<br/><br/>
 * Created: 09.03.2011 07:59:04
 * @since TODO version
 * @author Volker Bergmann
 */
public class WeightedGeneratorGenerator<E> extends MultiGeneratorWrapper<E, Generator<E>> {
	
	private List<Double> weights;
	private AttachedWeightSampleGenerator<Integer> indexGenerator;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WeightedGeneratorGenerator() {
		super((Class) Generator.class);
		this.weights = new ArrayList<Double>();
	}
	
	@Override
	public synchronized void addSource(Generator<E> source) {
		addSource(source, 1.);
	}
	
	public synchronized void addSource(Generator<E> source, Double weight) {
		if (weight == null)
			weight = 1.;
		this.weights.add(weight);
		super.addSource(source);
	}
	
	private void createAndInitIndexGenerator() {
		indexGenerator = new AttachedWeightSampleGenerator<Integer>();
		for (int i = 0; i < weights.size(); i++)
			indexGenerator.addSample(i, weights.get(i));
		indexGenerator.init(context);
	}

	@Override
	public synchronized void init(GeneratorContext context) {
		super.init(context);
		createAndInitIndexGenerator();
	}
	
	@SuppressWarnings("unchecked")
	public Generator<E> generate() {
    	assertInitialized();
    	if (availableSources.size() == 0)
    		return null;
    	int sourceIndex = indexGenerator.generate();
		return (Generator<E>) availableSources.get(sourceIndex);
	}

	@Override
	public synchronized void reset() {
		super.reset();
		createAndInitIndexGenerator();
	}
	
	@Override
	public synchronized void close() {
		super.close();
		indexGenerator.close();
	}

}
