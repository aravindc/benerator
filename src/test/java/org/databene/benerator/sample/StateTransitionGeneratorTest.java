/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import java.util.List;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link StateTransitionGenerator}.<br/>
 * <br/>
 * Created at 17.07.2009 08:15:03
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class StateTransitionGeneratorTest extends GeneratorTest {

	@Test
	@SuppressWarnings("unchecked")
    public void testDeterministicSequence() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<Integer>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, null, 1.);
		expectGeneratedSequence(generator, 
				new StateTransition(null,    1), 
				new StateTransition(   1,    2)
			).withCeasedAvailability();
	}
	
	/** Tests a setup that generates Sequences null->1, (1->2, 2->1)* */
	@Test
	@SuppressWarnings("unchecked")
    public void testRandomSequence() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<Integer>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 2, 1.);
		generator.addTransition(2, 1, 0.5);
		generator.addTransition(2, null, 0.5);
		for (int n = 0; n < 10; n++) {
			List<StateTransition> products = GeneratorUtil.allProducts(generator);
			assertTrue(products.size() % 2 == 0);
			assertEquals(new StateTransition(null, 1), products.get(0));
			for (int i = 1; i < products.size(); i++) {
				int oldState = 1 + ((i - 1) % 2);
				int newState = 1 + (i % 2);
				assertEquals(new StateTransition(oldState, newState), products.get(i));
			}
			generator.reset();
		}
	}
	
	/** Tests a setup that generates Sequences 1*, e.g. (1), (1, 1), (1, 1, 1), ... */
	@Test
	@SuppressWarnings("unchecked")
    public void testRecursion() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<Integer>(Integer.class);
		generator.addTransition(null, 1, 1.);
		generator.addTransition(1, 1, 0.5);
		generator.addTransition(1, null, 0.5);
		for (int n = 0; n < 10; n++) {
			List<StateTransition> products = GeneratorUtil.allProducts(generator);
			assertEquals(new StateTransition(null, 1), products.get(0));
			for (int i = 1; i < products.size(); i++)
				assertEquals(new StateTransition(1, 1), products.get(i));
			generator.reset();
		}
	}
	
	@Test
	public void testNoInitialState() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<Integer>(Integer.class);
		generator.addTransition(1, 2, 0.6);
		try {
			generator.validate();
			fail(InvalidGeneratorSetupException.class.getSimpleName() + " expected");
		} catch (InvalidGeneratorSetupException e) {
			// we expect that
		}
	}
	
	@Test
	public void testNoFinalState() {
		StateTransitionGenerator<Integer> generator = new StateTransitionGenerator<Integer>(Integer.class);
		generator.addTransition(null, 1, 0.6);
		try {
			generator.validate();
			fail(InvalidGeneratorSetupException.class.getSimpleName() + " expected");
		} catch (InvalidGeneratorSetupException e) {
			// we expect that
		}
	}
	
}
