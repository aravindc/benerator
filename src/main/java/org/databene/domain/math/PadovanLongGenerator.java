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

package org.databene.domain.math;

import org.databene.benerator.primitive.number.adapter.AbstractNumberGenerator;

/**
 * TODO document class PadovanLongGenerator.<br/>
 * <br/>
 * Created at 03.07.2009 13:22:41
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class PadovanLongGenerator extends AbstractNumberGenerator<Long> {
	// TODO extract common feature of FibonacciLongGenerator and PadovanLongGenerator to Recursive[Long]Generator
	
	private Long pn_1;
	private Long pn_2;
	private Long pn_3;

    public PadovanLongGenerator(Long min, Long max) {
    	super(Long.class, min, max, 1L);
	    resetFn();
    }

    @Override
    public boolean available() {
    	return (max == null || calculateNext() < max); 
    }
    
    public Long generate() {
	    long result = calculateNext();
	    if (max != null && result > max)
	    	throw stateException(this);
	    pn_3 = pn_2;
	    pn_2 = pn_1;
	    pn_1 = result;
	    return result;
    }

	private long calculateNext() {
	    return (pn_3 != null ? pn_2 + pn_3 : 1);
    }

	@Override
    public void reset() {
	    resetFn();
	    super.reset();
    }

	@Override
	public void close() {
	    pn_1 = pn_2 = pn_3 = null;
		super.close();
	}
	
	private void resetFn() {
	    pn_1 = pn_2 = pn_3 = null;
	    long tmp;
	    while (min != null && (tmp = calculateNext()) < min) {
	    	pn_3 = pn_2;
	    	pn_2 = pn_1;
	    	pn_1 = tmp;
	    }
    }

}
