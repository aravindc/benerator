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

import org.databene.benerator.primitive.number.NumberGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;

/**
 * Abstract parent class for all number-conversion adapters.
 * It hosts a distribution and defines abstract properties to be implemented by child classes.
 *
 * Created: 10.09.2006 19:47:32
 */
public abstract class AbstractNumberGenerator<P extends Number, S extends Number> extends LightweightGenerator<P> implements NumberGenerator<P> {

    protected P min;
    protected P max;
    protected P precision;
    protected P variation1;
    protected P variation2;
    protected Distribution distribution;

    protected NumberGenerator<S> source;

    protected boolean dirty;

    // constructors ----------------------------------------------------------------------------------------------------

    public AbstractNumberGenerator(Class<P> type, P min, P max, P precision) {
        this(type, min, max, precision, Sequence.RANDOM);
    }

    public AbstractNumberGenerator(Class<P> type, P min, P max, P precision, Distribution distribution) {
        this(type, min, max, precision, distribution, one(type), one(type));
    }

    private static <T extends Number> T one(Class<T> type) {
        return NumberToNumberConverter.convert(1, type);
    }

    public AbstractNumberGenerator(Class<P> type, P min, P max, P precision, Distribution distribution, P variation1, P variation2) {
    	super(type);
        this.distribution = distribution;
        setMin(min);
        setMax(max);
        setPrecision(precision);
        setVariation1(variation1);
        setVariation2(variation2);
        this.dirty = true;
    }

    // config properties -----------------------------------------------------------------------------------------------

    public P getMin() {
        return NumberToNumberConverter.convert(min, generatedType);
    }

    public void setMin(P min) {
        this.min = min;
        this.dirty = true;
    }

    public P getMax() {
        return NumberToNumberConverter.convert(max, generatedType);
    }

    public void setMax(P max) {
        this.max = max;
        this.dirty = true;
    }

    public P getPrecision() {
        return NumberToNumberConverter.convert(precision, generatedType);
    }

    public void setPrecision(P precision) {
        this.precision = precision;
        this.dirty = true;
    }

    public P getVariation1() {
        return variation1;
    }

    public void setVariation1(P variation1) {
        this.variation1 = variation1;
        this.dirty = true;
    }

    public P getVariation2() {
        return variation2;
    }

    public void setVariation2(P variation2) {
        this.variation2 = variation2;
        this.dirty = true;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public void validate() {
        if (dirty) {
            if (distribution == null)
                throw new IllegalArgumentException("distribution is null");
            else if (distribution instanceof Sequence)
                source = createSource((Sequence) distribution);
            else if (distribution instanceof WeightFunction)
                source = createSource((WeightFunction) distribution);
            source.validate();
            this.dirty = false;
        }
    }

    @Override
    public boolean available() {
        if (dirty)
            validate();
        return source.available();
    }

    @Override
    public void reset() {
        if (dirty)
            validate();
        super.reset();
        source.reset();
    }

    @Override
    public void close() {
    	if (source != null)
    		source.close();
        super.close();
    }
    // specific implementation -----------------------------------------------------------------------------------------

    protected abstract NumberGenerator<S> createSource(WeightFunction weightFunction);

    protected abstract NumberGenerator<S> createSource(Sequence sequence);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ']';
    }
}
