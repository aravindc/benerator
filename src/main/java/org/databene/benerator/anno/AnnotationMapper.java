/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.engine.DescriptorBasedGenerator;
import org.databene.benerator.factory.ArrayGeneratorFactory;
import org.databene.benerator.factory.DescriptorUtil;
import org.databene.benerator.wrapper.NShotGeneratorProxy;
import org.databene.commons.ConfigurationError;
import org.databene.commons.StringUtil;
import org.databene.commons.TimeUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PrimitiveDescriptorProvider;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.platform.java.BeanDescriptorProvider;
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

	static {
		DataModel dataModel = DataModel.getDefaultInstance();
		dataModel.addDescriptorProvider(PrimitiveDescriptorProvider.INSTANCE);
	}
	
	private AnnotationMapper() {}
	
	// utility methods -------------------------------------------------------------------------------------------------
	
	public static ArrayTypeDescriptor mapMethodParams(Method testMethod) {
	    DataModel dataModel = DataModel.getDefaultInstance();
		dataModel.addDescriptorProvider(PrimitiveDescriptorProvider.INSTANCE);
		ArrayTypeDescriptor arrayType = new ArrayTypeDescriptor(testMethod.getName());
		Class<?>[] parameterTypes = testMethod.getParameterTypes();
		Annotation[][] paramAnnos = testMethod.getParameterAnnotations();
		for (int i = 0; i < parameterTypes.length; i++)
			arrayType.addElement(mapArrayElement(parameterTypes[i], paramAnnos[i], i));
	    return arrayType;
    }

	public static <T> void mapAnnotation(Annotation annotation, InstanceDescriptor descriptor) {
	    Package annoPackage = annotation.annotationType().getPackage();
	    if (BENERATOR_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeneratorAnnotation(annotation, descriptor);
	    else if (BEANVAL_ANNO_PACKAGE.equals(annoPackage))
	    	mapBeanValidationParameter(annotation, descriptor);
    }

	public static void mapBeneratorAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("mapDetails(" + annotation + ", " + instanceDescriptor + ")");
	    try {
			Class<?> annotationType = annotation.annotationType();
			if (Unique.class.equals(annotationType))
				instanceDescriptor.setDetailValue("unique", true);
			else if (Granularity.class.equals(annotationType))
				instanceDescriptor.getLocalType(false).setDetailValue("precision", String.valueOf(DescriptorUtil.map(((Granularity) annotation).value(), (SimpleTypeDescriptor) instanceDescriptor.getLocalType(false))));
			else if (SizeDistribution.class.equals(annotationType))
				instanceDescriptor.getLocalType(false).setDetailValue("lengthDistribution", ((SizeDistribution) annotation).value());
			else if (Pattern.class.equals(annotationType))
				mapPatternAnnotation((Pattern) annotation, instanceDescriptor);
			else if (Size.class.equals(annotationType))
				mapSizeAnnotation((Size) annotation, instanceDescriptor);
			else if (Source.class.equals(annotationType))
				mapSourceAnnotation((Source) annotation, instanceDescriptor);
			else
				mapValueAnnotation(annotation, instanceDescriptor);
		} catch (Exception e) {
			throw new ConfigurationError("Error mapping annotation settings", e);
		}
    }

	@SuppressWarnings("unchecked")
    public static Generator<Object[]> createMethodParamGenerator(Method testMethod) { // TODO v0.6.2 wrap functionality with a class MethodArgsGenerator and support/test it in Descriptor files (combined with Invoker)
		try {
			Generator<Object[]> generator = null;
			BeneratorContext context = new BeneratorContext();
	
			// Evaluate @Generator and @Source annotations
			org.databene.benerator.anno.Generator generatorAnno = testMethod.getAnnotation(org.databene.benerator.anno.Generator.class);
			Source sourceAnno = testMethod.getAnnotation(Source.class);
			if (generatorAnno != null || sourceAnno != null) {
				String methodName = testMethod.getName();
				ArrayTypeDescriptor typeDescriptor = new ArrayTypeDescriptor(methodName);
				InstanceDescriptor descriptor = new InstanceDescriptor(methodName, typeDescriptor);
				if (sourceAnno != null)
					AnnotationMapper.mapAnnotation(sourceAnno, descriptor);
				else
					AnnotationMapper.mapAnnotation(generatorAnno, descriptor);
				Class<?>[] paramTypes = testMethod.getParameterTypes();
				for (int i = 0; i < paramTypes.length; i++) {
					String elementType = BeanDescriptorProvider.defaultInstance().abstractType(paramTypes[i]);
					ArrayElementDescriptor elementDescriptor = new ArrayElementDescriptor(i, elementType);
					typeDescriptor.addElement(elementDescriptor);
				}
				generator = ArrayGeneratorFactory.createArrayGenerator(
						testMethod.getName(), typeDescriptor, Uniqueness.NONE, context);
			} else if (testMethod.getAnnotation(DescriptorBased.class) != null) {
				String filename = testMethod.getDeclaringClass().getName().replace('.', File.separatorChar) + ".ben.xml";
				BeneratorContext beneratorContext = new BeneratorContext();
				generator = (Generator) new DescriptorBasedGenerator(filename, testMethod.getName(), beneratorContext);
			}
			
			// evaluate parameter generators if necessary
			if (generator == null)
				generator = createParamGenerator(testMethod, context);
			
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
	
	static Generator<Object[]> createParamGenerator(Method testMethod, BeneratorContext context) {
	    ArrayTypeDescriptor arrayType = AnnotationMapper.mapMethodParams(testMethod);
		return ArrayGeneratorFactory.createArrayGenerator(
				testMethod.getName(), arrayType, Uniqueness.NONE, context);
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

	private static void mapValueAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) throws Exception {
		Method method = annotation.annotationType().getMethod("value");
		Object value = normalize(method.invoke(annotation));
		String detailName = StringUtil.uncapitalize(annotation.annotationType().getSimpleName());
		setDetail(detailName, value, instanceDescriptor);
    }

	// helpers ---------------------------------------------------------------------------------------------------------
	
	private static ArrayElementDescriptor mapArrayElement(Class<?> type, Annotation[] annos, int index) {
		String abstractType = BeanDescriptorProvider.defaultInstance().abstractType(type);
	    return map(type, annos, new ArrayElementDescriptor(index, abstractType));
    }
	
	public static <T extends InstanceDescriptor> T map(Class<?> type, Annotation[] annos, T descriptor) {
		for (Annotation annotation : annos) {
	        mapAnnotation(annotation, descriptor);
	        
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
    		typeDescriptor.setMax(String.valueOf(DescriptorUtil.map(((DecimalMax) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof DecimalMin)
    		typeDescriptor.setMax(String.valueOf(DescriptorUtil.map(((DecimalMin) annotation).value(), typeDescriptor)));
    	else if (annotation instanceof Digits) {
    		Digits digits = (Digits) annotation;
			typeDescriptor.setPrecision(String.valueOf(Math.pow(10, - digits.fraction())));
			// TODO integer() part?
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
			instanceDescriptor.getLocalType(false).setDetailValue(detailName, detailValue);
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

}
