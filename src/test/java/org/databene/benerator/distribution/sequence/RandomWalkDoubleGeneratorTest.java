package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.distribution.sequence.RandomWalkDoubleGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.CollectionUtil;

import java.util.Set;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 18.06.2006 09:11:19
 */
public class RandomWalkDoubleGeneratorTest extends GeneratorClassTest {

    public RandomWalkDoubleGeneratorTest() {
        super(RandomWalkDoubleGenerator.class);
    }

    public void testGreater() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator simpleGenerator = new RandomWalkDoubleGenerator(1, 5, 1, 1, 1);
        assertEquals(1., simpleGenerator.generate());
        assertEquals(2., simpleGenerator.generate());
        assertEquals(3., simpleGenerator.generate());
        RandomWalkDoubleGenerator oddGenerator = new RandomWalkDoubleGenerator(1, 5, 2, 2, 2);
        assertEquals(1., oddGenerator.generate());
        assertEquals(3., oddGenerator.generate());
        assertEquals(5., oddGenerator.generate());
    }

    public void testGreaterOrEquals() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator generator = new RandomWalkDoubleGenerator(1, 5, 2, 0, 2);
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

    public void testEquals() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator generator = new RandomWalkDoubleGenerator(1, 5, 2, 0, 0);
        assertEquals(3., generator.generate());
        assertEquals(3., generator.generate());
        assertEquals(3., generator.generate());
    }

    public void testLessOrEquals() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator generator = new RandomWalkDoubleGenerator(1, 5, 2, -2, 0);
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

    public void testLess() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator generator = new RandomWalkDoubleGenerator(1, 5, 2, -2, -2);
        assertEquals(5., generator.generate());
        assertEquals(3., generator.generate());
        assertEquals(1., generator.generate());
    }

    public void testLessOrGreater() throws IllegalGeneratorStateException {
        RandomWalkDoubleGenerator generator = new RandomWalkDoubleGenerator(1, 5, 2, -2, 2);
        Set<Double> space = CollectionUtil.toSet(1., 3., 5.);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

}
