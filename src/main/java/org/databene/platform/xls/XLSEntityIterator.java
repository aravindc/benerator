/*
 * (c) Copyright 2009-2013 by Volker Bergmann. All rights reserved.
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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.context.ContextAware;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.NoOpConverter;
import org.databene.commons.converter.ToStringConverter;
import org.databene.document.xls.XLSLineIterator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.platform.array.Array2EntityConverter;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.OrthogonalArrayIterator;
import org.databene.webdecs.util.ThreadLocalDataContainer;

/**
 * Iterates an Excel sheet and maps its rows to {@link Entity} instances.<br/>
 * <br/>
 * Created at 27.01.2009 21:38:31
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIterator implements DataIterator<Entity>, ContextAware {

	private String uri;
	
	private Workbook workbook;

	private int sheetNo;
	private String sheetName;
	protected boolean formatted;
	private boolean rowBased;
	protected String emptyMarker;
	
	private Converter<String, ?> preprocessor;
	private DataIterator<Entity> source;
	private BeneratorContext context;
	
    private ComplexTypeDescriptor entityDescriptor;
	
	// constructors ----------------------------------------------------------------------------------------------------

	public XLSEntityIterator(String uri) throws IOException, InvalidFormatException {
		this(uri, new NoOpConverter<String>(), null, null, false);
	}

	public XLSEntityIterator(String uri, Converter<String, ?> preprocessor, ComplexTypeDescriptor entityDescriptor, String sheetName, boolean formatted) 
			throws IOException, InvalidFormatException {
		this.uri = uri;
		this.preprocessor = preprocessor;
		this.entityDescriptor = entityDescriptor;
		this.rowBased = (entityDescriptor != null && entityDescriptor.isRowBased() != null ? entityDescriptor.isRowBased() : true);
		this.emptyMarker = (entityDescriptor != null && entityDescriptor.getEmptyMarker() != null ? entityDescriptor.getEmptyMarker() : null);
		this.workbook = WorkbookFactory.create(IOUtil.getInputStreamForURI(uri));
		this.sheetName = sheetName;
		this.sheetNo = -1;
		this.formatted = formatted;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	public void setRowBased(boolean rowBased) {
		this.rowBased = rowBased;
	}
	
	@Override
	public void setContext(Context context) {
		this.context = (BeneratorContext) context;
	}
	
	// DataSource interface implementation -----------------------------------------------------------------------------

	@Override
	public Class<Entity> getType() {
		return Entity.class;
	}
	
	@Override
	public synchronized DataContainer<Entity> next(DataContainer<Entity> container) {
		if (sheetNo == -1)
			nextSheet();
		DataContainer<Entity> result;
		do {
			if (source == null)
				return null;
			result = source.next(container);
			if (result == null && sheetName == null)
				nextSheet();
		} while (source != null && result == null && sheetName == null);
		return result;
	}

	@Override
	public synchronized void close() {
		IOUtil.close(source);
	}
	
	// convenience methods ---------------------------------------------------------------------------------------------

	public static List<Entity> parseAll(String uri, Converter<String, ?> preprocessor) throws IOException, InvalidFormatException {
    	return parseAll(uri, preprocessor, null, false);
	}

	public static List<Entity> parseAll(String uri, Converter<String, ?> preprocessor, String sheetName, boolean formatted) 
			throws IOException, InvalidFormatException {
    	List<Entity> list = new ArrayList<Entity>();
    	XLSEntityIterator iterator = new XLSEntityIterator(uri, preprocessor, null, sheetName, formatted);
    	iterator.setContext(new DefaultBeneratorContext());
		DataContainer<Entity> container = new DataContainer<Entity>();
    	while ((container = iterator.next(container)) != null)
			list.add(container.getData());
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
	
	// private helpers -------------------------------------------------------------------------------------------------

	private void nextSheet() {
    	// check if a sheet is available
    	if (sheetNo >= workbook.getNumberOfSheets() - 1) {
    		source = null;
    		return;
    	}
    	
    	// if a sheet was already opened, then close it
		if (source != null)
			IOUtil.close(source);
		
		// select sheet
		if (!StringUtil.isEmpty(sheetName)) {
			this.sheetNo = workbook.getSheetIndex(sheetName);
			if (this.sheetNo < 0)
				throw new ConfigurationError("Sheet '" + sheetName + "' not found in file '" + uri + "': '");
		} else
			this.sheetNo++;
		
		// determine the entity type name from the name of the sheet
		Sheet sheet = workbook.getSheetAt(sheetNo);
		ComplexTypeDescriptor descriptorToUse = entityDescriptor;
		if (descriptorToUse == null) {
			String entityTypeName = sheet.getSheetName();
			if (context != null) {
				DataModel dataModel = context.getDataModel();
				descriptorToUse = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(entityTypeName);
				if (descriptorToUse != null)
					descriptorToUse = new ComplexTypeDescriptor(entityTypeName + "_", context.getLocalDescriptorProvider());
				else
					descriptorToUse = createDescriptor(entityTypeName);
			} else
				descriptorToUse = createDescriptor(entityTypeName);
		}
		
		// create iterator
		source = createSheetIterator(sheet, descriptorToUse, rowBased, preprocessor, uri);
    }

	public ComplexTypeDescriptor createDescriptor(String entityTypeName) {
		ComplexTypeDescriptor descriptor;
		descriptor = new ComplexTypeDescriptor(entityTypeName, context.getLocalDescriptorProvider());
		context.addLocalType(descriptor);
		return descriptor;
	}

	private DataIterator<Entity> createSheetIterator(
			Sheet sheet, ComplexTypeDescriptor complexTypeDescriptor, boolean rowBased, Converter<String, ?> preprocessor, String uri) {
		return new SingleSheetIterator(sheet, complexTypeDescriptor, rowBased, preprocessor, uri);
    }
	
	class SingleSheetIterator implements DataIterator<Entity> {
		
	    private DataIterator<Object[]> source;
	    private Converter<Object[], Entity> converter;
	    private Object[] buffer;
	    private ThreadLocalDataContainer<Object[]> sourceContainer = new ThreadLocalDataContainer<Object[]>();
		
		public SingleSheetIterator(Sheet sheet, ComplexTypeDescriptor complexTypeDescriptor, boolean rowBased, Converter<String, ?> preprocessor, 
				String defaultProviderId) {
	        this.source = createRawIterator(sheet, rowBased, preprocessor);
	        // parse headers
			String[] headers = parseHeaders();
			if (headers == null) {
				this.source = null; // empty sheet
				return;
			}
			// parse first data row
			DataContainer<Object[]> tmp = this.source.next(sourceContainer.get());
			if (tmp == null) {
				this.source = null; // no data in sheet
				return;
			}
			this.buffer = tmp.getData();
		    converter = new Array2EntityConverter(complexTypeDescriptor, headers, false);
        }

		private String[] parseHeaders() {
			String[] headers = null;
			DataContainer<Object[]> tmp = this.source.next(sourceContainer.get());
			if (tmp != null)
				headers = (String[]) ConverterManager.convertAll(tmp.getData(), new ToStringConverter(), String.class);
			return headers;
		}

		private DataIterator<Object[]> createRawIterator(Sheet sheet, boolean rowBased,
				Converter<String, ?> preprocessor) {
			XLSLineIterator iterator = new XLSLineIterator(sheet, preprocessor, formatted);
			if (emptyMarker != null)
				iterator.setEmptyMarker(emptyMarker);
	        if (!rowBased)
	        	return new OrthogonalArrayIterator<Object>(iterator);
	        return iterator;
		}
		
		@Override
		public Class<Entity> getType() {
			return Entity.class;
		}
		
		@Override
		public DataContainer<Entity> next(DataContainer<Entity> container) {
			if (source == null)
				return null;
			Object[] rawData;
			if (buffer != null) {
				rawData = buffer;
				buffer = null;
			} else {
				DataContainer<Object[]> tmp = source.next(sourceContainer.get());
				if (tmp == null)
					return null;
				rawData = tmp.getData();
			}
			return container.setData(converter.convert(rawData));
		}
		
		@Override
		public void close() {
			IOUtil.close(source);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + source + "]";
		}
	}

}
