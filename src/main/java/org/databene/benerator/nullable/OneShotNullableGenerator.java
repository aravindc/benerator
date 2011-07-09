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

package org.databene.benerator.nullable;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * {@link NullableGenerator} implementation which generates a given value 
 * exactly once and then goes unavailable.<br/><br/>
 * Created: 06.07.2011 17:37:49
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class OneShotNullableGenerator<E> extends AbstractNullableGenerator<E> { 
	// TODO extract common parts to common parent class of this class and OneShotGenerator

	private E value;
	private Class<E> generatedType;
	private boolean used;
	
    @SuppressWarnings("unchecked")
	public OneShotNullableGenerator(E value) {
	    this(value, (Class<E>) value.getClass());
    }

    public OneShotNullableGenerator(E value, Class<E> generatedType) {
	    this.value = value;
	    this.generatedType = generatedType;
	    this.used = false;
    }

    @Override
    public void close() {
    	used = true;
	    value = null;
	    super.close();
    }

    public synchronized ProductWrapper<E> generate(ProductWrapper<E> wrapper) throws IllegalGeneratorStateException {
	    if (used)
	    	return null;
	    used = true;
	    return wrapper.setProduct(value);
    }

    public Class<E> getGeneratedType() {
	    return generatedType;
    }

    @Override
    public void reset() {
	    used = false;
	    super.reset();
    }

	public boolean isParallelizable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}

}
