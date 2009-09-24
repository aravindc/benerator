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

import org.databene.commons.ConfigurationError;
import org.databene.commons.Converter;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.converter.NoOpConverter;
import org.databene.model.data.AbstractEntitySource;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;

/**
 * Implements an {@link EntitySource} that reads Entities from an Excel sheet.<br/>
 * <br/>
 * Created at 27.01.2009 21:31:54
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class XLSEntitySource extends AbstractEntitySource {
	
    private String uri;
    private Converter<String, ?> preprocessor;

    // constructors ----------------------------------------------------------------------------------------------------

    public XLSEntitySource() {
        this(null);
    }

    public XLSEntitySource(String uri) {
        this(uri, new NoOpConverter<String>());
    }

    public XLSEntitySource(String uri, Converter<String, ? extends Object> preprocessor) {
        this.uri = uri;
        this.preprocessor = preprocessor;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    // EntityIterable interface ----------------------------------------------------------------------------------------

    public HeavyweightIterator<Entity> iterator() {
        try {
			return new XLSEntityIterator(uri, preprocessor);
		} catch (IOException e) {
			throw new ConfigurationError("Cannot create iterator. ", e);
		}
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[" + uri + "]";
    }

}
