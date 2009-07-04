package org.databene.benerator.distribution.sequence;

import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.distribution.sequence.WedgeLongGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 13.11.2007 13:10:39
 */
public class WedgeLongGeneratorTest extends GeneratorClassTest {

    public WedgeLongGeneratorTest() {
        super(WedgeLongGenerator.class);
    }

    public void testInstantiation() throws Exception {
        new WedgeLongGenerator(0, 10, 1);
    }

    public void testPrecision1() throws Exception {
        expectGeneratedSequence(new WedgeLongGenerator( 1,  3, 1),  1L,  3L,  2L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator( 1,  4, 1),  1L,  4L,  2L,  3L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-3, -1, 1), -3L, -1L, -2L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-4, -1, 1), -4L, -1L, -3L, -2L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-1,  1, 1), -1L,  1L,  0L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-1,  2, 1), -1L,  2L,  0L,  1L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator( 0,  5, 1), 0L, 5L, 1L, 4L, 2L, 3L).withCeasedAvailability();
    }

    public void testPrecision5() throws Exception {
        expectGeneratedSequence(new WedgeLongGenerator(  1, 11, 5),   1L, 11L,  6L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(  1, 16, 5),   1L, 16L,  6L, 11L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-11, -1, 5), -11L, -1L, -6L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-16, -1, 5), -16L, -1L, -11L, -6L).withCeasedAvailability();
        expectGeneratedSequence(new WedgeLongGenerator(-11,  4, 5), -11L,  4L,  -6L, -1L).withCeasedAvailability();
    }
}
