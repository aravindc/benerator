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

package org.databene.benerator.engine;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.primitive.IncrementGenerator;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.expression.ConstantExpression;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link DynamicInstanceSequenceGenerator}.<br/>
 * <br/>
 * Created at 26.07.2009 06:17:13
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class DynamicInstanceSequenceGeneratorTest {

	@Test
	public void test() {
		Generator<Long> source = new IncrementGenerator(0); // generates 0, 1, 2, 3, ...
		Context context = new DefaultContext();
		Expression<? extends Number> minLengthExpr = new ConstantExpression<Integer>(0);
		Expression<? extends Number> maxLengthEpr = new ConstantExpression<Integer>(100);
		Expression<? extends Number> lengthPrecisionExpr = new ConstantExpression<Integer>(1);
		Expression<? extends Distribution> lengthDistributionExpr 
			= new ConstantExpression<Sequence>(Sequence.STEP); // generates 1, 2, 3, ...
		DynamicInstanceSequenceGenerator<Long> generator = new DynamicInstanceSequenceGenerator<Long>(
				source, context, minLengthExpr, maxLengthEpr, lengthPrecisionExpr, lengthDistributionExpr);
		// Now we are supposed to get the sequences (0), (0,1), (0, 1, 2), ... 
		for (int i = 0; i < 100; i++) {
			// check length
			assertEquals((long) i, generator.getSequenceLength());
			// check the whole expected sequence
			for (long j = 0; j < i; j++) {
				assertTrue("generator is expected to be available for generation #" + j + " of " + i, generator.available());
				assertEquals(j, generator.generate().longValue());
			}
			// after generating the sequence, the generator must be unavailable
			assertFalse(generator.available());
			generator.reset();
		}
	}
	
}
