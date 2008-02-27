/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import org.databene.platform.array.Array2EntityConverter;
import org.databene.model.data.DefaultEntityDescriptor;
import org.databene.model.data.EntityIterable;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.document.csv.CSVLineIterable;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.iterator.ConvertingIterator;

import java.util.Iterator;

/**
 * Imports Entites from a CSV file.<br/>
 * <br/>
 * Created: 26.08.2007 12:16:08
 * @author Volker Bergmann
 */
public class CSVEntityIterable implements EntityIterable {

    private String uri;
    private char   separator;
    private String encoding;
    private Converter<String, String> preprocessor;

    private Iterable<String[]> source;
    private EntityDescriptor entityDescriptor;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityIterable() {
        this(null, null);
    }

    public CSVEntityIterable(String uri, String entityName) {
        this(uri, entityName, ',', SystemInfo.fileEncoding());
    }

    public CSVEntityIterable(String uri, String entityName, char separator) {
        this(uri, entityName, separator, SystemInfo.fileEncoding());
    }

    public CSVEntityIterable(String uri, String entityName, char separator, String encoding) {
        this(uri, new DefaultEntityDescriptor(entityName, false), new NoOpConverter<String>(), separator, encoding); // TODO v0.5 finalize capitalization concept
    }

    public CSVEntityIterable(String uri, String entityName, Converter<String, String> preprocessor, char separator, String encoding) {
        this(uri, new DefaultEntityDescriptor(entityName, false), preprocessor, separator, encoding); // TODO v0.5 finalize capitalization concept
    }

    public CSVEntityIterable(String uri, EntityDescriptor descriptor, Converter<String, String> preprocessor, char separator, String encoding) {
        this.uri = uri;
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        this.preprocessor = preprocessor;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEntityName() {
        return entityDescriptor.getName();
    }

    public void setEntityName(String entityName) {
        this.entityDescriptor = new DefaultEntityDescriptor(entityName, false); // TODO v0.5 finalize capitaliyation concept
    }

    // EntityIterable interface ----------------------------------------------------------------------------------------

    public Class<Entity> getType() {
        return Entity.class;
    }

    public Iterator<Entity> iterator() {
        if (source == null)
            init();
        Iterator<String[]> arrayIterator = source.iterator();
        String[] featureNames = arrayIterator.next();
        Converter<String[], String[]> arrayConverter = new ArrayConverter<String, String>(String.class, preprocessor); 
        Array2EntityConverter<String> a2eConverter = new Array2EntityConverter<String>(entityDescriptor, featureNames);
        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(arrayConverter, a2eConverter);
        return new ConvertingIterator<String[], Entity>(arrayIterator, converter);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void init() {
        this.source = new CSVLineIterable(uri, separator, true, encoding);
    }
}
