/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.sample.DistributingGenerator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.ValidatingGeneratorProxy;
import org.databene.commons.ArrayUtil;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.commons.converter.ParseFormatConverter;
import org.databene.commons.converter.String2DateConverter;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.PrimitiveType;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import static org.databene.model.data.TypeDescriptor.*;

/**
 * Creates generators of type instances.<br/><br/>
 * Created: 05.03.2008 16:51:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class TypeGeneratorFactory {
    
    private static final Log logger = LogFactory.getLog(TypeGeneratorFactory.class);
    
    public static Generator<? extends Object> createTypeGenerator(
    		TypeDescriptor descriptor, boolean unique, BeneratorContext context) {
    	if (logger.isDebugEnabled())
    		logger.debug(descriptor + ", " + unique);
        if (descriptor instanceof SimpleTypeDescriptor)
            return SimpleTypeGeneratorFactory.createSimpleTypeGenerator((SimpleTypeDescriptor) descriptor, false, unique, context);
        else if (descriptor instanceof ComplexTypeDescriptor)
            return ComplexTypeGeneratorFactory.createComplexTypeGenerator((ComplexTypeDescriptor) descriptor, unique, context);
        else
            throw new UnsupportedOperationException("Descriptor type not supported: " + descriptor.getClass());
    }

    protected static Generator<? extends Object> createSampleGenerator(
    		TypeDescriptor descriptor, boolean unique, BeneratorContext context) {
        Generator<? extends Object> generator = null;
        // check for samples
        Object[] values = DescriptorUtil.getValues(descriptor, context);
        if (!ArrayUtil.isEmpty(values)) {
            Distribution distribution = DescriptorUtil.getDistribution(descriptor, unique, context);
            if (distribution instanceof Sequence)
                generator = new SequencedSampleGenerator<Object>(Object.class, (Sequence) distribution, values);
            else if (distribution instanceof WeightFunction)
                generator = new WeightedSampleGenerator<Object>(Object.class, (WeightFunction) distribution, values);
            else if (distribution == null)
                generator = new SequencedSampleGenerator<Object>(Object.class, values);
            else
                throw new ConfigurationError("Unsupported distribution type: " + distribution.getClass());
        }
        return generator;
    }

    protected static Generator<? extends Object> createScriptGenerator(TypeDescriptor descriptor, Context context) {
        Generator<? extends Object> generator = null;
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseUnspecificText(scriptText);
            generator = new ScriptGenerator(script, context);
        }
        return generator;
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
    protected static <T> Generator<T> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<T> generator, BeneratorContext context) {
        Validator<T> validator = DescriptorUtil.getValidator(descriptor, context);
        if (validator != null)
            generator = new ValidatingGeneratorProxy<T>(generator, validator);
        return generator;
    }

    protected static Generator createConvertingGenerator(TypeDescriptor descriptor, Generator generator, BeneratorContext context) {
        Converter converter = DescriptorUtil.getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = GeneratorFactory.getConvertingGenerator(generator, converter);
        }
        return generator;
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
                converter = new FormatFormatConverter(Date.class, new SimpleDateFormat(pattern));
            } else {
                // we need to expect the standard date format
                converter = (Converter<S, T>) new FormatFormatConverter(Date.class, TimeUtil.createDefaultDateFormat());
            }
        } else
        	converter = (Converter<S, T>) new AnyConverter<Object, T>(Object.class, targetType, descriptor.getPattern());
        return new ConvertingGenerator<S, T>(generator, converter);
    }

	public static Generator<? extends Object> applyDistribution(TypeDescriptor descriptor,
			Distribution distribution, Generator<? extends Object> generator) {
		return new DistributingGenerator(generator, distribution, descriptor.getVariation1(), descriptor.getVariation2());
	}

}
