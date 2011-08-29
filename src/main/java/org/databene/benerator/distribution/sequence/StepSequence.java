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

package org.databene.benerator.distribution.sequence;

import java.math.BigDecimal;

import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.wrapper.SkipGeneratorProxy;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.NumberUtil;

import static org.databene.commons.NumberUtil.*;

/**
 * Creates numbers by continuously incrementing a base value by a constant amount.<br/>
 * <br/>
 * Created at 30.06.2009 09:55:20
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class StepSequence extends Sequence {

    private BigDecimal delta;
	private BigDecimal initial;
	private BigDecimal limit;
	
	public StepSequence() {
	    this(null); // when using null, the granularity parameter will be used to set the increment in createGenerator
    }
	
	/**
	 * @param delta the increment to choose for created generators. 
	 * 		When using null, the granularity parameter will be used to set the increment 
	 * 		in {@link #createNumberGenerator(Class, Number, Number, Number, boolean)}
	 */
	public StepSequence(BigDecimal delta) {
	    this(delta, null);
    }
	
	public StepSequence(BigDecimal delta, BigDecimal initial) {
	    this(delta, initial, null);
    }
	
	public StepSequence(BigDecimal delta, BigDecimal initial, BigDecimal limit) {
	    this.delta = delta;
	    this.initial = initial;
	    this.limit = limit;
    }
	
	public void setDelta(BigDecimal delta) {
		this.delta = delta;
	}
	
	public BigDecimal getDelta() {
    	return delta;
    }

	public BigDecimal getInitial() {
    	return initial;
    }

	@Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
		int deltaToUse = (delta != null ? toInteger(delta) : 1);
		if (delta != null && delta.longValue() < 0)
			return super.applyTo(source, unique);
		else
			return new SkipGeneratorProxy<T>(source, deltaToUse, deltaToUse, 
					SequenceManager.RANDOM_SEQUENCE, toInteger(limit));
	}
	
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(
    		Class<T> numberType, T min, T max, T granularity, boolean unique) {
        Number deltaToUse = deltaToUse(granularity);
    	if (unique && deltaToUse.doubleValue() == 0)
    		throw new InvalidGeneratorSetupException("Can't generate unique numbers with an increment of 0.");
    	NonNullGenerator<? extends Number> base;
		if (BeanUtil.isIntegralNumberType(numberType)) {
			if (max == null)
				max = NumberUtil.maxValue(numberType);
	        base = new StepLongGenerator(
					toLong(min), toLong(max), toLong(deltaToUse), toLong(initial));
		} else
			base = new StepDoubleGenerator(
					toDouble(min), toDouble(max), toDouble(deltaToUse), toDouble(initial));
		return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, base, min, granularity);
	}

	private <T extends Number> Number deltaToUse(T granularity) {
	    return (delta != null ? delta : (granularity != null ? granularity : 1));
    }

	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}
	
}
