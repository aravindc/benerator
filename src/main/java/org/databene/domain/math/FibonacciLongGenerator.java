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

import org.databene.benerator.primitive.number.RecursiveNumberGenerator;

/**
 * Generates <a href="http://en.wikipedia.org/wiki/Fibonacci_number">Fibonacci Numbers</a>.<br/>
 * <br/>
 * Created at 03.07.2009 10:44:56
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FibonacciLongGenerator extends RecursiveNumberGenerator<Long> {
	
    public FibonacciLongGenerator(Long min, Long max) {
    	super(Long.class, 2, min, max);
    }

	@Override
    protected Long recursion() {
	    return recentProducts.get(0) + recentProducts.get(1);
    }

	@Override
    protected Long baseValue(int n) {
	    switch (n) {
		    case 0 : return 0L;
		    case 1 : return 1L;
		    default: throw new IllegalStateException();
	    }
    }

}
