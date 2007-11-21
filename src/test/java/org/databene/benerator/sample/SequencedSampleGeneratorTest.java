package org.databene.benerator.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 21:59:02
 */
public class SequencedSampleGeneratorTest extends GeneratorClassTest {

    // TODO v0.4 test with large data amounts

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
