/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.ArrayFormat;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_LINE_SEPARATOR = "\r\n"; // as defined by RFC 4180
    private static final char   DEFAULT_SEPARATOR = ',';
    private static final String DEFAULT_URI       = "export.csv";

    // configuration attributes ----------------------------------------------------------------------------------------

    private String[] columns;
    private boolean headless;
    private boolean endWithNewLine;
    private char separator;

    // state attributes ------------------------------------------------------------------------------------------------

    private boolean lfRequired;

    // constructors ----------------------------------------------------------------------------------------------------

    public CSVEntityExporter() {
        this(DEFAULT_URI);
    }
    
    public CSVEntityExporter(String uri) {
        this(uri, (String) null);
    }

    public CSVEntityExporter(String uri, String columnsSpec) {
        this(uri, columnsSpec, DEFAULT_SEPARATOR, null, DEFAULT_LINE_SEPARATOR);
    }

    public CSVEntityExporter(String uri, String columnsSpec, char separator, String encoding, String lineSeparator) {
    	super(uri, encoding, lineSeparator);
    	if (columnsSpec != null)
    		setColumns(ArrayFormat.parse(columnsSpec, ",", String.class));
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
        this.endWithNewLine = false;
        this.separator = separator;
    }

    
    
    // properties ------------------------------------------------------------------------------------------------------

	public void setColumns(String[] columns) {
		if (ArrayUtil.isEmpty(columns))
			this.columns = null;
		else {
	        this.columns = columns;
	        StringUtil.trimAll(this.columns);
		}
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }
    
	public boolean isHeadless() {
		return headless;
	}
	
	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public boolean isEndWithNewLine() {
		return endWithNewLine;
	}

	public void setEndWithNewLine(boolean endWithNewLine) {
		this.endWithNewLine = endWithNewLine;
	}
	
    // Callback methods for parent class functionality -----------------------------------------------------------------

    @Override
	protected void startConsumingImpl(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("exporting " + entity);
        if (lfRequired)
        	printer.println();
        else
        	lfRequired = true;
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
	protected void postInitPrinter(Entity entity) {
    	// determine columns from entity, if they have not been predefined
    	if (columns == null && entity != null)
			columns = CollectionUtil.toArray(entity.getComponents().keySet());
    	lfRequired = false;
    	if (!headless)
    		printHeaderRow();
    }
    
    @Override
    public void close() {
    	if (endWithNewLine)
    		printer.println();
    	super.close();
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

	private void printHeaderRow() {
		if (columns != null) {
			if (append)
				printer.println();
		    for (int i = 0; i < columns.length; i++) {
		        if (i > 0)
		            printer.print(separator);
		        printer.print(columns[i]);
		    }
	   		lfRequired = true;
		}
    }
	
}
