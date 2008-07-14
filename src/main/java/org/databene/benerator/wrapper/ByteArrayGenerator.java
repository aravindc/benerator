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
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Creates arrays of random length filled with random bytes.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class ByteArrayGenerator extends AbstractArrayGenerator<Byte, byte[]> {

    // constructors ----------------------------------------------------------------------------------------------------

    public ByteArrayGenerator() {
        this(null, 0, 30, Sequence.RANDOM);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength) {
        this(source, minLength, maxLength, Sequence.RANDOM);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength, Distribution distribution) {
        super(source, byte.class, byte[].class, minLength, maxLength, distribution);
    }

    /** @see org.databene.benerator.Generator#generate() */
    public byte[] generate() {
        int length = sizeGenerator.generate();
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++)
            array[i] = source.generate();
        return array;
    }
}
