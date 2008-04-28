/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.model.function;

import org.databene.model.function.Sequence;
import org.databene.model.function.String2DistributionConverter;

import junit.framework.TestCase;

/**
 * Tests the String2DistributionConverter.<br/><br/>
 * Created at 27.04.2008 17:55:56
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class String2DistributionConverterTest extends TestCase {

	String2DistributionConverter converter = new String2DistributionConverter();

	public void testSequence() {
		Distribution distribution = converter.convert(MySequence.class.getName());
		assertTrue(distribution instanceof Sequence);
	}
	
	public void testFunction() {
		Distribution distribution = converter.convert(MyFunction.class.getName());
		assertTrue(distribution instanceof WeightFunction);
	}
	
	public void testWeight() {
		Distribution distribution = converter.convert("weighted[age]");
		assertTrue(distribution instanceof FeatureWeight);
		assertEquals("age", ((FeatureWeight) distribution).getWeightFeature());
	}
	
	public static class MySequence extends Sequence {
		public MySequence() {
			super("mysec");
		}
	}
	
	public static class MyFunction implements WeightFunction {
		public double value(double param) {
			return param;
		}
	}
}
