/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.primitive.ValueMapper;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.ValidatingGeneratorProxy;
import org.databene.commons.BeanUtil;
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
import org.databene.model.data.Uniqueness;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.databene.model.data.TypeDescriptor.*;

/**
 * Creates generators of type instances.<br/><br/>
 * Created: 05.03.2008 16:51:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class TypeGeneratorFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(TypeGeneratorFactory.class);
    
    public static Generator<?> createTypeGenerator(String name, 
    		TypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
    	if (logger.isDebugEnabled())
    		logger.debug(descriptor + ", " + uniqueness);
        if (descriptor instanceof SimpleTypeDescriptor)
            return SimpleTypeGeneratorFactory.createSimpleTypeGenerator((SimpleTypeDescriptor) descriptor, false, uniqueness, context);
        else if (descriptor instanceof ComplexTypeDescriptor)
            return ComplexTypeGeneratorFactory.createComplexTypeGenerator(name, (ComplexTypeDescriptor) descriptor, uniqueness, context);
        else
            throw new UnsupportedOperationException("Descriptor type not supported: " + descriptor.getClass());
    }

    protected static Generator<?> createScriptGenerator(TypeDescriptor descriptor, Context context) {
    	Generator<?> generator = null;
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseScriptText(scriptText);
            generator = new ScriptGenerator(script, context);
        }
        return generator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Generator<?> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<?> generator, BeneratorContext context) {
        Validator<?> validator = DescriptorUtil.getValidator(descriptor, context);
        if (validator != null)
            generator = new ValidatingGeneratorProxy(generator, validator);
        return generator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor, Generator generator, BeneratorContext context) {
        Converter<?,?> converter = DescriptorUtil.getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = context.getGeneratorFactory().createConvertingGenerator(generator, converter);
        }
        return generator;
    }

	@SuppressWarnings("unchecked")
    static <E> Generator<E> wrapWithPostprocessors(Generator<E> generator, TypeDescriptor descriptor, BeneratorContext context) {
		generator = (Generator<E>) createConvertingGenerator(descriptor, generator, context);
		if (descriptor instanceof SimpleTypeDescriptor) {
			SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) descriptor;
			generator = (Generator<E>) createMappingGenerator(simpleType, generator);
			generator = (Generator<E>) createTypeConvertingGenerator(simpleType, generator);
		}
        generator = (Generator<E>) createValidatingGenerator(descriptor, generator, context);
		return generator;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Generator<?> createMappingGenerator(
            SimpleTypeDescriptor descriptor, Generator<?> generator) {
        if (descriptor == null || descriptor.getMap() == null)
            return generator;
        String mappingSpec = descriptor.getMap();
        ValueMapper mapper = new ValueMapper(mappingSpec);
        return new ConvertingGenerator(generator, mapper);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Generator<?> createTypeConvertingGenerator(
            SimpleTypeDescriptor descriptor, Generator<?> generator) {
        if (descriptor == null || descriptor.getPrimitiveType() == null)
            return generator;
        Converter<?, ?> converter = createConverter(descriptor, generator.getGeneratedType());
    	return (converter != null ? new ConvertingGenerator(generator, converter) : generator);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static Converter<?, ?> createConverter(SimpleTypeDescriptor descriptor, Class<?> sourceType) {
	    PrimitiveType primitiveType = descriptor.getPrimitiveType();
        Class<?> targetType = primitiveType.getJavaType();
        Converter<?,?> converter = null;
        if (Date.class.equals(targetType) && sourceType == String.class) {
            // String needs to be converted to Date
            if (descriptor.getPattern() != null) {
                // We can use the SimpleDateFormat with a pattern
                String pattern = descriptor.getPattern();
                converter = new ParseFormatConverter<Date>(Date.class, new SimpleDateFormat(pattern), false);
            } else {
                // we need to expect the standard date format
                converter = new String2DateConverter<Date>();
            }
        } else if (String.class.equals(targetType) && sourceType == Date.class) {
            // String needs to be converted to Date
            if (descriptor.getPattern() != null) {
                // We can use the SimpleDateFormat with a pattern
                String pattern = descriptor.getPattern();
                converter = new FormatFormatConverter<Date>(Date.class, new SimpleDateFormat(pattern), false);
            } else {
                // we need to expect the standard date format
                converter = new FormatFormatConverter<Date>(Date.class, TimeUtil.createDefaultDateFormat(), false);
            }
        } else if (targetType != sourceType) {
        	converter = new AnyConverter(targetType, descriptor.getPattern());
        }
	    return converter;
    }

}
