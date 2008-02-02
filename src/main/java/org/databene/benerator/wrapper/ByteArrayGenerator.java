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

import org.databene.benerator.Distribution;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorWrapper;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.Sequence;
import org.databene.benerator.primitive.number.adapter.IntegerGenerator;

/**
 * Creates arrays of random length filled with random bytes.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class ByteArrayGenerator extends GeneratorWrapper<Byte, byte[]> {

    /** The generator that creates the array length */
    private IntegerGenerator sizeGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public ByteArrayGenerator() {
        this(null, 0, 30, Sequence.RANDOM);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength) {
        this(source, minLength, maxLength, Sequence.RANDOM);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength, Distribution distribution) {
        super(source);
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

    public Class<byte[]> getGeneratedType() {
        return byte[].class;
    }

    public void validate() {
        if (dirty) {
            super.validate();
            sizeGenerator.validate();
            if (source == null)
                throw new InvalidGeneratorSetupException("source", " is null");
            dirty = false;
        }
    }

    /** @see org.databene.benerator.Generator#generate() */
    public byte[] generate() {
        int length = sizeGenerator.generate();
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++)
            array[i] = source.generate();
        return array;
    }

    // implementation --------------------------------------------------------------------------------------------------

}
