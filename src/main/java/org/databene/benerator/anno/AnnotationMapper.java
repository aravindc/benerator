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

package org.databene.benerator.anno;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.engine.DescriptorBasedGenerator;
import org.databene.benerator.factory.ArrayGeneratorFactory;
import org.databene.benerator.factory.CoverageGeneratorFactory;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.factory.EquivalenceGeneratorFactory;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.factory.GentleDefaultsProvider;
import org.databene.benerator.factory.InstanceGeneratorFactory;
import org.databene.benerator.factory.MeanDefaultsProvider;
import org.databene.benerator.factory.SerialGeneratorFactory;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import org.databene.benerator.script.BeneratorScriptParser;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ParseException;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.ComplexTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PrimitiveDescriptorProvider;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.platform.db.DBSystem;
import org.databene.platform.java.BeanDescriptorProvider;
import org.databene.platform.java.Entity2JavaConverter;
import org.databene.script.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps Java annotations to descriptor objects.<br/><br/>
 * Created: 29.04.2010 06:59:02
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class AnnotationMapper {
	
	private static final Package BENERATOR_ANNO_PACKAGE = Unique.class.getPackage();

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationMapper.class);
	
	private static final Set<String> STANDARD_METHODS;

	private static final Object BEANVAL_ANNO_PACKAGE = Max.class.getPackage();

	static {
		STANDARD_METHODS = new HashSet<String>();
		for (Method method : Annotation.class.getMethods())
			STANDARD_METHODS.add(method.getName());
	}

	private DataModel dataModel;

	private GeneratorFactory defaultFactory;
	
	public AnnotationMapper(GeneratorFactory defaultFactory) {
		this.defaultFactory = defaultFactory;
		this.dataModel = new DataModel();
		this.dataModel.addDescriptorProvider(PrimitiveDescriptorProvider.INSTANCE);
		this.dataModel.addDescriptorProvider(BeanDescriptorProvider.defaultInstance());
	}
	
	// interface -------------------------------------------------------------------------------------------------------
	
	public void parseClassAnnotations(Annotation[] annotations, BeneratorContext context) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Database)
				parseDatabase((Database) annotation, context);
			else if (annotation instanceof Bean)
				parseBean((Bean) annotation, context);
		}
	}
	
	public Generator<?> createAndInitAttributeGenerator(Field attribute, BeneratorContext context) {
		Source sourceAnno = attribute.getAnnotation(Source.class);
		if (sourceAnno != null)
			return createAndInitAttributeSourceGenerator(sourceAnno, attribute, context);
		else
			return null;
	}

    public Generator<Object[]> createAndInitMethodParamsGenerator(Method testMethod, BeneratorContext context) {
		try {
			applyMethodGeneratorFactory(testMethod, context);

			// Evaluate @Bean and @Database annotations
			if (testMethod.getAnnotation(Bean.class) != null)
				parseBean(testMethod.getAnnotation(Bean.class), context);
			if (testMethod.getAnnotation(Database.class) != null)
				parseDatabase(testMethod.getAnnotation(Database.class), context);
			Generator<Object[]> generator = null;
			
			// Evaluate annotations
			org.databene.benerator.anno.Generator generatorAnno = testMethod.getAnnotation(org.databene.benerator.anno.Generator.class);
			Source sourceAnno = testMethod.getAnnotation(Source.class);
			Descriptor descriptorBasedAnno = testMethod.getAnnotation(Descriptor.class);
			// If there are method annotations, evaluate them, ...
			if (generatorAnno != null)
				generator = createGeneratorGenerator(generatorAnno, testMethod, context);
			else if (sourceAnno != null)
				generator = createMethodSourceGenerator(sourceAnno, testMethod, context);
			else if (descriptorBasedAnno != null)
				generator = createDescriptorBasedGenerator(descriptorBasedAnno, testMethod);
			else // ... otherwise evaluate parameter annotations
				generator = createParamsGenerator(testMethod, context);
			
			// apply offset
			Offset offset = testMethod.getAnnotation(Offset.class);
			if (offset != null)
				generator = WrapperFactory.applyOffset(generator, offset.value());
			
			// evaluate @TestFeed annotation
			InvocationCount testCount = testMethod.getAnnotation(InvocationCount.class);
			if (testCount != null)
				generator = new NShotGeneratorProxy<Object[]>(generator, testCount.value());
			
			// create and return FeedIterator
			generator.init(context);
			return generator;
		} catch (IOException e) {
			throw new ConfigurationError(e);
		}
    }

    // helper methods --------------------------------------------------------------------------------------------------
	
	protected void applyMethodGeneratorFactory(Method testMethod, BeneratorContext context) {
		boolean configured = applyGeneratorFactory(testMethod.getAnnotations(), context);
		if (!configured)
			applyClassGeneratorFactory(testMethod.getDeclaringClass().getAnnotations(), context);
		applyMethodDefaultsProvider(testMethod, context);
	}

	private void applyClassGeneratorFactory(Annotation[] annotations, BeneratorContext context) {
		boolean configured = applyGeneratorFactory(annotations, context);
		if (!configured)
			context.setGeneratorFactory(defaultFactory);
	}

	protected boolean applyGeneratorFactory(Annotation[] annotations, BeneratorContext context) {
		boolean configured = false;
		for (Annotation annotation : annotations) {
			if (annotation instanceof Equivalence) {
				context.setGeneratorFactory(new EquivalenceGeneratorFactory());
				return true;
			} else if (annotation instanceof Coverage) {
				context.setGeneratorFactory(new CoverageGeneratorFactory());
				return true;
			} else if (annotation instanceof Stochastic) {
				context.setGeneratorFactory(new StochasticGeneratorFactory());
				return true;
			} else if (annotation instanceof Serial) {
				context.setGeneratorFactory(new SerialGeneratorFactory());
				return true;
			}
		}
		return configured;
	}

	protected void applyMethodDefaultsProvider(Method testMethod, BeneratorContext context) {
		// check if the method is annotated with an individual DefaultsProvider...
		boolean configured = applyDefaultsProvider(testMethod.getAnnotations(), context);
		// ... otherwise check for a class-wide DefaultsProvider annotation...
		if (!configured)
			applyDefaultsProvider(testMethod.getDeclaringClass().getAnnotations(), context);
		// ...otherwise the GeneratorFactory's DefaultProvider is used
	}

	private boolean applyDefaultsProvider(Annotation[] annotations, BeneratorContext context) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Gentle) {
				context.setDefaultsProvider(new GentleDefaultsProvider());
				return true;
			} else if (annotation instanceof Mean) {
				context.setDefaultsProvider(new MeanDefaultsProvider());
				return true;
			}
		}
		return false;
	}

	private Generator<Object[]> createMethodSourceGenerator(
			org.databene.benerator.anno.Source source, Method testMethod, BeneratorContext context) {
		String methodName = testMethod.getName();
		ArrayTypeDescriptor typeDescriptor = new ArrayTypeDescriptor(methodName);
		InstanceDescriptor descriptor = new InstanceDescriptor(methodName, typeDescriptor);
		mapAnnotation(source, descriptor);
		mapParamTypes(testMethod, typeDescriptor);
		Generator<Object[]> baseGenerator = ArrayGeneratorFactory.createArrayGenerator(
				testMethod.getName(), typeDescriptor, Uniqueness.NONE, context);
		return baseGenerator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Generator<?> createAndInitAttributeSourceGenerator(
			org.databene.benerator.anno.Source source, Field attribute, BeneratorContext context) {
		String attName = attribute.getName();
		TypeDescriptor typeDescriptor = createTypeDescriptor(attribute.getType());
		InstanceDescriptor descriptor = new InstanceDescriptor(attName, typeDescriptor);
		mapAnnotation(source, descriptor);
		Offset offset = attribute.getAnnotation(Offset.class);
		if (offset != null)
			mapAnnotation(offset, descriptor);
		Generator generator = InstanceGeneratorFactory.createSingleInstanceGenerator(
				descriptor, Uniqueness.NONE, context);
		generator = WrapperFactory.applyConverter(generator, new Entity2JavaConverter());
		generator.init(context);
		return generator;
	}

	private Generator<Object[]> createGeneratorGenerator(
			org.databene.benerator.anno.Generator annotation, Method testMethod, BeneratorContext context) {
		String methodName = testMethod.getName();
		ArrayTypeDescriptor typeDescriptor = new ArrayTypeDescriptor(methodName);
		InstanceDescriptor descriptor = new InstanceDescriptor(methodName, typeDescriptor);
		mapAnnotation(annotation, descriptor);
		mapParamTypes(testMethod, typeDescriptor);
		return ArrayGeneratorFactory.createArrayGenerator(
				testMethod.getName(), typeDescriptor, Uniqueness.NONE, context);
	}

	private void mapParamTypes(Method testMethod, ArrayTypeDescriptor typeDescriptor) {
		Class<?>[] paramTypes = testMethod.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			String elementType = BeanDescriptorProvider.defaultInstance().abstractType(paramTypes[i]);
			ArrayElementDescriptor elementDescriptor = new ArrayElementDescriptor(i, elementType);
			typeDescriptor.addElement(elementDescriptor);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Generator<Object[]> createDescriptorBasedGenerator(
			Descriptor descriptorAnno, Method testMethod)
			throws IOException {
		Generator<Object[]> generator;
		String filename;
		if (descriptorAnno.file().length() > 0)
			filename = descriptorAnno.file();
		else
			filename = testMethod.getDeclaringClass().getName().replace('.', File.separatorChar) + ".ben.xml";
		String testName;
		if (descriptorAnno.name().length() > 0)
			testName = descriptorAnno.name();
		else
			testName = testMethod.getName();
		BeneratorContext beneratorContext = new DefaultBeneratorContext();
		generator = (Generator) new DescriptorBasedGenerator(filename, testName, beneratorContext);
		return generator;
	}

    
    
	private static void parseDatabase(Database annotation, BeneratorContext context) {
		DBSystem db;
		if (!StringUtil.isEmpty(annotation.environment()))
			db = new DBSystem(annotation.id(), annotation.environment());
		else 
			db = new DBSystem(annotation.id(), annotation.url(), annotation.driver(), 
					annotation.user(), annotation.password());
		if (!StringUtil.isEmpty(annotation.catalog()))
			db.setCatalog(annotation.catalog());
		if (!StringUtil.isEmpty(annotation.schema()))
			db.setSchema(annotation.schema());
		db.setLazy(true);
		context.set(db.getId(), db);
	}
	
	private static void parseBean(Bean annotation, BeneratorContext context) {
        Object bean = instantiateBean(annotation, context);
        applyProperties(annotation.properties(), bean, context);
        context.set(annotation.id(), bean);
	}

	private static Object instantiateBean(Bean beanAnno, BeneratorContext context) {
		String beanSpec = beanAnno.spec();
		Class<?> beanClass = beanAnno.type();
		if (!StringUtil.isEmpty(beanSpec)) {
			try {
				if (beanClass != Object.class)
					throw new ConfigurationError("'type' and 'spec' exclude each other in a @Bean");
		        return BeneratorScriptParser.parseBeanSpec(beanSpec).evaluate(context);
			} catch (ParseException e) {
				throw new ConfigurationError("Error parsing bean spec: " + beanSpec, e);
			}
		} else if (beanClass != Object.class) {
		    return BeanUtil.newInstance(beanClass);
		} else
			throw new ConfigurationError("@Bean is missing 'type' or 'spec' attribute");
	}
	
	private static void applyProperties(Property[] properties, Object bean, BeneratorContext context) {
		for (Property property : properties) {
			Object value = resolveProperty(property, bean, context);
			BeanUtil.setPropertyValue(bean, property.name(), value, true, true);
		}
    }

    private static Object resolveProperty(Property property, Object bean, BeneratorContext context) {
		if (!StringUtil.isEmpty(property.value())) {
			if (!StringUtil.isEmpty(property.ref()))
				throw new ConfigurationError("'value' and 'ref' exclude each other in a @Property");
			Object value = ScriptUtil.evaluate(property.value(), context);
			if (value instanceof String)
				value = StringUtil.unescape((String) value);
			return value;
		} else if (!StringUtil.isEmpty(property.ref())) {
			return context.get(property.ref());
		} else
			throw new ConfigurationError("@Property is missing 'value' or 'ref' attribute");
	}

    private Generator<Object[]> createParamsGenerator(Method testMethod, BeneratorContext context) {
	    InstanceDescriptor array = mapMethodParamsAnnotations(testMethod);
        Uniqueness uniqueness = DescriptorUtil.getUniqueness(array, context);
        Generator<Object[]> generator = ArrayGeneratorFactory.createSimpleArrayGenerator(array.getName(),
				(ArrayTypeDescriptor) array.getTypeDescriptor(), uniqueness, context);
		return generator;
    }

	InstanceDescriptor mapMethodParamsAnnotations(Method testMethod) {
		dataModel.addDescriptorProvider(PrimitiveDescriptorProvider.INSTANCE);
		ArrayTypeDescriptor type = new ArrayTypeDescriptor(testMethod.getName());
		Class<?>[] parameterTypes = testMethod.getParameterTypes();
		Annotation[][] paramAnnos = testMethod.getParameterAnnotations();
		for (int i = 0; i < parameterTypes.length; i++)
			type.addElement(mapParameter(parameterTypes[i], paramAnnos[i], i));
	    InstanceDescriptor instance = new InstanceDescriptor(testMethod.getName(), type);
		if (testMethod.getAnnotation(Unique.class) != null)
			instance.setUnique(true);
		return instance;
    }

	private static <T> void mapAnnotation(Annotation annotation, InstanceDescriptor descriptor) {
	    Package annoPackage = annotation.annotationType().getPackage();
	    if (BENERATOR_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeneratorParamAnnotation(annotation, descriptor);
	    else if (BEANVAL_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeanValidationParameter(annotation, descriptor);
    }

	private static void mapBeneratorParamAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("mapDetails(" + annotation + ", " + instanceDescriptor + ")");
	    try {
			Class<?> annotationType = annotation.annotationType();
			if (annotationType == Unique.class)
				instanceDescriptor.setDetailValue("unique", true);
			else if (annotationType == Granularity.class)
				instanceDescriptor.getLocalType(false).setDetailValue("granularity", String.valueOf(DescriptorUtil.convertType(((Granularity) annotation).value(), (SimpleTypeDescriptor) instanceDescriptor.getLocalType(false))));
			else if (annotationType == DecimalGranularity.class)
				instanceDescriptor.getLocalType(false).setDetailValue("granularity", String.valueOf(DescriptorUtil.convertType(((DecimalGranularity) annotation).value(), (SimpleTypeDescriptor) instanceDescriptor.getLocalType(false))));
			else if (annotationType == SizeDistribution.class)
				instanceDescriptor.getLocalType(false).setDetailValue("lengthDistribution", ((SizeDistribution) annotation).value());
			else if (annotationType == Pattern.class)
				mapPatternAnnotation((Pattern) annotation, instanceDescriptor);
			else if (annotationType == Size.class)
				mapSizeAnnotation((Size) annotation, instanceDescriptor);
			else if (annotationType == Source.class)
				mapSourceAnnotation((Source) annotation, instanceDescriptor);
			else if (annotationType == Values.class)
				mapValuesAnnotation((Values) annotation, instanceDescriptor);
			else if (annotationType == Offset.class)
				mapOffsetAnnotation((Offset) annotation, instanceDescriptor);
			else if (annotationType == MinDate.class)
				mapMinDateAnnotation((MinDate) annotation, instanceDescriptor);
			else if (annotationType == MaxDate.class)
				mapMaxDateAnnotation((MaxDate) annotation, instanceDescriptor);
			else
				mapAnyValueTypeAnnotation(annotation, instanceDescriptor);
		} catch (Exception e) {
			throw new ConfigurationError("Error mapping annotation settings", e);
		}
    }

	private static void mapSizeAnnotation(Size size, InstanceDescriptor instanceDescriptor) {
    	setDetail("minLength", size.min(), instanceDescriptor);
    	setDetail("maxLength", size.max(), instanceDescriptor);
    }

	private static void mapPatternAnnotation(Pattern pattern, InstanceDescriptor instanceDescriptor) {
	    if (!StringUtil.isEmpty(pattern.regexp()))
	    	setDetail("pattern", pattern.regexp(), instanceDescriptor);
    }

	private static void mapSourceAnnotation(Source source, InstanceDescriptor instanceDescriptor) throws Exception {
		mapSourceSetting(source.value(),     "source",    instanceDescriptor);
		mapSourceSetting(source.id(),        "source",    instanceDescriptor);
		mapSourceSetting(source.uri(),       "source",    instanceDescriptor);
		mapSourceSetting(source.dataset(),   "dataset",   instanceDescriptor);
		mapSourceSetting(source.nesting(),   "nesting",   instanceDescriptor);
		mapSourceSetting(source.encoding(),  "encoding",  instanceDescriptor);
		mapSourceSetting(source.filter(),    "filter",    instanceDescriptor);
		mapSourceSetting(source.selector(),  "selector",  instanceDescriptor);
		mapSourceSetting(source.separator(), "separator", instanceDescriptor);
    }

	private static void mapSourceSetting(String value, String detailName, InstanceDescriptor instanceDescriptor) {
	    if (!StringUtil.isEmpty(value))
	    	setDetail(detailName, value, instanceDescriptor);
    }

	private static void mapValuesAnnotation(Values annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		Method method = annotation.annotationType().getMethod("value");
		String[] values = (String[]) method.invoke(annotation);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i > 0)
				builder.append(',');
			builder.append("'").append(values[i].replace("'", "\\'")).append("'");
		}
		((SimpleTypeDescriptor) instanceDescriptor.getLocalType(false)).setValues(builder.toString());
    }

	private static void mapOffsetAnnotation(Offset annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		if (annotation.value() != 0)
			instanceDescriptor.getLocalType().setOffset(annotation.value());
    }

	private static void mapMinDateAnnotation(MinDate annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		TypeDescriptor localType = instanceDescriptor.getLocalType();
		if (!(localType instanceof SimpleTypeDescriptor))
			throw new ConfigurationError("@MinDate can only be applied to Date types");
		((SimpleTypeDescriptor) localType).setMin(annotation.value());
    }

	private static void mapMaxDateAnnotation(MaxDate annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		TypeDescriptor localType = instanceDescriptor.getLocalType();
		if (!(localType instanceof SimpleTypeDescriptor))
			throw new ConfigurationError("@MaxDate can only be applied to Date types");
		((SimpleTypeDescriptor) localType).setMax(annotation.value());
    }

	private static void mapAnyValueTypeAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		Method method = annotation.annotationType().getMethod("value");
		Object value = normalize(method.invoke(annotation));
		String detailName = StringUtil.uncapitalize(annotation.annotationType().getSimpleName());
		setDetail(detailName, value, instanceDescriptor);
    }

	private ArrayElementDescriptor mapParameter(Class<?> type, Annotation[] annos, int index) {
		TypeDescriptor typeDescriptor = createTypeDescriptor(type);
		ArrayElementDescriptor descriptor = new ArrayElementDescriptor(index, typeDescriptor);
	    for (Annotation annotation : annos)
            mapAnnotation(annotation, descriptor);
	    if (descriptor.getDeclaredDetailValue("nullable") == null) { // assure an explicit setting for nullability
	    	if (BeanUtil.isPrimitiveType(type.getName()))
	    		descriptor.setNullable(false); // primitives can never be null
	    	else if (descriptor.getDeclaredDetailValue("nullQuota") != null && ((Double) descriptor.getDeclaredDetailValue("nullQuota")) == 0.)
	    		descriptor.setNullable(false); // if nullQuota == 1, then set nullable to false
	    	else
	    		descriptor.setNullable(null); // leave the decision to the generator factories
	    }
        return descriptor;
    }

	private static void mapBeanValidationParameter(Annotation annotation, InstanceDescriptor element) {
    	SimpleTypeDescriptor typeDescriptor = (SimpleTypeDescriptor) element.getLocalType(false);
		if (annotation instanceof AssertFalse)
    		typeDescriptor.setTrueQuota(0.);
    	else if (annotation instanceof AssertTrue)
    		typeDescriptor.setTrueQuota(1.);
    	else if (annotation instanceof DecimalMax)
    		typeDescriptor.setMax(String.valueOf(DescriptorUtil.convertType(((DecimalMax) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof DecimalMin)
    		typeDescriptor.setMin(String.valueOf(DescriptorUtil.convertType(((DecimalMin) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof Digits) {
    		Digits digits = (Digits) annotation;
			typeDescriptor.setGranularity(String.valueOf(Math.pow(10, - digits.fraction())));
    	} else if (annotation instanceof Future)
	        typeDescriptor.setMin(new SimpleDateFormat("yyyy-MM-dd").format(TimeUtil.tomorrow()));
        else if (annotation instanceof Max)
			typeDescriptor.setMax(String.valueOf(((Max) annotation).value()));
        else if (annotation instanceof Min)
    		typeDescriptor.setMin(String.valueOf(((Min) annotation).value()));
    	else if (annotation instanceof NotNull) {
    		element.setNullable(false);
    		element.setNullQuota(0.);
    	} else if (annotation instanceof Null) {
    		element.setNullable(true);
    		element.setNullQuota(1.);
    	} else if (annotation instanceof Past)
	        typeDescriptor.setMax(new SimpleDateFormat("yyyy-MM-dd").format(TimeUtil.yesterday()));
        else if (annotation instanceof Pattern)
    		typeDescriptor.setPattern(String.valueOf(((Pattern) annotation).regexp()));
    	else if (annotation instanceof Size) {
    		Size size = (Size) annotation;
    		typeDescriptor.setMinLength(size.min());
    		typeDescriptor.setMaxLength(size.max());
    	}
    }

	private static void setDetail(String detailName, Object detailValue, InstanceDescriptor instanceDescriptor) {
		if (instanceDescriptor.supportsDetail(detailName))
			instanceDescriptor.setDetailValue(detailName, detailValue);
		else
			instanceDescriptor.getLocalType().setDetailValue(detailName, detailValue);
    }

	private static Object normalize(Object value) {
		if (value == null)
			return null;
		if (value instanceof String && ((String) value).length() == 0)
			return null;
		if (value.getClass().isArray() && Array.getLength(value) == 0)
			return null;
		return value;
	}

	protected TypeDescriptor createTypeDescriptor(Class<?> type) {
		String abstractType = BeanDescriptorProvider.defaultInstance().abstractType(type);
		TypeDescriptor baseTypeDescriptor = dataModel.getTypeDescriptor(abstractType);
		TypeDescriptor typeDescriptor;
		if (baseTypeDescriptor instanceof SimpleTypeDescriptor) {
			typeDescriptor = new SimpleTypeDescriptor(type.getName(), (SimpleTypeDescriptor) baseTypeDescriptor);
		} else if (baseTypeDescriptor instanceof ComplexTypeDescriptor) {
			typeDescriptor = new ComplexTypeDescriptor(type.getName(), (ComplexTypeDescriptor) baseTypeDescriptor);
		} else
			throw new ConfigurationError("Cannot handle descriptor: " + baseTypeDescriptor);
		return typeDescriptor;
	}
	
}
