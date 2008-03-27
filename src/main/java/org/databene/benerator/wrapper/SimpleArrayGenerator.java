/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.primitive.number.adapter.IntegerGenerator;
import org.databene.benerator.*;
import org.databene.commons.ArrayUtil;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Assembles the output of a source generator into an array of random length.<br/>
 * <br/>
 * Created: 26.08.2006 09:37:55
 */
public class SimpleArrayGenerator <E> extends GeneratorWrapper<E, E[]> {

    /** The generator that creates the array length */
    private IntegerGenerator sizeGenerator;

    private Class<E> componentType;
    private Class<E[]> generatedType;

    // constructors ----------------------------------------------------------------------------------------------------

    public SimpleArrayGenerator() {
        this(null, null, 0, 30, Sequence.RANDOM);
    }

    public SimpleArrayGenerator(Generator<E> source, Class<E> productType, int minLength, int maxLength) {
        this(source, productType, minLength, maxLength, Sequence.RANDOM);
    }

    public SimpleArrayGenerator(Generator<E> source, Class<E> productType, int minLength, int maxLength, Distribution distribution) {
        super(source);
        this.componentType = productType;
        this.sizeGenerator = new IntegerGenerator(minLength, maxLength, 1, distribution);
    }

    // configuration properties ----------------------------------------------------------------------------------------

    /** Returns the minimum array length to generate */
    public long getMinLength() {
        return sizeGenerator.getMin();
    }

    /** Sets the minimum array length to generate */
    public void setMinLength(int minLength) {
        sizeGenerator.setMin(minLength);
    }

    /** Returns the maximum array length to generate */
    public long getMaxLength() {
        return sizeGenerator.getMin();
    }

    /** Sets the maximum array length to generate */
    public void setMaxLength(int maxLength) {
        sizeGenerator.setMax(maxLength);
    }

    public Distribution getLengthDistribution() {
        return sizeGenerator.getDistribution();
    }

    public void setLengthDistribution(Distribution distribution) {
        sizeGenerator.setDistribution(distribution);
    }

    // generator implementation ----------------------------------------------------------------------------------------

    public Class<E[]> getGeneratedType() {
        return generatedType;
    }

    public void validate() {
        if (dirty) {
            super.validate();
            sizeGenerator.validate();
            if (source == null)
                throw new InvalidGeneratorSetupException("source", " is null");
            Class<E> cType = (componentType != null ? componentType : source.getGeneratedType());
            this.generatedType = ArrayUtil.arrayType(cType);
            dirty = false;
        }
    }

    /** @see org.databene.benerator.Generator#generate() */
    public E[] generate() {
        int length = sizeGenerator.generate();
        E[] array = ArrayUtil.newInstance(componentType, length);
        for (int i = 0; i < length; i++)
            array[i] = source.generate();
        return array;
    }

    // implementation --------------------------------------------------------------------------------------------------

}
