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

package org.databene.benerator.primitive.number.adapter;

import org.databene.benerator.Sequence;
import org.databene.benerator.Distribution;

/**
 * Wrapper for a LongGenerator that maps the generated Longs to Bytes.<br/>
 * <br/>
 * Created: 23.06.2006 20:11:05
 */
public class ByteGenerator extends IntegralNumberGenerator<Byte> {

    /** Initializes the generator to create uniformly distributed random Bytes with precision 1 */
    public ByteGenerator() {
        this(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    /** Initializes the generator to create uniformly distributed random Bytes with precision 1 */
    public ByteGenerator(byte min, byte max) {
        this(min, max, (byte)1);
    }

    /** Initializes the generator to create uniformly distributed random Bytes with the specified precision */
    public ByteGenerator(byte min, byte max, byte precision) {
        this(min, max, precision, Sequence.RANDOM);
    }

    /** Initializes the generator to create Bytes */
    public ByteGenerator(byte min, byte max, byte precision, Distribution distribution) {
        super(Byte.class, min, max, precision, distribution);
    }
}
