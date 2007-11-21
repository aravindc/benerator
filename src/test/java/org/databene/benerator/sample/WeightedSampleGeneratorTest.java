package org.databene.benerator.sample;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 21:59:02
 */
public class WeightedSampleGeneratorTest extends GeneratorClassTest {

    private static Log logger = LogFactory.getLog(WeightedSampleGeneratorTest.class);

    public WeightedSampleGeneratorTest() {
        super(WeightedSampleGenerator.class);
    }

    public void testInstantiation() throws Exception {
        new WeightedSampleGenerator<Integer>();
        new WeightedSampleGenerator<String>();
    }

    public void testDistribution() throws Exception {
        WeightedSample<Integer>[] samples = new WeightedSample[] {
            new WeightedSample<Integer>(0, 0.1),
            new WeightedSample<Integer>(1, 0.3),
            new WeightedSample<Integer>(2, 0.6)
        };
        WeightedSampleGenerator<Integer> g = new WeightedSampleGenerator<Integer>();
        g.setSamples(samples);
        int n = 10000;
        int[] sampleCount = new int[3];
        for (int i = 0; i < n; i++) {
            sampleCount[g.generate()] ++;
        }
        List<WeightedSample<Integer>> samples2 = g.getSamples();
        for (int i = 0; i < sampleCount.length; i++) {
            int count = sampleCount[i];
            double measuredProbability = (float)count / n;
            double expectedProbability = samples2.get(i).getWeight();
            double ratio = measuredProbability / expectedProbability;
            logger.debug(i + " " + count + " " + ratio);
            assertTrue(ratio > 0.9 && ratio < 1.1);
        }
    }
}
