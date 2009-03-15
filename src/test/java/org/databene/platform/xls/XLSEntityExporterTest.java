/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.databene.commons.ArrayUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;

import junit.framework.TestCase;

/**
 * Tests the {@link XLSEntityExporter}.<br/>
 * <br/>
 * Created at 14.03.2009 07:27:34
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityExporterTest extends TestCase {

	private static final File STANDARD_FILE = new File("export.xls");
	private static final File CUSTOM_FILE = new File("target", XLSEntityExporterTest.class.getSimpleName() + ".xls");

	private static final String     EAN1   = "1234567890123";
    private static final BigDecimal PRICE1 = new BigDecimal(123);
    private static final Date		DATE1 = new Date();
    private static final Boolean	AVAIL1 = true;
    
    private static final String     EAN2   = "9876543210987";
    private static final BigDecimal PRICE2 = new BigDecimal(321);
    private static final Date		DATE2 = DATE1;
    private static final Boolean	AVAIL2 = false;

	private static final ComplexTypeDescriptor descriptor;
	static {
		descriptor = new ComplexTypeDescriptor("Product");
		descriptor.addComponent(new PartDescriptor("ean", "string"));
		descriptor.addComponent(new PartDescriptor("price", "big_decimal"));
		descriptor.addComponent(new PartDescriptor("date", "date"));
		descriptor.addComponent(new PartDescriptor("avail", "boolean"));
	}
	
	private static final Entity prod1 = new Entity(descriptor, 
			"ean", EAN1, 
			"price", PRICE1,
			"date", DATE1,
			"avail", AVAIL1
	);
	
	private static final Entity prod2 = new Entity(descriptor, 
			"ean", EAN2, 
			"price", PRICE2,
			"date", DATE2,
			"avail", AVAIL2
	);
	
	// tests -----------------------------------------------------------------------------------------------------------
	
	public void testEmptyStandard() {
		XLSEntityExporter exporter = new XLSEntityExporter();
		exporter.close();
		assertTrue(STANDARD_FILE.exists());
	}

	public void testPredefinedColumns() throws Exception {
		XLSEntityExporter exporter = new XLSEntityExporter(CUSTOM_FILE.getAbsolutePath(), "ean");
		consumeAndClose(exporter);
		assertTrue(CUSTOM_FILE.exists());
		HSSFSheet sheet = readFirstSheetOf(CUSTOM_FILE);
		checkCells(sheet.getRow(0), "ean", null);
		checkCells(sheet.getRow(1), EAN1, null);
		checkCells(sheet.getRow(2), EAN2, null);
		checkCells(sheet.getRow(3));
	}

	public void testColumnsByDescriptor() throws Exception {
		XLSEntityExporter exporter = new XLSEntityExporter(CUSTOM_FILE.getAbsolutePath(), descriptor);
		consumeAndClose(exporter);
		assertFullContent(CUSTOM_FILE);
	}

	public void testColumnsByFirstEntity() throws Exception {
		XLSEntityExporter exporter = new XLSEntityExporter(CUSTOM_FILE.getAbsolutePath());
		consumeAndClose(exporter);
		assertFullContent(CUSTOM_FILE);
	}

	// framework callbacks ---------------------------------------------------------------------------------------------
	
	@Override
	protected void setUp() throws Exception {
		deleteFiles();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteFiles();
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void consumeAndClose(XLSEntityExporter exporter) {
	    exporter.startConsuming(prod1);
		exporter.finishConsuming(prod1);
		exporter.startConsuming(prod2);
		exporter.finishConsuming(prod2);
		exporter.close();
    }
	
	private void assertFullContent(File file) throws IOException {
	    assertTrue(file.exists());
		HSSFSheet sheet = readFirstSheetOf(CUSTOM_FILE);
		checkCells(sheet.getRow(0), "ean", "price", "date", "avail", null);
		checkCells(sheet.getRow(1), EAN1, PRICE1, DATE1, AVAIL1, null);
		checkCells(sheet.getRow(2), EAN2, PRICE2, DATE2, AVAIL2, null);
		checkCells(sheet.getRow(3));
    }

    private HSSFSheet readFirstSheetOf(File file) throws IOException {
	    HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
	    return wb.getSheetAt(0);
    }

    private void checkCells(HSSFRow row, Object... values) {
	    if (ArrayUtil.isEmpty(values))
	    	assertNull(row);
	    for (int i = 0; i < values.length; i++) {
	    	HSSFCell cell = row.getCell(i);
	    	Object expectedContent = values[i];
	    	if (expectedContent == null)
	    		assertNull(cell);
	    	else if (expectedContent instanceof String) {
	    		assertEquals(HSSFCell.CELL_TYPE_STRING, cell.getCellType());
	    		assertEquals(expectedContent, cell.getStringCellValue());
	    	} else if (expectedContent instanceof Number) {
	    		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, cell.getCellType());
	    		assertEquals(((Number) expectedContent).doubleValue(), cell.getNumericCellValue());
	    	} else if (expectedContent instanceof Boolean) {
	    		assertEquals(HSSFCell.CELL_TYPE_BOOLEAN, cell.getCellType());
	    		assertEquals(expectedContent, cell.getBooleanCellValue());
	    	} else if (expectedContent instanceof Date) {
	    		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, cell.getCellType());
	    		assertEquals(expectedContent, cell.getDateCellValue());
	    	} else
	    		throw new RuntimeException("Type not supported: " + expectedContent.getClass());
	    }
    }

	private void deleteFiles() {
	    if (STANDARD_FILE.exists())
			STANDARD_FILE.delete();
		if (CUSTOM_FILE.exists())
			CUSTOM_FILE.delete();
    }

}
