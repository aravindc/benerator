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

import java.sql.Types;

/**
 * Created: 06.01.2007 10:12:29
 */
public class DBColumnType {
/*
    TODO v0.4 manage types
    private static final Map<String, DBColumnType> instances = new HashMap<String, DBColumnType>();

    public static final DBColumnType VARCHAR = new DBColumnType("VARCHAR");
    public static final DBColumnType DATETIME = new DBColumnType("DATETIME");
    public static final DBColumnType FLOAT = new DBColumnType("FLOAT");
*/
    public static DBColumnType getInstance(int jdbcType, String name) {
        return new DBColumnType(jdbcType, name.toUpperCase());
    }

    public static DBColumnType getInstance(String name) {
        return new DBColumnType(Integer.MIN_VALUE, name.toUpperCase());
    }

    private String name;
    private int jdbcType;

    // constructors ----------------------------------------------------------------------------------------------------
/*
    private DBColumnType() {
        this(null);
    }
*/
    private DBColumnType(int sqlType, String name) {
        this.jdbcType = sqlType;
        this.name = name.toUpperCase();
//        instances.put(this.name, this);
    }

// properties ------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public boolean isLOB() {
        return "CLOB".equals(name) || "BLOB".equals(name);
    }

    public boolean isAlpha() {
        return jdbcType == Types.VARCHAR || jdbcType == Types.CHAR
                || jdbcType == Types.CLOB || jdbcType == Types.LONGVARCHAR;
    }

// java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final DBColumnType that = (DBColumnType) o;
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
