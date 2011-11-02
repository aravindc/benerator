/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.script.Expression;

/**
 * Behaves similar to the {@link DynamicLongGenerator}, 
 * but generates <code>maxFallback</code> values, if <code>max</code> is set to <code>null</code>.<br/><br/>
 * Created: 28.03.2010 08:48:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicCountGenerator extends DynamicLongGenerator {
	
	private boolean resetToMin;

    public DynamicCountGenerator() {
        super();
    }

    public DynamicCountGenerator(Expression<Long> min, Expression<Long> max, Expression<Long> granularity, 
    		Expression<? extends Distribution> distribution, Expression<Boolean> unique, boolean resetToMin) {
        super(min, max, granularity, distribution, unique);
        this.resetToMin = resetToMin;
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void resetMembers(Long minValue, Long maxValue) {
		if (maxValue != null)
		    super.resetMembers(minValue, maxValue);
		else {
			// if it is not required to reset to min (<generate> or <iterate>), make it unlimited (returning null),
			// otherwise reset to the min value (component generation)
			Long constant = (resetToMin ? minValue : null);
			Generator<Long> source = new ConstantGenerator(constant);
	        source.init(context);
	        setSource(source);
		}
	}
	
}
