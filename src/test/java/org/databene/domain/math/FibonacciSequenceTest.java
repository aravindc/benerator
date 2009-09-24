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

package org.databene.domain.math;

import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.domain.math.FibonacciSequence;

/**
 * Tests the {@link FibonacciSequence}.<br/>
 * <br/>
 * Created at 03.07.2009 10:51:18
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FibonacciSequenceTest extends GeneratorTest {

	public void testLong() {
		Generator<Integer> intGenerator = new FibonacciSequence().createGenerator(Integer.class, 0, 10, 1);
		expectGeneratedSequence(intGenerator, 0, 1, 1, 2, 3, 5, 8).withCeasedAvailability();
		intGenerator = new FibonacciSequence().createGenerator(Integer.class, 0, null, 1);
		expectGeneratedSequence(intGenerator, 0, 1, 1, 2, 3, 5, 8).withContinuedAvailability();
		intGenerator = new FibonacciSequence().createGenerator(Integer.class, 4, 10, 1);
		expectGeneratedSequence(intGenerator, 5, 8).withCeasedAvailability();
	}
	
}
