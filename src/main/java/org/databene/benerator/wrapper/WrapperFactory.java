/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.factory.GeneratorFactoryUtil;

/**
 * Provides wrappers for number {@link Generator}s that converts 
 * their products to a target {@link Number} type.<br/>
 * <br/>
 * Created at 30.06.2009 10:48:59
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class WrapperFactory {

    public static <T extends Number> NonNullGenerator<T> wrapNonNullNumberGenerator(
    		Class<T> numberType, NonNullGenerator<? extends Number> source, T min, T precision) {
    	return GeneratorFactoryUtil.asNonNullGenerator(wrapNumberGenerator(numberType, source, min, precision));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Number> Generator<T> wrapNumberGenerator(
    		Class<T> numberType, Generator<? extends Number> source, T min, T precision) {
    	if (numberType.equals(source.getGeneratedType()))
    	 	return (Generator<T>) source;
    	if (Integer.class.equals(numberType))
    		return new AsIntegerGeneratorWrapper(source);
    	else if (Long.class.equals(numberType))
    		return new AsLongGeneratorWrapper(source);
    	else if (Short.class.equals(numberType))
    		return new AsShortGeneratorWrapper(source);
    	else if (Byte.class.equals(numberType))
    		return new AsByteGeneratorWrapper(source);
    	else if (Double.class.equals(numberType))
    		return new AsDoubleGeneratorWrapper(source);
    	else if (Float.class.equals(numberType))
    		return new AsFloatGeneratorWrapper(source);
    	else if (BigDecimal.class.equals(numberType))
    		return new AsBigDecimalGeneratorWrapper(source, (BigDecimal) min, (BigDecimal) precision);
    	else if (BigInteger.class.equals(numberType))
    		return new AsBigIntegerGeneratorWrapper(source);
    	else 
    		throw new UnsupportedOperationException("Not a supported number type: " + numberType);
    }
    
}
