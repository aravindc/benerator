/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.cumulative;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link ExponentialDensityIntegral}.<br/><br/>
 * Created: 12.03.2010 15:50:44
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ExponentialDensityIntegralTest extends GeneratorTest {

	private ExponentialDensityIntegral fcn = new ExponentialDensityIntegral(0.5);
	private BeneratorContext context = new DefaultBeneratorContext();

	@Test(expected = IllegalArgumentException.class)
	public void testCreateDoubleGenerator_unique() {
		fcn.createNumberGenerator(Double.class, 1., 4., 0.5, true);
	}
	
	@Test
	public void testCreateDoubleGenerator_notUnique() {
		Generator<Double> generator = fcn.createNumberGenerator(Double.class, 1., 2., 0.5, false);
		generator.init(context);
		int n = 2000;
		Map<Double, AtomicInteger> counts = super.countProducts(generator, n);
		assertEquals(3, counts.size());
		int lastCount = n + 1;
		for (double d = 1; d <= 2; d += 0.5) {
			int count = counts.get(d).intValue();
			assertTrue(count < lastCount);
			lastCount = count;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testApply_unique() {
		Generator<String> source = new SequenceTestGenerator<String>("A", "B");
		source.init(new DefaultBeneratorContext());
		fcn.applyTo(source, true);
	}
	
	@Test
	public void testApply_notUnique() {
		Generator<String> source = new SequenceTestGenerator<String>("A", "B");
		source.init(new DefaultBeneratorContext());
		Generator<String> generator = fcn.applyTo(source, false);
		generator.init(context);
		int n = 1000;
		Map<String, AtomicInteger> counts = super.countProducts(generator, n);
		assertEquals(2, counts.size());
		assertTrue(counts.get("A").doubleValue() > counts.get("B").doubleValue());
	}
	
}
