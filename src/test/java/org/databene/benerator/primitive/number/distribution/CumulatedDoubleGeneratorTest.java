package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.CollectionUtil;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 20:23:39
 */
public class CumulatedDoubleGeneratorTest extends GeneratorClassTest {

    public CumulatedDoubleGeneratorTest() {
        super(CumulatedDoubleGenerator.class);
    }

    public void testSingle() {
        checkProductSet(new CumulatedDoubleGenerator( 0,  0), 100, CollectionUtil.toSet( 0.));
        checkProductSet(new CumulatedDoubleGenerator(-1, -1), 100, CollectionUtil.toSet(-1.));
        checkProductSet(new CumulatedDoubleGenerator( 1,  1), 100, CollectionUtil.toSet( 1.));
        checkProductSet(new CumulatedDoubleGenerator( 1,  1, 1), 100, CollectionUtil.toSet(1.));
    }

    public void testRange() {
        checkProductSet(new CumulatedDoubleGenerator( 0,  1, 1), 1000, CollectionUtil.toSet( 0.,  1.));
        checkProductSet(new CumulatedDoubleGenerator( 1,  2, 1), 1000, CollectionUtil.toSet( 1.,  2.));
        checkProductSet(new CumulatedDoubleGenerator(-2, -1, 1), 1000, CollectionUtil.toSet(-2., -1.));
        checkProductSet(new CumulatedDoubleGenerator(-1,  0, 1), 1000, CollectionUtil.toSet(-1.,  0.));
        checkProductSet(new CumulatedDoubleGenerator(-1,  1, 1), 1000, CollectionUtil.toSet(-1.,  0., 1.));
    }

    public void testPrecision() {
        checkProductSet(new CumulatedDoubleGenerator( 1,  3, 2), 100, CollectionUtil.toSet( 1.,  3.));
        checkProductSet(new CumulatedDoubleGenerator(-3, -1, 2), 100, CollectionUtil.toSet(-3., -1.));
        checkProductSet(new CumulatedDoubleGenerator(-1,  1, 2), 100, CollectionUtil.toSet(-1.,  1.));
    }
}
