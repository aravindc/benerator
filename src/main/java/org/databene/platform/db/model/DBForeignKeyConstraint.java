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

import org.databene.commons.ArrayUtil;

import java.util.List;

/**
 * Created: 06.01.2007 09:00:59
 */
public class DBForeignKeyConstraint extends DBConstraint {

    private List<DBForeignKeyColumn> foreignKeyColumns;

    public DBForeignKeyConstraint(String name, DBForeignKeyColumn ... foreignKeyColumns) {
        super(name);
        this.foreignKeyColumns = ArrayUtil.toList(foreignKeyColumns);
    }

    public List<DBForeignKeyColumn> getForeignKeyColumns() {
        return foreignKeyColumns;
    }

    public void addForeignKeyColumn(DBForeignKeyColumn foreignKeyColumn) {
        this.foreignKeyColumns.add(foreignKeyColumn);
    }

    public DBTable getOwner() {
        return foreignKeyColumns.get(0).getForeignKeyColumn().getTable();
    }

    public DBColumn[] getColumns() {
        DBColumn[] columns = new DBColumn[foreignKeyColumns.size()];
        for (int i = 0; i < foreignKeyColumns.size(); i++)
            columns[i] = foreignKeyColumns.get(i).getForeignKeyColumn();
        return columns;
    }

}
