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

import org.databene.commons.collection.OrderedNameMap;

import java.util.List;

/**
 * Represents a JDBC catalog.<br/><br/>
 * Created: 06.01.2007 08:57:57
 * @author Volker Bergmann
 */
public class DBCatalog {

    private String name;
    private Database database;
    private OrderedNameMap<DBTable> tables;
    private OrderedNameMap<DBIndex> indexes;
    private String doc;

    // constructors ----------------------------------------------------------------------------------------------------

    public DBCatalog() {
        this(null);
    }

    public DBCatalog(String name) {
        this.name = name;
        this.tables = new OrderedNameMap<DBTable>();
        this.indexes = new OrderedNameMap<DBIndex>();
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    // table operations ------------------------------------------------------------------------------------------------

    public List<DBTable> getTables() {
        return tables.values();
    }

    public DBTable getTable(String tableName) {
        return tables.get(tableName.toUpperCase());
    }

    public void addTable(DBTable table) {
        table.setCatalog(this);
        tables.put(table.getName().toUpperCase(), table);
    }

    public void removeTable(DBTable table) {
        tables.remove(table.getName());
    }

    // index operations ------------------------------------------------------------------------------------------------

    public List<DBIndex> getIndexes() {
        return indexes.values();
    }

    public DBIndex getIndex(String indexName) {
        return indexes.get(indexName);
    }

    public void addIndex(DBIndex index) {
        index.setCatalog(this);
        indexes.put(index.getName(), index);
    }

    public void removeIndex(DBIndex index) {
        indexes.remove(index.getName());
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBCatalog that = (DBCatalog) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
