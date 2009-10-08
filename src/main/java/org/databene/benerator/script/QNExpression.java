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
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.accessor.FeatureAccessor;
import org.databene.commons.bean.DefaultClassProvider;
import org.databene.commons.expression.UnaryExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO document class QNExpression.<br/>
 * <br/>
 * Created at 08.10.2009 07:18:53
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class QNExpression extends UnaryExpression<Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeneratorScriptParser.class);

	public QNExpression(Expression<?> term) {
	    super(term);
    }

    public Object evaluate(Context context) {
        String[] qnParts = (String[]) term.evaluate(context);
        String qn = ArrayFormat.format(".", qnParts);
        if (context.contains(qn))
        	return context.get(qn);
        else
        	return readField(qnParts, qnParts.length - 1, ArrayUtil.lastElement(qnParts), context);
    }

    private Object readField(String[] qnParts, int qnLength, String fieldName, Context context) {
    	String objectOrClassName = ArrayFormat.formatPart(".", 0, qnLength, qnParts);
    	if (context.contains(objectOrClassName)) {
    		Object target = context.get(objectOrClassName);
    		return FeatureAccessor.getValue(target, fieldName);
    	} else {
    		try {
    			Class<?> type = DefaultClassProvider.resolveByObjectOrDefaultInstance(objectOrClassName, context);
    			return FeatureAccessor.getValue(type, fieldName);
    		} catch (ConfigurationError e) {
    			LOGGER.debug("Class not found: " + objectOrClassName);
    			// TODO avoid recursion overrun
    			Object base = readField(qnParts, qnLength - 1, qnParts[qnLength - 1], context);
    			return FeatureAccessor.getValue(base, fieldName);
    		}
    	}
    }
    
}

