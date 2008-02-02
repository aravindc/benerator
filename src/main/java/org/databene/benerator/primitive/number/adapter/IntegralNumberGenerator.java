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
import org.databene.commons.ConversionException;
import org.databene.commons.converter.AnyConverter;

/**
 * Parent class for all number-conversion adapters that internally access a LongGenerator.<br/>
 * <br/>
 * Created: 22.08.2006 20:22:44
 */
public class IntegralNumberGenerator<E extends Number> extends AbstractNumberGenerator<E, Long> {

    // constructors ----------------------------------------------------------------------------------------------------

    public IntegralNumberGenerator(Class<E> type, E min, E max, E precision) {
        super(type, min, max, precision);
    }

    public IntegralNumberGenerator(Class<E> type, E min, E max, E precision, Distribution distribution) {
        super(type, min, max, precision, distribution);
    }

    public IntegralNumberGenerator(Class<E> type, E min, E max, E precision, Distribution distribution, E variation1, E variation2) {
        super(type, min, max, precision, distribution, variation1, variation2);
    }

    // generator implementation ----------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
        return type;
    }

    public E generate() {
        if (dirty)
            validate();
        Long n = source.generate();
        try {
            return AnyConverter.convert(n, type);
        } catch (ConversionException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    // private helpers -------------------------------------------------------------------------------------------------

    protected NumberGenerator<Long> createSource(WeightFunction function) {
        return new WeightedLongGenerator(min.longValue(), max.longValue(), precision.longValue(), function);
    }

    protected NumberGenerator<Long> createSource(Sequence sequence) {
        AbstractLongGenerator longGenerator = sequence.createLongGenerator();
        longGenerator.setMin(min.longValue());
        longGenerator.setMax(max.longValue());
        longGenerator.setPrecision(precision.longValue());
        longGenerator.setVariation1(variation1.longValue());
        longGenerator.setVariation2(variation2.longValue());
        return longGenerator;
    }

}
