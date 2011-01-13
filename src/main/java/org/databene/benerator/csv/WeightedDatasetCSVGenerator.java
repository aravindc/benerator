/*
 * (c) Copyright 2008-2010 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;
import org.databene.dataset.Dataset;

/**
 * Generates data from a csv file set that is organized as {@link Dataset}.
 * For different regions, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 21.03.2008 16:32:04
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class WeightedDatasetCSVGenerator<E> extends GeneratorProxy <E> {
    
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
        super(new AttachedWeightSampleGenerator<E>());
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
	
	
	@Override
	public synchronized void init(GeneratorContext context) {
        List<WeightedSample<E>> samples = CSVGeneratorUtil.parseDatasetFiles(datasetName, separator, nesting, 
        		filenamePattern, encoding, converter);
		((AttachedWeightSampleGenerator<E>)source).setSamples(samples);
		super.init(context);
	}
	
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + filenamePattern + ',' + nesting + ':' + datasetName + ']';
    }
    
}
