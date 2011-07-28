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

package org.databene.benerator.distribution;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;

/**
 * Abstract implementation of the {@link WeightFunction} interface.<br/>
 * <br/>
 * Created at 30.06.2009 07:13:49
 * @since 0.6.0
 * @author Volker Bergmann
 */

public abstract class AbstractWeightFunction implements WeightFunction {

    @SuppressWarnings("unchecked")
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
    		Class<T> numberType, T min, T max, T precision, boolean unique) {
    	if (Long.class.equals(numberType))
    		return (NonNullGenerator<T>) createLongGenerator(min, max, precision);
    	else if (Double.class.equals(numberType))
    		return (NonNullGenerator<T>) createDoubleGenerator(min, max, precision);
    	else if (BeanUtil.isIntegralNumberType(numberType))
    		return WrapperFactory.wrapNonNullNumberGenerator(numberType, createLongGenerator(min, max, precision), min, precision);
    	else
    		return WrapperFactory.wrapNonNullNumberGenerator(numberType, createDoubleGenerator(min, max, precision), min, precision);
    }

    public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	    return new DistributingSampleGeneratorProxy<T>(source, this, unique);
    }
    
    // helper methods --------------------------------------------------------------------------------------------------

	private <T extends Number> WeightedLongGenerator createLongGenerator(T min, T max, T precision) {
	    return new WeightedLongGenerator(min.longValue(), max.longValue(), precision.longValue(), this);
    }

	private <T extends Number> NonNullGenerator<Double> createDoubleGenerator(T min, T max, T precision) {
	    return new WeightedDoubleGenerator(min.doubleValue(), max.doubleValue(), precision.doubleValue(), this);
    }

}
