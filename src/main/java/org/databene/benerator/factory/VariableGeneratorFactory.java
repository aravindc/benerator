/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import static org.databene.model.data.TypeDescriptor.PATTERN;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.primitive.ValueMapper;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.Validator;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;

/**
 * Factory class that creates {@link Generator}s for &lt;variable&gt;s.<br/><br/>
 * Created: 18.05.2011 12:01:24
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class VariableGeneratorFactory {

	public static Generator<?> createGenerator(
			InstanceDescriptor descriptor, BeneratorContext context) {
		Generator<?> generator = null;
		
		// check if nullQuota == 1
		generator = createNullQuotaOneGenerator(descriptor);
		if (generator != null)
			return null;
		
		// check for script
		TypeDescriptor type = descriptor.getTypeDescriptor();
		generator = createScriptGenerator(type, context);
		if (generator != null) {
	        generator = wrapWithPostprocessors(generator, type, context);
	        generator = wrapWithProxy(generator, type);
		}

		if (generator == null)
			generator = InstanceGeneratorFactory.createSingleInstanceGenerator(descriptor, Uniqueness.NONE, context);
		
		return context.getGeneratorFactory().applyNullSettings(generator, descriptor.isNullable(), descriptor.getNullQuota());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Generator<?> createNullQuotaOneGenerator(InstanceDescriptor descriptor) {
		// check if nullQuota is 1
        Double nullQuota = descriptor.getNullQuota();
        if (nullQuota != null && nullQuota.doubleValue() == 1.)
            return new ConstantGenerator(null);
        else
        	return null;
	}
	
    protected static Generator<?> createScriptGenerator(TypeDescriptor descriptor, Context context) {
        String scriptText = descriptor.getScript();
        if (scriptText != null) {
            Script script = ScriptUtil.parseScriptText(scriptText);
            return new ScriptGenerator(script, context);
        } else
        	return null;
    }

    static Generator<?> wrapWithPostprocessors(Generator<?> generator, TypeDescriptor descriptor, BeneratorContext context) {
		generator = createConvertingGenerator(descriptor, generator, context);
		if (descriptor instanceof SimpleTypeDescriptor) {
			SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) descriptor;
			generator = createMappingGenerator(simpleType, generator);
			generator = createTypeConvertingGenerator(simpleType, generator);
		}
        generator = createValidatingGenerator(descriptor, generator, context);
		return generator;
	}
    
    @SuppressWarnings("unchecked")
    protected static <T> Generator<T> createValidatingGenerator(
            TypeDescriptor descriptor, Generator<T> generator, BeneratorContext context) {
		Validator<T> validator = DescriptorUtil.getValidator(descriptor, context);
        if (validator != null)
            generator = GeneratorFactoryUtil.wrapWithValidator(validator, generator);
        return generator;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	static Generator<?> createTypeConvertingGenerator(SimpleTypeDescriptor descriptor, Generator<?> generator) {
        if (descriptor == null || descriptor.getPrimitiveType() == null)
            return generator;
        Converter<?, ?> converter = TypeGeneratorFactory.createConverter(descriptor, generator.getGeneratedType());
        if (converter != null)
        	return GeneratorFactoryUtil.createConvertingGenerator((Generator) generator, converter);
        else
        	return generator;
    }

    public static Generator<?> createConvertingGenerator(TypeDescriptor descriptor, Generator<?> generator, BeneratorContext context) {
        Converter<?,?> converter = DescriptorUtil.getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = GeneratorFactoryUtil.createConvertingGenerator(descriptor, generator, context);
        }
        return generator;
    }

	static Generator<?> createMappingGenerator(SimpleTypeDescriptor descriptor, Generator<?> generator) {
        if (descriptor == null || descriptor.getMap() == null)
            return generator;
        String mappingSpec = descriptor.getMap();
        ValueMapper mapper = new ValueMapper(mappingSpec);
        return GeneratorFactoryUtil.createConvertingGenerator(generator, mapper);
    }

    public static <T> Generator<T> wrapWithProxy(Generator<T> generator, TypeDescriptor descriptor) {
		boolean cyclic = descriptor.isCyclic() != null && descriptor.isCyclic().booleanValue();
		return wrapWithProxy(generator, cyclic);
    }

	public static <T> Generator<T> wrapWithProxy(Generator<T> generator, boolean cyclic) {
		if (cyclic)
			generator = GeneratorFactoryUtil.wrapCyclic(generator);
		return generator;
    }

}
