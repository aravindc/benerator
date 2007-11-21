package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the BitReverseNaturalNumberGeneratorTest<br/><br/>
 * Created: 13.11.2007 13:10:39
 */
public class BitReverseNaturalNumberGeneratorTest extends GeneratorClassTest {

    public BitReverseNaturalNumberGeneratorTest() {
        super(BitReverseNaturalNumberGenerator.class);
    }

    public void testSequences() throws Exception {
        expectGeneratedSequence(new BitReverseNaturalNumberGenerator(3),  0L,  2L,  1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseNaturalNumberGenerator(4),  0L,  4L,  2L, 1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseNaturalNumberGenerator(7),  0L,  4L,  2L, 6L, 1L, 5L, 3L, 7L).withCeasedAvailability();
    }

    public void testCoverage() throws Exception {
        expectGeneratedSet(new BitReverseNaturalNumberGenerator(2), 0L, 1L, 2L).withCeasedAvailability();
        expectGeneratedSet(new BitReverseNaturalNumberGenerator(9), 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
                .withCeasedAvailability();
    }
}
