/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.Expression;
import org.databene.commons.expression.ExpressionUtil;

/**
 * {@link Generator} implementation that generates {@link Long} numbers, 
 * redefining the underlying distribution on each <code>reset()</code> by
 * evaluating the <code>min</code>, <code>max</code>, <code>precision</code>,
 * <code>distribution</code> and <code>unique</code> values.<br/><br/>
 * Created: 27.03.2010 19:28:38
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicLongGenerator extends GeneratorProxy<Long> {

    protected Expression<Long> min;
    protected Expression<Long> max;
    protected Expression<Long> precision;
    protected Expression<? extends Distribution> distribution;
    protected Expression<Boolean> unique;

    // constructors ----------------------------------------------------------------------------------------------------

    public DynamicLongGenerator() {
        this(ExpressionUtil.constant(0L), ExpressionUtil.constant(30L), 
        		ExpressionUtil.constant(1L), ExpressionUtil.constant(SequenceManager.RANDOM_SEQUENCE), 
        		ExpressionUtil.constant(false));
    }

    public DynamicLongGenerator(Expression<Long> min, Expression<Long> max, 
    		Expression<Long> precision, Expression<? extends Distribution> distribution,
    	    Expression<Boolean> unique) {
        super(null);
        this.min = min;
        this.max = max;
        this.precision = precision;
        this.distribution = distribution;
    }
    
    // Generator interface ---------------------------------------------------------------------------------------------

	/** ensures consistency of the state */
    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	this.context = context;
    	resetMembers(max.evaluate(context));
        super.init(context);
    }

    @Override
    public void reset() {
    	assertInitialized();
    	resetMembers(max.evaluate(context));
        super.reset();
    }

	protected void resetMembers(Long maxValue) {
		Long minValue = ExpressionUtil.evaluate(min, context);
		if (minValue == null)
			minValue = 0L;
		Long precisionValue = ExpressionUtil.evaluate(precision, context);
		if (precisionValue == null)
			precisionValue = 1L;
	    Distribution dist = distribution.evaluate(context);
		source = dist.createGenerator(Long.class, minValue, maxValue, precisionValue, false);
        source.init(context);
    }
    
}
