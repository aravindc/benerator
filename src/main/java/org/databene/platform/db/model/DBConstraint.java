/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.platform.db.model;

import org.databene.commons.ArrayFormat;
import org.databene.commons.bean.ArrayPropertyExtractor;

import java.util.Arrays;

/**
 * Parent class for all database constraints.<br/><br/>
 * Created: 06.01.2007 08:58:49
 * @author Volker Bergmann
 */
public abstract class DBConstraint {

    protected String name;

    // interface -------------------------------------------------------------------------------------------------------

    /**
     * @param name the constraint name - it may be null
     */
    public DBConstraint(String name) {
        this.name = name;
    }

    /**
     * Returns the constraint name
     * @return the constraint name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the table which owns this constraint
     * @return the table which owns this constraint
     */
    public abstract DBTable getOwner();

    /**
     * Returns the columns which constitute this constraint
     * @return the columns which constitute this constraint
     */
    public abstract DBColumn[] getColumns();

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBConstraint that = (DBConstraint) o;
        return (this.getOwner().equals(that.getOwner()) 
        		&& Arrays.equals(this.getColumns(), that.getColumns()));
    }

    @Override
    public int hashCode() {
        return this.getOwner().hashCode() * 29 + Arrays.hashCode(getColumns());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append('[').append(getOwner().getName()).append('[');
        String[] columnNames = ArrayPropertyExtractor.convert(getColumns(), "name", String.class);
        builder.append(ArrayFormat.format(columnNames));
        builder.append("]]");
        return builder.toString();
    }
}
