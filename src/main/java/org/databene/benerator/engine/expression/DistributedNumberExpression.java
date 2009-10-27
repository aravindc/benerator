/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.engine.expression;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.commons.Context;
import org.databene.commons.Expression;

/**
 * TODO Document class.<br/><br/>
 * Created: 19.10.2009 10:44:51
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DistributedNumberExpression implements Expression<Long> {

	Expression<Long> min;
	Expression<Long> max;
	Expression<Long> precision;
	Expression<Distribution> distribution;
	
	private Generator<Long> generator;

	public DistributedNumberExpression(Expression<Distribution> distribution, 
			Expression<Long> min, Expression<Long> max, Expression<Long> precision) {
	    this.min = min;
	    this.max = max;
	    this.precision = precision;
	    this.distribution = distribution;
    }

	public Long evaluate(Context context) {
		if (generator == null)
	        initGenerator(context);
		return generator.generate();
    }

	private void initGenerator(Context context) {
	    Long minValue = min.evaluate(context);
		Long maxValue = max.evaluate(context);
		if (minValue == maxValue)
			generator = new ConstantGenerator<Long>(minValue);
		Long precisionValue = precision.evaluate(context);
		generator = distribution.evaluate(context).createGenerator(Long.class, 
	    	minValue, maxValue, precisionValue);
    }

}
