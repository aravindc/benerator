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

import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Wrapper for a LongGenerator that forwards the generated Longs.<br/>
 * <br/>
 * Created: 07.06.2006 19:04:08
 */
public class LongGenerator extends IntegralNumberGenerator<Long> {

    /** Initializes the generator to create uniformly distributed random Longs with precision 1 */
    public LongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /** Initializes the generator to create uniformly distributed random Longs with precision 1 */
    public LongGenerator(Long min, Long max) {
        this(min, max, 1L);
    }

    /** Initializes the generator to create uniformly distributed random Longs with the specified precision */
    public LongGenerator(Long min, Long max, Long precision) {
        this(min, max, precision, Sequence.RANDOM);
    }

    /** Initializes the generator to create Longs */
    public LongGenerator(Long min, Long max, Long precision, Distribution distribution) {
        super(Long.class, min, max, precision, distribution);
    }
}
