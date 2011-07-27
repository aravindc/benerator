/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorContext;
import org.databene.commons.converter.AnyConverter;
import org.databene.domain.math.FibonacciLongGenerator;

/**
 * Parent class for Number Generators that calculate numbers recursively.
 * Child classes can define recursive sequences easily by defining a depth and 
 * implementing the methods {@link #a0(int)} and {@link #aN()}.<br><br/> 
 * 
 * The recursion depth needs to be specified in the constructor call,
 * {@link #a0(int)} needs to return the predefined initial value(s) of the 
 * sequence (f0, f1, ...) and {@link #aN()} implements the recursion 
 * (fN = f(f(n-1), f(n-2), ...).<br/><br/>
 * 
 * Example: The Fibonacci sequence is defined recursively by
 * <ul>
 *   <li><code>F(0) = 1</code></li>
 *   <li><code>F(1) = 1</code></li>
 *   <li><code>F(n) = F(n-1) + F(n-2)</code></li>
 * </ul>
 * 
 * For a Generator of Long values, this translates to an implementation with<br/><br/>
 * <pre>depth = 2</code>
 *  
 * protected Long aN() {
 *     return aN(-1) + aN(-2);
 * }
 *
 * protected Long a0(int n) {
 *     return (n == 0 ? 0L : 1L);
 * }
 * </pre>
 * 
 * Have a look at the {@link FibonacciLongGenerator} source code for the complete implementation.
 * <br/><br/>
 * Created: 13.10.2009 19:07:27
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class RecurrenceRelationNumberGenerator<E extends Number> extends AbstractNonNullNumberGenerator<E> {

	private final int depth;
	private final LinkedList<E> recentProducts;
	private int n;

    public RecurrenceRelationNumberGenerator(Class<E> targetType, int depth, E min, E max) {
    	super(targetType, min, max, AnyConverter.convert(1, targetType));
    	this.depth = depth;
    	this.recentProducts = new LinkedList<E>();
    	this.n = 0;
	    resetMembers();
    }
    
    public int getDepth() {
    	return depth;
    }

    public int getN() {
    	return n;
    }
    
    // generator interface ---------------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
        super.init(context);
        resetMembers();
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public synchronized E generate() {
    	E next = calculateNext();
    	Comparable<E> c = (Comparable<E>) next;
    	if (max != null && !(c.compareTo(min) >= 0 && c.compareTo(max) <= 0))
    		return null;
	    if (n >= depth)
	    	recentProducts.removeLast();
	    n++;
	    recentProducts.push(next);
	    return next;
    }

	/** See {@link org.databene.benerator.Generator#reset()} */
	@Override
    public void reset() {
	    resetMembers();
	    super.reset();
    }

	/** See {@link org.databene.benerator.Generator#close()} */
	@Override
	public void close() {
	    recentProducts.clear();
		super.close();
	}
	
	// interface to be implemented / used by child classes -------------------------------------------------------------
	
	/**
	 * Must be implemented by child classes to return the seed values of the recurrence relation.
	 * These are the initial values which are defined as constants (a(0)..a(depth-1)). 
	 */
	protected abstract E a0(int n);
	
	/**
	 * Must be implemented by child classes to implement the recurrence relation.
	 * It needs to use the {@link #aN(int)} method to retrieve the most recent calculated values.
	 */
	protected abstract E aN();
	
	/**
	 * Provides the most recent calculated values. The index is the relative index, 
	 * <code>-1</code> stands for <code>a(N-1)</code>.
	 */
	protected final E aN(int offset) {
		return recentProducts.get(- offset - 1);
	}

	// helper methods --------------------------------------------------------------------------------------------------
	
	protected void resetMembers() {
	    recentProducts.clear();
	    n = 0;
    }

	protected E calculateNext() {
	    E result;
	    if (n < depth)
	    	result = a0(n);
	    else
	    	result = aN();
	    return result;
    }

}
