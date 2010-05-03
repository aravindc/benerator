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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.databene.commons.ConfigurationError;
import org.databene.commons.StringUtil;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PrimitiveDescriptorProvider;
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

	public static void mapAnnotation(Annotation annotation, InstanceDescriptor instanceDescriptor) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("mapDetails(" + annotation + ", " + instanceDescriptor + ")");
	    try {
			Class<?> annotationType = annotation.annotationType();
			if (Unique.class.equals(annotationType))
				instanceDescriptor.setDetailValue("unique", true);
			else if (Source.class.equals(annotationType))
				mapSourceAnnotation((Source) annotation, instanceDescriptor);
			else
				mapValueAnnotation(annotation, instanceDescriptor);
		} catch (Exception e) {
			throw new ConfigurationError("Error mapping annotation settings", e);
		}
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
	
	private static <T extends InstanceDescriptor> T map(Class<?> type, Annotation[] annos, T descriptor) {
		for (Annotation annotation : annos)
	        if (BENERATOR_ANNO_PACKAGE.equals(annotation.annotationType().getPackage()))
				mapAnnotation(annotation, descriptor);
		return descriptor;
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
