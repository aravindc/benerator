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
import org.databene.benerator.primitive.number.adapter.AbstractNumberGenerator;

/**
 * Long Generator that implements a 'step' Long Sequence.<br/>
 * <br/>
 * Created: 26.07.2007 18:36:45
 */
public class StepLongGenerator extends AbstractNumberGenerator<Long> {

	private long increment;
	private long initial;
	
    private long next;

    // constructors ----------------------------------------------------------------------------------------------------

    public StepLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public StepLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public StepLongGenerator(long min, long max, long increment) {
        this(min, max, increment, null);
    }

    public StepLongGenerator(long min, long max, long increment, Long initial) {
        super(Long.class, min, max, Math.abs(increment));
        this.increment = increment;
        this.initial = (initial != null ? initial : (increment >= 0 ? min : max));
        reset();
    }
    
    @Override
    public void setPrecision(Long precision) {
        super.setPrecision(precision);
        this.increment = precision;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
	public void validate() {
        if (dirty) {
            reset();
    		super.validate();
    		dirty = false;
        }
    }

    @Override
    public boolean available() {
    	validate();
        return (increment == 0 || (increment > 0 && next <= max) || (increment < 0 && next >= min));
    }

    public Long generate() throws IllegalGeneratorStateException {
        if (!available())
        	throw new IllegalGeneratorStateException(
        			"Generator " + this + " is not available. Check this by calling available() before generate()");
        long value = next;
        next += increment;
        return value;
    }

    @Override
	public void reset() {
		next = initial;
	}

}
