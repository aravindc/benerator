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

import java.util.Collection;

import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ArrayElementDescriptor;
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
import org.databene.benerator.composite.ArrayElementBuilder;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.DynamicInstanceArrayGenerator;
import org.databene.benerator.composite.PlainEntityComponentBuilder;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.distribution.sequence.ExpandSequence;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.nullable.ConstantNullableGenerator;
import org.databene.benerator.nullable.NullInjectingGeneratorProxy;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.nullable.NullableScriptGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.TypedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates {@link ComponentBuilder}s.<br/><br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory extends InstanceGeneratorFactory {
    
    protected ComponentBuilderFactory() { }

    private static final Logger logger = LoggerFactory.getLogger(ComponentBuilderFactory.class);
    
    private static DataModel dataModel = DataModel.getDefaultInstance();

    // factory methods for component generators ------------------------------------------------------------------------
/*
    protected static ComponentBuilder<?> createComponentBuilder(ComponentDescriptor descriptor, BeneratorContext context) { // TODO remove method 
    	return createComponentBuilder(descriptor, Uniqueness.NONE, context);
    }
*/
    protected static ComponentBuilder<?> createComponentBuilder(ComponentDescriptor descriptor, Uniqueness ownerUniqueness, BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("createComponentBuilder(" + descriptor.getName() + ')');
        
        // Check for settings that can be handled generically
        NullableGenerator<?> generator;
        generator = createNullableScriptBuilder(descriptor, context);
        if (generator != null)
        	return wrapGenerator(generator, descriptor);
        generator = createNullQuotaOneBuilder(descriptor);
        if (generator != null)
        	return wrapGenerator(generator, descriptor);
        generator = createNullableGenerator(descriptor, context);
        if (generator != null)
        	return wrapGenerator(generator, descriptor);
        
        // ...
        if (descriptor instanceof ArrayElementDescriptor)
        	return createPartBuilder(descriptor, ownerUniqueness, context);
        else if (descriptor instanceof PartDescriptor) {
        	TypeDescriptor type = descriptor.getTypeDescriptor();
        	if (type instanceof AlternativeGroupDescriptor)
				return createAlternativeGroupBuilder((AlternativeGroupDescriptor) type, ownerUniqueness, context);
			else
				return createPartBuilder(descriptor, ownerUniqueness, context);
        } else if (descriptor instanceof ReferenceDescriptor)
            return createReferenceBuilder((ReferenceDescriptor)descriptor, context);
        else if (descriptor instanceof IdDescriptor)
            return createIdBuilder((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Not a supported element: " + descriptor.getClass());
    }

    private static ComponentBuilder<?> wrapGenerator(NullableGenerator<?> generator, ComponentDescriptor descriptor) {
    	if (descriptor instanceof ArrayElementDescriptor) {
    		int index = ((ArrayElementDescriptor) descriptor).getIndex();
    		return new ArrayElementBuilder(index, generator);
    	} else
    		return new PlainEntityComponentBuilder(descriptor.getName(), generator);
    }

	protected static NullableScriptGenerator createNullableScriptBuilder(ComponentDescriptor component, Context context) {
    	TypeDescriptor type = component.getTypeDescriptor();
        if (type == null)
        	return null;
        String scriptText = type.getScript();
        if (scriptText == null)
        	return null;
        Script script = ScriptUtil.parseScriptText(scriptText);
        return new NullableScriptGenerator(script, context);
    }

    private static NullableGenerator<?> createNullQuotaOneBuilder(ComponentDescriptor descriptor) {
    	Generator<?> generator = InstanceGeneratorFactory.createNullQuotaOneGenerator(descriptor);
    	return (generator != null ? createNullGenerator(descriptor) : null);
	}

    private static NullableGenerator<?> createNullableGenerator(ComponentDescriptor descriptor, BeneratorContext context) {
    	Generator<?> generator = InstanceGeneratorFactory.createNullableGenerator(descriptor, context);
    	return (generator != null ? createNullGenerator(descriptor) : null);
	}

	private static NullableGenerator<Object> createNullGenerator(ComponentDescriptor descriptor) {
		Class<Object> generatedType = Object.class; 
		 // TODO v0.6.2 find out expected Java type or change global type handling to interpret generatedType==null as null-value-generator
	    return new ConstantNullableGenerator<Object>(null, generatedType); 
    }

	@SuppressWarnings("unchecked")
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
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return wrapWithNullInjector(generator, part);
    }

    @SuppressWarnings("unchecked")
    private static ComponentBuilder<?> wrapWithNullInjector(Generator<?> source, ComponentDescriptor descriptor) {
    	double nullQuota = DescriptorUtil.getNullQuota(descriptor);
    	NullableGenerator generator = new NullInjectingGeneratorProxy(source, nullQuota);
        return wrapGenerator(generator, descriptor);
    }

	@SuppressWarnings("unchecked")
    static ComponentBuilder<?> createReferenceBuilder(ReferenceDescriptor descriptor, BeneratorContext context) {
        boolean unique = DescriptorUtil.getUniqueness(descriptor).evaluate(context);
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
		            if (selector == null && distribution == null)
		            	if (context.isDefaultOneToOne())
		            		distribution = new ExpandSequence();
		            	else
		            		distribution = SequenceManager.RANDOM_SEQUENCE;
	            }
	            	
	        } else
	        	throw new ConfigurationError("Not a supported source type: " + sourceName);
        }
        
        // apply distribution if necessary
        if (distribution != null)
            generator = distribution.applyTo(generator, descriptor.isUnique());
        
        // check multiplicity
        generator = ComponentBuilderFactory.createMultiplicityWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        
        // check 'cyclic' config
        generator = DescriptorUtil.wrapWithProxy(generator, typeDescriptor);
        return wrapWithNullInjector(generator, descriptor);
    }

    static ComponentBuilder<?> createIdBuilder(IdDescriptor id, BeneratorContext context) {
        Generator<?> generator = createSingleInstanceGenerator(id, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return wrapWithNullInjector(generator, id);
    }

    // non-public helpers ----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static Generator<Object> createMultiplicityWrapper(
            ComponentDescriptor instance, Generator<?> generator, BeneratorContext context) {
    	Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(instance, true);
    	return new DynamicInstanceArrayGenerator((Generator<Object>) generator, 
    			countGenerator, context);
    }

}
