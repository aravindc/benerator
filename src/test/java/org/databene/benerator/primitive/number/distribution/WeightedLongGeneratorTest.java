package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.primitive.number.distribution.WeightedLongGenerator;

import java.util.Set;
import java.util.HashSet;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 18.06.2006 15:04:17
 */
public class WeightedLongGeneratorTest extends GeneratorClassTest {

    public WeightedLongGeneratorTest() {
        super(WeightedLongGenerator.class);
    }

    public void testRandomSequence() throws IllegalGeneratorStateException {
        checkUniformDistribution(-2,  2, 1, 10000, 0.1, -2, -1, 0, 1, 2);
        checkUniformDistribution(-2,  2, 2, 10000, 0.1, -2, 0, 2);
        checkUniformDistribution( 1,  5, 2, 10000, 0.1, 1, 3, 5);
        checkUniformDistribution(-5, -1, 2, 10000, 0.1, -5, -3, -1);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkUniformDistribution(int min, int max, int precision,
                                          int iterations, double tolerance, int ... expectedValuesAsInt) {
        Set<Long> expectedValues = new HashSet<Long>(expectedValuesAsInt.length);
        for (int i : expectedValuesAsInt)
            expectedValues.add((long)i);
        WeightedLongGenerator generator = new WeightedLongGenerator(min, max, precision);
        checkEqualDistribution(generator, iterations, tolerance, expectedValues);
    }


}
