/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.script;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.databene.commons.BeanUtil;

/**
 * Provides information how types can be combined in arithmetic operations.<br/>
 * <br/>
 * Created at 06.10.2009 09:49:53
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TypeManager { // TODO make use of ObjectTypeComparator

	private static Map<Class<?>, Integer> complexities;
	
	static {
		complexities = new HashMap<Class<?>, Integer>();
		addTypes(
			boolean.class, Boolean.class, 
			char.class, Character.class,
			byte.class, Byte.class,
			short.class, Short.class,
			int.class, Integer.class,
			long.class, Long.class,
			BigInteger.class,
			float.class, Float.class,
			double.class, Double.class,
			BigDecimal.class,
			Time.class,
			Date.class,
			Timestamp.class,
			String.class
		);
	}
	
	public static Class<?> combinedType(Class<?> type1, Class<?> type2) {
		int complexity1 = complexityOf(type1);
		int complexity2 = complexityOf(type2);
		Class<?> result = (complexity1 >= complexity2 ? type1 : type2);
		String className = result.getClass().getName();
		if (BeanUtil.isSimpleType(className))
			result = BeanUtil.getWrapper(className);
		return result;
	}

    private static void addTypes(Class<?> ... types) {
	    for (Class<?> type : types)
	    	complexities.put(type, complexities.size());
    }

	private static int complexityOf(Class<?> type) {
	    Integer result = complexities.get(type);
	    return (result != null ? result.intValue() : null);
    }
	
}
