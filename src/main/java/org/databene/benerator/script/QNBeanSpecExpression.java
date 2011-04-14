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
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.ExceptionUtil;
import org.databene.commons.Expression;
import org.databene.commons.bean.DefaultClassProvider;
import org.databene.commons.expression.DynamicExpression;

/**
 * {@link Expression} instance that evaluates the Benerator script notation for Java object specification
 * as one of the following: 
 * <ul>
 *   <li>reference: <code>myInstance</code></li>
 *   <li>class name: <code>com.my.SpecialClass</code></li>
 *   <li>constructor invocation: <code>new com.my.SpecialClass(3, 'test')</code></li>
 *   <li>JavaBean property syntax: <code>new com.my.SpecialClass[id=3, name='test']</code></li>
 * </ul>
 * <br/>
 * Created at 08.10.2009 18:15:15
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class QNBeanSpecExpression extends DynamicExpression<Object> {
	
	String[] qn;

    public QNBeanSpecExpression(String[] qn) {
    	this.qn = qn;
    }

    public Object evaluate(Context context) {
    	return resolve(context).getBean();
    }

    public BeanSpec resolve(Context context) {
    	String objectOrClassName = ArrayFormat.format(".", qn);
    	try {
    		if (context.contains(objectOrClassName))
    			return BeanSpec.createReference(context.get(objectOrClassName));
    		String className = objectOrClassName;
    		Class<?> type = DefaultClassProvider.resolveByObjectOrDefaultInstance(className, context);
    		return BeanSpec.createConstruction(BeanUtil.newInstance(type));
    	} catch (ConfigurationError e) {
    		if (ExceptionUtil.getRootCause(e) instanceof ClassNotFoundException)
    			return new QNExpression(qn).resolve(context);
    		else
    			throw new ConfigurationError("Cannot resolve " + objectOrClassName, e);
    	}
    }

}
