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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.CachedExpression;
import org.databene.benerator.wrapper.GeneratorWrapper;
import org.databene.commons.ArrayUtil;
import org.databene.commons.Context;
import org.databene.commons.context.ContextAware;

/**
 * Array generator that allows for dynamic change of the array length algorithm.<br/><br/>
 * Created: 21.10.2009 10:35:43
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DynamicInstanceArrayGenerator extends GeneratorWrapper<Object, Object> implements ContextAware {
	
	private CachedExpression countExpression;
	private Context context; // TODO how to get context from Benerator?

    public DynamicInstanceArrayGenerator(Generator<Object> source, CachedExpression countExpression, BeneratorContext context) {
        super(source);
        this.countExpression = countExpression;
        this.context = context;
    }
    
	public void setContext(Context context) {
	    this.context = context;
    }

	public Class<Object> getGeneratedType() {
	    return Object.class;
    }

    public Object generate() {
        int count = ((Number) countExpression.evaluate(context)).intValue();
        if (count == 0)
            return new Object[0];
        if (count == 1)
            return source.generate();
        else {
            Object[] result = ArrayUtil.newInstance(source.getGeneratedType(), count);
            for (int i = 0; i < count; i++) {
                if (source.available())
                    result[i] = source.generate();
                else {
                    // source generator went unavailable, 
                    // let's repack generated stuff to an array of appropriate length
                    return ArrayUtil.copyOfRange(result, 0, i);
                }
            }
            return result;
        }
    }

    @Override
    public void reset() {
        super.reset();
        countExpression.invalidate();
    }
    
}
