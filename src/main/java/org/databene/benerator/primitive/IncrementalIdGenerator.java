/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import java.util.concurrent.atomic.AtomicLong;

import org.databene.benerator.util.ThreadSafeNonNullGenerator;

/**
 * Generates unique long values incrementally.<br/><br/>
 * Created: 14.11.2009 06:49:49
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class IncrementalIdGenerator extends ThreadSafeNonNullGenerator<Long> {
	
	private long increment;
	private volatile AtomicLong cursor = new AtomicLong();
	
	// constructors ----------------------------------------------------------------------------------------------------

	public IncrementalIdGenerator() {
	    this(1, 1);
    }

	public IncrementalIdGenerator(long initial) {
	    this(initial, 1);
    }

	public IncrementalIdGenerator(long initial, long increment) {
	    setInitial(initial);
	    this.increment = increment;
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public long getCursor() {
		return cursor.get();
	}
	
	public void setInitial(long initial) {
    	this.cursor.set(initial);
    }

	public void setIncrement(long increment) {
    	this.increment = increment;
    }

	// Generator interface implementation ------------------------------------------------------------------------------
	
	public Class<Long> getGeneratedType() {
	    return Long.class;
    }
	
	@Override
	public Long generate() {
	    return cursor.getAndAdd(increment);
    }

}
