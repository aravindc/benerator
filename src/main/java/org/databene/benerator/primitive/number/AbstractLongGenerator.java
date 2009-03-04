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

package org.databene.benerator.primitive.number;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.LightweightGenerator;

/**
 * Abstract Long genarator that serves as parent class for implementation of custom Sequences.<br/>
 * <br/>
 * Created: 07.06.2006 18:51:16
 */
public abstract class AbstractLongGenerator extends LightweightGenerator<Long> implements NumberGenerator<Long> {

    protected long min;
    protected long max;
    protected long precision;
    protected long variation1;
    protected long variation2;

    protected boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    protected AbstractLongGenerator() {
        this(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    protected AbstractLongGenerator(long min, long max) {
        this(min, max, 1L);
    }

    protected AbstractLongGenerator(long min, long max, long precision) {
        this(min, max, precision, 1L, 1L);
    }

    protected AbstractLongGenerator(long min, long max, long precision, long variation1, long variation2) {
    	super(Long.class);
        setMin(min);
        setMax(max);
        setPrecision(precision);
        setVariation1(variation1);
        setVariation2(variation2);
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
        this.dirty = true;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
        this.dirty = true;
    }

    public Long getPrecision() {
        return precision;
    }

    public void setPrecision(Long precision) {
        this.precision = precision;
        this.dirty = true;
    }

    public Long getVariation1() {
        return variation1;
    }

    public void setVariation1(Long variation1) {
        this.variation1 = variation1;
        this.dirty = true;
    }

    public Long getVariation2() {
        return variation2;
    }

    public void setVariation2(Long variation2) {
        this.variation2 = variation2;
        this.dirty = true;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
	public Class<Long> getGeneratedType() {
        return Long.class;
    }

    @Override
	public void validate() {
        if (min > max)
            throw new InvalidGeneratorSetupException("min", " min (" + min + ") greater than max (" + max + ')');
        super.validate();
        this.dirty = false;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + "[min=" + min + ", max=" + max + ", precision=" + precision + ", " +
                "variation1=" + variation1 + ", variation2=" + variation2 + ']';
    }
}
