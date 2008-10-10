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

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.EntityIterable;
import org.databene.model.data.Entity;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;

import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Imports Entities from a CSV file.<br/>
 * <br/>
 * Created: 26.08.2007 12:16:08
 * @author Volker Bergmann
 */
public class CSVEntityIterable implements EntityIterable {

    private String uri;
    private char   separator;
    private String encoding;
    private Converter<String, String> preprocessor;

    private ComplexTypeDescriptor entityDescriptor; // TODO is this used/useful?

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
        this(uri, new ComplexTypeDescriptor(entityName), new NoOpConverter<String>(), separator, encoding);
    }

    public CSVEntityIterable(String uri, String entityName, Converter<String, String> preprocessor, char separator, String encoding) {
        this(uri, new ComplexTypeDescriptor(entityName), preprocessor, separator, encoding);
    }

    public CSVEntityIterable(String uri, ComplexTypeDescriptor descriptor, Converter<String, String> preprocessor, char separator, String encoding) {
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
        this.entityDescriptor = new ComplexTypeDescriptor(entityName);
    }

    // EntityIterable interface ----------------------------------------------------------------------------------------

    public Class<Entity> getType() {
        return Entity.class;
    }

    public Iterator<Entity> iterator() {
        try {
			return new CSVEntityIterator(uri, entityDescriptor, preprocessor, separator, encoding);
		} catch (FileNotFoundException e) {
			throw new ConfigurationError("Cannot create iterator. ", e);
		}
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }
}
