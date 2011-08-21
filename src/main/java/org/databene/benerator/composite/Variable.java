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

package org.databene.benerator.composite;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Wraps variable name and generator functionality.<br/><br/>
 * Created: 07.08.2011 16:24:10
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class Variable<E> implements GeneratorComponent<E> {
	
	private String name;
	private Generator<?> generator;
	private GeneratorContext context;
	
	public Variable(String name, Generator<?> generator) {
		this.name = name;
		this.generator = generator;
	}

	public boolean isParallelizable() {
		return generator.isParallelizable();
	}

	public boolean isThreadSafe() {
		return generator.isThreadSafe();
	}

	public void init(GeneratorContext context) {
		this.context = context;
		generator.init(context);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean buildComponentFor(Object target, GeneratorContext context) {
		ProductWrapper<?> productWrapper = generator.generate(new ProductWrapper());
		if (productWrapper == null) {
			context.remove(name);
            return false;
		}
        context.set(name, productWrapper.unwrap());
        return true;
	}
	
	public void reset() {
		generator.reset();
	}

	public void close() {
		context.remove(name);
		generator.close();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + name + ":" + generator + "]";
	}
	
}
