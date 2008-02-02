/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.Generator;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.converter.NoOpConverter;
import org.databene.document.csv.CSVLineIterator;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

/**
 * Sample Generator for values that are read from a CSV file.
 * The CSV file needs to be comma-separated and has to contain the values
 * in the first column. The remaining columns are ignored.
 * Example:
 * <pre>
 *   Alpha,sdlkvn,piac
 *   Bravo,lsdknac
 *   Charly,fuv
 * </pre>
 *
 * @see org.databene.benerator.sample.WeightedSampleGenerator<br/>
 * <br/>
 * Created: 26.07.2007 18:10:33
 */
public class SequencedCSVSampleGenerator<E> implements Generator<E> {

    /** The URL to read the samples from */
    private String url;

    /** The converter to create instances from the CSV cell strings */
    private Converter<String, E> converter;

    /** the SampleGenerator utilized for selecting among the samples */
    private SequencedSampleGenerator<E> source;

    /** flag that indicates if the generator needs to be initialized */
    private boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    public SequencedCSVSampleGenerator() {
        this((String)null);
    }

    public SequencedCSVSampleGenerator(String url) {
        this(url, new NoOpConverter());
    }

    public SequencedCSVSampleGenerator(Converter<String, E> converter) {
        this(null, converter);
    }

    public SequencedCSVSampleGenerator(String url, Converter<String, E> converter) {
        this.source = new SequencedSampleGenerator<E>(converter.getTargetType());
        this.converter = converter;
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

    // Generator interface ---------------------------------------------------------------------------------------------

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
            try {
                CSVLineIterator parser = new CSVLineIterator(url);
                String[] tokens;
                List<E> samples = new ArrayList<E>();
                while ((tokens = parser.next()) != null && tokens.length > 0)
                    samples.add(converter.convert(tokens[0]));
                source.setValues(samples);
                dirty = false;
            } catch (FileNotFoundException e) {
                throw new InvalidGeneratorSetupException("url", "not found: " + url);
            } catch (IOException e) {
                throw new IllegalGeneratorStateException(e); // file access was interrupted, no fail-over
            } catch (ConversionException e) {
                throw new InvalidGeneratorSetupException("URL content not valid", e);
            }
        }
    }

    public void reset() {
        source.reset();
    }

    public void close() {
        source.close();
    }

    public boolean available() {
        return source.available();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ", converter=" + converter + ']';
    }
}
