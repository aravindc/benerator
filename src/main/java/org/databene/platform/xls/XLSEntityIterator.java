/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.IOUtil;
import org.databene.commons.converter.NoOpConverter;
import org.databene.document.xls.XLSLineIterator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.DefaultDescriptorProvider;
import org.databene.model.data.Entity;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.PrimitiveDescriptorProvider;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.platform.array.Array2EntityConverter;
import org.databene.webdecs.DataContainer;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.ThreadLocalDataContainer;

/**
 * Iterates an Excel sheet and maps its rows to {@link Entity} instances.<br/>
 * <br/>
 * Created at 27.01.2009 21:38:31
 * @since 0.5.8
 * @author Volker Bergmann
 */

public class XLSEntityIterator implements DataIterator<Entity> {

	private String uri;
	
	private HSSFWorkbook workbook;

	private int sheetNo;
	
	private Converter<String, ?> preprocessor;
	
	private DataIterator<Entity> source;
	
	// constructors ----------------------------------------------------------------------------------------------------

	public XLSEntityIterator(String uri) throws IOException {
		this(uri, new NoOpConverter<String>());
	}

	public XLSEntityIterator(String uri, Converter<String, ?> preprocessor) 
			throws IOException {
		this.uri = uri;
		this.preprocessor = preprocessor;
		this.workbook = new HSSFWorkbook(IOUtil.getInputStreamForURI(uri));
		this.sheetNo = -1;
	}

	// DataSource interface implementation -----------------------------------------------------------------------------

	public Class<Entity> getType() {
		return Entity.class;
	}
	
	public synchronized DataContainer<Entity> next(DataContainer<Entity> container) {
		if (sheetNo == -1)
			nextSheet();
		DataContainer<Entity> result;
		do {
			if (source == null)
				return null;
			result = source.next(container);
			if (result == null)
				nextSheet();
		} while (source != null && result == null);
		return result;
	}

	public synchronized void close() {
		IOUtil.close(source);
	}
	
	// convenience methods ---------------------------------------------------------------------------------------------

	public static List<Entity> parseAll(String uri, Converter<String, ?> preprocessor) 
			throws IOException {
    	List<Entity> list = new ArrayList<Entity>();
    	XLSEntityIterator iterator = new XLSEntityIterator(uri, preprocessor);
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
    	if (sheetNo < workbook.getNumberOfSheets() - 1) {
    		if (source != null)
    			IOUtil.close(source);
			this.sheetNo++;
			source = createSheetIterator(
				workbook.getSheetAt(sheetNo), workbook.getSheetName(sheetNo), preprocessor, uri);
    	} else
    		source = null;
    }

	private static DataIterator<Entity> createSheetIterator(
			HSSFSheet sheet, String sheetName, Converter<String, ?> preprocessor, String uri) {
		return new SheetIterator(sheet, sheetName, preprocessor, uri);
    }
	
	static class SheetIterator implements DataIterator<Entity> {
		
	    private DataModel dataModel = DataModel.getDefaultInstance();

	    private String defaultProviderId;
	    private ComplexTypeDescriptor complexTypeDescriptor = null;
	    DataIterator<Object[]> source;
	    Converter<Object[], Entity> converter;
	    Object[] buffer;
	    ThreadLocalDataContainer<Object[]> sourceContainer = new ThreadLocalDataContainer<Object[]>();
		
		public SheetIterator(HSSFSheet sheet, String complexTypeName, Converter<String, ?> preprocessor, String defaultProviderId) {
	        this.source = new XLSLineIterator(sheet, true, preprocessor);
	        this.defaultProviderId = defaultProviderId;
	        init(complexTypeName);
        }
		
		public Class<Entity> getType() {
			return Entity.class;
		}
		
		public DataContainer<Entity> next(DataContainer<Entity> container) {
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
		
		public void close() {
			source.close();
		}
		
		private void init(String complexTypeName) {
			buffer = source.next(new DataContainer<Object[]>()).getData();
			String headers[] = ((XLSLineIterator) source).getHeaders();
		    createComplexTypeDescriptor(complexTypeName, headers, buffer);
		    converter = new Array2EntityConverter(complexTypeDescriptor, headers, false);
        }

		protected void createComplexTypeDescriptor(String complexTypeName, String[] headers, Object[] feed) {
			complexTypeDescriptor = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(complexTypeName);
		    if (complexTypeDescriptor == null) {
		    	complexTypeDescriptor = new ComplexTypeDescriptor(complexTypeName);
		    	for (int i = 0; i < headers.length; i++) {
		    		String header = headers[i];
		    		Object value = feed[i];
					SimpleTypeDescriptor componentType = (value != null ?
		    			PrimitiveDescriptorProvider.INSTANCE.getPrimitiveTypeDescriptor(value.getClass()) :
		    			null);
		    		ComponentDescriptor component = new PartDescriptor(header, componentType);
		    		complexTypeDescriptor.addComponent(component);
		    	}
		    	DefaultDescriptorProvider provider = (DefaultDescriptorProvider) dataModel.getDescriptorProvider(defaultProviderId);
		    	if (provider == null) {
		    		provider = new DefaultDescriptorProvider(defaultProviderId);
		    		dataModel.addDescriptorProvider(provider);
		    	}
		    	provider.addDescriptor(complexTypeDescriptor);
		    }
		}

	}

}
