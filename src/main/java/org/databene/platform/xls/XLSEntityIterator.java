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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.IOUtil;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.iterator.ConvertingIterator;
import org.databene.document.xls.XLSLineIterator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.platform.array.Array2EntityConverter;

/**
 * Iterates an Excel sheet and maps its rows to {@link Entity} instances.<br/>
 * <br/>
 * Created at 27.01.2009 21:38:31
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIterator implements HeavyweightIterator<Entity> {

	private String uri;
	
	private HSSFWorkbook workbook;

	private int sheetNo;
	
	private Converter<String, ? extends Object> preprocessor;
	
	private HeavyweightIterator<Entity> source;
	
	// constructors ----------------------------------------------------------------------------------------------------

	public XLSEntityIterator(String uri) throws IOException {
		this(uri, new NoOpConverter<String>());
	}

	public XLSEntityIterator(String uri, Converter<String, ? extends Object> preprocessor) 
			throws IOException {
		this.uri = uri;
		this.preprocessor = preprocessor;
		this.workbook = new HSSFWorkbook(IOUtil.getInputStreamForURI(uri));
		this.sheetNo = -1;
	}

	// HeavyweightIterator interface implementation --------------------------------------------------------------------

	public void remove() {
		source.remove();
	}

	public boolean hasNext() {
		if (sheetNo == -1 || (source != null && !source.hasNext()))
			nextSheet();
		return (source != null && source.hasNext());
	}

	public Entity next() {
		if (!hasNext())
			throw new IllegalStateException("No more entity to fetch, check hasNext() before calling next()");
		else
			return source.next();
	}

	public void close() {
		IOUtil.close(source);
	}
	
	// convenience methods ---------------------------------------------------------------------------------------------

	public static List<Entity> parseAll(String uri, Converter<String, ? extends Object> preprocessor) 
			throws IOException {
    	List<Entity> list = new ArrayList<Entity>();
    	XLSEntityIterator iterator = new XLSEntityIterator(uri, preprocessor);
    	while (iterator.hasNext())
    		list.add(iterator.next());
    	return list;
	}

	// properties ------------------------------------------------------------------------------------------------------
	
	public String getUri() {
		return uri;
	}

	// java.lang.Object overrides --------------------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + uri + "]";
	}

    private void nextSheet() {
    	if (sheetNo < workbook.getNumberOfSheets() - 1) {
			this.sheetNo++;
			source = createSheetIterator(workbook.getSheetAt(sheetNo), workbook.getSheetName(sheetNo), preprocessor);
    	} else
    		source = null;
    }

	private static HeavyweightIterator<Entity> createSheetIterator(
			HSSFSheet sheet, String sheetName, Converter<String, ? extends Object> preprocessor) {
	    XLSLineIterator sheetIterator = new XLSLineIterator(sheet, preprocessor);
	    String featureNames[] = sheetIterator.getHeaders();
	    DataModel dataModel = DataModel.getDefaultInstance();
	    ComplexTypeDescriptor typeDescriptor = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(sheetName);
	    if (typeDescriptor == null) {
	    	typeDescriptor = new ComplexTypeDescriptor(sheetName);
	    	// TODO set components
	    	// TODO add typeDescriptor to dataModel
	    }
	    Array2EntityConverter converter = new Array2EntityConverter(typeDescriptor, featureNames);
	    return new ConvertingIterator<Object[], Entity>(sheetIterator, converter);
    }
	
}
