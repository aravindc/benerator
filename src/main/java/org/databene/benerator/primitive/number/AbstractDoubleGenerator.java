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

package org.databene.benerator.primitive.number;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.LightweightGenerator;

/**
 * Abstract Double genarator that serves as parent class for implementation of custom Sequences.<br/>
 * <br/>
 * Created: 07.06.2006 18:51:16
 */
public abstract class AbstractDoubleGenerator extends LightweightGenerator<Double> implements NumberGenerator<Double> {

    /** The minimum value to create */
    protected double min;

    /** The maximum value to create */
    protected double max;

    /** the precision used */
    protected double precision;

    /** first distribution parameter */
    protected double variation1;

    /** second distribution parameter */
    protected double variation2;

    /** consistency flag */
    protected boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    protected AbstractDoubleGenerator() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    protected AbstractDoubleGenerator(double min, double max) {
        this(min, max, 1L);
    }

    protected AbstractDoubleGenerator(double min, double max, double precision) {
        this(min, max, precision, 1L, 1L);
    }

    protected AbstractDoubleGenerator(double min, double max, double precision, double variation1, double variation2) {
        if (min > max)
            throw new IllegalArgumentException("min. value (" + min + ") is greater than max. value (" + max + ')');
        this.min = min;
        this.max = max;
        if (precision < 0)
            throw new IllegalArgumentException("Unsupported precision: " + precision);
        this.precision = precision;
        this.variation1 = variation1;
        this.variation2 = variation2;
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
        this.dirty = true;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
        this.dirty = true;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
        this.dirty = true;
    }

    public Double getVariation1() {
        return variation1;
    }

    public void setVariation1(Double variation1) {
        this.variation1 = variation1;
        this.dirty = true;
    }

    public Double getVariation2() {
        return variation2;
    }

    public void setVariation2(Double variation2) {
        this.variation2 = variation2;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

	public Class<Double> getGeneratedType() {
        return Double.class;
    }

    @Override
	public void validate() {
        if (dirty) {
            if (min > max)
                throw new InvalidGeneratorSetupException("min", "greater than max (" + min + ")");
            super.validate();
            dirty = false;
        }
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[min=" + min + ", max=" + max + ", precision=" + precision + ", " +
                "variation1=" + variation1 + ", variation2=" + variation2 + ']';
    }
}
