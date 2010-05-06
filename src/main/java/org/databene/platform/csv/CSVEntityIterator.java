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

package org.databene.platform.csv;

import org.databene.platform.array.Array2EntityConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.document.csv.CSVLineIterator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.IOUtil;
import org.databene.commons.Patterns;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.iterator.ConvertingIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates Entities in a CSV file.<br/>
 * <br/>
 * Created: 07.04.2008 09:49:08
 * @since 0.5.1
 * @author Volker Bergmann
 */
public class CSVEntityIterator implements HeavyweightIterator<Entity> {

    private String uri;
    private char   separator;
    private String encoding;

    private HeavyweightIterator<Entity> source;
    
    private ComplexTypeDescriptor entityDescriptor;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityIterator(String uri, String entityName) throws FileNotFoundException {
        this(uri, entityName, ',', SystemInfo.getFileEncoding());
    }

    public CSVEntityIterator(String uri, String entityName, char separator) throws FileNotFoundException {
        this(uri, entityName, separator, SystemInfo.getFileEncoding());
    }

    public CSVEntityIterator(String uri, String entityName, char separator, String encoding) throws FileNotFoundException {
        this(uri, new ComplexTypeDescriptor(entityName), new NoOpConverter<String>(), separator, encoding);
    }

    public CSVEntityIterator(String uri, String entityName, Converter<String, ?> preprocessor, char separator, String encoding) throws FileNotFoundException {
        this(uri, new ComplexTypeDescriptor(entityName), preprocessor, separator, encoding);
    }

    public CSVEntityIterator(String uri, ComplexTypeDescriptor descriptor, Converter<String, ?> preprocessor, char separator, String encoding) throws FileNotFoundException {
        this.uri = uri;
        this.separator = separator;
        this.encoding = encoding;
        this.entityDescriptor = descriptor;
        init(uri, preprocessor, separator, encoding);
    }

    // HeavyweightIterator interface -----------------------------------------------------------------------------------
    
	public void remove() {
		source.remove();
	}

    public boolean hasNext() {
        return source.hasNext();
    }
    
    public Entity next() {
    	if (!source.hasNext())
    		throw new IllegalStateException("No more entity to fetch, check hasNext() before calling next()");
        return source.next();
    }
    
	public void close() {
		 IOUtil.close(source);
	}

    public static List<Entity> parseAll(String uri, char separator, String encoding, ComplexTypeDescriptor descriptor, Converter<String, String> preprocessor, Patterns patterns) throws FileNotFoundException {
    	List<Entity> list = new ArrayList<Entity>();
    	CSVEntityIterator iterator = new CSVEntityIterator(uri, descriptor, preprocessor, separator, encoding);
    	while (iterator.hasNext())
    		list.add(iterator.next());
    	return list;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[uri=" + uri + ", encoding=" + encoding + ", separator=" + separator +
                ", entityName=" + entityDescriptor.getName() + "]";
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
	@SuppressWarnings("unchecked")
	private void init(String uri, Converter<String, ?> preprocessor,
			char separator, String encoding) throws FileNotFoundException {
		try {
        	String[] featureNames;
			Iterator<String[]> cellIterator = new CSVLineIterator(uri, separator, true, encoding);
			if (cellIterator.hasNext())
				featureNames = cellIterator.next();
			else
				throw new ConfigurationError("empty CSV file");
	        Converter<String[], String[]> arrayConverter = new ArrayConverter(String.class, Object.class, preprocessor); 
	        Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, featureNames, true);
	        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(arrayConverter, a2eConverter);
	        this.source = new ConvertingIterator<String[], Entity>(cellIterator, converter);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException("Error in processing " + uri, e);
		}
	}

}
