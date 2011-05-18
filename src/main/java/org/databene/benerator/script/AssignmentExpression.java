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
import org.databene.commons.expression.DynamicExpression;
import org.databene.commons.mutator.AnyMutator;

/**
 * Evaluates an assignment expression like <code>x.y = f.d + 3</code>.<br/><br/>
 * Created: 23.02.2010 10:55:56
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class AssignmentExpression extends DynamicExpression<Object> {
	
	private String[] lhs;
	private Expression<?> rhs;

	public AssignmentExpression(String[] lhs, Expression<?> rhs) {
	    this.lhs = lhs;
	    this.rhs = rhs;
    }

	public Object evaluate(Context context) {
		Object value = rhs.evaluate(context);
		if (lhs.length == 1) {
			// if lhs is a simple variable name then put the result into context by this name
			context.set(lhs[0], value);
		} else {
			// get last parent object of QN and set the feature denoted by the last QN part
			String fieldName = lhs[lhs.length - 1];
			Object field = QNExpression.resolveNamePart(lhs, lhs.length - 1, context);
			AnyMutator.setValue(field, fieldName, value, false);
		}
	    return value;
    }

}
