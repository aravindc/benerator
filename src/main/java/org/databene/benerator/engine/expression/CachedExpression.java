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

import org.databene.commons.Context;
import org.databene.script.Expression;
import org.databene.script.expression.ExpressionProxy;

/**
 * Caches the result of another expression and returns it on subsequent calls 
 * without evaluating the other expression again. The cache can be invalidated 
 * by calling the <code>invalidate()</code> method.<br/><br/>
 * Created: 21.10.2009 14:42:15
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CachedExpression<E> extends ExpressionProxy<E> {
	
	private boolean valid;
	private E cachedValue;

	public CachedExpression(Expression<E> realExpression) {
	    super(realExpression);
	    this.cachedValue = null;
	    this.valid = false;
    }

	@Override
	public E evaluate(Context context) {
		if (!valid) {
			cachedValue = super.evaluate(context);
			valid = true;
		}
	    return cachedValue;
	}
	
	public void invalidate() {
		valid = false;
	}
	
}
