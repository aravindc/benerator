/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

import org.databene.commons.ConversionException;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * Quantizes floating point numbers ({@link Double} or {@link Float}) 
 * to be <code>min</code> plus an integral multiple of <code>granularity</code>.<br/><br/>
 * Created: 15.03.2010 15:35:05
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FloatingPointQuantizer<E extends Number> extends ThreadSafeConverter<E, E> {
	
	private Double min;
	private double granularity;
	private NumberToNumberConverter<Double, E> converter;

	public FloatingPointQuantizer(Class<E> numberType, Double min, double granularity) {
	    super(numberType, numberType);
	    this.min = (min != null ? min : 0.);
	    this.granularity = granularity;
	    this.converter = new NumberToNumberConverter<Double, E>(Double.class, numberType);
    }

	public E convert(E sourceValue) throws ConversionException {
		double l = Math.floor((sourceValue.doubleValue() - min) / granularity) * granularity + min;
	    return converter.convert(l);
    }

}
