/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.AbstractGenerator;
import org.databene.commons.IOUtil;
import org.databene.commons.TypedIterable;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Iterates over Iterators that are provided by an {@link Iterable}.<br/>
 * <br/>
 * Created: 16.08.2007 07:09:57
 * @author Volker Bergmann
 */
public class IteratingGenerator<E> extends AbstractGenerator<E> {

    private TypedIterable<E> iterable;
    private Iterator<E> iterator;

    // constructors ----------------------------------------------------------------------------------------------------

    public IteratingGenerator() {
        this(null);
    }

    public IteratingGenerator(TypedIterable<E> iterable) {
        this.iterable = iterable;
        this.iterator = null;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public TypedIterable<E> getIterable() {
        return iterable;
    }

    public void setIterable(TypedIterable<E> iterable) {
        if (this.iterable != null)
        	throw new IllegalGeneratorStateException("Mutating an initialized generator");
        this.iterable = iterable;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public boolean isParallelizable() { // TODO
	    return true;
    }

	public boolean isThreadSafe() { // TODO
	    return true;
    }
    
    @Override
    public void init(GeneratorContext context) {
    	if (iterable == null)
    		throw new InvalidGeneratorSetupException("iterable", "is null");
    	super.init(context);
		createIterator();
    }

    public Class<E> getGeneratedType() {
        return iterable.getType();
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        try {
            assertInitialized();
            if (iterator == null || !iterator.hasNext()) {
            	closeIterator();
            	return null;
            }
        	E result = iterator.next();
        	if (!iterator.hasNext())
	            closeIterator();
			return wrapper.wrap(result);
        } catch (Exception e) {
        	throw new IllegalGeneratorStateException("Generation failed: ", e);
        }
    }

	@Override
    public void reset() {
        closeIterator();
        super.reset();
        createIterator();
    }

    @Override
    public void close() {
        closeIterator();
        super.close();
        if (iterable instanceof Closeable)
        	IOUtil.close((Closeable) iterable);
    }

    // private helpers -------------------------------------------------------------------------------------------------    
    
	private void createIterator() {
	    iterator = iterable.iterator();
    }

    private void closeIterator() {
		if (iterator != null) {
            if (iterator instanceof Closeable)
                IOUtil.close((Closeable) iterator);
            iterator = null;
        }
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + iterable + ']';
    }

}
