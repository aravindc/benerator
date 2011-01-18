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

package org.databene.benerator.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.composite.AbstractComponentBuilder;
import org.databene.benerator.composite.ArrayElementTypeConverter;
import org.databene.benerator.composite.BlankArrayGenerator;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.SourceAwareGenerator;
import org.databene.benerator.composite.UniqueArrayGenerator;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.util.FilteringGenerator;
import org.databene.benerator.wrapper.ConvertingGenerator;
import org.databene.benerator.wrapper.IteratingGenerator;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.Mode;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import org.databene.platform.array.Entity2ArrayConverter;
import org.databene.platform.csv.CSVEntitySourceFactory;
import org.databene.platform.xls.XLSEntitySourceFactory;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates array {@link Generator}s.<br/><br/>
 * Created: 29.04.2010 07:45:18
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ArrayGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ArrayGeneratorFactory.class);

    @SuppressWarnings("unchecked")
    public static Generator<Object[]> createArrayGenerator(String instanceName, 
			ArrayTypeDescriptor type, Uniqueness uniqueness, BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("createArrayGenerator(" + type.getName() + ")");
        // create original generator
        Generator<Object[]> generator = null;
        generator = (Generator<Object[]>) DescriptorUtil.getGeneratorByName(type, context);
        if (generator == null)
            generator = createSourceGenerator(type, uniqueness, context);
        if (generator == null)
            generator = createSyntheticArrayGenerator(instanceName, type, uniqueness, context);
        else
            generator = createMutatingArrayGenerator(instanceName, type, uniqueness, generator, context);
        // create wrappers
        generator = TypeGeneratorFactory.wrapWithPostprocessors(generator, type, context);
        generator = DescriptorUtil.wrapWithProxy(generator, type);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<Object[]> createMutatingArrayGenerator(
    		String instanceName, ArrayTypeDescriptor type, Uniqueness uniqueness, Generator<Object[]> generator, BeneratorContext context) {
    	Map<String, NullableGenerator<?>> variables = DescriptorUtil.parseVariables(type, context);
    	List<ComponentBuilder<Object[]>> componentBuilders = null; // TODO v0.6.x mutate elements if configured createSyntheticElementBuilders(type, uniqueness, context);
	    return new SourceAwareGenerator<Object[]>(instanceName, generator, variables, componentBuilders, context);
    }

    private static Generator<Object[]> createSourceGenerator(
    		ArrayTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        // if no sourceObject is specified, there's nothing to do
        String sourceName = descriptor.getSource();
        if (sourceName == null)
            return null;
        // create sourceObject generator
        Generator<Object[]> generator = null;
        Object contextSourceObject = context.get(sourceName);
        if (contextSourceObject != null)
            generator = createSourceGeneratorFromObject(descriptor, context, generator, contextSourceObject);
        else {
        	String lcSourceName = sourceName.toLowerCase();
	        if (lcSourceName.endsWith(".csv"))
	            generator = createCSVSourceGenerator(descriptor, context, sourceName);
	        else if (lcSourceName.endsWith(".xls"))
	            generator = createXLSSourceGenerator(descriptor, context, sourceName);
	        else {
	        	try {
		        	Object sourceObject = BeneratorScriptParser.parseBeanSpec(sourceName).evaluate(context);
		        	return createSourceGeneratorFromObject(descriptor, context, generator, sourceObject);
	        	} catch (Exception e) {
	        		throw new UnsupportedOperationException("Unknown source type: " + sourceName);
	        	}
	        }
        }
        if (descriptor.getFilter() != null) {
        	Expression<Boolean> filter 
        		= new ScriptExpression<Boolean>(ScriptUtil.parseScriptText(descriptor.getFilter()));
        	generator = new FilteringGenerator<Object[]>(generator, filter);
        }
    	Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
        	generator = new DistributingGenerator<Object[]>(generator, distribution, uniqueness.isUnique());
    	return generator;
    }

    private static Generator<Object[]> createCSVSourceGenerator(ArrayTypeDescriptor descriptor, BeneratorContext context,
            String sourceName) {
    	if (logger.isDebugEnabled())
    		logger.debug("createCSVSourceGenerator(" + descriptor +")");
		String encoding = descriptor.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		char separator = DescriptorUtil.getSeparator(descriptor, context);
		SourceFactory<Entity> factory = new CSVEntitySourceFactory(descriptor.getName(), new ScriptConverter(context), separator, encoding);
		return createEntitySourceArrayGenerator(descriptor, context, sourceName, factory);
    }

    private static Generator<Object[]> createXLSSourceGenerator(ArrayTypeDescriptor descriptor, BeneratorContext context,
            String sourceName) {
    	if (logger.isDebugEnabled())
    		logger.debug("createXLSSourceGenerator(" + descriptor +")");
		SourceFactory<Entity> factory = new XLSEntitySourceFactory(descriptor.getName(), new ScriptConverter(context));
		return createEntitySourceArrayGenerator(descriptor, context, sourceName, factory);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<Object[]> createSourceGeneratorFromObject(ArrayTypeDescriptor descriptor,
            BeneratorContext context, Generator<Object[]> generator, Object sourceObject) {
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector))
	        	generator = GeneratorFactoryUtil.createCyclicHeadGenerator(new IteratingGenerator(storage.query(subSelector, context)));
	        else
	        	generator = new IteratingGenerator(storage.query(selector, context));
	    } else if (sourceObject instanceof EntitySource) {
	        IteratingGenerator<Entity> entityGenerator = new IteratingGenerator<Entity>((EntitySource) sourceObject);
			generator = new ConvertingGenerator<Entity, Object[]>(entityGenerator, new Entity2ArrayConverter());
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<Object[]>) sourceObject;
	    } else
	        throw new ConfigurationError("Source type not supported: " + sourceObject.getClass());
	    return generator;
    }

    private static Generator<Object[]> createSyntheticArrayGenerator(String name, 
            ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
        List<ComponentBuilder<Object[]>> elementBuilders = 
        	createSyntheticElementBuilders(arrayType, uniqueness, context);
        Generator<Object[]> baseGenerator;
        if (uniqueness.isUnique()) {
        	NullableGenerator<?>[] sources = new NullableGenerator[elementBuilders.size()];
        	for (int i = 0; i < sources.length; i++)
        		sources[i] = ((AbstractComponentBuilder<?>) elementBuilders.get(i)).getSource();
        	baseGenerator = new UniqueArrayGenerator(sources);
        	elementBuilders = null; // element builders are now controlled by the UniqueArrayGenerator
        } else
        	baseGenerator = new BlankArrayGenerator(arrayType.getElementCount());
        Map<String, NullableGenerator<?>> variables = DescriptorUtil.parseVariables(arrayType, context);
		return new SourceAwareGenerator<Object[]>(name, baseGenerator, variables, elementBuilders, context);
    }

	@SuppressWarnings("unchecked")
    private static List<ComponentBuilder<Object[]>> createSyntheticElementBuilders(ArrayTypeDescriptor arrayType,
            Uniqueness uniqueness, BeneratorContext context) {
	    List<ArrayElementDescriptor> elements = arrayType.getElements();
        List<ComponentBuilder<Object[]>> elementBuilders = new ArrayList<ComponentBuilder<Object[]>>();
		for (int i = 0; i < elements.size(); i++) {
			ArrayElementDescriptor elementDescriptor = elements.get(i);
            if (!arrayType.equals(elementDescriptor.getTypeDescriptor()) && elementDescriptor.getMode() != Mode.ignored) {
            	ComponentBuilder<Object[]> builder = (ComponentBuilder<Object[]>) 
            		ComponentBuilderFactory.createComponentBuilder(elementDescriptor, uniqueness, context);
				elementBuilders.add(builder); 
            }
        }
	    return elementBuilders;
    }

    
    private static Generator<Object[]> createEntitySourceArrayGenerator(ArrayTypeDescriptor arrayType,
            BeneratorContext context, String sourceName, SourceFactory<Entity> factory) {
	    Generator<Entity> generator = DescriptorUtil.createRawEntitySourceGenerator(arrayType, context, sourceName, factory);
		Generator<Object[]> result = new ConvertingGenerator<Entity, Object[]>(generator, new Entity2ArrayConverter());
		result = new ConvertingGenerator<Object[], Object[]>(result, new ArrayElementTypeConverter(arrayType));
		return result;
    }

}
