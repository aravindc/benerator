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

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.StringUtil;
import org.databene.commons.converter.NumberToNumberConverter;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.WeightedSample;

/**
 * {@link Sequence} implementation that provides values specified in a comma-separated value list, 
 * use like "new PredefinedSequence('A', 'B', 'C')" or "new PredefinedSequence(5, 7, 11)".<br/><br/>
 * Created: 03.06.2010 08:40:27
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class LiteralSequence extends Sequence {
	
	private Number[] numbers;

	protected LiteralSequence() {
	    this(null);
    }

	protected LiteralSequence(String spec) {
    	setSpec(spec);
    }

	private void setSpec(String spec) {
		this.numbers = parseSpec(spec);
	}

	private static Number[] parseSpec(String spec) {
		if (StringUtil.isEmpty(spec))
			return new Number[0];
	    WeightedSample<?>[] samples = DatabeneScriptParser.parseWeightedLiteralList(spec);
	    Number[] result = new Number[samples.length];
	    for (int i = 0; i < samples.length; i++)
	    	result[i] = (Number) samples[i].getValue();
	    return result;
    }

	@SuppressWarnings("unchecked")
    public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
            boolean unique) {
		Number[] ts = new Number[numbers.length];
		NumberToNumberConverter<Number, T> converter = new NumberToNumberConverter<Number, T>(Number.class, numberType);
		for (int i = 0; i < numbers.length; i++)
			ts[i] = converter.convert(numbers[i]);
	    return WrapperFactory.asNonNullGenerator(new PredefinedSequenceGenerator<T>((T[]) ts));
    }

}
