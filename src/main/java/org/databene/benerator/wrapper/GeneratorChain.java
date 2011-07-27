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

import java.util.HashSet;
import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;

/**
 * Proxies several source generators, initially returning products of the first source as long 
 * as it is available, then of the second source and son on.
 * When generating unique data, the last source generator is required to generate unique data itself.<br/>
 * <br/>
 * Created: 22.07.2011 14:58:00
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GeneratorChain<E> extends MultiGeneratorWrapper<E, E> {

	private boolean unique;
	private Set<E> usedValues;
	
	public GeneratorChain(Class<E> generatedType, boolean unique, Generator<? extends E>... sources) {
		super(generatedType, sources);
		this.unique = unique;
		this.usedValues = new HashSet<E>();
	}
	
	@Override
	public synchronized void init(GeneratorContext context) {
		clearMembers();
		super.init(context);
	}

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
		assertInitialized();
		boolean ok = true;
		E value;
		do {
			wrapper = generateUnvalidated(wrapper);
			if (wrapper == null)
				return null;
			value = wrapper.unwrap();
			if (unique) {
				if (availableSourceCount() > 1) {
					// for all but the last generator check if the value has already occurred and store it...
					ok = usedValues.add(value);
				} else {
					// ...since each generator is expected to be unique itself, 
					// there is no need to store the value of the last generator in the chain
					ok = !usedValues.contains(value);
				}
			}
		} while (!ok);
		return wrapper.wrap(value);
	}

	@Override
	public synchronized void reset() {
		super.reset();
		clearMembers();
	}

	@Override
	public synchronized void close() {
		super.close();
		clearMembers();
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	protected ProductWrapper<E> generateUnvalidated(ProductWrapper<E> wrapper) {
		return generateFromAvailableSource(0, wrapper);
	}

	protected void clearMembers() {
		this.usedValues.clear();
	}

}
