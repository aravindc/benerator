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

package org.databene.platform.csv;

import org.databene.commons.ConfigurationError;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.TypedIterable;

/**
 * Creates Iterators that iterate through the cells of a CSV file.<br/>
 * <br/>
 * Created: 01.09.2007 11:40:30
 * @author Volker Bergmann
 */
public class CSVCellIterable implements TypedIterable<String> {

    private String uri;
    private char separator;

    public CSVCellIterable() {
        this(null, ',');
    }

    public CSVCellIterable(String uri, char separator) {
        this.uri = uri;
        this.separator = separator;
    }
    
    public void setUri(String uri) {
		this.uri = uri;
	}

	public Class<String> getType() {
        return String.class;
    }

    public HeavyweightIterator<String> iterator() {
        try {
            return new CSVCellIterator(uri, separator);
        } catch (Exception e) {
            throw new ConfigurationError(e);
        }
    }
}
