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

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.context.ContextAware;
import org.databene.commons.expression.DynamicExpression;

/**
 * {@link Expression} implementation that instantiates a JavaBean by default constructor and 
 * calls its property setters for initializing state.<br/>
 * <br/>
 * Created at 06.10.2009 11:48:59
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class BeanConstruction<E> extends DynamicExpression<E> {
	
	private Expression<E> instantiation;
	private Assignment[] assignments;

    public BeanConstruction(String beanClassName, Assignment[] assignments) {
	    this(new DefaultConstruction<E>(beanClassName), assignments);
    }

    public BeanConstruction(Expression<E> instantiation, Assignment[] assignments) {
	    this.instantiation = instantiation;
	    this.assignments = assignments;
    }

	public E evaluate(Context context) {
	    E bean = instantiation.evaluate(context);
	    for (Assignment assignment : assignments)
	    	BeanUtil.setPropertyValue(bean, assignment.getName(), assignment.getExpression().evaluate(context), false);
	    if (bean instanceof ContextAware)
	    	((ContextAware) bean).setContext(context);
		return bean;
    }

}
