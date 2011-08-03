/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import org.databene.benerator.test.GeneratorClassTest;
import org.databene.benerator.util.GeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link SampleGenerator}.<br/>
 * Created: 07.06.2006 21:59:02
 * @author Volker Bergmann
 */
public class SampleGeneratorTest extends GeneratorClassTest {

    private static Logger logger = LoggerFactory.getLogger(SampleGeneratorTest.class);

    public SampleGeneratorTest() {
        super(SampleGenerator.class);
    }

    @Test
    public void testDistribution() throws Exception {
        Integer[] samples = new Integer[] { 0, 1, 2 };
        SampleGenerator<Integer> generator = new SampleGenerator<Integer>(Integer.class);
        generator.setValues(samples);
        generator.init(context);
        int n = 10000;
        int[] sampleCount = new int[3];
        for (int i = 0; i < n; i++) {
            sampleCount[GeneratorUtil.generateNonNull(generator)] ++;
        }
        for (int i = 0; i < sampleCount.length; i++) {
            int count = sampleCount[i];
            double measuredProbability = (float)count / n;
            double expectedProbability = 1. / samples.length;
            double ratio = measuredProbability / expectedProbability;
            logger.debug(i + " " + count + " " + ratio);
            assertTrue(ratio > 0.9 && ratio < 1.1);
        }
    }

    @Test
    public void testBigSet() {
    	// init generator
        SampleGenerator<Integer> generator = new SampleGenerator<Integer>(Integer.class);
        for (int i = 0; i < 200000; i++)
        	generator.addValue(i % 100);
        generator.init(context);
        // test
        for (int i = 0; i < 100; i++) {
            int product = GeneratorUtil.generateNonNull(generator);
            assertTrue("generated value not in expected value range: " + product, 0 <= product && product <= 99);
        }
    }
    
}
