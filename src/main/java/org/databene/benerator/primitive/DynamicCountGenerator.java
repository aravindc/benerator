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

import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.commons.Expression;

/**
 * Behaves similar to the {@link DynamicLongGenerator}, 
 * but generates <code>null</code> values, if <code>max</code> is set to <code>null</code>.
 * The <code>null</code> value are to be interpreted as not-externally-limited loop size
 * (the default case when iterating over a limited data source).<br/><br/>
 * Created: 28.03.2010 08:48:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicCountGenerator extends DynamicLongGenerator { // TODO test

    public DynamicCountGenerator() {
        super();
    }

    public DynamicCountGenerator(Expression<Long> min, Expression<Long> max, Expression<Long> precision, 
    		Expression<? extends Distribution> distribution, Expression<Boolean> unique) {
        super(min, max, precision, distribution, unique);
    }
    
	@Override
	protected void resetMembers(Long maxValue) {
		if (maxValue != null)
		    super.resetMembers(maxValue);
		else {
			source = GeneratorFactory.getConstantGenerator(null);
	        source.init(context);
		}
	}
	
}
