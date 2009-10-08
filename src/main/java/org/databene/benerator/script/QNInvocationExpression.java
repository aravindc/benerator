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

import org.databene.commons.ArrayFormat;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.bean.DefaultClassProvider;
import org.databene.commons.expression.ExpressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document class QNInvocationExpression.<br/>
 * <br/>
 * Created at 07.10.2009 22:27:26
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class QNInvocationExpression<T> implements Expression<Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QNInvocationExpression.class);

	private String[] qn;
	private Expression<?>[] argExpressions;
	
    public QNInvocationExpression(String[] qn, Expression<?>[] argExpressions) {
    	this.qn = qn;
    	this.argExpressions = argExpressions;
    }

	public Object evaluate(Context context) {
		Object[] args = ExpressionUtil.evaluateAll(argExpressions, context);
		String methodName = ArrayUtil.lastElement(qn);
		return invoke(qn, qn.length - 1, methodName, args, context);
    }

    private Object invoke(String[] qn, int qnLength, String methodName, Object[] args, Context context) {
	    String objectOrClassName = ArrayFormat.formatPart(".", 0, qnLength, qn);
	    if (context.contains(objectOrClassName)) {
	    	Object target = context.get(objectOrClassName);
			return BeanUtil.invoke(target, methodName, args);
	    } else {
	    	try {
	    		Class<?> type = DefaultClassProvider.resolveByObjectOrDefaultInstance(objectOrClassName, context);
	    		return BeanUtil.invokeStatic(type, methodName, args);
	    	} catch (ConfigurationError e) {
	    		if (LOGGER.isDebugEnabled())
	    			LOGGER.debug("Class not found: " + objectOrClassName);
	    	}
	    	throw new UnsupportedOperationException("Cannot evaluate " + objectOrClassName);
	    }
    }

}
