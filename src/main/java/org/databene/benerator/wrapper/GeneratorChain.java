/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;

/**
 * Proxies several source generators, initially returning products of the first source as long 
 * as it is available, then of the second source and son on.<br/><br/>
 * Created: 22.07.2011 14:58:00
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorChain<E> extends MultiGeneratorWrapper<E, E> {

	private int index;
	
	public GeneratorChain(Class<E> generatedType, Generator<? extends E>... sources) {
		super(generatedType, sources);
		this.index = 0;
	}

	public E generate() {
		if (sources.length < 1)
			return null;
		E result = sources[index].generate();
		while (result == null && index < sources.length - 1) {
			index++;
			result = sources[index].generate();
		}
		return result;
	}

	@Override
	public synchronized void reset() {
		super.reset();
		this.index = 0;
	}
	
}
