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
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ConstantExpression;
import org.databene.script.ScriptUtil;

/**
 * Expression that evaluates a script.<br/><br/>
 * Created: 27.10.2009 13:48:11
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ScriptExpression<E> implements Expression<E> {

	private String script;
	private Expression<E> defaultValueExpression;

    public ScriptExpression(String script) {
    	this(script, (E) null);
    }

    public ScriptExpression(String script, E defaultValue) {
    	this(script, new ConstantExpression<E>(defaultValue));
    }

    public ScriptExpression(String script, Expression<E> defaultValueExpression) {
    	this.script = script;
    	this.defaultValueExpression = defaultValueExpression;
    }

	@SuppressWarnings("unchecked")
    public E evaluate(Context context) {
		if (StringUtil.isEmpty(script))
			return (defaultValueExpression != null ? defaultValueExpression.evaluate(context) : null);
		else
			return (E) ScriptUtil.render(script, context);
    }

	@Override
	public String toString() {
		return script;
	}
	
}
