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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.wrapper.NShotGeneratorProxy;

/**
 * TODO Document class.<br/><br/>
 * Created: 25.07.2010 09:55:54
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class HeadSequence extends Sequence {
	
	private static final StepSequence STEP_SEQ = new StepSequence();

	long n;
	
	public HeadSequence() {
	    this(1);
    }

	public HeadSequence(long n) {
	    super("head");
	    this.n = n;
    }

	@Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	    return new NShotGeneratorProxy<T>(source, n);
	}
	
    public <T extends Number> Generator<T> createGenerator(
    		Class<T> numberType, T min, T max, T precision, boolean unique) {
    	Generator<T> source = STEP_SEQ.createGenerator(numberType, min, max, precision, unique);
		return new NShotGeneratorProxy<T>(source, n);
	}

}
