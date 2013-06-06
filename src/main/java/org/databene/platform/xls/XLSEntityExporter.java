/*
 * (c) Copyright 2008-2013 by Volker Bergmann. All rights reserved.
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.databene.benerator.consumer.FileExporter;
import org.databene.benerator.consumer.FormattingConsumer;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.IOUtil;
import org.databene.document.xls.HSSFUtil;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.platform.csv.CSVEntityExporter;
import org.databene.script.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exports entities to Excel sheets.<br/><br/>
 * Created at 07.05.2008 13:31:15
 * @since 0.5.3
 * @author Volker Bergmann
 */
public class XLSEntityExporter extends FormattingConsumer implements FileExporter {

    private static final Logger logger = LoggerFactory.getLogger(CSVEntityExporter.class);
    
    // defaults --------------------------------------------------------------------------------------------------------
    
    private static final String DEFAULT_URI = "export.xls";

    // attributes ------------------------------------------------------------------------------------------------------

    private String uri;
    private HSSFWorkbook workbook;
	private HSSFCellStyle dateCellStyle;

    // constructors ----------------------------------------------------------------------------------------------------

    public XLSEntityExporter() {
        this(DEFAULT_URI);
    }
    
    public XLSEntityExporter(String uri) {
        this.uri = uri;
        setDatePattern("m/d/yy");
        setDecimalPattern("#,##0.##");
        setIntegralPattern("0");
        setTimePattern("h:mm:ss");
        setTimestampPattern("m/d/yy h:mm");
    }

    // properties ------------------------------------------------------------------------------------------------------

	@Override
	public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    @Override
	public void startProductConsumption(Object object) {
        logger.debug("exporting {}", object);
        if (!(object instanceof Entity))
        	throw new IllegalArgumentException("Expecting Entity");
        Entity entity = (Entity) object;
        HSSFSheet sheet = getOrCreateSheet(entity);
        HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
        int i = 0;
        for (Map.Entry<String, Object> component : getComponents(entity))
            render(row, i++, component.getValue());
    }

	@Override
    public void close() {
		FileOutputStream out = null;
        try {
            if (workbook == null)
                workbook = new HSSFWorkbook(); // if no data was added, create an empty Excel document
            else
            	HSSFUtil.autoSizeColumns(workbook);
			// Write the output to a file
            out = new FileOutputStream(uri);
			workbook.write(out);
		} catch (FileNotFoundException e) {
			throw new ConfigurationError(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(out);
		}
    }

    // private helpers -------------------------------------------------------------------------------------------------
    
    private HSSFSheet getOrCreateSheet(Entity entity) {
        // create file
    	if (workbook == null)
    		createWorkbook();
        String sheetName = entity.type();
		HSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
        	sheet = workbook.createSheet(sheetName);
        	writeHeaderRow(entity, sheet);
        }
        return sheet;
    }

	private void createWorkbook() {
		this.workbook = new HSSFWorkbook();
        this.dateCellStyle = workbook.createCellStyle();
		HSSFDataFormat format = workbook.createDataFormat();
		short dateFormat = format.getFormat(getDatePattern());
		this.dateCellStyle.setDataFormat(dateFormat);
    }

	private void writeHeaderRow(Entity entity, HSSFSheet sheet) {
	    HSSFRow headerRow = sheet.createRow(0);
	    int colnum = 0;
	    for (Map.Entry<String, Object> component : getComponents(entity)) {
	        String componentName = component.getKey();
			headerRow.createCell(colnum).setCellValue(new HSSFRichTextString(componentName));
	        ComponentDescriptor cd = entity.descriptor().getComponent(componentName);
	        PrimitiveType primitiveType;
	        if (cd.getTypeDescriptor() instanceof SimpleTypeDescriptor)
	            primitiveType = ((SimpleTypeDescriptor) cd.getTypeDescriptor()).getPrimitiveType();
	        else
	        	throw new UnsupportedOperationException("Can only export simple type attributes, " +
	        			"failed to export " + entity.type() + '.' + cd.getName());
	        Class<?> javaType = (primitiveType != null ? primitiveType.getJavaType() : String.class);
	        String formatString = null;
	        if (BeanUtil.isIntegralNumberType(javaType))
	            formatString = getIntegralPattern();
            else if (BeanUtil.isDecimalNumberType(javaType))
            	formatString = getDecimalPattern();
            else if (Time.class.isAssignableFrom(javaType))
            	formatString = getTimePattern();
            else if (Timestamp.class.isAssignableFrom(javaType))
            	formatString = getTimestampPattern();
            else if (Date.class.isAssignableFrom(javaType))
            	formatString = getDatePattern();
	        if (formatString != null) {
	            HSSFDataFormat dataFormat = workbook.createDataFormat();
		        CellStyle columnStyle = workbook.createCellStyle();
		        columnStyle.setDataFormat(dataFormat.getFormat(formatString));
		        sheet.setDefaultColumnStyle(colnum, columnStyle);
	        }
	        colnum++;
	    }
    }

	private static Set<Entry<String, Object>> getComponents(Entity entity) {
	    return entity.getComponents().entrySet();
    }

    private void render(HSSFRow row, int column, Object value) {
    	HSSFCell cell = row.createCell(column);
		if (value instanceof Number)
    		cell.setCellValue(((Number) value).doubleValue());
    	else if (value instanceof Date)
    		cell.setCellValue((Date) value);
    	else if (value instanceof Boolean)
    		cell.setCellValue((Boolean) value);
    	else {
	        String s = plainConverter.convert(value);
	        cell.setCellValue(new HSSFRichTextString(s));
    	}
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + '(' + uri + ")";
    }

	@Override
    public int hashCode() {
	    return uri.hashCode();
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null || getClass() != obj.getClass())
		    return false;
	    XLSEntityExporter that = (XLSEntityExporter) obj;
	    return (this.uri.equals(that.uri));
    }
    
}
