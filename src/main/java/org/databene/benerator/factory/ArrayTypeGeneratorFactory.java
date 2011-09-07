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

import org.databene.benerator.Generator;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.composite.ArrayElementBuilder;
import org.databene.benerator.composite.ArrayElementTypeConverter;
import org.databene.benerator.composite.BlankArrayGenerator;
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
import org.databene.commons.ArrayBuilder;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Expression;
import org.databene.commons.StringUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.Uniqueness;
import org.databene.platform.array.Entity2ArrayConverter;
import org.databene.platform.csv.CSVArraySourceProvider;
import org.databene.platform.xls.XLSArraySourceProvider;
import org.databene.script.ScriptConverterForObjects;
import org.databene.script.ScriptConverterForStrings;
import org.databene.script.ScriptUtil;

/**
 * Creates array {@link Generator}s.<br/><br/>
 * Created: 29.04.2010 07:45:18
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ArrayTypeGeneratorFactory extends TypeGeneratorFactory<ArrayTypeDescriptor> {

    @Override
	protected Generator<Object[]> createSourceGenerator(
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

	@Override
	protected Generator<?> createSpecificGenerator(ArrayTypeDescriptor descriptor, String instanceName,
			boolean nullifyIfNullable, Uniqueness uniqueness, BeneratorContext context) {
		return null;
	}

	@Override
	protected Generator<?> createHeuristicGenerator(
			ArrayTypeDescriptor descriptor, String instanceName,
			Uniqueness uniqueness, BeneratorContext context) {
    	return new BlankArrayGenerator(descriptor.getElementCount());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Generator<?> applyWrappers(Generator<?> generator, ArrayTypeDescriptor descriptor, String instanceName,
			Uniqueness uniqueness, BeneratorContext context) {
        Generator<?>[] generators = createSyntheticElementGenerators(descriptor, uniqueness, context);
        if (uniqueness.isUnique() && generators.length > 1) {
        	generator = new UniqueMultiSourceArrayGenerator<Object>(Object.class, generators);
        	generators = null; // element builders are now controlled by the UniqueArrayGenerator
        }
		generator = new SourceAwareGenerator(
				instanceName, generator, createElementBuilders(generators), context);
		return super.applyWrappers(generator, descriptor, instanceName, uniqueness, context);
	}

	@Override
	protected Class<?> getGeneratedType(ArrayTypeDescriptor descriptor) {
		return Object[].class;
	}

    // private helpers -------------------------------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Generator<Object[]> createSimpleArrayGenerator(String name, 
            ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
        Generator[] elementGenerators = createSyntheticElementGenerators(arrayType, uniqueness, context);
    	return context.getGeneratorFactory().createCompositeArrayGenerator(Object.class, elementGenerators, uniqueness);
    }

    private Generator<Object[]> createCSVSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
            String sourceName) {
    	logger.debug("createCSVSourceGenerator({})", arrayType);
		String encoding = arrayType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		char separator = DescriptorUtil.getSeparator(arrayType, context);
		DataSourceProvider<Object[]> factory = new CSVArraySourceProvider(arrayType.getName(), new ScriptConverterForStrings(context), separator, encoding);
		Generator<Object[]> generator = SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
		return WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
    }

	private Generator<Object[]> createXLSSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
            String sourceName) {
		logger.debug("createXLSSourceGenerator({})", arrayType);
		DataSourceProvider<Object[]> factory = new XLSArraySourceProvider(new ScriptConverterForObjects(context));
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

    @SuppressWarnings("rawtypes")
	private Generator<?>[] createSyntheticElementGenerators(
    		ArrayTypeDescriptor arrayType, Uniqueness uniqueness, BeneratorContext context) {
		ArrayBuilder<Generator> result = new ArrayBuilder<Generator>(Generator.class);
		for (InstanceDescriptor instance : arrayType.getParts()) {
            if (!arrayType.equals(instance.getTypeDescriptor()) && 
            		!(instance instanceof ArrayElementDescriptor && 
            				((ArrayElementDescriptor) instance).getMode() == Mode.ignored)) {
            	Generator<?> generator = 
            		InstanceGeneratorFactory.createSingleInstanceGenerator(instance, uniqueness, context);
				result.add(generator); 
            } else
            	result.add(null);
        }
	    return result.toArray();
    }

	private List<ArrayElementBuilder> createElementBuilders(Generator<?>[] generators) {
		if (generators == null)
			return new ArrayList<ArrayElementBuilder>();
		ArrayList<ArrayElementBuilder> result = new ArrayList<ArrayElementBuilder>(generators.length);
		for (int i = 0; i < generators.length; i++)
			result.add(new ArrayElementBuilder(i, generators[i]));
		return result;
	}

}
