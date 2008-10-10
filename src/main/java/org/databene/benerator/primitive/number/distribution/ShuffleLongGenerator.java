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

package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Long Generator that implements a 'shuffle' Long Sequence.<br/>
 * <br/>
 * Created: 18.06.2006 14:40:29
 */
public class ShuffleLongGenerator extends AbstractLongGenerator {

    private long cursor;

    public ShuffleLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ShuffleLongGenerator(long min, long max) {
        this(min, max, 1);
    }

    public ShuffleLongGenerator(long min, long max, long variation1) {
        super(min, max, 1, variation1, variation1);
        this.cursor = min;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Distribution getDistribution() {
        return Sequence.SHUFFLE;
    }

    public long getIncrement() {
        return variation1;
    }

    public void setIncrement(long increment) {
        this.variation1 = increment;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public void validate() {
        if (dirty) {
            cursor = min;
            super.validate();
            this.dirty = false;
        }
    }

    public Long generate() throws IllegalGeneratorStateException {
        if (dirty)
            validate();
        long result = cursor;
        long increment = getIncrement();
        if (cursor + increment <= max)
            cursor += increment;
        else
            cursor = min + ((cursor - min + 1) % increment); // TODO check life cycle
        return result;
    }

}
