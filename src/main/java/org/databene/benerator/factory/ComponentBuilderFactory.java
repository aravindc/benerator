/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.id.GlobalIdProviderFactory;
import org.databene.id.IdProvider;
import org.databene.id.IdProviderFactory;
import org.databene.id.IdStrategy;
import org.databene.model.data.AlternativeGroupDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.PartDescriptor;
import org.databene.model.data.ReferenceDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.storage.StorageSystem;
import org.databene.benerator.Generator;
import org.databene.benerator.composite.AlternativeComponentBuilder;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.InstanceGenerator;
import org.databene.benerator.composite.PlainComponentBuilder;
import org.databene.benerator.wrapper.IdGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.TypedIterable;

import static org.databene.benerator.factory.GeneratorFactoryUtil.*;

/**
 * Creates generators that generate entity components.<br/>
 * <br/>
 * Created: 14.10.2007 22:16:34
 * @author Volker Bergmann
 */
public class ComponentBuilderFactory extends InstanceGeneratorFactory {
    
    protected ComponentBuilderFactory() { }

    private static final Log logger = LogFactory.getLog(ComponentBuilderFactory.class);
    
    private static DataModel dataModel = DataModel.getDefaultInstance();
    
    // factory methods for component generators ------------------------------------------------------------------------

    public static ComponentBuilder createComponentBuilder( 
            ComponentDescriptor descriptor, Context context, GenerationSetup setup) {
        if (logger.isDebugEnabled())
            logger.debug("addComponentGenerator(" + descriptor.getName() + ')');
        if (descriptor instanceof PartDescriptor) {
        	TypeDescriptor type = descriptor.getType();
        	if (type instanceof AlternativeGroupDescriptor) {
				return createAlternativeGroupGenerator((AlternativeGroupDescriptor) type, context, setup);
			} else
				return createPartGenerator((PartDescriptor)descriptor, context, setup);
        } else if (descriptor instanceof ReferenceDescriptor)
            return createReferenceGenerator((ReferenceDescriptor)descriptor, context, setup);
        else if (descriptor instanceof IdDescriptor)
            return createIdGenerator((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Unsupported element: " + descriptor.getClass());
    }

    private static ComponentBuilder createAlternativeGroupGenerator(AlternativeGroupDescriptor type, 
    		Context context, GenerationSetup setup) {
    	int i = 0;
		Collection<ComponentDescriptor> components = type.getComponents();
		ComponentBuilder[] builders = new ComponentBuilder[components.size()];
		for (ComponentDescriptor component : components) {
			builders[i++] = createComponentBuilder(component, context, setup);
		}
		return new AlternativeComponentBuilder(builders);
	}

/*
    private static Generator<? extends Object> createComponentGenerator(
            ComponentDescriptor descriptor, Context context, GeneratioComponentnSetup setup) {
        if (logger.isDebugEnabled())
            logger.debug("createComponentGenerator(" + descriptor.getName() + ')');
        if (descriptor instanceof PartDescriptor)
            return createPartGenerator((PartDescriptor)descriptor, context, setup);
        else if (descriptor instanceof ReferenceDescriptor)
            return createReferenceGenerator((ReferenceDescriptor)descriptor, context, setup);
        else if (descriptor instanceof IdDescriptor)
            return createIdGenerator((IdDescriptor)descriptor, context);
        else 
            throw new ConfigurationError("Unsupported element: " + descriptor.getClass());
    }
*/
    public static ComponentBuilder createPartGenerator(
            PartDescriptor descriptor, Context context, GenerationSetup setup) {
        Generator<? extends Object> generator = createSingleInstanceGenerator(
                descriptor, context, setup);
        generator = createComponentGeneratorWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return new PlainComponentBuilder(descriptor.getName(), generator);
    }

    public static ComponentBuilder createReferenceGenerator(ReferenceDescriptor descriptor, Context context, GenerationSetup setup) {
        Generator<? extends Object> generator = null;
        TypeDescriptor typeDescriptor = descriptor.getType();
        String targetTypeName = descriptor.getTargetTye();
		ComplexTypeDescriptor targetType = (ComplexTypeDescriptor) dataModel.getTypeDescriptor(targetTypeName);
        if (targetType == null)
            throw new ConfigurationError("Type not defined: " + targetTypeName);
        else {
            String sourceName = typeDescriptor.getSource();
            if (sourceName == null)
                throw new ConfigurationError("'source' is not set for " + descriptor);
            Object sourceObject = context.get(sourceName);
            if (sourceObject instanceof StorageSystem) {
                StorageSystem sourceSystem = (StorageSystem) sourceObject;
                String selector = typeDescriptor.getSelector();
                TypedIterable<Object> entityIds = sourceSystem.queryEntityIds(targetTypeName, selector);
                generator = new IteratingGenerator<Object>(entityIds);
            } else
            	throw new ConfigurationError("Not a supported source type: " + sourceName);
        }
        
        // check distribution
        Distribution distribution = descriptor.getType().getDistribution();
        if (distribution != null)
            generator = TypeGeneratorFactory.applyDistribution(descriptor.getType(), distribution, generator);
        else
        	generator = TypeGeneratorFactory.createProxy(descriptor.getType(), generator);
        
        generator = ComponentBuilderFactory.createComponentGeneratorWrapper(descriptor, generator, context);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return new PlainComponentBuilder(descriptor.getName(), generator);
    }
    
    public static ComponentBuilder createIdGenerator(IdDescriptor descriptor, Context context) {
        TypeDescriptor typeDescriptor = descriptor.getType();
        IdProviderFactory source = null;
        // check strategy
        String strategyName = descriptor.getStrategy();
        if (strategyName == null)
            throw new ConfigurationError("No strategy defined for key: " + descriptor.getName());

        // check scope
        String scope = descriptor.getScope();
        // check source
        String sourceId = typeDescriptor.getSource();
        if (sourceId != null) {
            source = (IdProviderFactory) context.get(sourceId);
        }
        // check param
        String param = descriptor.getParam();

        //checkUsedDetails(descriptor, usedDetails);
        IdStrategy idStrategy = IdStrategy.getInstance(strategyName);
        IdProvider idProvider;
        if (source != null)
            idProvider = source.idProvider(idStrategy, param, scope);
        else
            idProvider = GLOBAL_ID_PROVIDER_FACTORY.idProvider(idStrategy, param, scope);
        Generator<Object> generator = new IdGenerator(idProvider);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return new PlainComponentBuilder(descriptor.getName(), generator);
    }

    private static final GlobalIdProviderFactory GLOBAL_ID_PROVIDER_FACTORY = new GlobalIdProviderFactory();

    // non-public helpers ----------------------------------------------------------------------------------------------

    static Generator<Object> createComponentGeneratorWrapper(
            ComponentDescriptor descriptor, Generator<? extends Object> elementGenerator, Context context) {
        InstanceGenerator wrapper = new InstanceGenerator(elementGenerator);
        mapDetailsToBeanProperties(descriptor, wrapper, context);
        wrapper.setMaxCount(getMaxCount(descriptor));
        wrapper.setMinCount(getMinCount(descriptor));
        return (Generator<Object>) wrapper;
    }

	private static long getMaxCount(ComponentDescriptor descriptor) {
		if (descriptor.getCount() != null)
			return descriptor.getCount();
        if (descriptor.getMaxCount() != null)
        	return descriptor.getMaxCount();
        return 9;
	}

	private static long getMinCount(ComponentDescriptor descriptor) {
		if (descriptor.getCount() != null)
			return descriptor.getCount();
        if (descriptor.getMinCount() != null)
        	return descriptor.getMinCount();
        return 1;
	}

}
