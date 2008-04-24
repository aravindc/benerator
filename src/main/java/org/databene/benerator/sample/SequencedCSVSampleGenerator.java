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
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
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
public class SequencedCSVSampleGenerator<E> extends GeneratorProxy<E> {

    /** The URI to read the samples from */
    private String uri;

    /** The converter to create instances from the CSV cell strings */
    private Converter<String, E> converter;

    private static Escalator escalator = new LoggerEscalator();

    // constructors ----------------------------------------------------------------------------------------------------

    public SequencedCSVSampleGenerator() {
        this((String)null);
    }

    public SequencedCSVSampleGenerator(String uri) {
        this(uri, new NoOpConverter());
    }

    public SequencedCSVSampleGenerator(Converter<String, E> converter) {
        this(null, converter);
    }

    public SequencedCSVSampleGenerator(String uri, Converter<String, E> converter) {
        super(new SequencedSampleGenerator<E>(converter.getTargetType()));
        this.converter = converter;
        if (uri != null && uri.trim().length() > 0)
            setUri(uri);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        this.dirty = true;
    }

    @Deprecated
    public String getUrl() {
    	escalator.escalate("The 'url' property is deprecated, use 'uri' instead", getClass(), "getUrl() called");
        return getUri();
    }

    @Deprecated
    public void setUrl(String url) {
    	escalator.escalate("The 'url' property is deprecated, use 'uri' instead", getClass(), "setUrl() called");
        setUri(url);
    }
    
    /** test support method */
    void addValue(E value) {
        ((SequencedSampleGenerator<E>) source).addValue(value);
        // do not set dirty flag, otherwise this value would be cöeared
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public void validate() {
        if (dirty) {
            try {
            	super.validate();
            	if (uri == null)
            		throw new InvalidGeneratorSetupException("uri is not set");
                CSVLineIterator parser = new CSVLineIterator(uri);
                String[] tokens;
                List<E> samples = new ArrayList<E>();
                while (parser.hasNext()) {
                    tokens = parser.next();
                    if (tokens.length > 0)
                        samples.add(converter.convert(tokens[0]));
                }
                ((SequencedSampleGenerator<E>) source).setValues(samples);
                dirty = false;
            } catch (FileNotFoundException e) {
                throw new InvalidGeneratorSetupException("uri", "not found: " + uri);
            } catch (IOException e) {
                throw new IllegalGeneratorStateException(e); // file access was interrupted, no fail-over
            } catch (ConversionException e) {
                throw new InvalidGeneratorSetupException("URI content not valid", e);
            }
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ", converter=" + converter + ']';
    }
}
