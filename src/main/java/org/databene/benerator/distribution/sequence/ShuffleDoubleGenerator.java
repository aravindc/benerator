/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.primitive.number.AbstractNumberGenerator;

/**
 * Double Generator that implements a 'shuffle' Double Sequence.<br/>
 * <br/>
 * Created: 18.06.2006 14:40:29
 */
public class ShuffleDoubleGenerator extends AbstractNumberGenerator<Double> {

    private double increment;

    private Double next;

    public ShuffleDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE, 2, 1);
    }

    public ShuffleDoubleGenerator(double min, double max, double precision, double increment) {
        super(Double.class, min, max, precision);
        this.increment = increment;
        reset();
    }

    // config properties -----------------------------------------------------------------------------------------------

    @Override
    public void setMin(Double min) {
    	super.setMin(min);
    	reset();
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }

    // source interface ---------------------------------------------------------------------------------------------

    @Override
    public void validate() throws InvalidGeneratorSetupException {
    	if (dirty) {
            if (precision <= 0)
                throw new InvalidGeneratorSetupException("Precision must be greater than zero, but is " + precision);
            if (min < max && increment <= 0)
                throw new InvalidGeneratorSetupException("Unsupported increment value: " + increment);
    		super.validate();
    	}
    }
    
    @Override
    public boolean available() {
        return (next != null && super.available());
    }
    
    public Double generate() throws IllegalGeneratorStateException {
    	if (!available())
    		throw new IllegalGeneratorStateException("Generator is unavailable");
        double result = next;
        if (next + increment <= max)
            next += increment;
        else {
            double newOffset = (next - min + precision) % increment;
			next = (newOffset > 0 ? min + newOffset : null);
        }
        return result;
    }

    @Override
    public void reset() {
        this.next = min;
    }
    
}
