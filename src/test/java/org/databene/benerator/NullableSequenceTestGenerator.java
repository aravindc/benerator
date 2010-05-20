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

package org.databene.benerator;

import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ArrayFormat;

/**
 * Generates a predefined sequence of values for test purposes.<br/><br/>
 * Created: 16.05.2010 11:01:01
 * @since 0.6.2
 * @author Volker Bergmann
 */
public class NullableSequenceTestGenerator<E> implements NullableGenerator<E> {

    private E[] sequence;
    int cursor;
    boolean initialized;

    public NullableSequenceTestGenerator(E... sequence) {
        this.sequence = sequence;
        this.cursor = 0;
        this.initialized = false;
    }

    public void init(GeneratorContext context) {
        if (sequence == null)
            throw new IllegalArgumentException("sequence is null");
    }

    @SuppressWarnings("unchecked")
    public Class<E> getGeneratedType() {
        return (Class<E>) (sequence.length > 0 ? sequence[0].getClass() : Object.class);
    }

	public ProductWrapper<E> generate(ProductWrapper<E> wrapper) {
        if (cursor >= sequence.length)
            return null;
        return wrapper.setProduct(sequence[cursor++]);
    }

    public boolean wasInitialized() {
        return initialized;
    }

    public void reset() {
        this.cursor = 0;
    }

    public void close() {
        this.cursor = sequence.length;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + ArrayFormat.format(sequence) + ']';
    }

	public boolean isParallelizable() {
	    return false;
    }

	public boolean isThreadSafe() {
	    return false;
    }

}
