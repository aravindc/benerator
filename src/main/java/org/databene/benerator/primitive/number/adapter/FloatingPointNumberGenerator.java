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

import org.databene.benerator.primitive.number.distribution.*;
import org.databene.benerator.*;
import org.databene.model.converter.NumberToNumberConverter;

/**
 * Parent class for all number-conversion adapters that internally access a DoubleGenerator.<br/>
 * <br/>
 * Created: 22.08.2006 20:22:44
 */
public class FloatingPointNumberGenerator<E extends Number> extends AbstractNumberGenerator<E, Double> {

    // constructors ----------------------------------------------------------------------------------------------------

    public FloatingPointNumberGenerator(Class<E> type, E min, E max, E precision) {
        super(type, min, max, precision);
    }

    public FloatingPointNumberGenerator(Class<E> type, E min, E max, E precision, Distribution distribution) {
        super(type, min, max, precision, distribution);
    }

    public FloatingPointNumberGenerator(Class<E> type, E min, E max, E precision, Distribution distribution, E variation1, E variation2) {
        super(type, min, max, precision, distribution, variation1, variation2);
    }

    // generator implementation ----------------------------------------------------------------------------------------

    public E generate() {
        if (dirty)
            validate();
        Double x = source.generate();
        return NumberToNumberConverter.convert(x, type);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    protected NumberGenerator<Double> createSource(WeightFunction function) {
        return new WeightedDoubleGenerator(min.doubleValue(), max.doubleValue(), precision.doubleValue(), function);
    }

    protected NumberGenerator<Double> createSource(Sequence sequence) {
        AbstractDoubleGenerator doubleGenerator = sequence.createDoubleGenerator();
        doubleGenerator.setMin(min.doubleValue());
        doubleGenerator.setMax(max.doubleValue());
        doubleGenerator.setPrecision(precision.doubleValue());
        doubleGenerator.setVariation1(variation1.doubleValue());
        doubleGenerator.setVariation2(variation2.doubleValue());
        return doubleGenerator;
    }

}
