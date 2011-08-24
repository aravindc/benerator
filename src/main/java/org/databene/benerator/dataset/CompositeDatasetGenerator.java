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

package org.databene.benerator.dataset;

import org.databene.benerator.Generator;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.WeightedGeneratorGenerator;

/**
 * {@link DatasetBasedGenerator} implementation which bases on a composite dataset.<br/><br/>
 * Created: 09.03.2011 11:01:04
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class CompositeDatasetGenerator<E> extends GeneratorWrapper<Generator<E>, E> implements DatasetBasedGenerator<E> {

	private String nesting;
	private String dataset;
	
	public CompositeDatasetGenerator(String nesting, String dataset) {
		super(new WeightedGeneratorGenerator<E>());
		this.nesting = nesting;
		this.dataset = dataset;
	}
	
	// properties ------------------------------------------------------------------------------------------------------

	@Override
	public WeightedGeneratorGenerator<E> getSource() {
		return (WeightedGeneratorGenerator<E>) super.getSource();
	}
	
	public void addSubDataset(DatasetBasedGenerator<E> generator, double weight) {
		getSource().addSource(generator, weight);
	}
	
	// Generator interface implementation ------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public Class<E> getGeneratedType() {
		WeightedGeneratorGenerator<E> generatorGenerator = getSource();
		if (generatorGenerator.getSources().size() > 0)
			return (Class<E>) generatorGenerator.getSource(0).getGeneratedType();
		return (Class<E>) Object.class;
	}
	
	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		DatasetBasedGenerator<E> generator = randomAtomicGenerator();
		if (generator == null)
			return null;
		ProductWrapper<E> generation = generator.generate(getResultWrapper());
		if (generation == null)
			return null;
		return wrapper.wrap(generation.unwrap()).setTag(nesting, generator.getDataset());
	}

	// DatasetRelatedGenerator interface implementation ----------------------------------------------------------------
	
	public String getNesting() {
		return nesting;
	}
	
	public String getDataset() {
		return dataset;
	}
	
	public E generateForDataset(String dataset) {
		return getGeneratorForDataset(dataset, true).generate(getResultWrapper()).unwrap();
	}

	// helper methods --------------------------------------------------------------------------------------------------
	
	private DatasetBasedGenerator<E> randomGenerator() {
		return (DatasetBasedGenerator<E>) getSource().generate(new ProductWrapper<Generator<E>>()).unwrap();
	}

	private DatasetBasedGenerator<E> randomAtomicGenerator() {
		DatasetBasedGenerator<E> generator = this;
		while (generator instanceof CompositeDatasetGenerator) {
			generator = ((CompositeDatasetGenerator<E>) generator).randomGenerator();
		}
		return generator;
	}

	@SuppressWarnings("unchecked")
	private DatasetBasedGenerator<E> getGeneratorForDataset(String sourceDataset, boolean required) {
		if (dataset.equals(sourceDataset))
			return this;
		for (Generator<? extends E> generator : getSource().getSources()) {
			DatasetBasedGenerator<E> dbGenerator = (DatasetBasedGenerator<E>) generator;
			if (dbGenerator.getDataset().equals(sourceDataset))
				return dbGenerator;
			if (generator instanceof CompositeDatasetGenerator) {
				DatasetBasedGenerator<E> tmp = ((CompositeDatasetGenerator<E>) generator).getGeneratorForDataset(sourceDataset, false);
				if (tmp != null)
					return tmp;
			}
		}
		if (required)
			throw new IllegalArgumentException("No sub generator found for dataset '" + sourceDataset + "'");
		return null;
	}

}
