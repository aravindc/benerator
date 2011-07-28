/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.util.ThreadSafeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * {@link Generator} class for use by the {@link LiteralSequence}.<br/><br/>
 * Created: 03.06.2010 08:48:44
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class PredefinedSequenceGenerator<E extends Number> extends ThreadSafeGenerator<E> { // compare with SequenceGenerator

	private Class<E> numberType;
	private E[] numbers;
	private AtomicInteger cursor;
	
	@SuppressWarnings("unchecked")
    public PredefinedSequenceGenerator(E... numbers) {
	    this.numbers = numbers;
	    this.numberType = (numbers.length > 0 ? (Class<E>) numbers[0].getClass() : (Class<E>) Number.class);
	    this.cursor = new AtomicInteger(0);
    }

	public Class<E> getGeneratedType() {
	    return numberType;
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
	    int i = cursor.getAndIncrement();
	    if (i >= numbers.length)
	    	return null;
	    else
	    	return wrapper.wrap(numbers[i]);
    }

	@Override
	public void reset() {
		this.cursor.set(0);
	    super.reset();
	}

}
