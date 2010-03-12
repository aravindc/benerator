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

package org.databene.benerator.distribution;

import java.util.List;
import java.util.Random;

import org.databene.benerator.Generator;
import org.databene.benerator.primitive.number.AbstractNumberGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SampleGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.Converter;
import org.databene.commons.converter.ConverterManager;

/**
 * {@link Distribution} implementation which uses the inverse of a probability function integral 
 * for efficiently generating numbers with a given probability distribution. 
 * See <a href="http://www.stat.wisc.edu/~larget/math496/random2.html">Random 
 * Number Generation from Non-uniform Distributions</a>.<br/><br/>
 * Created: 12.03.2010 13:31:16
 * @since 0.6.0
 * @author Volker Bergmann
 */
public abstract class InverseProbabilityIntegral implements Distribution {
	
	public abstract double value(double probability);

	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
		if (unique)
			throw new IllegalArgumentException(this + " cannot generate unique values");
	    List<T> allProducts = GeneratorUtil.allProducts(source);
	    if (allProducts.size() == 1)
	    	return new ConstantGenerator<T>(allProducts.get(0));
	    return new SampleGenerator<T>(source.getGeneratedType(), this, allProducts);
    }

	public <T extends Number> Generator<T> createGenerator(
			Class<T> numberType, T min, T max, T precision, boolean unique) {
		if (unique)
			throw new IllegalArgumentException(this + " cannot generate unique values");
	    return new IPINumberGenerator<T>(this, numberType, min, max, precision);
    }
	
	@Override
	public String toString() {
	    return getClass().getSimpleName();
	}
	
    /**
     * Generates numbers according to an {@link InverseProbabilityIntegral}.<br/><br/>
     * Created: 12.03.2010 14:37:33
     * @since 0.6.0
     * @author Volker Bergmann
     */
    public static class IPINumberGenerator<E extends Number> extends AbstractNumberGenerator<E> {
    	
    	private InverseProbabilityIntegral fcn;
    	private Random random = new Random();
		private Converter<Double, E> converter;
		private double minD;
		private double maxD;
		private double precisionD;
    	
		public IPINumberGenerator(InverseProbabilityIntegral fcn, Class<E> targetType, E min, E max, E precision) {
			super(targetType, min, max, precision);
			this.fcn = fcn;
			this.minD = min.doubleValue();
			this.maxD = max.doubleValue();
			this.precisionD = precision.doubleValue();
			this.converter = ConverterManager.getInstance().createConverter(Double.class, targetType);
        }

		public E generate() {
			double tmp;
			do {
				tmp = fcn.value(random.nextDouble());
			} while (!(tmp >= minD && tmp < maxD + precisionD));
			tmp = Math.floor((tmp - minD) / precisionD) * precisionD + minD;
			return converter.convert(tmp);
        }

    }

}
