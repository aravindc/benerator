/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.xls;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.databene.commons.ArrayFormat;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.StringUtil;
import org.databene.model.consumer.FileExporter;
import org.databene.model.consumer.FormattingConsumer;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.platform.csv.CSVEntityExporter;

/**
 * Exports entities to Excel sheets.<br/><br/>
 * Created at 07.05.2008 13:31:15
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class XLSEntityExporter extends FormattingConsumer<Entity> implements FileExporter<Entity> {

    private static final Log logger = LogFactory.getLog(CSVEntityExporter.class);
    private static final Escalator escalator = new LoggerEscalator();
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_URI = "export.xls";

    // attributes ------------------------------------------------------------------------------------------------------

    private String uri;
    private String[] attributeNames;

    private HSSFWorkbook workbook;
    HSSFSheet sheet;
    private int rowCount;
	private HSSFCellStyle dateCellStyle;

    // constructors ----------------------------------------------------------------------------------------------------

    public XLSEntityExporter() {
        this(DEFAULT_URI, "");
    }
    
    public XLSEntityExporter(String uri, String attributes) {
        this.uri = uri;
        setAttributes(attributes);
    }

    public XLSEntityExporter(ComplexTypeDescriptor descriptor) {
        this(descriptor.getName() + ".csv", descriptor);
    }

    public XLSEntityExporter(String uri, ComplexTypeDescriptor descriptor) {
        this.uri = uri;
        Collection<ComponentDescriptor> componentDescriptors = descriptor.getComponents();
        List<String> componentNames = BeanUtil.extractProperties(componentDescriptors, "name");
        this.attributeNames = CollectionUtil.toArray(componentNames, String.class);
        this.workbook = null;
        this.sheet = null;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

	public void setAttributes(String attributes) {
        this.attributeNames = StringUtil.tokenize(attributes, ',');
        StringUtil.trimAll(attributeNames);
    }

	public void setProperties(String attributes) {
        escalator.escalate("XLSEntityExporter.properties is deprectated. Use XLSEntityExporter.attributes instead.", 
        		getClass(), null);
        setAttributes(attributes);
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    public void startConsuming(Entity entity) {
        if (logger.isDebugEnabled())
            logger.debug("exporting " + entity);
        if (workbook == null)
            initWorkbook();
        HSSFRow row = sheet.createRow(rowCount++);
        for (int i = 0; i < attributeNames.length; i++) {
            Object value = entity.getComponent(attributeNames[i]);
            render(row, (short) i, value);
        }
    }

	@Override
	public void flush() {
    }

    @Override
	public void close() {
        try {
        	autoSizeColumns();
			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(uri);
			workbook.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			throw new ConfigurationError(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}    
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    private void initWorkbook() {
        // create file
        this.workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("new sheet");
        this.rowCount = 0;
        this.dateCellStyle = workbook.createCellStyle();
		HSSFDataFormat format = workbook.createDataFormat();
		short dateFormat = format.getFormat(getDatePattern());
		this.dateCellStyle.setDataFormat(dateFormat);


        // write header
        HSSFRow row = sheet.createRow((short) rowCount++);
        for (int i = 0; i < attributeNames.length; i++)
            row.createCell((short)i).setCellValue(new HSSFRichTextString(attributeNames[i]));
    }

    private void render(HSSFRow row, short column, Object value) {
    	HSSFCell cell = row.createCell(column);
		if (value instanceof Number)
    		cell.setCellValue(((Number) value).doubleValue());
    	else if (value instanceof Date) {
			cell.setCellStyle(dateCellStyle);
    		cell.setCellValue((Date) value);
    	} else if (value instanceof Boolean)
    		cell.setCellValue((Boolean) value);
    	else {
	        String s = plainConverter.convert(value);
	        cell.setCellValue(new HSSFRichTextString(s));
    	}
	}

	private void autoSizeColumns() {
		for (short colnum = 0; colnum <= sheet.getLastRowNum(); colnum++)
			sheet.autoSizeColumn(colnum);
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + '(' + ArrayFormat.format(attributeNames) + ") -> " + uri;
    }

}
