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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link HeadSequence}.<br/><br/>
 * Created: 25.07.2010 11:19:57
 * @since 0.6.3
 * @author Volker Bergmann
 */
public class HeadSequenceTest extends GeneratorTest {

	@Test
    public void testLongGenerator() throws Exception {
        expectGeneratedSequence(longGenerator(1),  0L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(2),  0L, 1L).withCeasedAvailability();
    }

	@Test
    public void testDoubleGenerator() throws Exception {
        expectGeneratedSequence(doubleGenerator(1),  0.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(2),  0., 1.).withCeasedAvailability();
    }

	@Test
    public void testApply() throws Exception {
        expectGeneratedSequence(charGenerator(1),  'A').withCeasedAvailability();
        expectGeneratedSequence(charGenerator(2),  'A', 'B').withCeasedAvailability();
    }
	
	// test helpers ----------------------------------------------------------------------------------------------------

    private Generator<Long> longGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
        return initialize(sequence.createNumberGenerator(Long.class, 0L, 1000L, 1L, false));
    }

    private Generator<Double> doubleGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
        return initialize(sequence.createNumberGenerator(Double.class, 0., 1000., 1., false));
    }

    private Generator<Character> charGenerator(long n) {
		Sequence sequence = new HeadSequence(n);
		Generator<Character> source = new SequenceTestGenerator<Character>('A', 'B', 'C', 'D');
        return initialize(sequence.applyTo(source, false));
    }

}
