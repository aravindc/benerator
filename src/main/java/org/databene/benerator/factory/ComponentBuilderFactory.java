/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

import static org.databene.model.data.TypeDescriptor.PATTERN;

import java.util.Collection;

import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.databene.benerator.Generator;
import org.databene.benerator.composite.AlternativeComponentBuilder;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.DynamicInstanceArrayGenerator;
import org.databene.benerator.composite.PlainComponentBuilder;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.nullable.ConvertingNullableGeneratorProxy;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableScriptGenerator;
import org.databene.benerator.nullable.ValidatingNullableGeneratorProxy;
import org.databene.benerator.primitive.ValueMapper;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Converter;
import org.databene.commons.TypedIterable;
import org.databene.commons.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates generators that generate entity components.<br/><br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory extends InstanceGeneratorFactory {
    
    protected ComponentBuilderFactory() { }

    private static final Logger logger = LoggerFactory.getLogger(ComponentBuilderFactory.class);
    
    private static DataModel dataModel = DataModel.getDefaultInstance();

    // factory methods for component generators ------------------------------------------------------------------------

    public static ComponentBuilder createComponentBuilder(ComponentDescriptor descriptor, BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("createComponentBuilder(" + descriptor.getName() + ')');
        ComponentBuilder builder;
        builder = createNullableScriptBuilder(descriptor, context);
        if (builder != null)
        	return builder;
        builder = createNullQuotaOneBuilder(descriptor);
        if (builder != null)
        	return builder;
        builder = createNullableBuilder(descriptor, context);
        if (builder != null)
        	return builder;
        if (descriptor instanceof PartDescriptor) {
        	TypeDescriptor type = descriptor.getTypeDescriptor();
        	if (type instanceof AlternativeGroupDescriptor) {
				return createAlternativeGroupBuilder((AlternativeGroupDescriptor) type, context);
			} else
				return createPartBuilder((PartDescriptor)descriptor, context);
        } else if (descriptor instanceof ReferenceDescriptor)
            return createReferenceBuilder((ReferenceDescriptor)descriptor, context);
        else if (descriptor instanceof IdDescriptor)
            return createIdBuilder((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Unsupported element: " + descriptor.getClass());
    }

    protected static ComponentBuilder createNullableScriptBuilder(ComponentDescriptor component, Context context) {
    	TypeDescriptor type = component.getTypeDescriptor();
        if (type == null)
        	return null;
        String scriptText = type.getScript();
        if (scriptText == null)
        	return null;
        Script script = ScriptUtil.parseScriptText(scriptText);
        NullableScriptGenerator source = new NullableScriptGenerator(script, context);
		return new PlainComponentBuilder(component.getName(), source);
    }

	private static ComponentBuilder createNullQuotaOneBuilder(ComponentDescriptor descriptor) {
    	Generator<?> generator = InstanceGeneratorFactory.createNullQuotaOneGenerator(descriptor);
    	return (generator != null ? new PlainComponentBuilder(descriptor.getName(), generator, 1) : null);
	}

    private static ComponentBuilder createNullableBuilder(ComponentDescriptor descriptor, BeneratorContext context) {
    	Generator<?> generator = InstanceGeneratorFactory.createNullableGenerator(descriptor, context);
    	return (generator != null ? new PlainComponentBuilder(descriptor.getName(), generator, 1) : null);
	}

	private static ComponentBuilder createAlternativeGroupBuilder(
			AlternativeGroupDescriptor type, BeneratorContext context) {
    	int i = 0;
		Collection<ComponentDescriptor> components = type.getComponents();
		ComponentBuilder[] builders = new ComponentBuilder[components.size()];
		for (ComponentDescriptor component : components) {
			builders[i++] = createComponentBuilder(component, context);
		}
		return new AlternativeComponentBuilder(builders);
	}

    public static ComponentBuilder createPartBuilder(
            PartDescriptor part, BeneratorContext context) {
        Generator<?> generator = createSingleInstanceGenerator(part, context);
        generator = createMultiplicityWrapper(part, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return new PlainComponentBuilder(part.getName(), generator, DescriptorUtil.getNullQuota(part));
    }

    @SuppressWarnings("unchecked")
    public static ComponentBuilder createReferenceBuilder(ReferenceDescriptor descriptor, BeneratorContext context) {
        boolean unique = DescriptorUtil.getUniqueness(descriptor).evaluate(context);
        Uniqueness uniqueness = (unique ? Uniqueness.SIMPLE : Uniqueness.ORDERED);
        SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();

        Generator<?> generator = null;
		generator = DescriptorUtil.getGeneratorByName(typeDescriptor, context);
        if (generator == null)
            generator = SimpleTypeGeneratorFactory.createScriptGenerator(typeDescriptor, context);
        if (generator == null)
        	generator = SimpleTypeGeneratorFactory.createConstantGenerator(typeDescriptor, context);
        if (generator == null)
        	generator = SimpleTypeGeneratorFactory.createSampleGenerator(typeDescriptor, uniqueness, context);
        
        if (generator == null) {
	        // check target type
	        String targetTypeName = descriptor.getTargetTye();
			ComplexTypeDescriptor targetType = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(targetTypeName);
	        if (targetType == null)
	            throw new ConfigurationError("Type not defined: " + targetTypeName);
	        
	        // check source
	        String sourceName = typeDescriptor.getSource();
	        if (sourceName == null)
	            throw new ConfigurationError("'source' is not set for " + descriptor);
	        Object sourceObject = context.get(sourceName);
	        if (sourceObject instanceof StorageSystem) {
	            StorageSystem sourceSystem = (StorageSystem) sourceObject;
	            String selector = typeDescriptor.getSelector();
	            if (selector != null && selector.startsWith("select")) {
	            	generator = new IteratingGenerator(sourceSystem.query(selector, context));
	            } else {
		            TypedIterable<Object> entityIds = sourceSystem.queryEntityIds(targetTypeName, selector, context);
		            generator = new IteratingGenerator<Object>(entityIds);
	            }
	        } else
	        	throw new ConfigurationError("Not a supported source type: " + sourceName);
        }
        
        // check distribution
    	Distribution distribution = GeneratorFactoryUtil.getDistribution(
    			typeDescriptor.getDistribution(), descriptor.getUniqueness(), false, context);
        if (distribution != null)
            generator = distribution.applyTo(generator, descriptor.isUnique());
        
        // check multiplicity
        generator = ComponentBuilderFactory.createMultiplicityWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        
        // check 'cyclic' config
        generator = DescriptorUtil.wrapWithProxy(generator, typeDescriptor);
        return new PlainComponentBuilder(descriptor.getName(), generator, DescriptorUtil.getNullQuota(descriptor));
    }

    public static ComponentBuilder createIdBuilder(IdDescriptor id, BeneratorContext context) {
        Generator<?> generator = createSingleInstanceGenerator(id, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return new PlainComponentBuilder(id.getName(), generator, DescriptorUtil.getNullQuota(id));
    }

    // non-public helpers ----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static Generator<Object> createMultiplicityWrapper(
            ComponentDescriptor instance, Generator<?> generator, BeneratorContext context) {
    	Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(instance, true);
    	return new DynamicInstanceArrayGenerator((Generator<Object>) generator, 
    			countGenerator, context);
    }

	@SuppressWarnings("unchecked")
    static <E> NullableGenerator<E> wrapWithPostprocessors(NullableGenerator<E> generator, TypeDescriptor descriptor, BeneratorContext context) {
		generator = (NullableGenerator<E>) createConvertingGenerator(descriptor, generator, context);
		if (descriptor instanceof SimpleTypeDescriptor) {
			SimpleTypeDescriptor simpleType = (SimpleTypeDescriptor) descriptor;
			generator = (NullableGenerator<E>) createMappingGenerator(simpleType, generator);
			generator = (NullableGenerator<E>) createTypeConvertingGenerator(simpleType, generator);
		}
        generator = (NullableGenerator<E>) createValidatingGenerator(descriptor, generator, context);
		return generator;
	}
    
    @SuppressWarnings("unchecked")
    protected static NullableGenerator<?> createConvertingGenerator(TypeDescriptor descriptor, NullableGenerator generator, BeneratorContext context) {
        Converter<?,?> converter = DescriptorUtil.getConverter(descriptor, context);
        if (converter != null) {
            if (descriptor.getPattern() != null && BeanUtil.hasProperty(converter.getClass(), PATTERN)) {
                BeanUtil.setPropertyValue(converter, PATTERN, descriptor.getPattern(), false);
            }
            generator = new ConvertingNullableGeneratorProxy(generator, converter);
        }
        return generator;
    }

    @SuppressWarnings("unchecked")
    static NullableGenerator<?> createMappingGenerator(
            SimpleTypeDescriptor descriptor, NullableGenerator<?> generator) {
        if (descriptor == null || descriptor.getMap() == null)
            return generator;
        String mappingSpec = descriptor.getMap();
        ValueMapper mapper = new ValueMapper(mappingSpec);
        return new ConvertingNullableGeneratorProxy(generator, mapper);
    }

    @SuppressWarnings("unchecked")
    static NullableGenerator<?> createTypeConvertingGenerator(
            SimpleTypeDescriptor descriptor, NullableGenerator<?> generator) {
        if (descriptor == null || descriptor.getPrimitiveType() == null)
            return generator;
        Converter<?, ?> converter = TypeGeneratorFactory.createConverter(descriptor, generator.getGeneratedType());
    	return (converter != null ? new ConvertingNullableGeneratorProxy(generator, converter) : generator);
    }

    @SuppressWarnings("unchecked")
    protected static NullableGenerator<?> createValidatingGenerator(
            TypeDescriptor descriptor, NullableGenerator<?> generator, BeneratorContext context) {
        Validator<?> validator = DescriptorUtil.getValidator(descriptor, context);
        if (validator != null)
            generator = new ValidatingNullableGeneratorProxy(generator, validator);
        return generator;
    }

}
