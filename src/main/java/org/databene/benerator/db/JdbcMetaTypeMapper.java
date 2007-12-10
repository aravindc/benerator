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

package org.databene.benerator.db;

import java.sql.Types;
import java.util.Map;

import org.databene.commons.CollectionUtil;
import org.databene.commons.ConfigurationError;
import org.databene.platform.db.model.DBColumnType;

/**
 * Maps JDBC types to benerator types.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class JdbcMetaTypeMapper {

    private static final Map TYPE_MAP;

    static {

        TYPE_MAP = CollectionUtil.buildMap(
                // TODO v0.4 handle missing SQL types
                //Types.ARRAY, "",
                Types.BIGINT, "big_integer",
                Types.BINARY, "binary",
                Types.BIT, "byte",
                Types.BLOB, "binary",
                Types.BOOLEAN, "boolean",
                Types.CHAR, "string",
                Types.CLOB, "string",
                //Types.DATALINK, "",
                Types.DATE, "date",
                Types.DECIMAL, "big_decimal",
                //Types.DISTINCT, "",
                Types.DOUBLE, "double",
                Types.FLOAT, "float",
                Types.INTEGER, "int",
                Types.JAVA_OBJECT, "object",
                Types.LONGVARBINARY, "binary",
                Types.LONGVARCHAR, "string",
                //Types.NULL, "",
                Types.NUMERIC, "double",
                Types.REAL, "double",
                //Types.REF, "",
                Types.SMALLINT, "short",
                //Types.STRUCT, "",
                Types.TIME, "date",
                Types.TIMESTAMP, "timestamp",
                Types.TINYINT, "byte",
                Types.VARBINARY, "binary",
                Types.VARCHAR, "string");
    }

    public static String abstractType(DBColumnType columnType) {
        int jdbcType = columnType.getJdbcType();
        String result = (String) TYPE_MAP.get(jdbcType);
        if (result == null) {
            String lcName = columnType.getName().toLowerCase();
            if (lcName.startsWith("timestamp"))
                return "timestamp";
            else if (lcName.startsWith("xml"))
                return "string";
            else
                throw new ConfigurationError("Platform specific SQL type (" + jdbcType + ") not mapped: " + jdbcType);
        }
        return result;
    }

}
