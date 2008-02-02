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

package org.databene.benerator.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the {@link SequencedSampleGenerator}.<br/>
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 21:59:02
 */
public class SequencedSampleGeneratorTest extends GeneratorClassTest {

    // TODO v0.5 test with large data amounts

    private static Log logger = LogFactory.getLog(SequencedSampleGeneratorTest.class);

    public SequencedSampleGeneratorTest() {
        super(SequencedSampleGenerator.class);
    }

    public void testInstantiation() throws Exception {
        new SequencedSampleGenerator<Integer>(Integer.class);
        new SequencedSampleGenerator<String>(String.class);
    }

    public void testDistribution() throws Exception {
        Integer[] samples = new Integer[] { 0, 1, 2 };
        SequencedSampleGenerator<Integer> g = new SequencedSampleGenerator<Integer>(Integer.class);
        g.setValues(samples);
        int n = 10000;
        int[] sampleCount = new int[3];
        for (int i = 0; i < n; i++) {
            sampleCount[g.generate()] ++;
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
}
