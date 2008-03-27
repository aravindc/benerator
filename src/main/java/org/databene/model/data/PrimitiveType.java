/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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
public class PrimitiveType<E> {
    
    private static final Map<String, PrimitiveType<? extends Object>> instances = new HashMap<String, PrimitiveType<? extends Object>>();
    
    public static final PrimitiveType<Byte>       BYTE        = new PrimitiveType<Byte>("byte", Byte.class);
    public static final PrimitiveType<Short>      SHORT       = new PrimitiveType<Short>("short", Short.class);
    public static final PrimitiveType<Integer>    INT         = new PrimitiveType<Integer>("int", Integer.class);
    public static final PrimitiveType<Long>       LONG        = new PrimitiveType<Long>("long", Long.class);
    public static final PrimitiveType<BigInteger> BIG_INTEGER = new PrimitiveType<BigInteger>("big_integer", BigInteger.class);
    public static final PrimitiveType<Float>      FLOAT       = new PrimitiveType<Float>("float", Float.class);
    public static final PrimitiveType<Double>     DOUBLE      = new PrimitiveType<Double>("double", Double.class);
    public static final PrimitiveType<BigDecimal> BIG_DECIMAL = new PrimitiveType<BigDecimal>("big_decimal", BigDecimal.class);
    public static final PrimitiveType<Boolean>    BOOLEAN     = new PrimitiveType<Boolean>("boolean", Boolean.class);
    public static final PrimitiveType<String>     STRING      = new PrimitiveType<String>("string", String.class);
    public static final PrimitiveType<Date>       DATE        = new PrimitiveType<Date>("date", Date.class);
    public static final PrimitiveType<Time>       TIME        = new PrimitiveType<Time>("time", Time.class);
    public static final PrimitiveType<Timestamp>  TIMESTAMP   = new PrimitiveType<Timestamp>("timestamp", Timestamp.class);
    public static final PrimitiveType<Object>     OBJECT      = new PrimitiveType<Object>("object", Object.class);
    public static final PrimitiveType<byte[]>     BINARY      = new PrimitiveType<byte[]>("binary", byte[].class);
    
    private String   name;
    private Class<E> javaType;
    
    public PrimitiveType(String name, Class<E> javaType) {
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
    
    public Class<E> getJavaType() {
        return javaType;
    }

    public static <T> PrimitiveType<T> getInstance(String parentName) {
        return (PrimitiveType<T>) instances.get(parentName);
    }

    public static Collection<PrimitiveType<? extends Object>> getInstances() {
        return instances.values();
    }

}