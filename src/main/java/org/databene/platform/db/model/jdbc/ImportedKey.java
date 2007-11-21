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

import org.databene.platform.db.model.*;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created: 13.01.2007 23:22:55
 */
class ImportedKey {

    /** primary key table catalog being imported (may be null) */
    public String PK_TABLE_CAT;

    /** primary key table schema being imported (may be null) */
    public String PKTABLE_SCHEM;

    /** primary key table name being imported */
    public String PKTABLE_NAME;

    /** primary key column name being imported */
    public String PKCOLUMN_NAME;

    /** foreign key table catalog (may be null) */
    public String FKTABLE_CAT;

    /** foreign key table schema (may be null) */
    public String FKTABLE_SCHEM;

    /** foreign key table name */
    public String FKTABLE_NAME;

    /** foreign key column name */
    public String FKCOLUMN_NAME;

    /** sequence number within a foreign key */
    public short KEY_SEQ;

    /**
     * What happens to a foreign key when the primary key is updated:
     * <UL>
     *   <LI>importedNoAction - do not allow update of primary key if it has been imported</LI>
     *   <LI>importedKeyCascade - change imported key to agree with primary key update</LI>
     *   <LI>importedKeySetNull - change imported key to NULL if its primary key has been updated</LI>
     *   <LI>importedKeySetDefault - change imported key to default values if its primary key has been updated</LI>
     *   <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)</LI>
     * </UL>
     */
     public short UPDATE_RULE;

    /**
     * What happens to the foreign key when primary is deleted.
     * <UL>
     *   <LI>importedKeyNoAction - do not allow delete of primary key if it has been imported</LI>
     *   <LI>importedKeyCascade - delete rows that import a deleted key</LI>
     *   <LI>importedKeySetNull - change imported key to NULL if its primary key has been deleted</LI>
     *   <LI>importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)</LI>
     *   <LI>importedKeySetDefault - change imported key to default if its primary key has been deleted</LI>
     * </UL>
     */
    public short DELETE_RULE;

    /** foreign key name (may be null) */
    public String FK_NAME;

    /** primary key name (may be null) */
    public String PK_NAME;

    /**
     * can the evaluation of foreign key constraints be deferred until commit
     * <UL>
     *   <LI>importedKeyInitiallyDeferred - see SQL92 for definition</LI>
     *   <LI>importedKeyInitiallyImmediate - see SQL92 for definition</LI>
     *   <LI>importedKeyNotDeferrable - see SQL92 for definition</LI>
     * </UL>
     */
    public short DEFERRABILITY;

    private List<DBForeignKeyColumn> foreignKeyColumns = new ArrayList<DBForeignKeyColumn>();

    public void addForeignKeyColumn(DBColumn foreignKeyColumn, DBColumn targetColumn) {
        foreignKeyColumns.add(new DBForeignKeyColumn(foreignKeyColumn,  targetColumn));
    }

    public List<DBForeignKeyColumn> getForeignKeyColumns() {
        return foreignKeyColumns;
    }

    public static ImportedKey parse(ResultSet resultSet, DBCatalog catalog, DBSchema schema, DBTable fkTable) throws SQLException {
        ImportedKey key = new ImportedKey();
        key.PK_TABLE_CAT = resultSet.getString(1);
        key.PKTABLE_SCHEM = resultSet.getString(2);
        key.PKTABLE_NAME = resultSet.getString(3);
        key.PKCOLUMN_NAME = resultSet.getString(4);
        key.FKTABLE_CAT = resultSet.getString(5);
        key.FKTABLE_SCHEM = resultSet.getString(6);
        key.FKTABLE_NAME = resultSet.getString(7);
        assert key.FKTABLE_NAME.equals(fkTable.getName());
        key.FKCOLUMN_NAME = resultSet.getString(8);
        key.KEY_SEQ = resultSet.getShort(9);
        key.UPDATE_RULE = resultSet.getShort(10);
        key.DELETE_RULE = resultSet.getShort(11);
        key.FK_NAME = resultSet.getString(12);
        key.PK_NAME = resultSet.getString(13);
        key.DEFERRABILITY = resultSet.getShort(14);
        DBColumn fkColumn = fkTable.getColumn(key.FKCOLUMN_NAME);
        DBTable pkTable = null;
        if (catalog != null)
            pkTable = catalog.getTable(key.PKTABLE_NAME);
        else
            pkTable = schema.getTable(key.PKTABLE_NAME);    
        DBColumn pkColumn = pkTable.getColumn(key.PKCOLUMN_NAME);
        key.addForeignKeyColumn(fkColumn, pkColumn);
        return key;
    }
}
