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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.util.RandomUtil;

/**
 * Generates random {@link BigDecimal}s with a uniform distribution.
 * <br/>
 * Created at 23.06.2009 23:36:15
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomBigDecimalGenerator extends LightweightGenerator<BigDecimal> {

	private static final BigDecimal DEFAULT_MIN = BigDecimal.valueOf(Double.MIN_VALUE);
	private static final BigDecimal DEFAULT_MAX = BigDecimal.valueOf(Double.MAX_VALUE);
	private static final BigDecimal DEFAULT_PRECISION = BigDecimal.valueOf(1);
	
	private BigDecimal min;
	private BigDecimal max;
	private BigDecimal precision;
	private BigDecimal range;

    public RandomBigDecimalGenerator() {
        this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max) {
        this(min, max, DEFAULT_PRECISION);
    }

    public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal precision) {
        this.min = min;
        this.max = max;
        this.precision = precision;
        BigDecimal tmp = max.subtract(min).divide(precision);
        tmp.setScale(0, RoundingMode.DOWN);
        this.range = tmp.multiply(precision);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public Class<BigDecimal> getGeneratedType() {
    	return BigDecimal.class;
    }

    public BigDecimal generate() {
        long n = range.divide(precision).longValue();
        BigDecimal i = BigDecimal.valueOf(RandomUtil.randomLong(0, n));
		return min.add(i.multiply(precision));
    }

    // properties ------------------------------------------------------------------------------------------------------

    public BigDecimal getMin() {
    	return min;
    }

	public BigDecimal getMax() {
    	return max;
    }

	public BigDecimal getPrecision() {
    	return precision;
    }

}
