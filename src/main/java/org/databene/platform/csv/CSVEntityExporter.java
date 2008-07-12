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

import org.databene.model.consumer.TextFileExporter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;

/**
 * Exports Entities to a CSV file.<br/>
 * <br/>
 * Created: 21.08.2007 21:16:59
 * @author Volker Bergmann
 */
public class CSVEntityExporter extends TextFileExporter<Entity> {

    private static final Log logger = LogFactory.getLog(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final char   DEFAULT_SEPARATOR = ',';
    private static final String DEFAULT_ENCODING  = SystemInfo.fileEncoding();
    private static final String DEFAULT_URI       = "export.csv";

    // attributes ------------------------------------------------------------------------------------------------------

    private String[] propertyNames;
    private char separator;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityExporter() {
        this(DEFAULT_URI, "");
    }
    
    public CSVEntityExporter(String uri, String attributes) {
        this(uri, attributes, DEFAULT_SEPARATOR, DEFAULT_ENCODING);
    }

    public CSVEntityExporter(String uri, String attributes, char separator, String encoding) {
    	super(uri, encoding);
        setProperties(attributes);
        this.separator = separator;
    }

    public CSVEntityExporter(ComplexTypeDescriptor descriptor) {
        this(descriptor.getName() + ".csv", descriptor);
    }

    public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor) {
        this(uri, descriptor, DEFAULT_SEPARATOR, DEFAULT_ENCODING);
    }

    public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor, char separator, String encoding) {
        super(uri, encoding);
        Collection<ComponentDescriptor> componentDescriptors = descriptor.getComponents();
        List<String> componentNames = BeanUtil.extractProperties(componentDescriptors, "name");
        this.propertyNames = CollectionUtil.toArray(componentNames, String.class);
        this.separator = separator;
    }

    // properties ------------------------------------------------------------------------------------------------------

	public void setProperties(String attributes) {
        this.propertyNames = StringUtil.tokenize(attributes, ',');
        StringUtil.trimAll(propertyNames);
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    // Callback methods for parent class functionality -----------------------------------------------------------------

    protected void startConsumingImpl(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("exporting " + entity);
        for (int i = 0; i < propertyNames.length; i++) {
            if (i > 0)
                printer.print(separator);
            Object value = entity.getComponent(propertyNames[i]);
            String s = plainConverter.convert(value);
            if (s.indexOf(separator) >= 0)
                s = '"' + s + '"';
            printer.print(s);
        }
        printer.println();
    }

    protected void postInitPrinter() {
        // write header
        for (int i = 0; i < propertyNames.length; i++) {
            if (i > 0)
                printer.print(separator);
            printer.print(propertyNames[i]);
        }
        printer.println();
    }
}
