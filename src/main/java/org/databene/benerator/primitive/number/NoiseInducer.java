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

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.script.ArithmeticEngine;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.context.ContextAware;
import org.databene.commons.converter.ConverterManager;
import org.databene.commons.converter.ThreadSafeConverter;

/**
 * TODO Document class.<br/><br/>
 * Created: 06.10.2010 17:14:46
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class NoiseInducer extends ThreadSafeConverter<Number, Number> implements ContextAware { // TODO test
	
	private Number minNoise;
	private Number maxNoise;
	private Number noisePrecision;
	private Distribution noiseDistribution;
	private boolean relative;
	
	private Class<? extends Number> numberType;
	private ArithmeticEngine arithmetic;
	private Generator<Number> noiseGenerator;
	private Context context;

	protected NoiseInducer() {
	    this(0, 0);
    }
	
	protected NoiseInducer(Number minNoise, Number maxNoise) {
	    super(Number.class, Number.class);
	    this.minNoise = minNoise;
	    this.maxNoise = maxNoise;
	    this.noisePrecision = 1;
	    this.noiseDistribution = SequenceManager.CUMULATED_SEQUENCE;
	    this.relative = true;
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public Number getMinNoise() {
    	return minNoise;
    }

	public void setMinNoise(Number minNoise) {
    	this.minNoise = minNoise;
    }

	public Number getMaxNoise() {
    	return maxNoise;
    }

	public void setMaxNoise(Number maxNoise) {
    	this.maxNoise = maxNoise;
    }

	public Number getNoisePrecision() {
    	return noisePrecision;
    }

	public void setPrecision(Number noisePrecision) {
    	this.noisePrecision = noisePrecision;
    }

	public Distribution getNoiseDistribution() {
    	return noiseDistribution;
    }

	public void setNoiseDistribution(Distribution noiseDistribution) {
    	this.noiseDistribution = noiseDistribution;
    }

	public boolean isRelative() {
    	return relative;
    }

	public void setRelative(boolean relative) {
    	this.relative = relative;
    }

	public void setContext(Context context) {
	    this.context = context;
    }

	// Converter interface implementation ------------------------------------------------------------------------------
	
	public Number convert(Number sourceValue) {
		if (sourceValue == null)
			return null;
		if (numberType == null)
			initialize(sourceValue);
		Number delta = noiseGenerator.generate();
		Number result;
		if (relative)
			result = (Number) arithmetic.multiply(sourceValue, arithmetic.subtract(1, delta));
		else
			result = (Number) arithmetic.add(sourceValue, delta);
	    return result;
    }

	public Number convert(Number sourceValue, Number minValue, Number maxValue) {
		if (sourceValue == null)
			return null;
		Number result = convert(sourceValue);
		double rd = result.doubleValue();
		if (rd < minValue.doubleValue())
			return minValue;
		if (rd > maxValue.doubleValue())
			return maxValue;
		return result;
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
    private void initialize(Number sourceValue) {
	    this.numberType = sourceValue.getClass();
	    Converter<Number, ? extends Number> converter = ConverterManager.getInstance().createConverter(Number.class, numberType);
	    arithmetic = new ArithmeticEngine();
	    noiseGenerator = GeneratorFactory.getNumberGenerator(
	    		(Class<Number>) numberType, 
	    		(Number) converter.convert(minNoise), 
	    		(Number) converter.convert(maxNoise), 
	    		(Number) converter.convert(noisePrecision), 
	    		noiseDistribution, false);
	    noiseGenerator.init((GeneratorContext) context);
	}

}