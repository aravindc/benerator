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

import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.databene.model.data.EntitySource;
import org.databene.model.data.Mode;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.model.storage.StorageSystem;
import org.databene.benerator.*;
import org.databene.benerator.composite.BlankEntityGenerator;
import org.databene.benerator.composite.ComponentBuilder;
import org.databene.benerator.composite.ComponentTypeConverter;
import org.databene.benerator.composite.SimpleTypeEntityGenerator;
import org.databene.benerator.composite.SourceAwareGenerator;
import org.databene.benerator.distribution.DistributingGenerator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.expression.ScriptExpression;
import org.databene.benerator.script.BeanSpec;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.util.FilteringGenerator;
import org.databene.benerator.wrapper.*;
import org.databene.commons.*;
import org.databene.document.flat.FlatFileColumnDescriptor;
import org.databene.document.flat.FlatFileUtil;
import org.databene.platform.dbunit.DbUnitEntitySource;
import org.databene.platform.flat.FlatFileEntitySource;
import org.databene.platform.xls.XLSEntitySourceFactory;
import org.databene.platform.csv.CSVEntitySourceFactory;
import org.databene.script.ScriptConverter;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Creates entity generators from entity metadata.<br/>
 * <br/>
 * Created: 08.09.2007 07:45:40
 * @author Volker Bergmann
 */
public class ComplexTypeGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ComplexTypeGeneratorFactory.class);

    /** private constructor for preventing instantiation */
    private ComplexTypeGeneratorFactory() {}
    
    // public utility methods ------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static Generator<Entity> createComplexTypeGenerator(String instanceName, 
    		ComplexTypeDescriptor type, Uniqueness uniqueness, BeneratorContext context) {
        if (logger.isDebugEnabled())
            logger.debug("createComplexTypeGenerator(" + type.getName() + ")");
        // create original generator
        Generator<Entity> generator = null;
        generator = (Generator<Entity>) DescriptorUtil.getGeneratorByName(type, context);
        if (generator == null)
            generator = createSourceGenerator(type, uniqueness, context);
        if (generator == null)
            generator = createSyntheticEntityGenerator(instanceName, type, uniqueness, context);
        else
            generator = createMutatingEntityGenerator(instanceName, type, uniqueness, context, generator);
        // create wrappers
        generator = TypeGeneratorFactory.wrapWithPostprocessors(generator, type, context);
        generator = GeneratorFactoryUtil.wrapWithProxy(generator, type);
        if (logger.isDebugEnabled())
            logger.debug("Created " + generator);
        return generator;
    }
    
    public static Generator<Entity> createMutatingEntityGenerator(String name, ComplexTypeDescriptor descriptor, 
    		Uniqueness ownerUniqueness, BeneratorContext context, Generator<Entity> source) {
    	List<ComponentBuilder<Entity>> componentBuilders = 
    		createMutatingComponentBuilders(descriptor, ownerUniqueness, context);
        Map<String, Generator<?>> variables = DescriptorUtil.parseVariables(descriptor, context);
        return new SourceAwareGenerator<Entity>(name, source, variables, componentBuilders, context);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static Generator<Entity> createSourceGenerator(ComplexTypeDescriptor descriptor, Uniqueness uniqueness, BeneratorContext context) {
        // if no sourceObject is specified, there's nothing to do
        String sourceSpec = descriptor.getSource();
        if (sourceSpec == null)
            return null;
        Object sourceObject = null;
        if (ScriptUtil.isScript(sourceSpec)) {
        	Object tmp = ScriptUtil.evaluate(sourceSpec, context); // TODO When to resolve scripts?
        	if (tmp != null && tmp instanceof String) {
        		sourceSpec = (String) tmp;
        		sourceObject = context.get(sourceSpec);
        	} else
        		sourceObject = tmp;
        }
        // create sourceObject generator
        
        Generator<Entity> generator = null;
        if (sourceObject != null)
            generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
        else {
        	String lcSourceName = sourceSpec.toLowerCase();
        	if (lcSourceName.endsWith(".xml"))
	            generator = new DataSourceGenerator<Entity>(new DbUnitEntitySource(sourceSpec, context));
	        else if (lcSourceName.endsWith(".csv"))
	            generator = createCSVSourceGenerator(descriptor, context, sourceSpec);
	        else if (lcSourceName.endsWith(".flat"))
	            generator = createFlatSourceGenerator(descriptor, context, sourceSpec);
	        else if (lcSourceName.endsWith(".xls"))
	            generator = createXLSSourceGenerator(descriptor, context, sourceSpec);
	        else {
	        	try {
		        	BeanSpec sourceBeanSpec = BeneratorScriptParser.resolveBeanSpec(sourceSpec, context);
		        	sourceObject = sourceBeanSpec.getBean();
		        	generator = createSourceGeneratorFromObject(descriptor, context, sourceObject);
		        	if (sourceBeanSpec.isReference() && !(sourceObject instanceof StorageSystem))
		        		generator = GeneratorFactoryUtil.wrapNonClosing(generator);
	        	} catch (Exception e) {
	        		throw new UnsupportedOperationException("Error resolving source: " + sourceSpec, e);
	        	}
	        }
        }
        if (generator.getGeneratedType() != Entity.class)
        	generator = new SimpleTypeEntityGenerator(generator, descriptor);
        if (descriptor.getFilter() != null) {
        	Expression<Boolean> filter 
        		= new ScriptExpression<Boolean>(ScriptUtil.parseScriptText(descriptor.getFilter()));
        	generator = new FilteringGenerator<Entity>(generator, filter);
        }
    	Distribution distribution = GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), uniqueness, false, context);
        if (distribution != null)
        	generator = new DistributingGenerator<Entity>(generator, distribution, uniqueness.isUnique());
    	return generator;
    }

    @SuppressWarnings("unchecked")
    private static Generator<Entity> createSourceGeneratorFromObject(ComplexTypeDescriptor descriptor,
            BeneratorContext context, Object sourceObject) {
    	Generator<Entity> generator;
	    if (sourceObject instanceof StorageSystem) {
	        StorageSystem storage = (StorageSystem) sourceObject;
	        String selector = descriptor.getSelector();
	        String subSelector = descriptor.getSubSelector();
	        if (!StringUtil.isEmpty(subSelector))
	        	generator = GeneratorFactoryUtil.createCyclicHeadGenerator(new DataSourceGenerator<Entity>(storage.queryEntities(descriptor.getName(), subSelector, context)));
	        else 
	        	generator = new DataSourceGenerator<Entity>(storage.queryEntities(descriptor.getName(), selector, context));
	    } else if (sourceObject instanceof EntitySource) {
	        generator = new DataSourceGenerator<Entity>((EntitySource) sourceObject);
	    } else if (sourceObject instanceof Generator) {
	        generator = (Generator<Entity>) sourceObject;
	    } else
	        throw new UnsupportedOperationException("Source type not supported: " + sourceObject.getClass());
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
		Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
		FlatFileEntitySource iterable = new FlatFileEntitySource(sourceName, descriptor, scriptConverter, encoding, null, ffcd);
		iterable.setContext(context);
		generator = new DataSourceGenerator<Entity>(iterable);
		return generator;
	}

    private static Generator<Entity> createCSVSourceGenerator(
			ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
		String encoding = complexType.getEncoding();
		if (encoding == null)
		    encoding = context.getDefaultEncoding();
		Converter<String, String> scriptConverter = DescriptorUtil.createStringScriptConverter(context);
		char separator = DescriptorUtil.getSeparator(complexType, context);
	    SourceFactory<Entity> fileProvider = new CSVEntitySourceFactory(complexType.getName(), scriptConverter, 
	    		separator, encoding);
	    return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
	}
    
    private static Generator<Entity> createXLSSourceGenerator(
			ComplexTypeDescriptor complexType, BeneratorContext context, String sourceName) {
	    SourceFactory<Entity> fileProvider = new XLSEntitySourceFactory(
	    		complexType.getName(), new ScriptConverter(context));
		return createEntitySourceGenerator(complexType, context, sourceName, fileProvider);
	}

    private static Generator<Entity> createSyntheticEntityGenerator(String name, 
            ComplexTypeDescriptor complexType, Uniqueness ownerUniqueness, BeneratorContext context) {
        Map<String, Generator<?>> variables = DescriptorUtil.parseVariables(complexType, context);
        
        Generator<Entity> source;
        List<ComponentBuilder<Entity>> componentBuilders = null;
        if (DescriptorUtil.isWrappedSimpleType(complexType))
    		source = createSimpleTypeEntityGenerator(complexType, ownerUniqueness, context);
        else {
        	componentBuilders = createSyntheticComponentBuilders(complexType, ownerUniqueness, context);
    		source = new BlankEntityGenerator(complexType);
        }
		return new SourceAwareGenerator<Entity>(name, source, variables, componentBuilders, context);
    }

	private static Generator<Entity> createSimpleTypeEntityGenerator(ComplexTypeDescriptor complexType,
            Uniqueness ownerUniqueness, BeneratorContext context) {
	    TypeDescriptor contentType = complexType.getComponent(ComplexTypeDescriptor.__SIMPLE_CONTENT).getTypeDescriptor();
	    Generator<?> generator = SimpleTypeGeneratorFactory.createSimpleTypeGenerator(
	    		(SimpleTypeDescriptor) contentType, false, ownerUniqueness, context);
	    Generator<Entity> source = new SimpleTypeEntityGenerator(generator, complexType);
	    return source;
    }

	@SuppressWarnings("unchecked")
    private static List<ComponentBuilder<Entity>> createSyntheticComponentBuilders(ComplexTypeDescriptor complexType,
            Uniqueness ownerUniqueness, BeneratorContext context) {
	    List<ComponentBuilder<Entity>> componentBuilders = new ArrayList<ComponentBuilder<Entity>>();
        Collection<ComponentDescriptor> components = complexType.getComponents();
        for (ComponentDescriptor component : components) {
            if (!complexType.equals(component.getTypeDescriptor()) && component.getMode() != Mode.ignored) {
            	String componentName = component.getName();
				ComponentDescriptor defaultComponentConfig = context.getDefaultComponentConfig(componentName);
				if (!complexType.isDeclaredComponent(componentName) && defaultComponentConfig != null)
					component = defaultComponentConfig;
            	ComponentBuilder<Entity> builder = (ComponentBuilder<Entity>) ComponentBuilderFactory.createComponentBuilder(component, ownerUniqueness, context);
				componentBuilders.add(builder); 
            }
        }
	    return componentBuilders;
    }

	@SuppressWarnings("unchecked")
	public static List<ComponentBuilder<Entity>> createMutatingComponentBuilders(ComplexTypeDescriptor descriptor,
            Uniqueness ownerUniqueness, BeneratorContext context) {
	    List<ComponentBuilder<Entity>> componentBuilders = new ArrayList<ComponentBuilder<Entity>>();
        Collection<ComponentDescriptor> components = descriptor.getDeclaredComponents();
        for (ComponentDescriptor component : components)
            if (component.getMode() != Mode.ignored && !ComplexTypeDescriptor.__SIMPLE_CONTENT.equals(component.getName())) {
            	try {
                	ComponentBuilder<Entity> builder = (ComponentBuilder<Entity>) 
                		ComponentBuilderFactory.createComponentBuilder(component, ownerUniqueness, context);
    	            componentBuilders.add(builder);
            	} catch (Exception e) {
            		throw new ConfigurationError("Error creating component builder for " + component, e);
            	}
            }
	    return componentBuilders;
    }
	
	private static Generator<Entity> createEntitySourceGenerator(ComplexTypeDescriptor complexType,
            BeneratorContext context, String sourceName, SourceFactory<Entity> factory) {
	    Generator<Entity> generator = DescriptorUtil.createRawSourceGenerator(complexType.getNesting(), complexType.getDataset(), sourceName, factory, Entity.class, context);
		generator = new ConvertingGenerator<Entity, Entity>(generator, new ComponentTypeConverter(complexType));
		return generator;
    }

}
