package org.databene.benerator.primitive.number.distribution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 20:23:39
 */
public class CumulatedLongGeneratorTest extends GeneratorClassTest {

    private static Log logger = LogFactory.getLog(CumulatedLongGeneratorTest.class);

    public CumulatedLongGeneratorTest() {
        super(CumulatedLongGenerator.class);
    }

    // tests -----------------------------------------------------------------------------------------------------------

    public void testInstantiation() throws Exception {
        new CumulatedLongGenerator(0, 10);
    }

    public void testAverage() throws Exception {
        checkAverage(0, 1, 0.5);
        checkAverage(1, 2, 1.5);
        checkAverage(0, 2, 1);
        checkAverage(0, 50, 25);
    }

    public void testDistribution() throws Exception {
        checkDistribution(0, 1, 1000);
        checkDistribution(0, 5, 5000);
        //checkDistribution(0, 25, 100000);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void checkAverage(int min, int max, double average) {
        CumulatedLongGenerator g = new CumulatedLongGenerator(min, max);
        assertEquals(average, g.average(), 0.1);
    }

    private void checkDistribution(int min, int max, int n) {
        logger.debug("checkDistribution(" + min + ", " + max + ", " + n + ")");
        CumulatedLongGenerator g = new CumulatedLongGenerator(min, max);
        int[] sampleCount = new int[max - min + 1];
        for (int i = 0; i < n; i++) {
            int sample = g.generate().intValue();
            sampleCount[sample - min]++;
        }
        assert(sampleCount[0] > 0);
        assert(sampleCount[sampleCount.length - 1] > 0);
        for (int i = 0; i <= sampleCount.length / 2; i++) {
            int c1 = sampleCount[i];
            int c2 = sampleCount[sampleCount.length - 1 - i];
            int threshold = n * 2 / sampleCount.length / sampleCount.length;
            if (c1 > threshold && c2 > threshold) {
                float ratio = (float)c1/c2;
                boolean check = (ratio > 0.8 && ratio < 1.2);
                logger.debug((i + " " + c1 + " " + ratio + " " + check));
                assertTrue("Distribution expected to be symmetric", check);
            }
        }
    }
}
