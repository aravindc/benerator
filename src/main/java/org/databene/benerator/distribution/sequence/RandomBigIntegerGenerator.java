/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.math.BigInteger;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.PropertyMessage;
import org.databene.benerator.util.ThreadSafeGenerator;

/**
 * Generates random {@link BigInteger} with a uniform distribution.<br/>
 * <br/>
 * Created at 23.06.2009 23:26:06
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomBigIntegerGenerator extends ThreadSafeGenerator<BigInteger> {

    private static final BigInteger DEFAULT_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);
	private static final BigInteger DEFAULT_PRECISION = BigInteger.valueOf(1);

    private BigInteger min;
    private BigInteger max;
    private BigInteger precision;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public RandomBigIntegerGenerator() {
    	this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public RandomBigIntegerGenerator(BigInteger min, BigInteger max) {
        this(min, max, DEFAULT_PRECISION);
    }

    public RandomBigIntegerGenerator(BigInteger min, BigInteger max, BigInteger precision) {
        this.min = min;
        this.max = max;
        this.precision = precision;
    }

    // Generator implementation ----------------------------------------------------------------------------------------

    public Class<BigInteger> getGeneratedType() {
	    return BigInteger.class;
    }

    public BigInteger generate() {
        return generate(min, max, precision);
    }
    
    // public convenience method ---------------------------------------------------------------------------------------

    public static BigInteger generate(BigInteger min, BigInteger max, BigInteger precision) {
        if (min.compareTo(max) > 0)
            throw new InvalidGeneratorSetupException(
                    new PropertyMessage("min", "greater than max"),
                    new PropertyMessage("max", "less than min"));
        long range = max.subtract(min).divide(precision).longValue();
        return min.add(BigInteger.valueOf(RandomLongGenerator.generate(0, range, 1)).multiply(precision));
    }

}
