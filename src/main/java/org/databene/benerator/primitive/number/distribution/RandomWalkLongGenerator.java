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

import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

/**
 * Long Generator that implements a 'randomWalk' Long Sequence.<br/>
 * <br/>
 * Created: 13.06.2006 07:36:45
 */
public class RandomWalkLongGenerator extends AbstractLongGenerator {

    private long initial;
    private long next;

    private RandomLongGenerator incrementGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public RandomWalkLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public RandomWalkLongGenerator(long min, long max) {
        this(min, max, 1, 1);
    }

    public RandomWalkLongGenerator(long min, long max, long variation1, long variation2) {
        this(min, max, 1, variation1, variation2);
    }

    public RandomWalkLongGenerator(long min, long max, long precision, long variation1, long variation2) {
        super(min, max, precision, variation1, variation2);
        incrementGenerator = new RandomLongGenerator(variation1, variation2);
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Distribution getDistribution() {
        return Sequence.RANDOM_WALK;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    @Override
    public void validate() {
        if (dirty) {
            long minIncrement = variation1;
            long maxIncrement = variation2;
            incrementGenerator.setMin(minIncrement);
            incrementGenerator.setMax(maxIncrement);
            incrementGenerator.setPrecision(precision);
            if (minIncrement < 0 && maxIncrement <= 0)
                initial = max;
            else if (minIncrement >= 0 && maxIncrement > 0)
                initial = min;
            else
                initial = (min + max) / 2;
            next = initial;
            incrementGenerator.validate();
            super.validate();
        }
    }

    public Long generate() {
        if (dirty)
            validate();
        long value = next;
        next += incrementGenerator.generate();
        if (next > max)
            next = max;
        else if (next < min)
            next = min;
        return value;
    }

    @Override
    public void reset() {
    	super.reset();
    	next = initial;
    }
    
    @Override
    public void close() {
    	super.close();
    	next = initial;
    }
}
