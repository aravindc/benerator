/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine.expression;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.commons.Context;
import org.databene.commons.ErrorHandler;
import org.databene.commons.Level;
import org.databene.script.Expression;
import org.databene.script.expression.DynamicExpression;
import org.databene.script.expression.ExpressionUtil;

/**
 * Parses an <code>onError</code> attribute in an XML descriptor element.<br/>
 * <br/>
 * Created at 24.07.2009 08:42:51
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ErrorHandlerExpression extends DynamicExpression<ErrorHandler> {
	
	private String category;
	private Expression<String> levelExpr;

    public ErrorHandlerExpression(String category, Expression<String> levelExpr) {
	    this.levelExpr = levelExpr;
	    this.category = category;
    }

	public ErrorHandler evaluate(Context context) {
		String levelName = ExpressionUtil.evaluate(levelExpr, context);
		if (levelName == null)
			if (context != null)
				levelName = ((BeneratorContext) context).getDefaultErrorHandler();
			else
				levelName = Level.fatal.name();
		Level level = Level.valueOf(levelName);
		return new ErrorHandler(category, level);
    }

}
