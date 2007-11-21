package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.ArrayUtil;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 18.06.2006 15:04:17
 */
public class WeightedDoubleGeneratorTest extends GeneratorClassTest {

    public WeightedDoubleGeneratorTest() {
        super(WeightedDoubleGenerator.class);
    }

    public void testSingleValueGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
                new WeightedDoubleGenerator( 0,  0, 0, new ConstantFunction(1)), 300, ArrayUtil.toSet(0.));
        checkProductSet(
                new WeightedDoubleGenerator( 1,  1, 0.5, new ConstantFunction(1)), 300, ArrayUtil.toSet(1.));
        checkProductSet(
                new WeightedDoubleGenerator(-1, -1, 0, new ConstantFunction(1)), 300, ArrayUtil.toSet(-1.));
    }

    public void testDiscreteRangeGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
                new WeightedDoubleGenerator( -1,  0, 0.5, new ConstantFunction(1)), 300, ArrayUtil.toSet(-1., -0.5, 0.));
        checkProductSet(
                new WeightedDoubleGenerator(-1, 1, 0.5, new ConstantFunction(1)), 300, ArrayUtil.toSet(-1., -0.5, 0., 0.5, 1.));
    }

    public void testInvalidPrecisions() throws IllegalGeneratorStateException {
        try {
            new WeightedDoubleGenerator( 0,  1, -1, new ConstantFunction(1)); // negative precision
            fail("IllegalArgumentException expected for negative precision");
        } catch (IllegalArgumentException e) {
            // this is the desired behaviour
        }
        try {
            new WeightedDoubleGenerator( 0,  1,  0, new ConstantFunction(1)); // precision == 0
            fail("IllegalArgumentException expected for precision == 0");
        } catch (IllegalArgumentException e) {
            // this is the desired behaviour
        }
    }

    public void testInvalidRange() throws IllegalGeneratorStateException {
        try {
            new WeightedDoubleGenerator( 2,  1,  1, new ConstantFunction(1)); // min > max
            fail("IllegalArgumentException expected if min > max");
        } catch (IllegalArgumentException e) {
            // this is the desired behaviour
        }
    }
}
