/*
 * (c) Copyright 2010-2013 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.StorageSystem;
import org.databene.benerator.composite.ArrayElementTypeConverter;
import org.databene.benerator.composite.BlankArrayGenerator;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.util.FilteringGenerator;
import org.databene.benerator.wrapper.DataSourceGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ConfigurationError;
import org.databene.commons.StringUtil;
import org.databene.commons.context.ContextAware;
import org.databene.formats.script.ScriptConverterForObjects;
import org.databene.formats.script.ScriptConverterForStrings;
import org.databene.formats.script.ScriptUtil;
import org.databene.formats.util.DataFileUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.Mode;
import org.databene.model.data.Uniqueness;
import org.databene.platform.array.Entity2ArrayConverter;
import org.databene.platform.csv.CSVArraySourceProvider;
import org.databene.platform.xls.XLSArraySourceProvider;
import org.databene.script.BeanSpec;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;

/**
 * Creates array {@link Generator}s.<br><br>
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
	        if (DataFileUtil.isCsvDocument(sourceName))
	            generator = createCSVSourceGenerator(descriptor, context, sourceName);
	        else if (DataFileUtil.isExcelDocument(sourceName))
	            generator = createXLSSourceGenerator(descriptor, context, sourceName);
	        else {
	        	try {
		        	BeanSpec sourceBeanSpec = DatabeneScriptParser.resolveBeanSpec(sourceName, context);
		        	Object sourceObject = sourceBeanSpec.getBean();
		        	if (!sourceBeanSpec.isReference() && sourceObject instanceof ContextAware)
		        		((ContextAware) sourceObject).setContext(context);
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
    	return generator;
    }

	@Override
	protected Generator<?> createSpecificGenerator(ArrayTypeDescriptor descriptor, String instanceName,
			boolean nullable, Uniqueness uniqueness, BeneratorContext context) {
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
	protected Generator<?> applyComponentBuilders(Generator<?> generator, ArrayTypeDescriptor descriptor, String instanceName,
			Uniqueness uniqueness, BeneratorContext context) {
		Generator[] generators;
		// create synthetic element generators if necessary
		if (generator instanceof BlankArrayGenerator) {
			generators = createSyntheticElementGenerators(descriptor, uniqueness, context);
        	generator = context.getGeneratorFactory().createCompositeArrayGenerator(
        			Object.class, generators, uniqueness);
		}
		// ... and don't forget to support the parent class' functionality
		generator = super.applyComponentBuilders(generator, descriptor, instanceName, uniqueness, context);
		return generator;
	}

	@Override
	protected Class<?> getGeneratedType(ArrayTypeDescriptor descriptor) {
		return Object[].class;
	}

    // private helpers -------------------------------------------------------------------------------------------------

    private Generator<Object[]> createCSVSourceGenerator(ArrayTypeDescriptor arrayType, BeneratorContext context,
            String sourceName) {
    	logger.debug("createCSVSourceGenerator({})", arrayType);
		String encoding = arrayType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		char separator = DescriptorUtil.getSeparator(arrayType, context);
		boolean rowBased = (arrayType.isRowBased() != null ? arrayType.isRowBased() : true);
		DataSourceProvider<Object[]> factory = new CSVArraySourceProvider(arrayType.getName(), new ScriptConverterForStrings(context), rowBased, separator, encoding);
		Generator<Object[]> generator = SourceFactory.createRawSourceGenerator(arrayType.getNesting(), arrayType.getDataset(), sourceName, factory, Object[].class, context);
		return WrapperFactory.applyConverter(generator, new ArrayElementTypeConverter(arrayType));
    }

	private Generator<Object[]> createXLSSourceGenerator(
			ArrayTypeDescriptor arrayType, BeneratorContext context, String sourceName) {
		logger.debug("createXLSSourceGenerator({})", arrayType);
		boolean rowBased = (arrayType.isRowBased() != null ? arrayType.isRowBased() : true);
		String emptyMarker = arrayType.getEmptyMarker();
		String nullMarker = arrayType.getNullMarker();
    	boolean formatted = isFormatted(arrayType);
		DataSourceProvider<Object[]> factory = new XLSArraySourceProvider(formatted, 
				new ScriptConverterForObjects(context), emptyMarker, nullMarker, rowBased);
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
		Generator[] result = new Generator[arrayType.getElementCount()];
		for (int i = 0; i < arrayType.getElementCount(); i++) {
			ArrayElementDescriptor element = getElementOfTypeOrParents(arrayType, i);
			if (element.getMode() != Mode.ignored) {
	            Generator<?> generator = InstanceGeneratorFactory.createSingleInstanceGenerator(
	            		element, uniqueness, context);
				result[i] = generator; 
			}
        }
	    return result;
    }

	protected ArrayElementDescriptor getElementOfTypeOrParents(ArrayTypeDescriptor arrayType, int index) {
		ArrayTypeDescriptor tmp = arrayType;
		ArrayElementDescriptor result;
		while ((result = tmp.getElement(index)) == null && tmp.getParent() != null)
			tmp = tmp.getParent();
		return result;
	}

}
