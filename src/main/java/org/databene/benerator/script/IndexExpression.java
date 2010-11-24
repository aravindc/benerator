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

import java.util.List;
import java.util.Map;

import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.expression.BinaryExpression;

/**
 * {@link Expression} that evaluates an index argument.<br/><br/>
 * Created: 24.11.2010 14:12:48
 * @since 0.6.4
 * @author Volker Bergmann
 */
final class IndexExpression extends BinaryExpression<Object> {
	
	public IndexExpression(Expression<?> term1, Expression<?> term2) {
		super(term1, term2);
	}

	public Object evaluate(Context context) {
	    Object container = term1.evaluate(context);
	    Object indexObject = term2.evaluate(context);
	    if (container instanceof List) {
			int index = AnyConverter.convert(indexObject, Integer.class);
	    	return ((List<?>) container).get(index);
	    } else if (container.getClass().isArray()) {
			int index = AnyConverter.convert(indexObject, Integer.class);
	    	return ((Object[]) container)[index];
	    } else if (container instanceof String) {
			int index = AnyConverter.convert(indexObject, Integer.class);
	    	return ((String) container).charAt(index);
	    } else if (container instanceof Map) {
	    	return ((Map<?,?>) container).get(indexObject);
	    } else
	    	throw new IllegalArgumentException("Cannot do index-based access on " 
	    			+ BeanUtil.simpleClassName(container));
	}
	
	@Override
	public String toString() {
		return term1 + "[" + term2 + "]";
	}
	
}