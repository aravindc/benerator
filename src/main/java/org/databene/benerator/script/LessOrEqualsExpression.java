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

package org.databene.benerator.script;

import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.expression.BinaryExpression;

/**
 * Boolean {@link Expression} that checks if the results of two other expressions are less or equal.<br/><br/>
 * Created: 24.11.2010 14:23:26
 * @since 0.6.4
 * @author Volker Bergmann
 */
final class LessOrEqualsExpression extends BinaryExpression<Boolean> {
	
	public LessOrEqualsExpression(Expression<?> term1, Expression<?> term2) {
		super(term1, term2);
	}

	public Boolean evaluate(Context context) {
	    return ArithmeticEngine.defaultInstance().lessOrEquals(term1.evaluate(context), term2.evaluate(context));
	}
	
	@Override
	public String toString() {
		return "(" + term1 + " <= " + term2 + ")";
	}
	
}