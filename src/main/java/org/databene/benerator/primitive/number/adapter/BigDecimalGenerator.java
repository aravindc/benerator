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

import org.databene.benerator.Distribution;
import org.databene.benerator.Sequence;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Wrapper for a LongGenerator that maps the generated Longs to BigDecimals.<br/>
 * <br/>
 * Created: 01.07.2006 17:43:29
 */
public class BigDecimalGenerator extends IntegralNumberGenerator<BigDecimal> {

    public static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.01");

    private Integer scale;

    /** Initializes the generator to create uniformly distributed random BigDecimals with precision 1 */
    public BigDecimalGenerator() {
        this(new BigDecimal(Long.MIN_VALUE), new BigDecimal(Long.MAX_VALUE));
    }

    /** Initializes the generator to create uniformly distributed random BigDecimals with precision 1 */
    public BigDecimalGenerator(BigDecimal min, BigDecimal max) {
        this(min, max, DEFAULT_PRECISION);
    }

    /** Initializes the generator to create uniformly distributed random BigDecimals */
    public BigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal precision) {
        this(min, max, precision, Sequence.RANDOM);
    }

    /** Initializes the generator to create uniformly distributed random BigDecimals with the specified precision */
    public BigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal precision, Distribution distribution) {
        this(min, max, precision, distribution, new BigDecimal(1), new BigDecimal(1));
    }

    /** Initializes the generator to create BigDecimals */
    public BigDecimalGenerator(BigDecimal min, BigDecimal max, BigDecimal precision, Distribution distribution, BigDecimal variation1, BigDecimal variation2) {
        super(BigDecimal.class, min, max, precision, distribution, variation1, variation2);
    }

    // config properties -----------------------------------------------------------------------------------------------

    public void setPrecision(BigDecimal precision) {
        super.setPrecision(precision);
        scale = precision.scale();
        dirty = true;
    }

    public Integer getScale() {
        return scale;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    /** Sets the precision */
    public void validate() {
        if (dirty) {
            super.validate();
            dirty = true;
            source.setMin(0L);
            source.setMax((long)((max.doubleValue() - min.doubleValue()) / precision.doubleValue()));
            source.setPrecision(1L);
            source.validate();
            dirty = false;
        }
    }
    
    public BigDecimal generate() {
        if (dirty)
            validate();
        BigDecimal tmp = super.generate().multiply(precision).add(min);
        return tmp.setScale(scale, RoundingMode.HALF_UP);
    }

}
