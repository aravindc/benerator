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

package org.databene.benerator.engine.expression;

import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.commons.expression.ConstantExpression;
import org.databene.script.ScriptUtil;

/**
 * Evaluates a string which may be a script (indicated by {}).<br/><br/>
 * Created: 19.02.2010 10:39:29
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ScriptableExpression implements Expression<Object> {

	private String scriptOrText;
	private Expression<?> defaultValueExpression;

    public ScriptableExpression(String scriptOrText, Object defaultValue) {
    	this(scriptOrText, (defaultValue != null ? new ConstantExpression<Object>(defaultValue) : null));
    }

    private ScriptableExpression(String scriptOrText, Expression<?> defaultValueExpression) {
    	this.scriptOrText = scriptOrText;
    	this.defaultValueExpression = defaultValueExpression;
    }
    
    public static Expression<?> createWithDefaultExpression(
    		String scriptOrText, Expression<?> defaultValueExpression) {
    	return new ScriptableExpression(scriptOrText, defaultValueExpression);
    }

    public Object evaluate(Context context) {
		if (StringUtil.isEmpty(scriptOrText))
			return (defaultValueExpression != null ? defaultValueExpression.evaluate(context) : null);
		else
			return ScriptUtil.render(scriptOrText, context);
    }

	@Override
	public String toString() {
		return scriptOrText.toString();
	}
	
}
