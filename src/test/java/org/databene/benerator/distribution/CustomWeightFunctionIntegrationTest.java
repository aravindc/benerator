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

package org.databene.benerator.distribution;

import static org.junit.Assert.*;

import java.util.List;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Tests the definition of custom weight functions.<br/><br/>
 * Created: 09.07.2010 07:23:54
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class CustomWeightFunctionIntegrationTest extends BeneratorIntegrationTest {

	String xml = 
		"<generate type='entity' count='1000' consumer='cons'>" +
		"	<attribute name='c' values=\"'a', 'b', 'c'\" " +
				"distribution='new " + StandardWeightingFunction.class.getName() + "(50,30,20)' />" +
		"</generate>";
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		ConsumerMock consumer = new ConsumerMock(true);
		context.set("cons", consumer);
		parseAndExecute(xml);
		List<Entity> products = (List<Entity>) consumer.getProducts();
		assertEquals(1000, products.size());
		int a = 0, b = 0, c = 0;
		for (Entity e : products) {
			String val = (String) e.get("c");
			switch (val.charAt(0)) {
				case 'a' : a++; break;
				case 'b' : b++; break;
				case 'c' : c++; break;
				default: fail("expected 'a', 'b' or 'c', found: " + val.charAt(0));
			}
		}
		assertTrue(a > b);
		assertTrue(b > c);
	}
	
}

