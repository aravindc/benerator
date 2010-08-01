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

package org.databene.benerator.script;

import org.databene.commons.ArrayFormat;
import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Expression;

/**
 * {@link Expression} implementation that instantiates a Java object by constructor invocation.<br/>
 * <br/>
 * Created at 06.10.2009 11:48:59
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class ParameterizedConstruction<E> extends Construction<E> {
	
	private Expression<?>[] argumentExpressions;

    public ParameterizedConstruction(String className, Expression<?>[] argumentExpressions) {
	    super(className);
	    this.argumentExpressions = argumentExpressions;
    }
    
	@SuppressWarnings("unchecked")
    public E evaluate(Context context) {
		Class<?> type = getType(context);
		Object[] arguments = new Object[argumentExpressions.length];
		for (int i = 0; i < argumentExpressions.length; i++)
			arguments[i] = argumentExpressions[i].evaluate(context);
	    return (E) BeanUtil.newInstance(type, false, arguments);
    }

	public boolean classExists(Context context) {
		try {
			getType(context);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
	    return "new " + className + '(' + ArrayFormat.format(", ", argumentExpressions) + ')';
	}
	
}
