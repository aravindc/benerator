/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;

/**
 * Exports Entities to a CSV file.
 * The default line separator is CR LF according to RFC 4180. 
 * It can be set explicitly by <code>setLineSeparator()</code>.<br/>
 * <br/>
 * Created: 21.08.2007 21:16:59
 * @author Volker Bergmann
 */
public class CSVEntityExporter extends TextFileExporter<Entity> {

	private static final Log logger = LogFactory.getLog(CSVEntityExporter.class);
    private static final Escalator escalator = new LoggerEscalator();
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_LINE_SEPARATOR = "\r\n"; // as defined by RFC 4180
    private static final char   DEFAULT_SEPARATOR = ',';
    private static final String DEFAULT_URI       = "export.csv";

    // attributes ------------------------------------------------------------------------------------------------------

    private String[] columns;
    private char separator;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityExporter() {
        this(DEFAULT_URI, (String) null);
    }
    
    public CSVEntityExporter(String uri, String columns) {
        this(uri, columns, DEFAULT_SEPARATOR, null, DEFAULT_LINE_SEPARATOR);
    }

    public CSVEntityExporter(String uri, String columns, char separator, String encoding, String lineSeparator) {
    	super(uri, encoding, lineSeparator);
        setColumns(columns);
        this.separator = separator;
    }

    public CSVEntityExporter(ComplexTypeDescriptor descriptor) {
        this(descriptor.getName() + ".csv", descriptor);
    }

    public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor) {
        this(uri, descriptor, DEFAULT_SEPARATOR, null, DEFAULT_LINE_SEPARATOR);
    }

    public CSVEntityExporter(String uri, ComplexTypeDescriptor descriptor, char separator, String encoding, String lineSeparator) {
        super(uri, encoding, lineSeparator);
        Collection<ComponentDescriptor> componentDescriptors = descriptor.getComponents();
        List<String> componentNames = BeanUtil.extractProperties(componentDescriptors, "name");
        this.columns = CollectionUtil.toArray(componentNames, String.class);
        this.separator = separator;
    }

    // properties ------------------------------------------------------------------------------------------------------

	public void setColumns(String columnsSpec) {
		if (StringUtil.isEmpty(columnsSpec))
			this.columns = null;
		else {
	        this.columns = StringUtil.tokenize(columnsSpec, ',');
	        StringUtil.trimAll(this.columns);
		}
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    // Callback methods for parent class functionality -----------------------------------------------------------------

    @Override
	protected void startConsumingImpl(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("exporting " + entity);
        if (ArrayUtil.isEmpty(columns)) {
        	columns = CollectionUtil.toArray(entity.getComponents().keySet());
        	printHeaderRow();
        }
        printer.println();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0)
                printer.print(separator);
            Object value = entity.getComponent(columns[i]);
            String s = plainConverter.convert(value);
            if (s.indexOf(separator) >= 0)
                s = '"' + s + '"';
            printer.print(s);
        }
    }

    @Override
	protected void postInitPrinter() {
    	if (!append)
	        printHeaderRow();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

	private void printHeaderRow() {
		if (columns != null) {
		    for (int i = 0; i < columns.length; i++) {
		        if (i > 0)
		            printer.print(separator);
		        printer.print(columns[i]);
		    }
		}
    }
}
