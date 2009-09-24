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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.benerator.wrapper.InstanceSequenceGenerator;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.MathUtil;

/**
 * TODO document class DynamicInstanceSequenceGenerator.<br/>
 * <br/>
 * Created at 26.07.2009 05:57:42
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DynamicInstanceSequenceGenerator<E> extends GeneratorProxy<E> {
	
	private Expression<? extends Number> minCountExpr;
	private Expression<? extends Number> maxCountExpr;
	private Expression<? extends Number> countPrecisionExpr;
	private Expression<? extends Distribution> countDistributionExpr;
	private Context context;
	
	public DynamicInstanceSequenceGenerator(Generator<E> source, Context context) {
	    this(source, context, null, null, null, null);
    }

	public DynamicInstanceSequenceGenerator(Generator<E> source, Context context, 
			Expression<? extends Number> minLengthExpr, Expression<? extends Number> maxLengthExpr, 
			Expression<? extends Number> lengthPrecisionExpr, Expression<? extends Distribution> lengthDistributionExpr) {
	    super(new InstanceSequenceGenerator<E>(source));
	    this.context = context;
	    this.minCountExpr = minLengthExpr;
	    this.maxCountExpr = maxLengthExpr;
	    this.countPrecisionExpr = lengthPrecisionExpr;
	    this.countDistributionExpr = lengthDistributionExpr;
	    this.dirty = true;
    }

	public void setMinCount(Expression<? extends Number> minLengthExpr) {
    	this.minCountExpr = minLengthExpr;
    	this.dirty = true;
    }

	public void setMaxCount(Expression<? extends Number> maxLengthExpr) {
    	this.maxCountExpr = maxLengthExpr;
    	this.dirty = true;
    }

	public void setCountPrecision(Expression<? extends Number> lengthPrecisionExpr) {
    	this.countPrecisionExpr = lengthPrecisionExpr;
    	this.dirty = true;
    }

	public void setCountDistribution(Expression<? extends Distribution> lengthDistributionExpr) {
    	this.countDistributionExpr = lengthDistributionExpr;
    	this.dirty = true;
    }

	private Long evaluateLong(Expression<? extends Number> expression) {
	    return MathUtil.toLong(evaluate(expression));
	}
	
	private <T> T evaluate(Expression<T> expression) {
	    return (expression != null ? expression.evaluate(context) : null);
    }
	
	@Override
	public void validate() {
		if (dirty) {
			// check settings
			Assert.notNull(minCountExpr, "minLengthExpr");
			Assert.notNull(maxCountExpr, "maxLengthExpr");
			Assert.notNull(countPrecisionExpr, "lengthPrecisionExpr");
			Assert.notNull(countDistributionExpr, "lengthDistributionExpr");
			// initialize source
			InstanceSequenceGenerator<E> isg = (InstanceSequenceGenerator<E>) source;
			isg.setMinCount(evaluateLong(minCountExpr));
			isg.setMaxCount(evaluateLong(maxCountExpr)); // TODO is this default value alright? Have global config?
			isg.setCountPrecision(evaluateLong(countPrecisionExpr));
		    isg.setCountDistribution(evaluate(countDistributionExpr));
			super.validate();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		dirty = true;
	}

    public Object getSequenceLength() {
    	if (dirty)
    		validate();
	    return ((InstanceSequenceGenerator<E>) source).getSequenceLength();
    }
	
}
