/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.DistributingGenerator;
import org.databene.benerator.wrapper.ValidatingGeneratorProxy;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.LocaleUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.converter.ParseFormatConverter;
import org.databene.commons.converter.String2DateConverter;
import org.databene.commons.validator.AndValidator;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.Iteration;
import org.databene.model.data.PrimitiveType;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import static org.databene.model.data.TypeDescriptor.*;
import static org.databene.benerator.factory.GeneratorFactoryUtil.*;

/**
 * Creates generators of type instances.<br/><br/>
 * Created: 05.03.2008 16:51:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class TypeGeneratorFactory {
    
    private static final Log logger = LogFactory.getLog(TypeGeneratorFactory.class);

    public static Generator<? extends Object> createTypeGenerator(TypeDescriptor descriptor, boolean unique, BeneratorContext context, GenerationSetup setup) {
    	if (logger.isDebugEnabled())
    		logger.debug(descriptor + ", " + unique);
        if (descriptor instanceof SimpleTypeDescriptor)
            return SimpleTypeGeneratorFactory.createSimpleTypeGenerator((SimpleTypeDescriptor) descriptor, false, unique, context, setup);
        else if (descriptor instanceof ComplexTypeDescriptor)
            return ComplexTypeGeneratorFactory.createComplexTypeGenerator((ComplexTypeDescriptor) descriptor, unique, context, setup);
        else
            throw new UnsupportedOperationException("Descriptor type not supported: " + descriptor.getClass());
    }

    protected static Generator<? extends Object> createByGeneratorName(TypeDescriptor descriptor, BeneratorContext context) {
        Generator<? extends Object> generator = null;
        String generatorClassName = descriptor.getGenerator();
        if (generatorClassName != null) {
        	generator = (Generator<Object>) newInstance(generatorClassName, context);
            mapDetailsToBeanProperties(descriptor, generator, context);
        }
        return generator;
    }

	private static Object newInstance(String className, BeneratorContext context) {
		Class generatorClass = context.forName(className);
		return BeanUtil.newInstance(generatorClass);
	}
    
    protected static Generator<? extends Object> createSampleGenerator(TypeDescriptor descriptor, boolean unique) {
        Generator<? extends Object> generator = null;
        // check for samples
        String[] values = descriptor.getValues();
        if (!ArrayUtil.isEmpty(values)) {
            Distribution distribution = getDistribution(descriptor, unique);
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator<String>(String.class, (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator<String>(String.class, (WeightFunction) distribution, values);
            else
                throw new ConfigurationError("Unsupported distribution type: " + distribution.getClass());
        }
        return generator;
    }

    protected static Generator<? extends Object> createScriptGenerator(
            TypeDescriptor descriptor, Context context) {
        Generator<String> generator = null;
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseUnspecificText(scriptText);
            generator = new ScriptGenerator(script, context);
        }
        return generator;
    }

    static Validator getValidator(TypeDescriptor descriptor, BeneratorContext context) {
        String validatorSpec = descriptor.getValidator();
        if (StringUtil.isEmpty(validatorSpec))
            return null;
        String[] specs = StringUtil.tokenize(validatorSpec, ',');
        if (specs.length == 1) {
        	return parseSingleValidatorSpec(validatorSpec, context);
        } else {
        	Validator[] validators = new Validator[specs.length];
        	for (int i = 0; i < specs.length; i++)
        		validators[i] = parseSingleValidatorSpec(specs[i], context);
			return new AndValidator(validators);
        }
    }

	static Validator parseSingleValidatorSpec(String validatorSpec,
			BeneratorContext context) {
		Object validator = null;
        // first try to resolve the converterSpec from the context
        validator = context.get(validatorSpec);
        // if the converter is not in the context, interpret the converterSpec as classname
        if (validator == null)
        	validator = newInstance(validatorSpec, context);
        if (!(validator instanceof Validator))
        	throw new ConfigurationError(validatorSpec + " is not an instance of " + Validator.class);
		return (Validator) validator;
	}
	
	static Converter getConverter(TypeDescriptor descriptor, BeneratorContext context) {
        String converterSpec = descriptor.getConverter();
        if (StringUtil.isEmpty(converterSpec))
            return null;
        String[] specs = StringUtil.tokenize(converterSpec, ',');
        if (specs.length == 1) {
        	return parseConverterSpec(converterSpec, context);
        } else {
        	Converter[] converters = new Converter[specs.length];
        	for (int i = 0; i < specs.length; i++)
        		converters[i] = parseConverterSpec(specs[i], context);
			return new ConverterChain(converters);
        }
    }

	private static Converter parseConverterSpec(String converterSpec,
			BeneratorContext context) {
		Object converter = null;
        // first try to resolve the converterSpec from the context
        converter = context.get(converterSpec);
        // if the converter is not in the context, interpret the converterSpec as class name
        if (converter == null)
        	converter = newInstance(converterSpec, context);
        if (converter instanceof java.text.Format)
        	converter = new FormatFormatConverter((java.text.Format) converter);
        if (!(converter instanceof Converter))
        	throw new ConfigurationError(converterSpec + " is not an instance of " + Converter.class);
		return (Converter) converter;
	}
/*
    public static void checkUsedDetails(TypeDescriptor descriptor,
            Set<String> usedDetails) {
        for (FeatureDetail<? extends Object> detail : descriptor.getDetails()) {
            String name = detail.getName();
            if (!NAME.equals(name) && detail.getValue() != null
                    && !usedDetails.contains(name))
                logger.debug("Ignored detail: " + detail + " in descriptor "
                        + descriptor); // TODO v1.0 improve tracking of unused features
        }
    }
*/
    protected static <T> Generator<T> wrapWithProxy(Generator<T> generator,
            TypeDescriptor descriptor) {
        // check cyclic flag
        Boolean cyclic = descriptor.isCyclic();
        if (cyclic == null)
            cyclic = false;

        // check proxy
        Long proxyParam1 = null;
        Long proxyParam2 = null;
        Iteration iteration = descriptor.getProxy();
        if (iteration != null) {
            proxyParam1 = descriptor.getProxyParam1();
            proxyParam2 = descriptor.getProxyParam2();
        }
        return GeneratorFactory.createProxy(generator, cyclic, iteration,
                proxyParam1, proxyParam2);
    }

    protected static <T> Generator<T> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<T> generator, BeneratorContext context) {
        Validator<T> validator = getValidator(descriptor, context);
        if (validator != null)
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        return generator;
    }

    protected static Locale getLocale(TypeDescriptor descriptor) {
        Locale locale = descriptor.getLocale();
        if (locale == null)
            locale = (Locale) descriptor.getDetailDefault(LOCALE);
        if (locale == null)
            locale = LocaleUtil.getFallbackLocale();
        return locale;
    }

    protected static DateFormat getPatternAsDateFormat(TypeDescriptor descriptor) {
        String pattern = descriptor.getPattern();
        if (pattern != null)
            return new SimpleDateFormat(pattern);
        else
        	return TimeUtil.createDefaultDateFormat();
    }

    protected static Generator createConvertingGenerator(TypeDescriptor descriptor, Generator generator, BeneratorContext context) {
        Converter converter = getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = GeneratorFactory.getConvertingGenerator(generator, converter);
        }
        return generator;
    }

    protected static Distribution getDistribution(TypeDescriptor descriptor, boolean unique) {
        Distribution distribution = descriptor.getDistribution();
        if (distribution == null)
            distribution = (unique ? Sequence.BIT_REVERSE : Sequence.RANDOM);
        return distribution;
    }

	public static Generator<? extends Object> applyDistribution(TypeDescriptor descriptor,
			Distribution distribution, Generator<? extends Object> generator) {
		return new DistributingGenerator(generator, distribution, descriptor.getVariation1(), descriptor.getVariation2());
	}

	static <E> Generator<E> wrapWithPostprocessors(Generator<E> generator, TypeDescriptor descriptor, BeneratorContext context) {
		generator = createConvertingGenerator(descriptor, generator, context);
		if (descriptor instanceof SimpleTypeDescriptor)
			generator = createTypeConvertingGenerator((SimpleTypeDescriptor) descriptor, generator);
        generator = createValidatingGenerator(descriptor, generator, context);
		return generator;
	}
    
    static <S, T> Generator<T> createTypeConvertingGenerator(
            SimpleTypeDescriptor descriptor, Generator<S> generator) {
        if (descriptor == null || descriptor.getPrimitiveType() == null)
            return (Generator<T>) generator;
        PrimitiveType<T> primitiveType = descriptor.getPrimitiveType();
        Class<T> targetType = primitiveType.getJavaType();
        Converter<S, T> converter = null;
        if (Date.class.equals(targetType) && generator.getGeneratedType() == String.class) {
            // String needs to be converted to Date
            if (descriptor.getPattern() != null) {
                // We can use the SimpleDateFormat with a pattern
                String pattern = descriptor.getPattern();
                converter = new ParseFormatConverter(Date.class, new SimpleDateFormat(pattern));
            } else {
                // we need to expect the standard date format
                converter = new String2DateConverter();
            }
        } else if (String.class.equals(targetType) && generator.getGeneratedType() == Date.class) {
            // String needs to be converted to Date
            if (descriptor.getPattern() != null) {
                // We can use the SimpleDateFormat with a pattern
                String pattern = descriptor.getPattern();
                converter = new FormatFormatConverter(new SimpleDateFormat(pattern));
            } else {
                // we need to expect the standard date format
                converter = (Converter<S, T>) new FormatFormatConverter(TimeUtil.createDefaultDateFormat());
            }
        } else
        	converter = new AnyConverter<S, T>(targetType, descriptor.getPattern());
        return new ConvertingGenerator<S, T>(generator, converter);
    }

}
