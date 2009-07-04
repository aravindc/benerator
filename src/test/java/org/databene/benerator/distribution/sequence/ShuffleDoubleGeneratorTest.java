package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.distribution.sequence.ShuffleDoubleGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 20:23:39
 */
public class ShuffleDoubleGeneratorTest extends GeneratorClassTest {

    public ShuffleDoubleGeneratorTest() {
        super(ShuffleDoubleGenerator.class);
    }

    public void testSingleValue() throws Exception {
        check( 1,  1, 1, 0,   1,  1);
        check(-1, -1, 1, 0,  -1, -1);
        check( 0,  0, 1, 0,   0,  0);
    }

    public void testIncrementOne() throws Exception {
        check( 0, 2, 1, 1,   0,  1, 2,  0);
        check(-2, 0, 1, 1,  -2, -1, 0, -2);
    }

    public void testIncrementTwo() throws Exception {
        check( 0, 2, 1, 2,   0,  2, 1,  0);
        check(-2, 0, 1, 2,  -2, 0, -1, -2);
    }

    public void testFractionalPrecision() throws Exception {
        check( 0, 1, 0.5, 1,   0, 1,  0.5,  0);
        check(-1, 0, 0.5, 1,  -1, 0, -0.5, -1);
    }

    public void testInvalidSetup() {
        try {
            new ShuffleDoubleGenerator(1, 0,  1, 1);
            fail("IllegalArgumentException expected if min > max");
        } catch (IllegalArgumentException e) {
            // this is the desired behavior
        }
        try {
            new ShuffleDoubleGenerator(0, 1,  1, 0);
            fail("IllegalArgumentException expected for increment == 0");
        } catch (IllegalArgumentException e) {
            // this is the desired behavior
        }
        try {
            new ShuffleDoubleGenerator(0, 1, 1, -1);
            fail("IllegalArgumentException expected for negative increment");
        } catch (IllegalArgumentException e) {
            // this is the desired behavior
        }
        try {
            new ShuffleDoubleGenerator(0, 1, 0, 1);
            fail("IllegalArgumentException expected for precision == 0");
        } catch (IllegalArgumentException e) {
            // this is the desired behavior
        }
        try {
            new ShuffleDoubleGenerator(0, 1, -1, 1);
            fail("IllegalArgumentException expected for negative precision");
        } catch (IllegalArgumentException e) {
            // this is the desired behavior
        }
    }

    public void testReset() throws Exception {
    	ShuffleDoubleGenerator generator = new ShuffleDoubleGenerator(0., 3., 1., 2.);
        expectGeneratedSequence(generator, 0., 2., 1., 3., 0.).withContinuedAvailability();
    }

    private void check(double min, double max, double precision, double increment, double ... expectedProducts) {
        Generator<Double> generator = new ShuffleDoubleGenerator(min, max, precision, increment);
        for (double product : expectedProducts) {
            assertEquals(product, generator.generate());
        }
    }

}
