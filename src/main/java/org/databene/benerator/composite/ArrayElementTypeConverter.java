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

package org.databene.benerator.composite;

import org.databene.commons.ConversionException;
import org.databene.commons.converter.AbstractConverter;
import org.databene.commons.converter.AnyConverter;
import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;
import org.databene.script.PrimitiveType;

/**
 * Converts an array's elements to the types defined in a related {@link ArrayTypeDescriptor}.<br/><br/>
 * Created: 05.05.2010 15:36:41
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class ArrayElementTypeConverter extends AbstractConverter<Object[], Object[]>{

	private ArrayTypeDescriptor type;

	public ArrayElementTypeConverter(ArrayTypeDescriptor type) {
		super(Object[].class, Object[].class);
		this.type = type;
	}

	public Object[] convert(Object[] array) throws ConversionException {
		if (array == null)
			return null;
		for (int i = 0; i < array.length; i++) {
			ArrayElementDescriptor elementDescriptor = type.getElement(i, true);
			if (elementDescriptor != null) {
				TypeDescriptor elementType = elementDescriptor.getTypeDescriptor();
				Object elementValue = array[i];
				if (elementType instanceof SimpleTypeDescriptor) {
					PrimitiveType primitive = ((SimpleTypeDescriptor) elementType).getPrimitiveType();
					if (primitive == null)
						primitive = PrimitiveType.STRING;
			        Class<?> javaType = primitive.getJavaType();
			        Object javaValue = AnyConverter.convert(elementValue, javaType);
			        array[i] = javaValue;
				} else {
					array[i] = elementValue;
				}
			}
		}
		return array;
	}

	public boolean isParallelizable() {
	    return false;
    }

	public boolean isThreadSafe() {
	    return false;
    }

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type + "]";
	}
	
}
