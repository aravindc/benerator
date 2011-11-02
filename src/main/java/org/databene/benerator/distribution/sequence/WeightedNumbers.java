/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ConfigurationError;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.WeightedSample;

/**
 * Generates numbers with weights that are defined using a literal, 
 * for example "1^3,2^7" would cause generation of 30% '1' values and 
 * 70% '2' values.<br/><br/>
 * Created: 02.06.2010 07:27:36
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class WeightedNumbers<E> extends Sequence {

	private WeightedSample<?>[] samples;
	
    public WeightedNumbers() {
    	this(null);
    }

    public WeightedNumbers(String spec) {
    	if (spec != null)
    		setSpec(spec);
    }

	public void setSpec(String spec) {
		samples = DatabeneScriptParser.parseWeightedLiteralList(spec);
	}

	public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
            boolean unique) {
		if (unique)
			throw new ConfigurationError(getClass().getSimpleName() + " is not designed to generate unique values");
		AttachedWeightSampleGenerator<T> generator = new AttachedWeightSampleGenerator<T>(numberType);
		for (int i = 0; i < samples.length; i++)
			generator.addSample(
					NumberToNumberConverter.convert((Number) samples[i].getValue(), numberType), 
					samples[i].getWeight());
		return WrapperFactory.asNonNullGenerator(generator);
    }

	@Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
		if (unique)
			throw new ConfigurationError(getClass().getSimpleName() + " is not designed to generate unique values");
	    return super.applyTo(source, unique);
	}
	
}
