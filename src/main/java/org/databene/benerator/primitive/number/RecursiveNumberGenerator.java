/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.number;

import java.util.LinkedList;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.commons.converter.AnyConverter;

/**
 * Parent class for Number Generators that works recursively.<br/><br/>
 * Created: 13.10.2009 19:07:27
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class RecursiveNumberGenerator<E extends Number> extends AbstractNumberGenerator<E> {

	private int depth;
	protected LinkedList<E> recentProducts;
	private int n;

    public RecursiveNumberGenerator(Class<E> targetType, int depth, E min, E max) {
    	super(targetType, min, max, AnyConverter.convert(1, targetType));
    	this.depth = depth;
    	this.recentProducts = new LinkedList<E>();
	    reset();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean available() {
    	E next = calculateNext();
    	Comparable<E> c = (Comparable<E>) next;
    	return (max == null || (c.compareTo(min) >= 0 && c.compareTo(max) <= 0));
    }
    
    public E generate() {
    	if (!available())
    		throw new IllegalGeneratorStateException("Generator not available any more. " +
    				"Query available() before calling generate().");
	    E result = calculateNext();
	    if (n >= depth)
	    	recentProducts.removeLast();
	    n++;
	    recentProducts.push(result);
	    return result;
    }

	protected E calculateNext() {
	    E result;
	    if (n < depth)
	    	result = baseValue(n);
	    else
	    	result = recursion();
	    return result;
    }

	@Override
    public void reset() {
	    recentProducts.clear();
	    n = 0;
	    super.reset();
    }

	@Override
	public void close() {
	    recentProducts = null;
		super.close();
	}
	
	protected abstract E recursion();

	protected abstract E baseValue(int n);
	
}
