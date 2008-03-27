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

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.PropertyMessage;
import org.databene.benerator.primitive.number.AbstractLongGenerator;
import org.databene.model.Distribution;
import org.databene.model.Sequence;

import java.util.Random;

/**
 * Long Generator that implements a 'random' Long Sequence.<br/>
 * <br/>
 * Created: 03.09.2006 09:53:01
 */
public class RandomLongGenerator extends AbstractLongGenerator {

    private static Random random = new Random();

    // constructors ----------------------------------------------------------------------------------------------------

    public RandomLongGenerator() {
    }

    public RandomLongGenerator(long min, long max) {
        super(min, max);
    }

    public RandomLongGenerator(long min, long max, long precision) {
        super(min, max, precision);
    }

    public Distribution getDistribution() {
        return Sequence.RANDOM;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public Long generate() {
        return generate(min, max, precision);
    }

    public static long generate(long min, long max, long precision) {
        if (min > max)
            throw new InvalidGeneratorSetupException(
                    new PropertyMessage("min", "greater than max"),
                    new PropertyMessage("max", "less than min"));
        long range = (max - min + precision) / precision;
        long result;
        if (range != 0)
            result = min + Math.abs(random.nextLong() % range) * precision;
        else
            result = random.nextLong() * precision;
        if (result < min)
            result += range;
        return result;
    }

}
