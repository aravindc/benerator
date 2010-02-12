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

import java.sql.Timestamp;
import java.util.Date;

import org.databene.commons.BeanUtil;
import org.databene.commons.TimeUtil;

/**
 * {@link TypeArithmetic} implementation for the {@link Date} type.<br/>
 * <br/>
 * Created at 06.10.2009 10:31:14
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class TimestampArithmetic extends TypeArithmetic<Timestamp> {

    public TimestampArithmetic() {
	    super(Timestamp.class);
    }
    
    // Arithmetic interface implementation -----------------------------------------------------------------------------

    @Override
    public Timestamp add(Object summand1, Object summand2) {
    	if (summand1 instanceof Timestamp)
    		return addToTimestamp((Timestamp) summand1, summand2);
    	else if (summand2 instanceof Timestamp)
    		return addToTimestamp((Timestamp) summand2, summand1);
    	else
    		throw new IllegalArgumentException("No argument is of type " + baseType + ": " + 
    				summand1 + ", " + summand2);
    }

    @Override
    public Object subtract(Object minuend, Object subtrahend) {
    	if (minuend instanceof Timestamp)
    		return subtractFromTimestamp((Timestamp) minuend, subtrahend);
    	else
    		throw new IllegalArgumentException("No argument is of type " + baseType + ": " + 
    				minuend + ", " + subtrahend);
    }

    @Override
    public Object multiply(Object factor1, Object factor2) {
	    throw new UnsupportedOperationException("Cannot multiply timestamps");
    }

	@Override
	public Object divide(Object quotient, Object divisor) {
	    throw new UnsupportedOperationException("Cannot divide timestamps");
	}
	
    // private methods -------------------------------------------------------------------------------------------------
    
	private Timestamp addToTimestamp(Timestamp summand1, Object summand2) {
    	if (summand2 instanceof Number)
    		return new Timestamp(summand1.getTime() + ((Number) summand2).longValue());
    	else if (summand2 instanceof Timestamp)
    		return addTimestamps(summand1, (Timestamp) summand2);
    	else if (summand2 instanceof Date)
    		return addTimestamps(summand1, new Timestamp(((Date) summand2).getTime()));
    	else
    		throw new IllegalArgumentException("Cannot add " +
    				BeanUtil.simpleClassName(summand2) + " to " + baseType.getName());
    }

	private Timestamp addTimestamps(Timestamp summand1, Timestamp summand2) {
	    int nanoSum = summand1.getNanos() + summand2.getNanos();
	    Timestamp result = new Timestamp(summand1.getTime() + TimeUtil.millisSinceOwnEpoch(summand2) + nanoSum / 1000000000L);
	    result.setNanos(nanoSum % 1000000000);
	    return result;
    }

    private Timestamp subtractFromTimestamp(Timestamp minuend, Object subtrahend) {
    	if (subtrahend instanceof Number)
    		return new Timestamp(minuend.getTime() - ((Number) subtrahend).longValue());
    	else if (subtrahend instanceof Timestamp)
    		return subtractTimestamps(minuend, (Timestamp) subtrahend);
    	else if (subtrahend instanceof Date)
    		return subtractTimestamps(minuend, new Timestamp(((Date) subtrahend).getTime()));
    	else
    		throw new IllegalArgumentException("Cannot subtract " +
    				BeanUtil.simpleClassName(subtrahend) + " from " + minuend.getClass().getName());
    }

	private Timestamp subtractTimestamps(Timestamp minuend, Timestamp subtrahend) {
	    int nanoDiff = minuend.getNanos() - subtrahend.getNanos();
	    if (nanoDiff < 0)
	    	nanoDiff += 1000000000;
	    Timestamp result = new Timestamp(minuend.getTime() - TimeUtil.millisSinceOwnEpoch(subtrahend) - nanoDiff / 1000000);
	    result.setNanos(nanoDiff % 1000000000);
	    return result;
    }

}
