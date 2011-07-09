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

import org.databene.benerator.wrapper.ProductWrapper;

/**
 * {@link NullableGenerator} implementation which returns a constant value 
 * (supporting <code>null</code> as value).<br/><br/>
 * Created: 30.04.2010 12:21:47
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ConstantNullableGenerator<E> extends AbstractNullableGenerator<E> {
	
	private Class<E> generatedType;
	private E value;

	@SuppressWarnings("unchecked")
    public ConstantNullableGenerator(E value) {
	    this(value, (Class<E>) (value != null ? value.getClass() : Object.class));
    }

	public ConstantNullableGenerator(Class<E> generatedType) {
	    this(null, generatedType);
    }

	public ConstantNullableGenerator(E value, Class<E> generatedType) {
		this.value = value;
	    this.generatedType = generatedType;
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
	    return wrapper.setProduct(value);
    }

	public Class<E> getGeneratedType() {
	    return generatedType;
    }

	public boolean isParallelizable() {
	    return true;
    }

	public boolean isThreadSafe() {
	    return true;
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + value + ']';
	}
	
}
