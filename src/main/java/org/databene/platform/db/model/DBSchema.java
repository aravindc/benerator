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

import java.util.List;

import org.databene.commons.collection.OrderedNameMap;

/**
 * Created: 06.01.2007 08:57:57
 */
public class DBSchema {

    private Database database;
    private String name;
    private DBCatalog catalog;
    private OrderedNameMap<DBTable> tables;

    // constructors ----------------------------------------------------------------------------------------------------

    public DBSchema() {
        this(null);
    }

    public DBSchema(String name) {
        this(null, name);
    }

    public DBSchema(Database database, String name) {
        this.name = name;
        this.database = database;
        this.catalog = null;
        this.tables = new OrderedNameMap<DBTable>();
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

    // catalog operations ----------------------------------------------------------------------------------------------

    public DBCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(DBCatalog catalog) {
        this.catalog = catalog;
    }

    // table operations ------------------------------------------------------------------------------------------------

    public List<DBTable> getTables() {
        return tables.values();
    }

    public DBTable getTable(String tableName) {
        return tables.get(tableName.toUpperCase());
    }

    public void addTable(DBTable table) {
        tables.put(table.getName().toUpperCase(), table);
    }

    public void removeTable(DBTable table) {
        tables.remove(table.getName());
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBSchema that = (DBSchema) o;
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
