/*
 * (c) Copyright 2007-2011 by Volker Bergmann. All rights reserved.
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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Collection;

import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.script.Script;
import org.databene.script.ScriptUtil;
import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.composite.AlternativeComponentBuilder;
import org.databene.benerator.composite.ArrayElementBuilder;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.ConditionalComponentBuilder;
import org.databene.benerator.composite.SimplifyingSingleSourceArrayGenerator;
import org.databene.benerator.composite.PlainEntityComponentBuilder;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.distribution.sequence.ExpandSequence;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.primitive.ScriptGenerator;
import org.databene.benerator.wrapper.AsIntegerGeneratorWrapper;
import org.databene.benerator.wrapper.DataSourceGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates {@link ComponentBuilder}s.<br/><br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory extends InstanceGeneratorFactory {
    
    protected ComponentBuilderFactory() { }

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentBuilderFactory.class);
    
    private static DataModel dataModel = DataModel.getDefaultInstance();

    // factory methods for component generators ------------------------------------------------------------------------

    protected static ComponentBuilder<?> createComponentBuilder(ComponentDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {
        LOGGER.debug("createComponentBuilder({})", descriptor.getName());
        
        // do I only need to generate nulls?
        if (getNullability(descriptor, context) && shouldNullifyEachNullable(descriptor, context))
            return builderFromNullableGenerator(createNullGenerator(descriptor, context), descriptor);
        
        ComponentBuilder<?> result = null;
        result = createNullableScriptBuilder(descriptor, context);
        
        // ...
        if (result == null) {
	        if (descriptor instanceof ArrayElementDescriptor)
	        	result = createPartBuilder(descriptor, ownerUniqueness, context);
	        else if (descriptor instanceof PartDescriptor) {
	        	TypeDescriptor type = descriptor.getTypeDescriptor();
	        	if (type instanceof AlternativeGroupDescriptor)
	        		result = createAlternativeGroupBuilder((AlternativeGroupDescriptor) type, ownerUniqueness, context);
				else
					result = createPartBuilder(descriptor, ownerUniqueness, context);
	        } else if (descriptor instanceof ReferenceDescriptor)
	        	result = createReferenceBuilder((ReferenceDescriptor) descriptor, context);
	        else if (descriptor instanceof IdDescriptor)
	        	result = createIdBuilder((IdDescriptor)descriptor, ownerUniqueness, context);
	        else 
	            throw new ConfigurationError("Not a supported element: " + descriptor.getClass());
        }
        result = wrapWithCondition(descriptor, result);
        return result;
    }

	protected static ComponentBuilder<?> createNullableScriptBuilder(ComponentDescriptor component, BeneratorContext context) {
    	TypeDescriptor type = component.getTypeDescriptor();
        if (type == null)
        	return null;
        String scriptText = type.getScript();
        if (scriptText == null)
        	return null;
        Script script = ScriptUtil.parseScriptText(scriptText);
        Generator<?> generator = new ScriptGenerator(script, context);
        generator = GeneratorFactoryUtil.createConvertingGenerator(component.getTypeDescriptor(), generator, context);
        generator = context.getGeneratorFactory().applyNullSettings(generator, getNullability(component, context), component.getNullQuota());
		return builderFromNullableGenerator(generator, component);

    }

	protected static boolean getNullability(InstanceDescriptor descriptor, BeneratorContext context) {
		Boolean nullable = descriptor.isNullable();
		if (nullable != null)
			return nullable;
		Double nullQuota = descriptor.getNullQuota();
		if (nullQuota != null && nullQuota > 0)
			return true;
		TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
		if (descriptor.getNullQuota() == null && typeDescriptor != null) {
			// if nullability is not specified, but a source or generator, then do not generate nulls
			if (typeDescriptor.getSource() != null || typeDescriptor.getGenerator() != null)
				return false;
		}
		return context.getDefaultsProvider().defaultNullable();
	}

	protected static boolean shouldNullifyEachNullable(InstanceDescriptor descriptor,
			BeneratorContext context) {
		// nullQuota == 1?
        Double nullQuota = descriptor.getNullQuota();
		if (nullQuota != null && nullQuota.doubleValue() == 1.)
			return true;
		// nullable?
		Boolean nullable = descriptor.isNullable();
		if (nullable != null && nullable == false) // nullable defaults to true
			return false;
		if (context.getDefaultsProvider().defaultNullQuota() < 1) 
			return false; // if the factory requires nullification, it overrides the context setting
		return (!descriptor.overwritesParent() && context.isDefaultNull());
	}


	private static Generator<?> createNullGenerator(InstanceDescriptor descriptor, BeneratorContext context) {
		Class<?> generatedType;
		TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
		if (typeDescriptor instanceof SimpleTypeDescriptor)
			generatedType = ((SimpleTypeDescriptor) typeDescriptor).getPrimitiveType().getJavaType();
		else
			generatedType = String.class;
		return context.getGeneratorFactory().createNullGenerator(generatedType); 
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static ComponentBuilder<?> createAlternativeGroupBuilder(
			AlternativeGroupDescriptor type, Uniqueness ownerUniqueness, BeneratorContext context) {
    	int i = 0;
		Collection<ComponentDescriptor> components = type.getComponents();
		ComponentBuilder<?>[] builders = new ComponentBuilder[components.size()];
		for (ComponentDescriptor component : components) {
			builders[i++] = createComponentBuilder(component, ownerUniqueness, context);
		}
		return new AlternativeComponentBuilder(builders);
	}

    private static ComponentBuilder<?> createPartBuilder(
            ComponentDescriptor part, Uniqueness ownerUniqueness, BeneratorContext context) {
        Generator<?> generator = createSingleInstanceGenerator(part, ownerUniqueness, context);
        generator = createMultiplicityWrapper(part, generator, context);
        LOGGER.debug("Created {}", generator);
        return builderFromGenerator(generator, part, context);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    static ComponentBuilder<?> createReferenceBuilder(ReferenceDescriptor descriptor, BeneratorContext context) {
        boolean unique = DescriptorUtil.isUnique(descriptor, context);
        Uniqueness uniqueness = (unique ? Uniqueness.SIMPLE : Uniqueness.NONE);
        SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) descriptor.getTypeDescriptor();

        Generator<?> generator = null;
		generator = DescriptorUtil.getGeneratorByName(typeDescriptor, context);
        if (generator == null)
            generator = SimpleTypeGeneratorFactory.createScriptGenerator(typeDescriptor, context);
        if (generator == null)
        	generator = SimpleTypeGeneratorFactory.createConstantGenerator(typeDescriptor, context);
        if (generator == null)
        	generator = SimpleTypeGeneratorFactory.createSampleGenerator(typeDescriptor, uniqueness, context);
        
        // get distribution
    	Distribution distribution = GeneratorFactoryUtil.getDistribution(
    			typeDescriptor.getDistribution(), descriptor.getUniqueness(), false, context);
    	
    	// check source
        if (generator == null) {
	        // check target type
	        String targetTypeName = descriptor.getTargetType();
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
	            String subSelector = typeDescriptor.getSubSelector();
	            boolean subSelect = !StringUtil.isEmpty(subSelector);
				String selectorToUse = (subSelect ? subSelector : selector);
	            if (isIndividualSelector(selectorToUse)) {
	            	generator = new DataSourceGenerator(sourceSystem.query(selectorToUse, true, context));
	            } else {
		            generator = new DataSourceGenerator(sourceSystem.queryEntityIds(targetTypeName, selectorToUse, context));
		            if (selectorToUse == null && distribution == null)
		            	if (context.isDefaultOneToOne())
		            		distribution = new ExpandSequence();
		            	else
		            		distribution = SequenceManager.RANDOM_SEQUENCE;
	            }
	            if (subSelect)
	            	generator = GeneratorFactoryUtil.createCyclicHeadGenerator(generator);
	        } else
	        	throw new ConfigurationError("Not a supported source type: " + sourceName);
        }
        
        
        // apply distribution if necessary
        if (distribution != null)
            generator = new DistributingGenerator(generator, distribution, unique);
        
        // check multiplicity
        generator = ComponentBuilderFactory.createMultiplicityWrapper(descriptor, generator, context);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Created " + generator);
        
        // check 'cyclic' config
        generator = GeneratorFactoryUtil.wrapWithProxy(generator, typeDescriptor);
        return builderFromGenerator(generator, descriptor, context);
    }

	/**
	 * helper method to check for selectors of individual fields like "select x from y" or 
	 * "{'select x from y where id=' + z}". For such selectors it returns true, otherwise false
	 */
	protected static boolean isIndividualSelector(String selector) {
		if (selector == null)
			return false;
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(selector));
		tokenizer.ordinaryChar('\'');
		tokenizer.ordinaryChar('"');
		int token; 
		try {
			while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
				if (token == StreamTokenizer.TT_WORD)
					return StringUtil.startsWithIgnoreCase(tokenizer.sval.trim(), "select");
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected error", e);
		}
		return false;
	}

    // non-public helpers ----------------------------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static ComponentBuilder<?> wrapWithCondition(ComponentDescriptor descriptor, ComponentBuilder<?> builder) {
    	TypeDescriptor typeDescriptor = descriptor.getTypeDescriptor();
    	if (typeDescriptor == null)
    		return builder;
    	String conditionText = typeDescriptor.getCondition();
    	if (!StringUtil.isEmpty(conditionText)) {
		    Expression<Boolean> condition = new ScriptExpression<Boolean>(conditionText);
		    return new ConditionalComponentBuilder(builder, condition);
    	} else
    		return builder;
    }

	static ComponentBuilder<?> createIdBuilder(IdDescriptor id, Uniqueness ownerUniqueness, BeneratorContext context) {
        Generator<?> generator = createSingleInstanceGenerator(id, Uniqueness.ORDERED, context);
        LOGGER.debug("Created {}", generator);
        return builderFromGenerator(generator, id, context);
    }

    private static ComponentBuilder<?> builderFromGenerator(Generator<?> source, ComponentDescriptor descriptor, BeneratorContext context) {
    	Generator<?> generator = context.getGeneratorFactory().applyNullSettings(source, getNullability(descriptor, context), descriptor.getNullQuota());
        return builderFromNullableGenerator(generator, descriptor);
    }

	private static ComponentBuilder<?> builderFromNullableGenerator(Generator<?> generator, ComponentDescriptor descriptor) {
    	if (descriptor instanceof ArrayElementDescriptor) {
    		int index = ((ArrayElementDescriptor) descriptor).getIndex();
    		return new ArrayElementBuilder(index, generator);
    	} else
    		return new PlainEntityComponentBuilder(descriptor.getName(), generator);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Generator<Object> createMultiplicityWrapper(
            ComponentDescriptor instance, Generator<?> generator, BeneratorContext context) {
    	Generator<Long> source = GeneratorFactoryUtil.getCountGenerator(instance, true, context);
    	NonNullGenerator<Integer> countGenerator = GeneratorFactoryUtil.asNonNullGenerator(
    			new AsIntegerGeneratorWrapper<Number>((Generator) source));
    	return new SimplifyingSingleSourceArrayGenerator(generator, countGenerator);
    }

}
