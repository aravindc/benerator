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
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ConfigurationError;

/**
 * Abstract implementation of the {@link DatasetBasedGenerator} interface.
 * It is configured with 'nesting' and 'dataset'. Depending on the type of 
 * the dataset (atomic or composite), it initializes a delegate instance 
 * of a {@link DatasetBasedGenerator}, either a {@link CompositeDatasetGenerator}
 * or an {@link AtomicDatasetGenerator}. For the dfinition of custom 
 * {@link DatasetBasedGenerator}s, inherit from this class and implement 
 * the abstract method {@link #createAtomicDatasetGenerator(Dataset, boolean)}.
 * All dataset recognition and handling and data generation will be handled 
 * automatically.<br/><br/>
 * Created: 10.03.2011 10:44:58
 * @since 0.6.6
 * @author Volker Bergmann
 */
public abstract class AbstractDatasetGenerator<E> extends GeneratorProxy<E> implements DatasetBasedGenerator<E> {
    
    protected String nesting;
    protected String datasetName;
    
    // constructor -----------------------------------------------------------------------------------------------------
    
    public AbstractDatasetGenerator(String nesting, String datasetName) {
        super(new CompositeDatasetGenerator<E>(nesting, datasetName)); // TODO v0.7 support atomic dataset here, too
        this.nesting = nesting;
        this.datasetName = datasetName;
    }
    
	public void setNesting(String nesting) {
		this.nesting = nesting;
	}
	
	public void setDataset(String datasetName) {
		this.datasetName = datasetName;
	}
	
    // DatasetBasedGenerator interface implementation ------------------------------------------------------------------
    
	public String getNesting() {
		return nesting;
	}
	
	public String getDataset() {
		return datasetName;
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
		setSource(createDatasetGenerator(dataset, true));
		super.init(context);
	}
	
	public E generateForDataset(String requestedDataset) {
		DatasetBasedGenerator<E> sourceGen = getSource();
		if (sourceGen instanceof CompositeDatasetGenerator)
			return ((CompositeDatasetGenerator<E>) sourceGen).generateForDataset(requestedDataset);
		else { // assume that either the dataset matches or an appropriate failover has been chosen
			ProductWrapper<E> wrapper = sourceGen.generate(getResultWrapper());
			return (wrapper != null ? wrapper.unwrap() : null);
		}
	}
    
	public String randomDataset() {
		if (getSource() instanceof CompositeDatasetGenerator) {
			Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
			return RandomUtil.randomElement(dataset.getSubSets()).getName();
		} else
			return datasetName;
	}
	

	// helper methods --------------------------------------------------------------------------------------------------
	
    protected DatasetBasedGenerator<E> createDatasetGenerator(Dataset dataset, boolean required) {
    	if (dataset.isAtomic())
    		return createAtomicDatasetGenerator(dataset, required);
    	else 
    		return createCompositeDatasetGenerator(dataset, required);
	}

    protected CompositeDatasetGenerator<E> createCompositeDatasetGenerator(Dataset dataset, boolean required) {
		CompositeDatasetGenerator<E> generator = new CompositeDatasetGenerator<E>(nesting, dataset.getName());
		for (Dataset subSet : dataset.getSubSets()) {
			DatasetBasedGenerator<E> subGenerator = createDatasetGenerator(subSet, false);
			if (subGenerator != null)
				generator.addSubDataset(subGenerator, 1.); // TODO v0.7 support individual weights
		}
		if (generator.getSource().getSources().size() > 0)
			return generator;
		if (required)
			throw new ConfigurationError("No samples defined for composite dataset: " + dataset.getName());
		else
			return null;
	}

	protected AtomicDatasetGenerator<E> createAtomicDatasetGenerator(Dataset dataset, boolean required) {
		Generator<E> generator = createGeneratorForAtomicDataset(dataset);
		if (generator != null)
			return new AtomicDatasetGenerator<E>(generator, nesting, dataset.getName());
		if (required)
			throw new InvalidGeneratorSetupException("Unable to create generator for atomic dataset: " + dataset.getName());
		else
			return null;
	}

	protected abstract Generator<E> createGeneratorForAtomicDataset(Dataset dataset);

	@Override
	public DatasetBasedGenerator<E> getSource() {
		return (DatasetBasedGenerator<E>) super.getSource();
	}
	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + nesting + ':' + datasetName + ']';
    }

}
