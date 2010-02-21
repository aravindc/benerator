/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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
 * {@link Expression} that resolves to a number with statistical properties.<br/><br/>
 * Created: 19.10.2009 10:44:51
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DistributedNumberExpression<E extends Number> implements Expression<E> {

	protected Class<E> numberType;
	protected Expression<E> min;
	protected Expression<E> max;
	protected Expression<E> precision;
	protected Expression<Distribution> distribution;
	protected Expression<Boolean> unique;
	
	private Generator<E> generator;

	public DistributedNumberExpression(Class<E> numberType, Expression<Distribution> distribution, 
			Expression<E> min, Expression<E> max, Expression<E> precision, Expression<Boolean> unique) {
		this.numberType = numberType;
	    this.min = min;
	    this.max = max;
	    this.precision = precision;
	    this.distribution = distribution;
	    this.unique = unique;
    }

	public E evaluate(Context context) {
		if (generator == null)
	        initGenerator(context);
		return generator.generate();
    }

	private void initGenerator(Context context) {
	    E minValue = min.evaluate(context);
		E maxValue = max.evaluate(context);
		if (minValue == maxValue)
			generator = new ConstantGenerator<E>(minValue);
		Distribution distr = distribution.evaluate(context);
		generator = distr.createGenerator(numberType, 
	    	minValue, maxValue, precision.evaluate(context), unique.evaluate(context));
    }

}
