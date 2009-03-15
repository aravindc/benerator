/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.model.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a primitive benerator type.<br/>
 * <br/>
 * Created: 27.02.2008 16:28:22
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class PrimitiveType {
    
    private static final Map<String, PrimitiveType> instances = new HashMap<String, PrimitiveType>();
    
    public static final PrimitiveType BYTE        = new PrimitiveType("byte", Byte.class);
    public static final PrimitiveType SHORT       = new PrimitiveType("short", Short.class);
    public static final PrimitiveType INT         = new PrimitiveType("int", Integer.class);
    public static final PrimitiveType LONG        = new PrimitiveType("long", Long.class);
    public static final PrimitiveType BIG_INTEGER = new PrimitiveType("big_integer", BigInteger.class);
    public static final PrimitiveType FLOAT       = new PrimitiveType("float", Float.class);
    public static final PrimitiveType DOUBLE      = new PrimitiveType("double", Double.class);
    public static final PrimitiveType BIG_DECIMAL = new PrimitiveType("big_decimal", BigDecimal.class);
    public static final PrimitiveType BOOLEAN     = new PrimitiveType("boolean", Boolean.class);
    public static final PrimitiveType STRING      = new PrimitiveType("string", String.class);
    public static final PrimitiveType DATE        = new PrimitiveType("date", Date.class);
    public static final PrimitiveType TIME        = new PrimitiveType("time", Time.class);
    public static final PrimitiveType TIMESTAMP   = new PrimitiveType("timestamp", Timestamp.class);
    public static final PrimitiveType OBJECT      = new PrimitiveType("object", Object.class);
    public static final PrimitiveType BINARY      = new PrimitiveType("binary", byte[].class);
    
    private String   name;
    private Class<?> javaType;
    
    public PrimitiveType(String name, Class<?> javaType) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (javaType == null)
            throw new IllegalArgumentException("javaType is null");
        this.name = name;
        this.javaType = javaType;
        instances.put(name, this);
    }
    
    public String getName() {
        return name;
    }
    
    public Class<?> getJavaType() {
        return javaType;
    }

    public static PrimitiveType getInstance(String parentName) {
        return instances.get(parentName);
    }

    public static Collection<PrimitiveType> getInstances() {
        return instances.values();
    }

}