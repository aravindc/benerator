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

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.databene.model.data.ArrayElementDescriptor;
import org.databene.model.data.ArrayTypeDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.junit.Test;

/**
 * Tests the {@link AnnotationMapper}.<br/><br/>
 * Created: 30.04.2010 13:57:59
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class AnnotationMapperTest {

	@Test
	public void test() throws Exception {
		Method stringMethod = getClass().getDeclaredMethod("stringMethod", new Class[] { String.class });
		ArrayTypeDescriptor descriptor = AnnotationMapper.mapMethodParams(stringMethod);
		assertEquals(1, descriptor.getElements().size());
		ArrayElementDescriptor param1 = descriptor.getElement(0);
		assertEquals("string", ((SimpleTypeDescriptor) param1.getTypeDescriptor()).getPrimitiveType().getName());
		assertEquals("ABC", param1.getTypeDescriptor().getPattern());
	}
	
	public void stringMethod(@GeneratedString(pattern="ABC") String name) { }
	
}
