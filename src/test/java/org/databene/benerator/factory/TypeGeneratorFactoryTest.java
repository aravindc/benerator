/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

import org.databene.commons.Context;
import org.databene.commons.ConversionException;
import org.databene.commons.Converter;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.converter.AbstractConverter;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.TypeDescriptor;

import junit.framework.TestCase;

/**
 * Tests the {@link TypeGeneratorFactory}.<br/>
 * <br/>
 * Created at 25.08.2008 18:37:56
 * @since 0.5.5
 * @author Volker Bergmann
 */
public class TypeGeneratorFactoryTest extends TestCase {

	public void testGetConverter() {
		// test bean reference
		checkGetConverter("c", new TestConverter(1), "c", 1);
		// test class name specification
		checkGetConverter(null, null, TestConverter.class.getName(), -1);
		// test converter chaining
		checkGetConverter("c", new TestConverter(1), "c," + TestConverter.class.getName(), -1);
	}
	
	// private helpers ------------------------------------------------------------------------

	private void checkGetConverter(String contextKey, TestConverter contextValue, String converterSpec, int expectedValue) {
		Context context = new DefaultContext();
		if (contextKey != null)
			context.set(contextKey, contextValue);
		TypeDescriptor descriptor = new SimpleTypeDescriptor("x");
		descriptor.setConverter(converterSpec);
		Converter converter = TypeGeneratorFactory.getConverter(descriptor, context);
		assertNotNull(converter);
		assertTrue(converter instanceof Converter);
		assertEquals(expectedValue, converter.convert(null));
	}
	
	public static final class TestConverter extends AbstractConverter<Integer, Integer>{
		
		int value = 0;
		
		public TestConverter() {
			this(-1);
		}
		
		public TestConverter(int value) {
			super(Integer.class);
			this.value = value;
		}
		
		public Integer convert(Integer sourceValue) throws ConversionException {
			return value;
		}
	}
}
