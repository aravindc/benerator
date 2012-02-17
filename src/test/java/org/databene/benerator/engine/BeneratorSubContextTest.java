/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import org.databene.benerator.wrapper.ProductWrapper;
import org.junit.Test;

/**
 * Tests the {@link BeneratorSubContext}.<br/><br/>
 * Created: 15.02.2012 05:35:10
 * @since 0.8.0
 * @author Volker Bergmann
 */
public class BeneratorSubContextTest {
	
	@Test
	public void testGetParent() {
		DefaultBeneratorContext parent = new DefaultBeneratorContext();
		BeneratorSubContext child = (BeneratorSubContext) parent.createSubContext();
		assertTrue(parent == child.getParent());
	}
	
	@Test
	public void testGetAndSet() {
		DefaultBeneratorContext parent = new DefaultBeneratorContext();
		BeneratorSubContext child = (BeneratorSubContext) parent.createSubContext();
		// verify that child settings are not available in parent
		child.set("c", 2);
		assertEquals(null, parent.get("c"));
		assertEquals(2, child.get("c"));
		// verify that parent settings are available in child
		parent.set("x", 3);
		assertEquals(3, parent.get("x"));
		assertEquals(3, child.get("x"));
		// verify override of parent setting in child
		parent.set("x", 3);
		child.set("x", 4);
		assertEquals(3, parent.get("x"));
		assertEquals(4, child.get("x"));
	}
	
	@Test
	public void testCurrentProduct() {
		DefaultBeneratorContext parent = new DefaultBeneratorContext();
		BeneratorSubContext child = (BeneratorSubContext) parent.createSubContext();
		// verify access to parent's currentProduct
		ProductWrapper<Integer> pp = new ProductWrapper<Integer>(11);
		parent.setCurrentProduct(pp);
		assertEquals(pp, parent.getCurrentProduct());
		assertEquals(pp.unwrap(), parent.get("this"));
		assertEquals(pp.unwrap(), child.get("this"));
		// verify access to child's currentProduct
		ProductWrapper<Integer> cp = new ProductWrapper<Integer>(12);
		child.setCurrentProduct(cp);
		assertEquals(pp, parent.getCurrentProduct());
		assertEquals(pp.unwrap(), parent.get("this"));
		assertEquals(cp.unwrap(), child.get("this"));
	}
	
}
