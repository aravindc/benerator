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
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;

/**
 * Creates numbers by continuously incrementing a base value by a constant amount.<br/>
 * <br/>
 * Created at 30.06.2009 09:55:20
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class StepSequence extends Sequence {

    private static final String NAME = "step";

    private BigDecimal increment;
	private BigDecimal initial;
	
	public StepSequence() {
	    this(null); // when using null, the precision parameter will be used to set the increment in createGenerator
    }
	
	/**
	 * @param increment the increment to choose for created generators. 
	 * 		When using null, the precision parameter will be used to set the increment 
	 * 		in {@link #createGenerator(Class, Number, Number, Number, boolean)}
	 */
	public StepSequence(BigDecimal increment) {
	    this(increment, null);
    }
	
	public StepSequence(BigDecimal increment, BigDecimal initial) {
	    super(NAME);
	    this.increment = increment;
	    this.initial = initial;
    }
	
	public BigDecimal getIncrement() {
    	return increment;
    }

	public BigDecimal getInitial() {
    	return initial;
    }

    public <T extends Number> Generator<T> createGenerator(
    		Class<T> numberType, T min, T max, T precision, boolean unique) {
        Number incrementToUse = incrementToUse(precision);
    	if (unique && incrementToUse.doubleValue() == 0)
    		throw new InvalidGeneratorSetupException("Can't generate unique numbers with an increment of 0.");
		Generator<? extends Number> base;
		if (BeanUtil.isIntegralNumberType(numberType))
	        base = new StepLongGenerator(
					toLong(min), toLong(max), toLong(incrementToUse), toLong(initial));
        else
			base = new StepDoubleGenerator(toDouble(min), toDouble(max), 
					toDouble(incrementToUse), toDouble(initial));
		return WrapperFactory.wrapNumberGenerator(numberType, base);
	}

	private <T extends Number> Number incrementToUse(T precision) {
	    return (increment != null ? increment : precision);
    }

	@Override
	public String toString() {
	    return BeanUtil.toString(this);
	}
	
}
