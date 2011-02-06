/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import java.io.FileNotFoundException;

import org.databene.commons.ArrayUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.NoOpConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.FileBasedEntitySource;

/**
 * Imports {@link Entity} data from CSV files.<br/><br/>
 * @author Volker Bergmann
 */
public class CSVEntitySource extends FileBasedEntitySource {
	
    private char separator;
    private String encoding;
    private Converter<String, ?> preprocessor;

    private ComplexTypeDescriptor entityDescriptor;
	private String[] columns;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntitySource() {
        this(null, null);
    }

    public CSVEntitySource(String uri, String entityName) {
        this(uri, entityName, ',', SystemInfo.getFileEncoding());
    }

    public CSVEntitySource(String uri, String entityName, char separator) {
        this(uri, entityName, separator, SystemInfo.getFileEncoding());
    }

    public CSVEntitySource(String uri, String entityName, char separator, String encoding) {
        this(uri, new ComplexTypeDescriptor(entityName), new NoOpConverter<String>(), separator, encoding);
    }

    public CSVEntitySource(String uri, String entityName, Converter<String, ?> preprocessor, 
    		char separator, String encoding) {
        this(uri, new ComplexTypeDescriptor(entityName), preprocessor, separator, encoding);
    }

    public CSVEntitySource(String uri, ComplexTypeDescriptor descriptor, Converter<String, ?> preprocessor, 
    		char separator, String encoding) {
        super(uri);
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        this.preprocessor = preprocessor;
        
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setEntityName(String entityName) {
        this.entityDescriptor = new ComplexTypeDescriptor(entityName);
    }

	public void setColumns(String[] columns) {
		if (ArrayUtil.isEmpty(columns))
			this.columns = null;
		else {
	        this.columns = columns;
	        StringUtil.trimAll(this.columns);
		}
    }

    // EntitySource interface ------------------------------------------------------------------------------------------

	public HeavyweightIterator<Entity> iterator() {
        try {
			CSVEntityIterator iterator = new CSVEntityIterator(resolveUri(), entityDescriptor, preprocessor, separator, encoding);
			iterator.setColumns(columns);
			return iterator;
		} catch (FileNotFoundException e) {
			throw new ConfigurationError("Cannot create iterator. ", e);
		}
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }
    
}
