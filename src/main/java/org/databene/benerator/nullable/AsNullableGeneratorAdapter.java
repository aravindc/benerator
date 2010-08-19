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

package org.databene.benerator.nullable;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Wraps a {@link Generator} with a {@link NullableGenerator}.<br/><br/>
 * Created: 22.07.2010 19:14:36
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class AsNullableGeneratorAdapter<E> implements NullableGenerator<E> {
	
	private Generator<E> source;
	
    public AsNullableGeneratorAdapter(Generator<E> source) {
	    this.source = source;
    }

    public Class<E> getGeneratedType() {
    	return source.getGeneratedType();
    }

    public void init(GeneratorContext context) throws InvalidGeneratorSetupException {
    	source.init(context);
    }

    public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
    	wrapper.product = source.generate();
    	if (wrapper.product == null)
    		return null;
    	return wrapper;
    }

    public void reset() throws IllegalGeneratorStateException {
    	source.reset();
    }

    public void close() {
    	source.close();
    }

	public boolean isParallelizable() {
	    return source.isParallelizable();
    }

	public boolean isThreadSafe() {
	    return source.isThreadSafe();
    }
	
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "[" + source + "]";
	}
    
}
