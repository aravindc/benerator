/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link ConcatenatingGenerator}.<br/><br/>
 * Created: 14.10.2009 10:16:05
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ConcatenatingGeneratorTest extends GeneratorTest {

	Generator<String[]> source = new ConstantGenerator<String[]>( new String[] { "Expressis", "Verbis"});			

    @Test
	public void testDefault() {
		Generator<String> generator = new ConcatenatingGenerator(source);
		generator.init(context);
		expectGeneratedSequence(generator, "ExpressisVerbis", "ExpressisVerbis").withContinuedAvailability();
	}

    @Test
	public void testSeparator() {
		Generator<String> generator = new ConcatenatingGenerator(source, " ");
		generator.init(context);
		expectGeneratedSequence(generator, "Expressis Verbis", "Expressis Verbis").withContinuedAvailability();
	}

}
