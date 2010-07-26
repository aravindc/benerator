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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link NShotGeneratorProxy}.<br/><br/>
 * Created: 25.07.2010 11:38:23
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class NShotGeneratorProxyTest extends GeneratorTest {

	@Test
	public void testN1() {
		Generator<Character> generator = createAndInitGenerator(1);
		expectGeneratedSequence(generator, 'A').withCeasedAvailability();
	}

	@Test
	public void testN2() {
		Generator<Character> generator = createAndInitGenerator(2);
		expectGeneratedSequence(generator, 'A', 'B').withCeasedAvailability();
	}

	private Generator<Character> createAndInitGenerator(int n) {
	    Generator<Character> source = new SequenceTestGenerator<Character>('A', 'B', 'C', 'D');
		Generator<Character> generator = new NShotGeneratorProxy<Character>(source, n);
		generator.init(context);
	    return generator;
    }
	
}
