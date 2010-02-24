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

package org.databene.platform.flat;

import org.databene.commons.Converter;
import org.databene.commons.Escalator;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.SystemInfo;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.format.PadFormat;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.document.flat.FlatFileLineIterable;
import org.databene.document.flat.FlatFileUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.platform.array.Array2EntityConverter;

/**
 * Reads Entities from a flat file.<br/>
 * <br/>
 * Created at 07.11.2008 18:18:24
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class FlatFileEntitySource extends ConvertingIterable<String[], Entity> implements EntitySource {

	private static final Escalator escalator = new LoggerEscalator();
	
    private String uri;
    private String encoding;
    private ComplexTypeDescriptor entityDescriptor;
    private FlatFileColumnDescriptor[] descriptors;
    private String lineFilter;
    private boolean initialized;
    
    private Converter<String, String> preprocessor;

    public FlatFileEntitySource() {
        this(null, null, SystemInfo.getFileEncoding(), null);
    }

    public FlatFileEntitySource(String uri, ComplexTypeDescriptor entityDescriptor, 
    		String encoding, String lineFilter, FlatFileColumnDescriptor ... descriptors) {
        this(uri, entityDescriptor, new NoOpConverter<String>(), encoding, lineFilter, descriptors);
    }

    public FlatFileEntitySource(String uri, ComplexTypeDescriptor entityDescriptor, 
    		Converter<String, String> preprocessor, String encoding, String lineFilter, 
    		FlatFileColumnDescriptor ... descriptors) {
        super(null, null);
        this.uri = uri;
        this.encoding = encoding;
        this.entityDescriptor = entityDescriptor;
        this.descriptors = descriptors;
        this.preprocessor = preprocessor;
        this.initialized = false;
        this.lineFilter = lineFilter;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEntity() {
        return entityDescriptor.getName();
    }

    public void setEntity(String entity) {
        this.entityDescriptor = new ComplexTypeDescriptor(entity);
    }

    public void setProperties(String properties) {
    	escalator.escalate("The property 'properties' of class " + getClass() + "' has been renamed to 'columns'. " +
    			"Please fix the property name in your configuration", this.getClass(), "setProperties()");
        setColumns(properties);
    }

    public void setColumns(String columns) {
        this.descriptors = FlatFileUtil.parseProperties(columns);
    }
    
    // Iterable interface ----------------------------------------------------------------------------------------------

    public void setLineFilter(String lineFilter) {
    	this.lineFilter = lineFilter;
    }

	@Override
    public Class<Entity> getType() {
    	if (!initialized)
    		init();
    	return Entity.class;
    }
    
    @Override
    public HeavyweightIterator<Entity> iterator() {
        if (!initialized)
            init();
        return super.iterator();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------
    
    private void init() {
        this.iterable = createIterable(uri, descriptors, encoding, lineFilter);
        this.converter = createConverter(entityDescriptor, descriptors);
    }
    
    @SuppressWarnings("unchecked")
    private Converter<String[], Entity> createConverter(ComplexTypeDescriptor entityDescriptor, FlatFileColumnDescriptor[] descriptors) {
        String[] featureNames = ArrayPropertyExtractor.convert(descriptors, "name", String.class);
        Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, featureNames);
        Converter<String[], String[]> aConv = new ArrayConverter<String, String>(String.class, String.class, preprocessor);
        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(aConv, a2eConverter);
        return converter;
    }

    private static Iterable<String[]> createIterable(String uri, FlatFileColumnDescriptor[] descriptors, 
    		String encoding, String lineFilter) {
        PadFormat[] formats = ArrayPropertyExtractor.convert(descriptors, "format", PadFormat.class);
        return new FlatFileLineIterable(uri, formats, true, encoding, lineFilter);
    }

}
