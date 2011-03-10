/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.csv;

import java.util.List;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.dataset.AtomicDatasetGenerator;
import org.databene.benerator.dataset.CompositeDatasetGenerator;
import org.databene.benerator.dataset.Dataset;
import org.databene.benerator.dataset.DatasetBasedGenerator;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.benerator.dataset.ProductFromDataset;
import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.Assert;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.IOUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;

/**
 * Generates data from a csv file set that is organized as {@link Dataset}.
 * For different regions, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 21.03.2008 16:32:04
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class WeightedDatasetCSVGenerator<E> extends GeneratorProxy<E> implements DatasetBasedGenerator<E> {
    
    protected String filenamePattern;
    protected String datasetName;
    protected String nesting;
    protected String encoding;
    protected char separator;
    protected Converter<String, E> converter;
    
    
    
    // constructors ----------------------------------------------------------------------------------------------------
    
    public WeightedDatasetCSVGenerator(String filenamePattern, String datasetName, String nesting) {
        this(filenamePattern, ',', datasetName, nesting, SystemInfo.getFileEncoding());
    }

    @SuppressWarnings({ "unchecked", "cast", "rawtypes" })
    public WeightedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting, String encoding) {
        this(filenamePattern, separator, datasetName, nesting, encoding, (Converter<String, E>) new NoOpConverter());
    }

    @SuppressWarnings({ "cast", "unchecked", "rawtypes" })
    public WeightedDatasetCSVGenerator(String filenamePattern, String datasetName, String nesting, String encoding) {
        this(filenamePattern, ',', datasetName, nesting, encoding, (Converter<String, E>) new NoOpConverter());
    }

    public WeightedDatasetCSVGenerator(String filenamePattern, char separator, String datasetName, String nesting, 
    		String encoding, Converter<String, E> converter) {
        super(new CompositeDatasetGenerator<E>(nesting, datasetName));
        this.filenamePattern = filenamePattern;
        this.separator = separator;
        this.datasetName = datasetName;
        this.nesting = nesting;
        this.encoding = encoding;
        this.converter = converter;
    }
    
    
    
    // properties ------------------------------------------------------------------------------------------------------
    
	public void setFilenamePattern(String filenamePattern) {
		this.filenamePattern = filenamePattern;
	}
	
	public String getFilenamePattern() {
		return filenamePattern;
	}
	
	public String getDataset() {
		return datasetName;
	}
	
	public void setDataset(String datasetName) {
		this.datasetName = datasetName;
	}
	
	public String getNesting() {
		return nesting;
	}
	
	public void setNesting(String nesting) {
		this.nesting = nesting;
	}
	
	public ProductFromDataset<E> generateWithDatasetInfo() {
		return getSource().generateWithDatasetInfo();
	}
    
	public E generateForDataset(String requestedDataset) {
		DatasetBasedGenerator<E> sourceGen = getSource();
		if (sourceGen instanceof CompositeDatasetGenerator)
			return ((CompositeDatasetGenerator<E>) sourceGen).generateForDataset(requestedDataset);
		else {
			Assert.equals(requestedDataset, sourceGen.getDataset(), 
					"Wrong dataset, expected " + requestedDataset + ", found " + sourceGen.getDataset());
			return sourceGen.generate();
		}
	}
    

	
	@Override
	public DatasetBasedGenerator<E> getSource() {
		return (DatasetBasedGenerator<E>) super.getSource();
	}
	
	// Generator interface implementation ------------------------------------------------------------------------------
	
	@Override
	public synchronized void init(GeneratorContext context) {
		Dataset dataset = DatasetUtil.getDataset(nesting, datasetName);
		setSource(createDatasetGenerator(dataset, true));
		super.init(context);
	}
	
    private DatasetBasedGenerator<E> createDatasetGenerator(Dataset dataset, boolean required) {
    	if (dataset.isAtomic())
    		return createAtomicDatasetGenerator(dataset, required);
    	else 
    		return createCompositeDatasetGenerator(dataset, required);
	}

	private CompositeDatasetGenerator<E> createCompositeDatasetGenerator(Dataset dataset, boolean required) {
		CompositeDatasetGenerator<E> generator = new CompositeDatasetGenerator<E>(nesting, dataset.getName());
		for (Dataset subSet : dataset.getSubSets()) {
			DatasetBasedGenerator<E> subGenerator = createDatasetGenerator(subSet, false);
			if (subGenerator != null)
				generator.addSubDataset(subGenerator, 1.); // TODO support individual weights
		}
		if (generator.getSource().getSources().length > 0)
			return generator;
		if (required)
			throw new ConfigurationError("No samples defined for composite dataset: " + dataset.getName());
		else
			return null;
	}

	private AtomicDatasetGenerator<E> createAtomicDatasetGenerator(Dataset dataset, boolean required) {
		String filename = DatasetUtil.filenameOfDataset(dataset.getName(), filenamePattern);
		if (IOUtil.isURIAvailable(filename)) {
			List<WeightedSample<E>> samples = CSVGeneratorUtil.parseFile(filename, separator, encoding, converter);
			AttachedWeightSampleGenerator<E> generator = new AttachedWeightSampleGenerator<E>();
			generator.setSamples(samples);
			if (samples.size() > 0)
				return new AtomicDatasetGenerator<E>(generator, filename, dataset.getName());
		}
		if (required)
			throw new ConfigurationError("File not found: " + filename);
		else
			return null;
	}

	
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + filenamePattern + ',' + nesting + ':' + datasetName + ']';
    }

}
