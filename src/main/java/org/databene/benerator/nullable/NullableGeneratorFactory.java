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

package org.databene.benerator.nullable;

import static org.databene.model.data.TypeDescriptor.PATTERN;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.Converter;
import org.databene.commons.Validator;
import org.databene.model.data.TypeDescriptor;

/**
 * Provides factory methods for {@link NullableGenerator}s.<br/><br/>
 * Created: 22.07.2010 19:19:04
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class NullableGeneratorFactory {
	
	private NullableGeneratorFactory() {}
	
	public static NullableGenerator<?> createConstantGenerator(Object value) {
		return new ConstantNullableGenerator<Object>(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> NullableGenerator<T>[] wrapAll(Generator<T>[] sources) {
		NullableGenerator<T>[] result = ArrayUtil.newInstance(NullableGenerator.class, sources.length);
		for (int i = 0; i < sources.length; i++)
			result[i] = wrap(sources[i]);
		return result;
	}

	public static <T> NullableGenerator<T> wrap(Generator<T> source) {
		return new AsNullableGeneratorAdapter<T>(source);
	}

	public static <T> NullableGenerator<T> injectNulls(Generator<T> source, double nullQuota) {
		if (nullQuota == 0.)
			return wrap(source);
		else
			return new NullInjectingGeneratorWrapper<T>(source, nullQuota);
	}

	public static <T> NullableGenerator<T> injectNulls(NullableGenerator<T> source, double nullQuota) {
		if (nullQuota == 0.)
			return source;
		else
			return new NullInjectingGeneratorProxy<T>(source, nullQuota);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static NullableGenerator<?> createConvertingGenerator(TypeDescriptor descriptor,
            NullableGenerator<?> generator, BeneratorContext context) {
        Converter<?, ?> converter = DescriptorUtil.getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN))
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            return new ConvertingNullableGeneratorProxy(generator, converter);
        }
        return generator;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static NullableGenerator<?> createConvertingGenerator(NullableGenerator<?> generator, Converter<?,?> converter) {
        if (converter != null)
            return new ConvertingNullableGeneratorProxy(generator, converter);
        return generator;
	}

	public static <T> NullableGenerator<T> wrapWithValidator(Validator<T> validator, NullableGenerator<T> generator) {
		return new ValidatingNullableGeneratorProxy<T>(generator, validator);
	}

	public static <T> NullableGenerator<T> wrapCyclic(NullableGenerator<T> generator) {
		return new CyclicNullableGeneratorProxy<T>(generator);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static NullableGenerator<?> createNullStartingGenerator(Generator<?> source) {
		return new NullStartingGenerator(source);
	}

	public static <T> NullableGenerator<T> createNullStartingGenerator(NullableGenerator<T> source) {
		return new NullStartingNullableGenerator<T>(source);
	}

}
