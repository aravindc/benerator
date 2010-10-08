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

import org.databene.commons.ArrayFormat;
import org.databene.commons.Assert;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.expression.CompositeExpression;

/**
 * Calculates the sum of two or more expressions.<br/><br/>
 * Created: 07.10.2010 11:24:34
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SumExpression extends CompositeExpression<Object> {
	
	@SuppressWarnings("unchecked")
    public SumExpression(Expression<?>... terms) {
	    super((Expression<Object>[]) terms);
    }

    public Object evaluate(Context context) {
        Expression<?>[] summands = { terms[0], terms[1] };
    	Assert.isTrue(summands.length > 1, "At least two summands needed");
        Object result = summands[0].evaluate(context);
        for (int i = 1; i < summands.length; i++)
        	result = ArithmeticEngine.defaultInstance().add(result, summands[i].evaluate(context));
        return result;
    }
    
    @Override
    public String toString() {
        return "(" + ArrayFormat.format(" + ", terms) + ")";
    }
    
}

