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

package org.databene.benerator.sample;

import org.databene.benerator.distribution.IndividualWeight;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the {@link IndividualWeightSampleGenerator}.<br/><br/>
 * Created: 29.08.2011 15:52:51
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class IndividualWeightSampleGeneratorTest extends GeneratorTest {

    @Test
    public void testDistribution() throws Exception {
    	// given an IndividualWeight which gives 'Alice' weight 2, all others the weight 1
    	IndividualWeight<String> individualWeight = new IndividualWeight<String>() {
			@Override
			public double weight(String name) {
				return ("Alice".equals(name) ? 2 : 1);
			}
		};
    	// when using a IndividualWeightSampleGenerator with values 'Alice' and 'Bob'
		IndividualWeightSampleGenerator<String> generator = new IndividualWeightSampleGenerator<String>(
				String.class, individualWeight, "Alice", "Bob");
		generator.init(context);
        // then the outcome should be 66% 'Alice' andf 33% 'Bob'
        expectRelativeWeights(generator, 1000, "Alice", 2, "Bob", 1);
    }
    
}
