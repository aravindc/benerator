/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.csv;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.DataSourceProvider;
import org.databene.commons.converter.ArrayTypeConverter;
import org.databene.commons.Converter;
import org.databene.document.csv.CSVLineSource;
import org.databene.webdecs.DataSource;
import org.databene.webdecs.util.ConvertingDataSource;
import org.databene.webdecs.util.OffsetDataSource;

/**
 * {@link DataSourceProvider} which creates array {@link Iterable}s for CSV files.<br/><br/>
 * Created: 19.07.2011 08:23:39
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class CSVArraySourceProvider implements DataSourceProvider<Object[]> {
	
	@SuppressWarnings("unused")
	private Converter<String, ?> preprocessor; // TODO v0.7 use preprocessor
	private char separator;
	private String encoding;
	
	public CSVArraySourceProvider(String type, Converter<String, ?> preprocessor, char separator, String encoding) {
	    this.preprocessor = preprocessor;
	    this.separator = separator;
	    this.encoding = encoding;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataSource<Object[]> create(String uri, BeneratorContext context) {
		CSVLineSource source = new CSVLineSource(uri, separator, true, encoding);
		
		Converter<String[], Object[]> converter = new ArrayTypeConverter(Object.class);
		DataSource<Object[]> result = new ConvertingDataSource<String[], Object[]>(source, converter);
		result = new OffsetDataSource<Object[]>(result, 1); // offset = 1 in order to skip header row
		return result;
    }

}
