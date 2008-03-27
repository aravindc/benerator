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

package org.databene.platform.flat;

import org.databene.platform.array.Array2EntityConverter;
import org.databene.commons.bean.ArrayPropertyExtractor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.EntityIterable;
import org.databene.model.data.Entity;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.document.flat.FlatFileLineIterable;
import org.databene.document.flat.FlatFileUtil;
import org.databene.commons.Converter;
import org.databene.commons.SystemInfo;
import org.databene.commons.converter.ArrayConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.ConvertingIterable;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.format.PadFormat;

import java.util.Iterator;

/**
 * Reads Entities from a flat file.<br/>
 * <br/>
 * Created: 26.08.2007 12:16:08
 * @author Volker Bergmann
 */
public class FlatFileEntityIterable extends ConvertingIterable<String[], Entity> implements EntityIterable {

    private String uri;
    private String encoding;
    private ComplexTypeDescriptor entityDescriptor;
    private FlatFileColumnDescriptor[] descriptors;
    private boolean initialized;
    
    private Converter<String, String> preprocessor;

    public FlatFileEntityIterable() {
        this(null, null, SystemInfo.fileEncoding());
    }

    public FlatFileEntityIterable(String uri, ComplexTypeDescriptor entityDescriptor, String encoding, FlatFileColumnDescriptor ... descriptors) {
        this(uri, entityDescriptor, new NoOpConverter<String>(), encoding, descriptors);
    }

    public FlatFileEntityIterable(String uri, ComplexTypeDescriptor entityDescriptor, Converter<String, String> preprocessor, String encoding, FlatFileColumnDescriptor ... descriptors) {
        super(null, null);
        this.uri = uri;
        this.encoding = encoding;
        this.entityDescriptor = entityDescriptor;
        this.descriptors = descriptors;
        this.preprocessor = preprocessor;
        this.initialized = false;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEntity() {
        return entityDescriptor.getName();
    }

    public void setEntity(String entity) {
        this.entityDescriptor = new ComplexTypeDescriptor(entity);
    }

    public void setProperties(String properties) {
        this.descriptors = FlatFileUtil.parseProperties(properties);
    }

    // Iterable interface ----------------------------------------------------------------------------------------------

    public Iterator<Entity> iterator() {
        if (!initialized)
            init();
        return super.iterator();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------
    
    private void init() {
        this.iterable = createIterable(uri, descriptors, encoding);
        this.converter = createConverter(entityDescriptor, descriptors);
    }
    
    private Converter<String[], Entity> createConverter(ComplexTypeDescriptor entityDescriptor, FlatFileColumnDescriptor[] descriptors) {
        String[] featureNames = ArrayPropertyExtractor.convert(descriptors, "name", String.class);
        Array2EntityConverter<String> a2eConverter = new Array2EntityConverter<String>(entityDescriptor, featureNames);
        Converter<String[], String[]> aConv = new ArrayConverter<String, String>(String.class, preprocessor);
        Converter<String[], Entity> converter = new ConverterChain<String[], Entity>(aConv, a2eConverter);
        return converter;
    }

    private static Iterable<String[]> createIterable(String uri, FlatFileColumnDescriptor[] descriptors, String encoding) {
        PadFormat[] formats = ArrayPropertyExtractor.convert(descriptors, "format", PadFormat.class);
        return new FlatFileLineIterable(uri, formats, true, encoding);
    }

}
