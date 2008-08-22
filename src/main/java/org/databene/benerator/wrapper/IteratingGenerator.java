/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.Heavyweight;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.TypedIterable;

import java.util.Iterator;

/**
 * Iterates over Iterators that are provided by an Iterable.<br/>
 * <br/>
 * Created: 16.08.2007 07:09:57
 */
public class IteratingGenerator<E> implements Generator<E> {

	private static final int NEW       = -1;
	private static final int AVAILABLE =  0;
	private static final int UTILIZED  =  1;
	private static final int CLOSED    =  2;
	
    private TypedIterable<E> iterable;

    private Iterator<E> iterator;
    private int state;

    // constructors ----------------------------------------------------------------------------------------------------

    public IteratingGenerator() {
        this(null);
    }

    public IteratingGenerator(TypedIterable<E> iterable) {
        this.iterable = iterable;
        this.state = NEW;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public TypedIterable<E> getIterable() {
        return iterable;
    }

    public void setIterable(TypedIterable<E> iterable) {
        if (state != NEW)
        	throw new IllegalGeneratorStateException("Mutating an initialized generator");
        this.iterable = iterable;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public void validate() {
    	if (iterable == null)
    		throw new InvalidGeneratorSetupException("iterable", "is null");
    }

    public Class<E> getGeneratedType() {
        return iterable.getType();
    }

    public boolean available() {
    	if (state == NEW)
    		initialize();
        return (iterator != null && iterator.hasNext());
    }

    public E generate() {
        try {
        	if (state == NEW)
        		initialize();
        	if (state != AVAILABLE)
        		throw GeneratorUtil.stateException(this);
        	E result = iterator.next();
        	if (!iterator.hasNext())
        		closeIterator(UTILIZED);
			return result;
        } catch (Exception e) {
        	throw new IllegalGeneratorStateException("Generation failed: ", e);
        }
    }

	public void reset() {
        closeIterator(AVAILABLE);
        initialize();
    }

    public void close() {
        closeIterator(CLOSED);
        if (iterable instanceof Heavyweight)
        	((Heavyweight) iterable).close();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + "[" + iterable + ']';
    }
    
    // private helpers -------------------------------------------------------------------------------------------------    
    
    private void initialize() {
    	switch (state) {
	    	case NEW: 
	    	case AVAILABLE:
	    	case UTILIZED:
	    		if (iterator == null)
    				iterator = iterable.iterator();
				state = AVAILABLE;
				break;
	    	case CLOSED:
	    		throw new IllegalGeneratorStateException("Generator is not available");
    	}
	}

	private void closeIterator(int state) {
		if (iterator != null) {
            if (iterator instanceof HeavyweightIterator)
                ((HeavyweightIterator)iterator).close();
            iterator = null;
        }
		this.state = state;
	}

}
