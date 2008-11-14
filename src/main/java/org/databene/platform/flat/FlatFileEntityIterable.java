/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.flat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.commons.Converter;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;

/**
 * Reads Entities from a flat file.<br/>
 * <br/>
 * Created: 26.08.2007 12:16:08
 * @author Volker Bergmann
 */
public class FlatFileEntityIterable extends FlatFileEntitySource {
	
	private static final Log logger = LogFactory.getLog(FlatFileEntityIterable.class);

	public FlatFileEntityIterable() {
		super();
		logger.warn(getClass().getName() + " is deprecated. " +
				"Use " + FlatFileEntitySource.class.getName() + " instead.");
	}

	public FlatFileEntityIterable(String uri,
			ComplexTypeDescriptor entityDescriptor,
			Converter<String, String> preprocessor, String encoding,
			FlatFileColumnDescriptor... descriptors) {
		super(uri, entityDescriptor, preprocessor, encoding, descriptors);
		logger.warn(getClass().getName() + " is deprecated. " +
				"Use " + FlatFileEntitySource.class.getName() + " instead.");
	}

	public FlatFileEntityIterable(String uri,
			ComplexTypeDescriptor entityDescriptor, String encoding,
			FlatFileColumnDescriptor... descriptors) {
		super(uri, entityDescriptor, encoding, descriptors);
		logger.warn(getClass().getName() + " is deprecated. " +
				"Use " + FlatFileEntitySource.class.getName() + " instead.");
	}
	
}
