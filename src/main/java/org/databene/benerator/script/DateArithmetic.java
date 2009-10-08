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

import java.util.Date;

import org.databene.commons.BeanUtil;
import org.databene.commons.TimeUtil;

/**
 * TODO document class DateArithmetic.<br/>
 * <br/>
 * Created at 06.10.2009 10:31:14
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DateArithmetic extends TypeArithmetic<Date> { // TODO test

    public DateArithmetic() {
	    super(Date.class);
    }

    @Override
    public Date add(Object summand1, Object summand2) {
    	if (summand1 instanceof Date)
    		return addImpl((Date) summand1, summand2);
    	else if (summand2 instanceof Date)
    		return addImpl((Date) summand2, summand1);
    	else
    		throw new IllegalArgumentException("No argument is of type " + baseType + ": " + 
    				summand1 + ", " + summand2);
    }

	private Date addImpl(Date summand1, Object summand2) {
    	if (summand2 instanceof Number)
    		return new Date(summand1.getTime() + ((Number) summand2).longValue());
    	else if (summand2 instanceof Date) {
    		Date date2 = (Date) summand2;
			return new Date(summand1.getTime() + date2.getTime() - TimeUtil.date(1970, 0, 1).getTime()); // TODO improve performance
    	} else
    		throw new UnsupportedOperationException("Cannot add " +
    				BeanUtil.simpleClassName(summand2) + " to " + baseType.getClass().getName());
    }

    @Override
    public Object subtract(Object minuend, Object subtrahend) {
    	return subtractImpl((Date) minuend, subtrahend);
    }

    private Object subtractImpl(Date minuend, Object subtrahend) {
	    if (subtrahend instanceof Number)
    		return new Date(minuend.getTime() - ((Number) subtrahend).longValue());
    	else if (subtrahend instanceof Date) {
    		Date date2 = (Date) subtrahend;
			return new Date(minuend.getTime() - date2.getTime() + TimeUtil.date(1970, 0, 1).getTime()); // TODO improve performance
    	} else
    		throw new UnsupportedOperationException("Cannot subtract " +
    				BeanUtil.simpleClassName(subtrahend) + " from " + minuend.getClass().getName());
    }

    @Override
    public Object product(Object factor1, Object factor2) {
	    throw new UnsupportedOperationException("Cannot multiply dates");
    }

}
