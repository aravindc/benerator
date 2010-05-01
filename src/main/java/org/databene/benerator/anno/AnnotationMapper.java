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

import org.databene.commons.ArrayUtil;
import org.databene.commons.ConfigurationError;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.DataModel;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.PrimitiveDescriptorProvider;
import org.databene.model.data.TypeDescriptor;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationMapper.class);
	
	private static final Set<String> STANDARD_METHODS;
	
	static {
		STANDARD_METHODS = new HashSet<String>();
		for (Method method : Annotation.class.getMethods())
			STANDARD_METHODS.add(method.getName());
		for (Method method : Object.class.getMethods())
			STANDARD_METHODS.add(method.getName());
	}

	private static BeanDescriptorProvider bdp;

	static {
		DataModel dataModel = DataModel.getDefaultInstance();
		dataModel.addDescriptorProvider(PrimitiveDescriptorProvider.INSTANCE);
		bdp = new BeanDescriptorProvider();
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

	public static void mapDetails(Object annotation, InstanceDescriptor instanceDescriptor) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("mapDetails(" + annotation + ", " + instanceDescriptor + ")");
		try {
			TypeDescriptor typeDescriptor = instanceDescriptor.getLocalType(false);
			Method[] methods = annotation.getClass().getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (ArrayUtil.isEmpty(method.getParameterTypes()) && !STANDARD_METHODS.contains(methodName)) {
					Object value = method.invoke(annotation);
					value = normalize(value);
					if (instanceDescriptor.supportsDetail(methodName))
						instanceDescriptor.setDetailValue(methodName, value);
					else
						typeDescriptor.setDetailValue(methodName, value);
				}
			}
		} catch (Exception e) {
			throw new ConfigurationError("Error mapping annotation settings", e);
		}
    }

	

	// helpers ---------------------------------------------------------------------------------------------------------
	
	private static ArrayElementDescriptor mapArrayElement(Class<?> type, Annotation[] annos, int index) {
		String abstractType = bdp.abstractType(type);
	    return map(type, annos, new ArrayElementDescriptor(index, abstractType));
    }
	
	private static <T extends InstanceDescriptor> T map(Class<?> type, Annotation[] annos, T descriptor) {
		for (Annotation annotation : annos)
			if (annotation instanceof GeneratedNumber || annotation instanceof GeneratedString 
					|| annotation instanceof GeneratedObject)
				mapDetails(annotation, descriptor);
		return descriptor;
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
