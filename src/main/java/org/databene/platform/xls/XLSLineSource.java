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

package org.databene.platform.xls;

import java.io.IOException;
import java.util.Iterator;

import org.databene.commons.Converter;
import org.databene.document.xls.XLSLineIterator;
import org.databene.webdecs.DataIterator;
import org.databene.webdecs.util.AbstractDataSource;

/**
 * {@link Iterable} implementation which creates {@link Iterator}s 
 * that provide lines of XLS files as array objects.<br/><br/>
 * Created: 19.07.2011 08:36:18
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class XLSLineSource extends AbstractDataSource<Object[]> {
	
	String uri;
	Converter<String, ?> preprocessor;
	boolean usingHeaders;

	public XLSLineSource(String uri, boolean usingHeaders, Converter<String, ?> preprocessor) {
		super(Object[].class);
		this.uri = uri;
		this.usingHeaders = usingHeaders;
		this.preprocessor = preprocessor;
	}

	public DataIterator<Object[]> iterator() {
		try {
			return new XLSLineIterator(uri, 0, usingHeaders); // TODO v0.7 use preprocessor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
