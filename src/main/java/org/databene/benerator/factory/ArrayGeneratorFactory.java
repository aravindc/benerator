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
import org.databene.benerator.StorageSystem;
import org.databene.benerator.composite.AbstractComponentBuilder;
import org.databene.benerator.composite.ArrayElementTypeConverter;
import org.databene.benerator.composite.BlankArrayGenerator;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.SourceAwareGenerator;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.script.BeanSpec;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.util.FilteringGenerator;
import org.databene.benerator.wrapper.DataSourceGenerator;
import org.databene.benerator.wrapper.UniqueMultiSourceArrayGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.Mode;
import org.databene.model.data.Uniqueness;
import org.databene.platform.array.Entity2ArrayConverter;
import org.databene.platform.csv.CSVArraySourceProvider;
import org.databene.platform.xls.XLSArraySourceProvider;
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
        logger.debug("createArrayGenerator({})", type.getName());
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
        logger.debug("Created {}", generator);
        return generator;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    public static Generator<Object[]> createSyntheticArrayGenerator(String name, 
            ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
        List<ComponentBuilder<Object[]>> elementBuilders = 
        	createSyntheticElementBuilders(arrayType, uniqueness, context);
        Generator<Object[]> baseGenerator;
        if (uniqueness.isUnique()) {
        	// TODO v0.8 we first create ComponentBuilders and then unwrap them! This can be simplified... 
        	// ...possibly with a NullableInstanceGeneratorFactory which takes over...
        	// ...most of the ComponentBuilderFactory's functionality
        	Generator<?>[] generators = new Generator[elementBuilders.size()];
        	for (int i = 0; i < generators.length; i++)
        		generators[i] = ((AbstractComponentBuilder<?>) elementBuilders.get(i)).getSource();
        	baseGenerator = new UniqueMultiSourceArrayGenerator<Object>(Object.class, generators);
        	elementBuilders = null; // element builders are now controlled by the UniqueArrayGenerator
        } else
        	baseGenerator = new BlankArrayGenerator(arrayType.getElementCount());
        Map<String, Generator<?>> variables = DescriptorUtil.parseVariables(arrayType, context);
		return new SourceAwareGenerator<Object[]>(name, baseGenerator, variables, elementBuilders, context);
    }

    @SuppressWarnings("unchecked")
	public static Generator<Object[]> createSimpleArrayGenerator(String name, 
            ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
    	// TODO v0.8 this method was written as a quick hack for the functionality required in Feed4JUnit 
		// and needs to be merged into createSyntheticArrayGenerator() in the long run
        List<ComponentBuilder<Object[]>> elementBuilders = 
        	createSyntheticElementBuilders(arrayType, uniqueness, context);
    	@SuppressWarnings("rawtypes")
		Generator[] generators = new Generator[elementBuilders.size()];
    	// TODO v0.8 we first create ComponentBuilders and then unwrap them! This can be simplified... 
    	// ...possibly with a NullableInstanceGeneratorFactory which takes over most of the ComponentBuilderFactory's functionality
    	for (int i = 0; i < generators.length; i++)
    		generators[i] = ((AbstractComponentBuilder<?>) elementBuilders.get(i)).getSource();
    	return context.getGeneratorFactory().createCompositeArrayGenerator(Object.class, generators, uniqueness);
    }

    private static Generator<Object[]> createMutatingArrayGenerator(String instanceName, ArrayTypeDescriptor type, 
    		Uniqueness uniqueness, Generator<Object[]> generator, BeneratorContext context) {
    	Map<String, Generator<?>> variables = DescriptorUtil.parseVariables(type, context);
    	List<ComponentBuilder<Object[]>> componentBuilders = null; // TODO v0.8 mutate elements if configured createSyntheticElementBuilders(type, uniqueness, context);
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
		        	BeanSpec sourceBeanSpec = BeneratorScriptParser.resolveBeanSpec(sourceName, context);
		        	Object sourceObject = sourceBeanSpec.getBean();
		        	generator = createSourceGeneratorFromObject(descriptor, context, generator, sourceObject);
		        	if (sourceBeanSpec.isReference())
		        		generator = WrapperFactory.preventClosing(generator);
		        	return generator;
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
    	Distribution distribution = FactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
        	generator = new DistributingGenerator<Object[]>(generator, distribution, uniqueness.isUnique());
        generator = DescriptorUtil.wrapWithProxy(generator, descriptor);
    	return generator;
    }

    private static Generator<Object[]> createCSVSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
            String sourceName) {
    	logger.debug("createCSVSourceGenerator({})", arrayType);
		String encoding = arrayType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		char separator = DescriptorUtil.getSeparator(arrayType, context);
		DataSourceProvider<Object[]> factory = new CSVArraySourceProvider(arrayType.getName(), new ScriptConverter(context), separator, encoding);
		Generator<Object[]> generator = SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
		return WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
    }

	private static Generator<Object[]> createXLSSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
            String sourceName) {
		logger.debug("createXLSSourceGenerator({})", arrayType);
		DataSourceProvider<Object[]> factory = new XLSArraySourceProvider(new ScriptConverter(context));
		Generator<Object[]> generator = SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
		generator = WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
		return generator;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
    private static Generator<Object[]> createSourceGeneratorFromObject(ArrayTypeDescriptor descriptor,
            BeneratorContext context, Generator<Object[]> generator, Object sourceObject) {
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector))
	        	generator = WrapperFactory.applyHeadCycler(new DataSourceGenerator(storage.query(subSelector, false, context)));
	        else
	        	generator = new DataSourceGenerator(storage.query(selector, false, context));
	    } else if (sourceObject instanceof EntitySource) {
	    	DataSourceGenerator<Entity> entityGenerator = new DataSourceGenerator<Entity>((EntitySource) sourceObject);
			generator = WrapperFactory.applyConverter(entityGenerator, new Entity2ArrayConverter());
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<Object[]>) sourceObject;
	    } else
	        throw new ConfigurationError("Source type not supported: " + sourceObject.getClass());
	    return generator;
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

}
