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

import static junit.framework.Assert.assertTrue;

import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the {@link MappedWeightSampleGenerator}.<br/><br/>
 * Created: 29.08.2011 03:44:39
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class MappedWeightSampleGeneratorTest extends GeneratorTest {

    private static Logger logger = LoggerFactory.getLogger(MappedWeightSampleGeneratorTest.class);

    @Test
    public void testInstantiation() throws Exception {
        new MappedWeightSampleGenerator<Integer>(Integer.class);
        new MappedWeightSampleGenerator<String>(String.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDistribution() throws Exception {
    	// prepare
        WeightedSample<Integer>[] samples = new WeightedSample[] {
            new WeightedSample<Integer>(0, 1),
            new WeightedSample<Integer>(1, 3),
            new WeightedSample<Integer>(2, 6)
        };
        MappedWeightSampleGenerator<Integer> g = new MappedWeightSampleGenerator<Integer>(Integer.class);
        for (WeightedSample<Integer> sample : samples)
        	g.addSample(sample.getValue(), sample.getWeight());
        g.init(context);
        // execute
        int n = 10000;
        int[] sampleCount = new int[3];
        for (int i = 0; i < n; i++) {
            sampleCount[GeneratorUtil.generateNonNull(g)] ++;
        }
        for (int i = 0; i < sampleCount.length; i++) {
            int count = sampleCount[i];
            double measuredProbability = (float)count / n;
            double expectedProbability = g.weights.get(samples[i].getValue()).value / 10;
            double ratio = measuredProbability / expectedProbability;
            logger.debug(i + " " + count + " " + ratio);
            assertTrue("Ratio is expected to be between 0.9 and 1.1 but was " + ratio, ratio > 0.9 && ratio < 1.1);
        }
    }
    
}
