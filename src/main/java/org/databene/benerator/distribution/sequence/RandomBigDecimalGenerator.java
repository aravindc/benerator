/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.RandomUtil;
import org.databene.benerator.util.ThreadSafeNonNullGenerator;

/**
 * Generates random {@link BigDecimal}s with a uniform distribution.
 * <br/>
 * Created at 23.06.2009 23:36:15
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class RandomBigDecimalGenerator extends ThreadSafeNonNullGenerator<BigDecimal> {

	private static final BigDecimal DEFAULT_MIN = BigDecimal.valueOf(Double.MIN_VALUE);
	private static final BigDecimal DEFAULT_MAX = BigDecimal.valueOf(Double.MAX_VALUE);
	private static final BigDecimal DEFAULT_GRANULARITY = BigDecimal.valueOf(1);
	
	private BigDecimal min;
	private BigDecimal max;
	private BigDecimal granularity;
	private BigDecimal range;

    public RandomBigDecimalGenerator() {
        this(DEFAULT_MIN, DEFAULT_MAX);
    }

    public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max) {
        this(min, max, DEFAULT_GRANULARITY);
    }

    public RandomBigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal granularity) {
        this.min = min;
        this.max = max;
        this.granularity = granularity;
        BigDecimal tmp = max.subtract(min).divide(granularity);
        tmp.setScale(0, RoundingMode.DOWN);
        this.range = tmp.multiply(granularity);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public Class<BigDecimal> getGeneratedType() {
    	return BigDecimal.class;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
    	if (BigDecimal.ONE.compareTo(granularity) == 0)
    		throw new InvalidGeneratorSetupException(getClass().getSimpleName() + ".granularity may not be 0");
	    super.init(context);
	}
	
	@Override
	public BigDecimal generate() {
        long n = range.divide(granularity).longValue();
        BigDecimal i = BigDecimal.valueOf(RandomUtil.randomLong(0, n));
		return min.add(i.multiply(granularity));
    }

    // properties ------------------------------------------------------------------------------------------------------

    public BigDecimal getMin() {
    	return min;
    }

	public BigDecimal getMax() {
    	return max;
    }

	public BigDecimal getGranularity() {
    	return granularity;
    }

}
