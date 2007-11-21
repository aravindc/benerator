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

package org.databene.platform.db.model.jdbc;

import java.util.List;
import java.util.ArrayList;

/**
 * Created: 13.01.2007 23:40:52
 */
class DBIndexInfo {
    public String name;
    public boolean unique;
    public String catalogName;
    public short type;
    /* TODO v0.4
         * tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
         * tableIndexClustered - this is a clustered index
         * tableIndexHashed - this is a hashed index
         * tableIndexOther - this is some other style of index
         */
    public Boolean ascending;
    public int cardinality;
    public int pages;
    public String filterCondition;

    public List<String> columnNames;

    public DBIndexInfo(String name, short type, String catalogName, boolean unique, short ordinalPosition, String columnName, Boolean ascending, int cardinality, int pages, String filterCondition) {
        this.name = name;
        this.unique = unique;
        this.catalogName = catalogName;
        this.type = type;
        this.ascending = ascending;
        this.cardinality = cardinality;
        this.pages = pages;
        this.filterCondition = filterCondition;
        this.columnNames = new ArrayList<String>();
        if (ordinalPosition != 1)
            throw new IllegalArgumentException("ordinalPosition is expected to be 1, found: " + ordinalPosition);
        columnNames.add(columnName);
    }

    public void addColumn(short ordinalPosition, String columnName) {
        int expectedPosition = columnNames.size() + 1;
        if (ordinalPosition != expectedPosition)
            throw new IllegalArgumentException("ordinalPosition is expected to be " + expectedPosition + ", " +
                    "found: " + ordinalPosition);
        columnNames.add(columnName);
    }
}
