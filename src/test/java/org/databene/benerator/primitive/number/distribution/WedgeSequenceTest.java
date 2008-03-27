package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorTest;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.model.function.Sequence;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 13.11.2007 14:20:39
 */
public class WedgeSequenceTest extends GeneratorTest {

    public void testLongPrecision1() throws Exception {
        expectGeneratedSequence(longGenerator(1L, 3L, 1L),  1L,  3L,  2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator( 1L,  4L, 1L),  1L,  4L,  2L,  3L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-3L, -1L, 1L), -3L, -1L, -2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-4L, -1L, 1L), -4L, -1L, -3L, -2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-1L,  1L, 1L), -1L,  1L,  0L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-1L,  2L, 1L), -1L,  2L,  0L,  1L).withCeasedAvailability();
    }

    public void testLongPrecision5() throws Exception {
        expectGeneratedSequence(longGenerator(  1L, 11L, 5L),   1L, 11L,   6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(  1L, 16L, 5L),   1L, 16L,   6L, 11L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-11L, -1L, 5L), -11L, -1L,  -6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-16L, -1L, 5L), -16L, -1L, -11L, -6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-11L,  4L, 5L), -11L,  4L,  -6L, -1L).withCeasedAvailability();
    }

    public void testDoublePrecision1() throws Exception {
        expectGeneratedSequence(doubleGenerator(1., 3., 1.),  1.,  3.,  2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator( 1.,  4., 1.),  1.,  4.,  2.,  3.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-3., -1., 1.), -3., -1., -2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-4., -1., 1.), -4., -1., -3., -2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.,  1., 1.), -1.,  1.,  0.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.,  2., 1.), -1.,  2.,  0.,  1.).withCeasedAvailability();
    }

    public void testDoublePrecision5() throws Exception {
        expectGeneratedSequence(doubleGenerator(  1., 11., 5.),   1., 11.,   6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(  1., 16., 5.),   1., 16.,   6., 11.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-11., -1., 5.), -11., -1.,  -6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-16., -1., 5.), -16., -1., -11., -6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-11.,  4., 5.), -11.,  4.,  -6., -1.).withCeasedAvailability();
    }

    public void testDoublePrecision0_5() throws Exception {
        expectGeneratedSequence(doubleGenerator( 0.5,  1.5, 0.5),  0.5,  1.5,  1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator( 0.5,  2.0, 0.5),  0.5,  2.0,  1.0,  1.5).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.5, -0.5, 0.5), -1.5, -0.5, -1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-2.0, -0.5, 0.5), -2.0, -0.5, -1.5, -1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-0.5,  0.5, 0.5), -0.5,  0.5, 0.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.0,  0.5, 0.5), -1.0,  0.5, -0.5,  0.0).withCeasedAvailability();
    }

    private Generator<Long> longGenerator(long min, long max, long precision) {
        return GeneratorFactory.getNumberGenerator(Long.class,  min,  max, precision, Sequence.WEDGE, 0);
    }

    private Generator<Double> doubleGenerator(double min, double max, double precision) {
        return GeneratorFactory.getNumberGenerator(Double.class,  min,  max, precision, Sequence.WEDGE, 0);
    }
}
