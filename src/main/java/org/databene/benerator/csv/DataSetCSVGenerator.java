/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.databene.benerator.sample.WeightedSample;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;
import org.databene.dataset.DataSet;
import org.databene.dataset.DataSetFactory;
import org.databene.document.csv.CSVLineIterator;

/**
 * Generates data from a csv file set that is organized as {@link DataSet}.
 * For different regions, different CSV versions may be provided by appending region suffixes,
 * similar to the JDK ResourceBundle handling.<br/>
 * <br/>
 * Created: 21.03.2008 16:32:04
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DataSetCSVGenerator<E> extends GeneratorProxy<E> {

    private String baseName;
    private String suffix;
    private String dataSetType;
    private String dataSetName;

    // constructors ----------------------------------------------------------------------------------------------------
    public DataSetCSVGenerator(String dataSetType, String dataSetName, String baseName, String suffix) {
        this(dataSetType, dataSetName, baseName, suffix, SystemInfo.fileEncoding());
    }

    public DataSetCSVGenerator(String dataSetType, String dataSetName, String baseName, String suffix, String encoding) {
        this(dataSetType, dataSetName, baseName, suffix, encoding, (Converter<String, E>) new NoOpConverter());
    }

    public DataSetCSVGenerator(String dataSetType, String dataSetName, String baseName, String suffix, String encoding, Converter<String, E> converter) {
        super(new WeightedSampleGenerator<E>());
        ((WeightedSampleGenerator<E>)source).setSamples(createSamples(dataSetType, dataSetName, baseName, suffix, encoding, converter));
        this.dataSetType = dataSetType;
        this.dataSetName = dataSetName;
        this.baseName = baseName;
        this.suffix = suffix;
    }

    private static <T> List<WeightedSample<T>> createSamples(
            String dataSetType, String dataSetName, String baseName, String suffix,
            String encoding, Converter<String, T> converter) {
        List<WeightedSample<T>> samples = new ArrayList<WeightedSample<T>>();
        String[] dataFilenames = DataSetFactory.getDataFiles(dataSetType, dataSetName, baseName, suffix);
        for (String dataFilename : dataFilenames)
            parse(dataFilename, encoding, converter, samples);
        return samples;
    }
    
    private static <T> void parse(String filename, String encoding, Converter<String, T> converter, List<WeightedSample<T>> samples) {
        try {
            CSVLineIterator iterator = new CSVLineIterator(filename, ',', encoding);
            while (iterator.hasNext()) {
                String[] tokens = iterator.next();
                if (tokens.length == 0)
                    continue;
                double weight = (tokens.length < 2 ? 1. : Double.parseDouble(tokens[1]));
                T value = converter.convert(tokens[0]);
                WeightedSample<T> sample = new WeightedSample<T>(value, weight);
                samples.add(sample);
            }
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

    // properties ------------------------------------------------------------------------------------------------------
/*
    public void setDataset(String dataset) {
        String[] tokens = StringUtil.tokenize(dataset, ':');
        if (tokens == null || tokens.length != 2)
            throw new IllegalArgumentException("Invalid DataSet identifier: " + dataset);
        dataSetType = tokens[0];
        dataSetName = tokens[1];
    }
*/
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + baseName + ',' + dataSetType + ':' + dataSetName + ',' + suffix + ']';
    }
}
