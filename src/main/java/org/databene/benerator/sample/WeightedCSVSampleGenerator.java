/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.csv.CSVGeneratorUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;

import java.util.List;

/**
 * Sample Generator for values that are read from a CSV file.
 * The CSV file needs to be comma-separated and has to contain the values
 * in the first column. The second column optionally may have a weight value.
 * Example:
 * <pre>
 *   Alpha,1
 *   Bravo,4
 *   Charly,2
 * </pre>
 * <br/>
 * Created: 11.06.2006 20:49:33
 * @author Volker Bergmann
 * @see AttachedWeightSampleGenerator
 */
public class WeightedCSVSampleGenerator<E> implements Generator<E> { // TODO merge with AttachedWeight distribution

    /** The URL to read the samples from */
    private String url;
    
    private String encoding;

    /** The converter to create instances from the CSV cell strings */
    private Converter<String, E> converter;

    /** the SampleGenerator utilized for selecting among the samples */
    private AttachedWeightSampleGenerator<E> source;

    /** flag that indicates if the generator needs to be initialized */
    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public WeightedCSVSampleGenerator() {
        this((Converter) null);
    }

    @SuppressWarnings("unchecked")
    public WeightedCSVSampleGenerator(String url, String encoding) {
        this(url, encoding, new NoOpConverter());
    }

    public WeightedCSVSampleGenerator(Converter<String, E> converter) {
        this(null, SystemInfo.getFileEncoding(), converter);
    }

    public WeightedCSVSampleGenerator(String url, String encoding, Converter<String, E> converter) {
        this.source = new AttachedWeightSampleGenerator<E>();
        this.converter = converter;
        this.encoding = encoding;
        if (url != null && url.trim().length() > 0)
            setUrl(url);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public void setUrl(String url) {
        this.url = url;
        this.dirty = true;
    }

    public String getUrl() {
        return url;
    }

    // generator interface ---------------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
        return source.getGeneratedType();
    }

    public E generate() {
        if (dirty)
            validate();
        return source.generate();
    }

    public void validate() {
        if (dirty) {
            List<WeightedSample<E>> samples = CSVGeneratorUtil.parseFile(url, ',', encoding, converter);
            source.setSamples(CollectionUtil.toArray(samples));
            dirty = false;
        }
    }

    public void reset() {
        source.reset();
    }

    public void close() {
        source.close();
    }
/*
    public boolean available() {
        return source.available();
    }
*/
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ", converter=" + converter + ']';
    }
}
