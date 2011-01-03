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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.csv.CSVGeneratorUtil;
import org.databene.benerator.wrapper.GeneratorProxy;
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
 * @since 0.1
 * @author Volker Bergmann
 * @see AttachedWeightSampleGenerator
 */
public class WeightedCSVSampleGenerator<E> extends GeneratorProxy<E> {

    /** The URI to read the samples from */
    protected String uri;
    
    private String encoding;

    /** The converter to create instances from the CSV cell strings */
    private Converter<String, E> converter;

    // constructors ----------------------------------------------------------------------------------------------------

    public WeightedCSVSampleGenerator(String url) {
        this(url, SystemInfo.getFileEncoding());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public WeightedCSVSampleGenerator(String url, String encoding) {
        this(url, encoding, new NoOpConverter());
    }

    public WeightedCSVSampleGenerator(String uri, String encoding, Converter<String, E> converter) {
        this.source = new AttachedWeightSampleGenerator<E>();
        this.converter = converter;
        this.encoding = encoding;
        if (uri != null && uri.trim().length() > 0)
            this.uri = uri;
    }

    // generator interface ---------------------------------------------------------------------------------------------

    @Override
    public E generate() {
        assertInitialized();
        return source.generate();
    }

    @Override
    public void init(GeneratorContext context) {
        List<WeightedSample<E>> samples = CSVGeneratorUtil.parseFile(uri, ',', encoding, converter);
        ((AttachedWeightSampleGenerator<E>) source).setSamples(CollectionUtil.toArray(samples));
    	super.init(context);
    }

}
