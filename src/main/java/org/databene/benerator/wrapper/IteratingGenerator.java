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
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.TypedIterable;

import java.util.Iterator;

/**
 * Iterates over Iterators that are provided by an Iterable.<br/>
 * <br/>
 * Created: 16.08.2007 07:09:57
 */
public class IteratingGenerator<E> implements Generator<E> {

    private TypedIterable<E> iterable;

    private Iterator<E> iterator;
    private boolean dirty;

    public IteratingGenerator() {
        this(null);
    }

    public IteratingGenerator(TypedIterable<E> iterable) {
        this.iterable = iterable;
        this.dirty = true;
    }

    public TypedIterable<E> getIterable() {
        return iterable;
    }

    public void setIterable(TypedIterable<E> iterable) {
        this.iterable = iterable;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public void validate() {
        if (dirty) {
            if (iterable == null)
                throw new InvalidGeneratorSetupException("iterable", "is null");
            close();
            this.iterator = iterable.iterator();
            dirty = false;
        }
    }

    public Class<E> getGeneratedType() {
        if (dirty)
            validate();
        return iterable.getType();
    }

    public E generate() {
        try {
	        if (dirty)
	            validate();
        	return iterator.next();
        } catch (Exception e) {
        	throw new IllegalGeneratorStateException("Generation failed: ", e);
        }
    }

    public void reset() {
        close();
        iterator = iterable.iterator();
    }

    public void close() {
        if (iterator != null) {
            if (iterator instanceof HeavyweightIterator)
                ((HeavyweightIterator)iterator).close();
            iterator = null;
        }
    }

    public boolean available() {
        if (dirty)
            validate();
        return (iterator != null && iterator.hasNext());
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return "IteratingGenerator[" + iterable + ']';
    }
}
