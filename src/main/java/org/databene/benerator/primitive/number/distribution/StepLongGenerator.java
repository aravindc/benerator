/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Long Generator that implements a 'step' Long Sequence.<br/>
 * <br/>
 * Created: 26.07.2007 18:36:45
 */
public class StepLongGenerator extends AbstractLongGenerator {

    private long next;

    // constructors ----------------------------------------------------------------------------------------------------

    public StepLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public StepLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public StepLongGenerator(long min, long max, long increment) {
        super(min, max, Math.abs(increment), increment, 0);
        this.variation1 = increment;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Distribution getDistribution() {
        return Sequence.STEP;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public void validate() {
        if (dirty) {
            reset();
    		super.validate();
        }
    }

    public Class<Long> getGeneratedType() {
        return Long.class;
    }
    
    @Override
    public boolean available() {
    	validate();
        return (variation1 == 0 || (variation1 > 0 && next <= max) || (variation1 < 0 && next >= min));
    }

    public Long generate() throws IllegalGeneratorStateException {
        if (!available())
        	throw new IllegalGeneratorStateException(
        			"Generator " + this + " is not available. Check this by calling available() before generate()");
        long value = next;
        next += variation1;
        return value;
    }

    @Override
	public void reset() {
		if (variation1 < 0)
		    next = max;
		else
		    next = min;
	}

    @Override
    public void close() {
    	// nothing to do
    }
}
