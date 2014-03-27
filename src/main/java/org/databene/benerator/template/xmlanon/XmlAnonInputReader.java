/*
 * (c) Copyright 2014 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.template.xmlanon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.databene.benerator.template.TemplateInputReader;
import org.databene.commons.Assert;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.IOUtil;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;

/**
 * Reads XLS documents for a multi-file XML anonymization.<br/><br/>
 * Created: 06.03.2014 08:25:43
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class XmlAnonInputReader implements TemplateInputReader {

	@Override
	public void parse(String uri, Context context) throws IOException, ParseException {
		try {
			AnonymizationSetup setup = parseXls(uri);
			verifyXMLFileSettings(setup);
			context.set("setup", setup);
		} catch (InvalidFormatException e) {
			throw new ParseException("Error parsing Input file for XML Anonymization: " + e.getMessage(), uri);
		}
	}

	private static AnonymizationSetup parseXls(String xlsUri) throws IOException, InvalidFormatException {
		Workbook workbook = WorkbookFactory.create(IOUtil.getInputStreamForURI(xlsUri));
		Sheet sheet = workbook.getSheetAt(0);
		
		// parse header information
		int varnameColumnIndex = -1;
		ArrayList<String> files = new ArrayList<String>();
		Row headerRow = sheet.getRow(0);
		Assert.notNull(headerRow, "header row");
		for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
			String header = headerRow.getCell(i).getStringCellValue();
			if ("varname".equals(header)) {
				varnameColumnIndex = i;
				break;
			} else {
				if (StringUtil.isEmpty(header))
					throw new ConfigurationError("Filename missing in column header #" + i + " of Excel document " + xlsUri);
				files.add(header);
			}
		}
		if (varnameColumnIndex == -1)
			throw new ConfigurationError("No 'varname' header defined in Excel document " + xlsUri);
		if (files.size() == 0)
			throw new ConfigurationError("No files specified in Excel document " + xlsUri);
		
		// parse anonymization rows
		List<Anonymization> anonymizations = new ArrayList<Anonymization>();
		for (int rownum = 1; rownum <= sheet.getLastRowNum(); rownum++) {
			Row row = sheet.getRow(rownum);
			Anonymization anon = new Anonymization(row.getCell(varnameColumnIndex).getStringCellValue());
			// parse locators
			for (int colnum = 0; colnum < varnameColumnIndex; colnum++) {
				Cell cell = row.getCell(colnum);
				String path = (cell != null ? cell.getStringCellValue() : null);
				if (!StringUtil.isEmpty(path)) {
					List<String> tokens = XPathTokenizer.tokenize(path);
					String entityPath = XPathTokenizer.merge(tokens, 0, tokens.size() - 2);
					String entity = normalizeXMLPath(XPathTokenizer.nodeName(tokens.get(tokens.size() - 2)));
					String attribute = normalizeXMLPath(tokens.get(tokens.size() - 1));
					anon.addLocator(new Locator(files.get(colnum), path, entityPath, entity, attribute));
				}
			}
			// parse settings
			for (int colnum = varnameColumnIndex + 1; colnum < row.getLastCellNum() - 1; colnum += 2) {
				String key = row.getCell(colnum).getStringCellValue();
				String value = row.getCell(colnum + 1).getStringCellValue();
				if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value))
					anon.addSetting(key, value);
			}
			anonymizations.add(anon);
		}
		return new AnonymizationSetup(files, anonymizations);
	}
	
	private static void verifyXMLFileSettings(AnonymizationSetup setup) {
		for (String file : setup.getFiles())
			if (StringUtil.isEmpty(System.getProperty(file)))
				throw new ConfigurationError("No concrete file specified for file variable " + file);
	}

	private static String normalizeXMLPath(String path) {
		return path.replace('.', '_').replace('-', '_');
	}

}
