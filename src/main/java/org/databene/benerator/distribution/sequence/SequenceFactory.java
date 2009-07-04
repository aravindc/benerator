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

package org.databene.benerator.distribution.sequence;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.commons.ArrayFormat;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ExceptionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.converter.NumberConverter;

/**
 * Creates Generators for {@link Sequence}s.<br/>
 * <br/>
 * Created: 27.03.2008 13:00:35
 * @author Volker Bergmann
 */
public class SequenceFactory {
	
	@SuppressWarnings("unchecked")
    private static final Class[] INTEGRAL_NUMBER_TYPE_ORDER = {
		Long.class, Integer.class, Short.class, Byte.class, BigInteger.class, 
		Double.class, Float.class, BigDecimal.class
	};
	
	@SuppressWarnings("unchecked")
    private static final Class[] DECIMAL_NUMBER_TYPE_ORDER = {
		Double.class, Float.class, BigDecimal.class, 
		Long.class, Integer.class, Short.class, Byte.class, BigInteger.class
	};
	
	private static ClassProvider classProvider;
	
    public static void setClassProvider(ClassProvider classProvider) {
    	SequenceFactory.classProvider = classProvider;
    }

	public static <T extends Number> Generator<T> createGenerator(
    		String sequenceName, Class<T> numberType, Object... parameters) {
    	try {
    		return createExactSequence(sequenceName, numberType, parameters);
    	} catch (Exception e) {
    		try {
    			return createAdaptedSequence(sequenceName, numberType, parameters);
    		} catch (RuntimeException e2) {
    			if (!(ExceptionUtil.getRootCause(e2) instanceof ClassNotFoundException))
    				throw e2;
    			else
    				throw new ConfigurationError("No appropriate number generator found for sequence: " 
    					+ sequenceName + ", " + numberType.getName() + ", " + ArrayFormat.format(parameters));
    		}
    	}
    }
	
	// internal helper methods -----------------------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    protected static <T extends Number> Generator<T> createAdaptedSequence(
    		String sequenceName, Class<T> numberType, Object... parameters) {
    	Generator<? extends Number> source = createSourceGenerator(sequenceName, numberType, parameters);
    	return new ConvertingGenerator(source, new NumberConverter<T>(numberType));
    }
    
    @SuppressWarnings("unchecked")
    protected static <T extends Number> Generator<? extends Number> createSourceGenerator(
    		String sequenceName, Class<T> numberType, Object... parameters) {
    	String className = numberType.getName();
		if (BeanUtil.isIntegralNumber(className))
    		return createSourceGenerator(sequenceName, parameters, INTEGRAL_NUMBER_TYPE_ORDER);
    	else
    		return createSourceGenerator(sequenceName, parameters, DECIMAL_NUMBER_TYPE_ORDER);
    }
    
    protected static Generator<? extends Number> createSourceGenerator(
    		String sequenceName, Object[] parameters, Class<? extends Number>[] typeOrder) {
    	for (Class<? extends Number> type : typeOrder) {
    		try {
    			return createExactSequence(sequenceName, type, parameters);
    		} catch (RuntimeException e) {
    			if (!(ExceptionUtil.getRootCause(e) instanceof ClassNotFoundException))
    				throw e;
    		}
    	}
    	throw new ConfigurationError("No Generator found for sequence " + sequenceName);
    }
    
    @SuppressWarnings("unchecked")
    protected static <T extends Number> Generator<T> createExactSequence(
    		String sequenceName, Class<T> numberType, Object... parameters) {
    	String className = StringUtil.capitalize(sequenceName) + numberType.getSimpleName() + "Generator";
    	return (Generator<T>) BeanUtil.newInstance(classProvider.forName(className), false, parameters);
    }
    
}
