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

package org.databene.platform.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.databene.commons.HeavyweightIterator;
import org.databene.commons.IOUtil;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Entity;

/**
 * Iterates a ResultSet returning Entity objects.
 * 
 * @author Volker Bergmann
 * |since 0.3.04
 */
public class ResultSetEntityIterator implements HeavyweightIterator<Entity> {

    private HeavyweightIterator<ResultSet> resultSetIterator;
    
    private ComplexTypeDescriptor descriptor;

    public ResultSetEntityIterator(HeavyweightIterator<ResultSet> resultSetIterator, ComplexTypeDescriptor descriptor) {
        this.resultSetIterator = resultSetIterator;
        this.descriptor = descriptor;
    }

    public boolean hasNext() {
        return resultSetIterator.hasNext();
    }
    
    public Entity next() {
        if (!hasNext())
            throw new IllegalStateException("No more row available. Check 'hasNext()' before calling next()!");
        try {
    	    ResultSet resultSet = resultSetIterator.next();
            return ResultSet2EntityConverter.convert(resultSet, descriptor);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

	public void close() {
		IOUtil.close(resultSetIterator);
	}

}
