/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.wrapper.SkipGeneratorProxy;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.MathUtil;
import org.databene.commons.converter.NumberToNumberConverter;

import static org.databene.commons.NumberUtil.*;

/**
 * Random Walk {@link Sequence} implementation that supports a variable step width.<br/>
 * <br/>
 * Created at 30.06.2009 07:48:40
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomWalkSequence extends Sequence {
	
	private static final boolean DEFAULT_BUFFERED = false;
	
	private BigDecimal initial;
	private BigDecimal minStep;
	private BigDecimal maxStep;
	private boolean buffered;
	
	// constructors ----------------------------------------------------------------------------------------------------

    public RandomWalkSequence() {
	    this(BigDecimal.ONE, BigDecimal.ONE);
    }

    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep) {
	    this(minStep, maxStep, null);
    }
    
    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep, BigDecimal initial) {
	    this(minStep, maxStep, initial, DEFAULT_BUFFERED);
    }
    
    public RandomWalkSequence(BigDecimal minStep, BigDecimal maxStep, BigDecimal initial, boolean buffered) {
	    super("randomWalk");
	    this.minStep = minStep;
	    this.maxStep = maxStep;
	    this.initial = initial;
	    this.buffered = buffered;
    }
    
    // Distribution interface implementation ---------------------------------------------------------------------------

    public <T extends Number> Generator<T> createGenerator(Class<T> numberType, T min, T max, T precision, boolean unique) {
		Generator<? extends Number> base;
		if (BeanUtil.isIntegralNumberType(numberType))
			base = createLongGenerator(toLong(min), toLong(max), toLong(precision), unique);
		else
			base = createDoubleGenerator(toDouble(min), toDouble(max), toDouble(precision), unique);
		return WrapperFactory.wrapNumberGenerator(numberType, base);
    }
    
    @Override
    public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
        if (buffered || MathUtil.between(0L, toLong(minStep), toLong(maxStep)))
        	return super.applyTo(source, unique);
        else
	        return applySkipGenerator(source, unique);
    }

	private <T> Generator<T> applySkipGenerator(Generator<T> source, boolean unique) {
		long minStepL = toLong(minStep);
		if (unique && minStepL <= 0)
			throw new ConfigurationError("Cannot generate unique values when minStep=" + minStep);
	    return new SkipGeneratorProxy<T>(source, minStepL, toLong(maxStep));
    }
    
    // helper methods --------------------------------------------------------------------------------------------------

	private <T> Generator<? extends Number> createDoubleGenerator(double min, double max, double precision, boolean unique) {
	    if (unique && MathUtil.rangeIncludes(0., min, max)) // check if uniqueness requirements can be met
	    	throw new InvalidGeneratorSetupException("Cannot guarantee uniqueness for [min=" + min + ",max=" + max + "]");
	    return new RandomWalkDoubleGenerator(
	    		toDouble(min), toDouble(max), toDouble(precision), toDouble(minStep), toDouble(maxStep));
    }

	private <T> Generator<? extends Number> createLongGenerator(long min, long max, long precision, boolean unique) {
	    if (unique && MathUtil.rangeIncludes(0, min, max)) // check if uniqueness requirements can be met
	    	throw new InvalidGeneratorSetupException("Cannot guarantee uniqueness for [min=" + min + ",max=" + max + "]");
	    return new RandomWalkLongGenerator(
	    		min, max, toLong(precision), toLong(initial(min, max, Long.class)), toLong(minStep), toLong(maxStep));
    }

    private <T extends Number> T initial(T min, T max, Class<T> numberType) {
    	if (initial != null)
    		return NumberToNumberConverter.convert(initial, numberType);
    	if (minStep.doubleValue() > 0)
    		return min;
		if (maxStep.doubleValue() > 0)
			return NumberToNumberConverter.convert((min.doubleValue() + max.doubleValue()) / 2, numberType);
		else
			return max;
    }

}
