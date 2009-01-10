/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.Mode;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.function.Distribution;
import org.databene.model.function.IndividualWeight;
import org.databene.model.function.Sequence;
import org.databene.model.function.WeightFunction;
import org.databene.model.storage.StorageSystem;
import org.databene.benerator.*;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.ComponentTypeConverter;
import org.databene.benerator.composite.ConfiguredEntityGenerator;
import org.databene.benerator.composite.EntityGenerator;
import org.databene.benerator.composite.SimpleTypeEntityGenerator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.sample.WeightedSampleGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.commons.converter.ConverterChain;
import org.databene.commons.converter.ToStringConverter;
import org.databene.dataset.DatasetFactory;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.document.flat.FlatFileUtil;
import org.databene.platform.dbunit.DbUnitEntitySource;
import org.databene.platform.flat.FlatFileEntitySource;
import org.databene.platform.csv.CSVEntitySource;
import org.databene.script.ScriptConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class ComplexTypeGeneratorFactory {

    private static final Log logger = LogFactory.getLog(ComplexTypeGeneratorFactory.class);

    /** private constructor for preventing instantiation */
    private ComplexTypeGeneratorFactory() {}
    
    // public utility methods ------------------------------------------------------------------------------------------

    public static Generator<Entity> createComplexTypeGenerator(
    		ComplexTypeDescriptor type, boolean unique, BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("create(" + type.getName() + ")");
        // create original generator
        Generator<Entity> generator = null;
        generator = (Generator<Entity>) DescriptorUtil.getGeneratorByName(type, context);
        if (generator == null)
            generator = createSourceGenerator(type, context);
        if (generator == null)
            generator = createSyntheticEntityGenerator(type, unique, context);
        else
            generator = createMutatingEntityGenerator(type, context, generator);
        // create wrappers
        generator = TypeGeneratorFactory.wrapWithPostprocessors(generator, type, context);
        generator = wrapGeneratorWithVariables(type, context, generator);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<Entity> wrapGeneratorWithVariables(
            ComplexTypeDescriptor type, BeneratorContext context, Generator<Entity> generator) {
        Collection<InstanceDescriptor> variables = variablesOfThisAndParents(type);
            Map<String, Generator<? extends Object>> varGens = new HashMap<String, Generator<? extends Object>>();
            for (InstanceDescriptor variable : variables) {
                Generator<? extends Object> varGen = InstanceGeneratorFactory.createInstanceGenerator(variable, context);
                varGens.put(variable.getName(), varGen);
            }
        return new ConfiguredEntityGenerator((Generator<Entity>) generator, varGens, context);
    }

    @SuppressWarnings("unchecked")
	private static Collection<InstanceDescriptor> variablesOfThisAndParents(TypeDescriptor type) {
        Collection<InstanceDescriptor> variables = new ArrayList<InstanceDescriptor>();
        while (type instanceof ComplexTypeDescriptor) {
            variables.addAll(((ComplexTypeDescriptor) type).getVariables());
            type = type.getParent();
        }
        return variables;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<Entity> createSourceGenerator(
            ComplexTypeDescriptor descriptor, BeneratorContext context) {
        // if no sourceObject is specified, there's nothing to do
        String sourceName = descriptor.getSource();
        if (sourceName == null)
            return null;
        // create sourceObject generator
        Generator<Entity> generator = null;
        Object sourceObject = context.get(sourceName);
        if (sourceObject != null) {
            if (sourceObject instanceof StorageSystem) {
                StorageSystem storage = (StorageSystem) sourceObject;
                String selector = descriptor.getSelector();
                generator = new IteratingGenerator<Entity>(storage.queryEntities(descriptor.getName(), selector, context));
            } else if (sourceObject instanceof EntitySource) {
                generator = new IteratingGenerator<Entity>((EntitySource) sourceObject);
            } else if (sourceObject instanceof Generator) {
                generator = (Generator) sourceObject;
            } else
                throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
        } else {
        	String uri = IOUtil.resolveLocalUri(sourceName, context.getContextUri());
            if (uri.endsWith(".xml")) {
                generator = new IteratingGenerator<Entity>(new DbUnitEntitySource(uri, context));
            } else if (uri.endsWith(".csv")) {
                generator = createCSVSourceGenerator(descriptor, context, uri);
            } else if (uri.endsWith(".flat")) {
                generator = createFlatSourceGenerator(descriptor, context, uri);
            } else
                throw new UnsupportedOperationException("Unknown source type: " + sourceName);
        }
        if (generator.getGeneratedType() != Entity.class)
        	generator = new SimpleTypeEntityGenerator(generator, descriptor);
		Distribution distribution = DescriptorUtil.getDistribution(descriptor, false, context); // TODO what about uniqueness? (arbitrarily set to false)
        if (distribution != null)
        	return applyDistribution(distribution, descriptor, generator, context);
        else
        	return DescriptorUtil.wrapWithProxy(generator, descriptor, context);
    }

	private static Generator<Entity> applyDistribution(
			Distribution distribution, ComplexTypeDescriptor descriptor, Generator<Entity> generator, BeneratorContext context) {
		List<Entity> values = GeneratorUtil.allProducts(generator);
		if (distribution instanceof Sequence) {
			generator = new SequencedSampleGenerator<Entity>(Entity.class, (Sequence) distribution, values);
		} else if (distribution instanceof WeightFunction || distribution instanceof IndividualWeight)
			generator = new WeightedSampleGenerator<Entity>(Entity.class, distribution, values);
		else
			throw new ConfigurationError("Not a supported distribution: " + distribution);
		if (descriptor.getVariation1() != null)
			BeanUtil.setPropertyValue(generator, "variation1", descriptor.getVariation1(), false);
		if (descriptor.getVariation2() != null)
			BeanUtil.setPropertyValue(generator, "variation2", descriptor.getVariation2(), false);
		return generator;
	}

	private static Generator<Entity> createFlatSourceGenerator(
			ComplexTypeDescriptor descriptor, BeneratorContext context, String sourceName) {
		Generator<Entity> generator;
		String encoding = descriptor.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		String pattern = descriptor.getPattern();
		if (pattern == null)
		    throw new ConfigurationError("No pattern specified for flat file import: " + sourceName);
		FlatFileColumnDescriptor[] ffcd = FlatFileUtil.parseProperties(pattern);
		Converter<String, String> scriptConverter = createScriptConverter(context);
		FlatFileEntitySource iterable = new FlatFileEntitySource(sourceName, descriptor, scriptConverter, encoding, ffcd);
		generator = new IteratingGenerator<Entity>(iterable);
		return generator;
	}

	private static Converter<String, String> createScriptConverter(
			BeneratorContext context) {
		Converter<String, String> scriptConverter = new ConverterChain<String, String>(
				new ScriptConverter(context),
				new ToStringConverter<Object>(null)
			);
		return scriptConverter;
	}

	private static Generator<Entity> createCSVSourceGenerator(
			ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
		Generator<Entity> generator;
		String encoding = complexType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		Converter<String, String> scriptConverter = createScriptConverter(context);
		String dataset = complexType.getDataset();
		String nesting = complexType.getNesting();
		char separator = DescriptorUtil.getSeparator(complexType, context);
		if (dataset != null && nesting != null) {
		    String[] dataFiles = DatasetFactory.getDataFiles(sourceName, dataset, nesting);
		    Generator<Entity>[] sources = new Generator[dataFiles.length];
		    for (int i = 0; i < dataFiles.length; i++)
		        sources[i] = new IteratingGenerator(new CSVEntitySource(dataFiles[i], complexType.getName(), separator, encoding));
		    generator = new AlternativeGenerator(Entity.class, sources); 
		} else {
		    // iterate over (possibly large) data file
			CSVEntitySource iterable = new CSVEntitySource(sourceName, complexType.getName(), scriptConverter, separator, encoding);
		    generator = new IteratingGenerator(iterable);
		}
		generator = new ConvertingGenerator<Entity, Entity>(generator, new ComponentTypeConverter(complexType));
		return generator;
	}

    private static Generator<Entity> createSyntheticEntityGenerator(
            ComplexTypeDescriptor complexType, boolean unique, BeneratorContext context) {
        List<ComponentBuilder> componentBuilders = new ArrayList<ComponentBuilder>();
        if (DescriptorUtil.isWrappedSimpleType(complexType)) {
    		TypeDescriptor contentType = complexType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getType();
    		Generator<Object> generator = (Generator<Object>) SimpleTypeGeneratorFactory.createSimpleTypeGenerator(
    				(SimpleTypeDescriptor) contentType, false, unique, context);
        	return new SimpleTypeEntityGenerator(generator, complexType);
        }
        Collection<ComponentDescriptor> components = complexType.getComponents();
        for (ComponentDescriptor component : components) {
            if (!complexType.equals(component.getType()) && component.getMode() != Mode.ignored) {
            	String componentName = component.getName();
				ComponentDescriptor defaultComponentConfig = context.getDefaultComponentConfig(componentName);
				if (!complexType.isDeclaredComponent(componentName) && defaultComponentConfig != null)
					component = defaultComponentConfig;
            	ComponentBuilder builder = ComponentBuilderFactory.createComponentBuilder(component, context);
				componentBuilders.add(builder); 
            }
        }
    	return new EntityGenerator(complexType, componentBuilders, context);
    }

	private static Generator<Entity> createMutatingEntityGenerator(
            ComplexTypeDescriptor descriptor, BeneratorContext context, Generator<Entity> generator) {
    	List<ComponentBuilder> componentGenerators = new ArrayList<ComponentBuilder>();
        Collection<ComponentDescriptor> components = descriptor.getDeclaredComponents();
        for (ComponentDescriptor component : components)
            if (component.getMode() != Mode.ignored && !ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(component.getName()))
            	componentGenerators.add(ComponentBuilderFactory.createComponentBuilder(component, context));
        return new EntityGenerator(descriptor, generator, componentGenerators, context);
    }
}
