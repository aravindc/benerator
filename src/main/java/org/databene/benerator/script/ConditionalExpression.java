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

package org.databene.benerator.script;

import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.converter.AnyConverter;

/**
 * {@link Expression} implementation that evaluates a boolean (condition) expression 
 * and, depending if the result was true or false, evaluates the first or second expression delegate.
 * The semantic is like the Java expression <code>(condition ? alt1 : alt2)</code>.<br/>
 * <br/>
 * Created at 06.10.2009 16:04:15
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ConditionalExpression implements Expression {
	
	private Expression condition;
	private Expression alt1;
	private Expression alt2;
	
    public ConditionalExpression(Expression condition, Expression alt1, Expression alt2) {
	    this.condition = condition;
	    this.alt1 = alt1;
	    this.alt2 = alt2;
    }

	public Object evaluate(Context context) {
        boolean cond = AnyConverter.convert(condition.evaluate(context), Boolean.class);
        return (cond ? alt1 : alt2).evaluate(context);
    }

}
