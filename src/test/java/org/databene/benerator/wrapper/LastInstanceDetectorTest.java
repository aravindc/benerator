/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link LastProductDetector}.<br/><br/>
 * Created: 12.09.2011 12:46:53
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class LastInstanceDetectorTest extends GeneratorTest {

	@Test
	public void testLifeCycle() {
		Generator<Integer> source = new SequenceTestGenerator<Integer>(1, 2);
		Generator<Integer> generator = initialize(new LastProductDetector<Integer>(source));
		ProductWrapper<Integer> wrapper = new ProductWrapper<Integer>();
		checkSequence(generator, wrapper);
		generator.reset();
		checkSequence(generator, wrapper);
	}

	protected void checkSequence(Generator<Integer> generator,
			ProductWrapper<Integer> wrapper) {
		// generating 1
		wrapper = generator.generate(wrapper);
		assertNotNull(wrapper);
		assertNull(wrapper.getTag("last"));
		// generating 2
		wrapper = generator.generate(wrapper);
		assertNotNull(wrapper);
		assertEquals("true", wrapper.getTag("last"));
		// unavailable
		assertNull(generator.generate(wrapper));
	}
	
}
