/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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
 * Created: 06.01.2007 08:58:49
 */
public abstract class DBConstraint {

    protected String name;

    /**
     * @param name the constraint name - it may be null
     */
    public DBConstraint(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract DBTable getOwner();

    public abstract DBColumn[] getColumns();


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBConstraint that = (DBConstraint) o;
        return (this.getOwner().equals(that.getOwner()) && Arrays.equals(getColumns(), that.getColumns()));
    }

    public int hashCode() {
        return this.getOwner().hashCode() * 29 + Arrays.hashCode(getColumns());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append('[').append(getOwner().getName()).append('[');
        String[] columnNames = ArrayPropertyExtractor.convert(getColumns(), "name", String.class);
        builder.append(ArrayFormat.format(columnNames));
        builder.append("]]");
        return builder.toString();
    }
}
