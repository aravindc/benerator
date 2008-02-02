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

import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntityDescriptor;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.ArrayFormat;
import org.databene.commons.SystemInfo;
import org.databene.commons.IOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Exports Entities to a CSV file.<br/>
 * <br/>
 * Created: 21.08.2007 21:16:59
 * @author Volker Bergmann
 */
public class CSVEntityExporter extends AbstractConsumer<Entity> {

    private static final Log logger = LogFactory.getLog(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final char   DEFAULT_SEPARATOR = ',';
    private static final String DEFAULT_ENCODING  = SystemInfo.fileEncoding();
    private static final String DEFAULT_URI       = "export.csv";

    // attributes ------------------------------------------------------------------------------------------------------

    private String uri;
    private String[] propertyNames;
    private String encoding;
    private char separator;

    private PrintWriter printer;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityExporter() {
        this(DEFAULT_URI, "");
    }
    
    public CSVEntityExporter(String uri, String attributes) {
        this(uri, attributes, DEFAULT_SEPARATOR, DEFAULT_ENCODING);
    }

    public CSVEntityExporter(String uri, String attributes, char separator, String encoding) {
        this.uri = uri;
        setProperties(attributes);
        this.separator = separator;
        this.encoding = encoding;
    }

    public CSVEntityExporter(EntityDescriptor descriptor) {
        this(descriptor.getName() + ".csv", descriptor);
    }

    public CSVEntityExporter(String uri, EntityDescriptor descriptor) {
        this(uri, descriptor, DEFAULT_SEPARATOR, DEFAULT_ENCODING);
    }

    public CSVEntityExporter(String uri, EntityDescriptor descriptor, char separator, String encoding) {
        this.uri = uri;
        Collection<ComponentDescriptor> componentDescriptors = descriptor.getComponentDescriptors();
        List<String> componentNames = BeanUtil.extractProperties(componentDescriptors, "name");
        this.propertyNames = CollectionUtil.toArray(componentNames, String.class);
        this.separator = separator;
        this.encoding = encoding;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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

    // Consumer interface ----------------------------------------------------------------------------------------------

    public void startConsuming(Entity entity) {
        try {
            if (logger.isDebugEnabled())
                logger.debug("exporting " + entity);
            if (printer == null)
                initPrinter();
            for (int i = 0; i < propertyNames.length; i++) {
                if (i > 0)
                    printer.print(separator);
                Object value = entity.getComponent(propertyNames[i]);
                String s = String.valueOf(value);
                if (s.indexOf(separator) >= 0)
                    s = '"' + s + '"';
                printer.print(s);
            }
            printer.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        if (printer != null)
            printer.flush();
    }

    public void close() {
        if (printer != null)
            printer.close();
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    private void initPrinter() throws IOException {
        // create file
        printer = IOUtil.getPrinterForURI(uri, encoding);
        // write header
        for (int i = 0; i < propertyNames.length; i++) {
            if (i > 0)
                printer.print(separator);
            printer.print(propertyNames[i]);
        }
        printer.println();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '(' + ArrayFormat.format(propertyNames) + ") -> " + uri;
    }

}
