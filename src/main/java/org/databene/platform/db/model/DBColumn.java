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

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a database column.<br/><br/>
 * Created: 06.01.2007 08:58:49
 * @author Volker Bergmann
 */
public class DBColumn {

    private String name;
    private DBColumnType type;
    private int[] modifiers; // TODO v0.4.2 transform to 'size' and 'scale' attributes
    private String doc;
    private String defaultValue;
    private DBTable table;
    private boolean versionColumn;

    private List<DBConstraint> ukConstraints; // constraints may be unnamed, so a Map does not make sense
    private DBConstraint notNullConstraint;
//    private DBForeignKeyConstraint fkConstraint;

    // constructors ----------------------------------------------------------------------------------------------------

    public DBColumn() {
        this(null, null);
    }

    public DBColumn(String name, DBColumnType type, int ... modifiers) {
        this(null, name, type, modifiers);
    }

    public DBColumn(DBTable table, String name, DBColumnType type, int ... modifiers) {
        this.table = table;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.doc = null;
        this.defaultValue = null;
        this.ukConstraints = new ArrayList<DBConstraint>();
        this.notNullConstraint = null;
//        this.fkConstraint = null;
        this.versionColumn = false;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public DBTable getTable() {
        return table;
    }

    public void setTable(DBTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public DBColumnType getType() {
        return type;
    }

    public int[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(int[] modifiers) {
        this.modifiers = modifiers;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<DBConstraint> getUkConstraints() {
        return ukConstraints;
    }

    public void addUkConstraint(DBConstraint constraint) {
        this.ukConstraints.add(constraint);
    }

    public DBConstraint getNotNullConstraint() {
        return notNullConstraint;
    }

    public void setNotNullConstraint(DBConstraint notNullConstraint) {
        this.notNullConstraint = notNullConstraint;
    }

    public boolean isNullable() {
        return (notNullConstraint == null);
    }

    public void setNullable(boolean nullable) {
        if (nullable) {
            // if a NotNullConstraint exists then remove it
            notNullConstraint = null;
        } else {
            // if there needs to be a NotNullConstraint, check if there exists one, first
            if (this.isNullable()) {
                this.notNullConstraint = new DBNotNullConstraint(this);
            }
        }
    }
/*
    public DBForeignKeyConstraint getFkConstraint() {
        return fkConstraint;
    }
*/

    public boolean isVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(boolean versionColumn) {
        this.versionColumn = versionColumn;
    }

    public int size() {
        if (modifiers != null && modifiers.length > 0)
            return modifiers[0]; // TODO v0.4.2 evaluate if byte or char
        return 1;
    }

    // java.lang.overrides ---------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBColumn that = (DBColumn) o;
        return this.table.equals(that.table) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return table.hashCode() * 29 + name.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name).append(" : ").append(type);
        if (modifiers.length > 0) {
            builder.append('(');
            builder.append(ArrayFormat.formatInts(",", modifiers));
            builder.append(')');
        }
        if (!isNullable())
            builder.append(" NOT NULL");
        return builder.toString();
    }

    // static convenience methods --------------------------------------------------------------------------------------

    public static String formatColumnNames(DBColumn[] columns) {
        StringBuilder builder = new StringBuilder(columns[0].getName());
        for (int i = 1; i < columns.length; i++)
            builder.append(", ").append(columns[i].getName());
        return builder.toString();
    }

    public static String formatColumnNames(List<DBColumn> columns) {
        StringBuilder builder = new StringBuilder(columns.get(0).getName());
        for (int i = 1; i < columns.size(); i++)
            builder.append(", ").append(columns.get(i).getName());
        return builder.toString();
    }

}
