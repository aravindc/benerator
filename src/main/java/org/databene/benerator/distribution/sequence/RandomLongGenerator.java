/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.PropertyMessage;
import org.databene.benerator.primitive.number.adapter.AbstractNumberGenerator;

import java.util.Random;

/**
 * Long Generator that implements a 'random' Long Sequence.<br/>
 * <br/>
 * Created: 03.09.2006 09:53:01
 * @author Volker Bergmann
 */
public class RandomLongGenerator extends AbstractNumberGenerator<Long> {

    private static final long DEFAULT_MIN = Long.MIN_VALUE / 2 + 1; // test if it works with these min/max values
	private static final long DEFAULT_MAX = Long.MAX_VALUE / 2 - 1;
	private static final long DEFAULT_PRECISION = 1;

	private static Random random = new Random();

    // constructors ----------------------------------------------------------------------------------------------------

    public RandomLongGenerator() {
    	this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public RandomLongGenerator(long min, Long max) {
        this(min, max, DEFAULT_PRECISION);
    }

    public RandomLongGenerator(long min, Long max, long precision) {
        super(Long.class, min, max, precision);
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public Long generate() {
        return generate(min, max, precision);
    }
    
    // public convenience method ---------------------------------------------------------------------------------------

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
