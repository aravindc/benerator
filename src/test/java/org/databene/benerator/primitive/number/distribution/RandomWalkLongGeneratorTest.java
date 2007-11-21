package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.ArrayUtil;
import junit.framework.TestCase;

import java.util.Set;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 18.06.2006 09:11:19
 */
public class RandomWalkLongGeneratorTest extends GeneratorClassTest {

    public RandomWalkLongGeneratorTest() {
        super(RandomWalkLongGenerator.class);
    }

    public void testGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator simpleGenerator = new RandomWalkLongGenerator(1, 5, 1, 1, 1);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(2L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        RandomWalkLongGenerator oddGenerator = new RandomWalkLongGenerator(1, 5, 2, 2, 2);
        assertEquals(1L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(5L, (long)oddGenerator.generate());
    }

    public void testGreaterOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 0, 2);
        Set<Long> space = ArrayUtil.toSet(1L, 3L, 5L);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

    public void testEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 0, 0);
        assertEquals(3L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
    }

    public void testLessOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, -2, 0);
        Set<Long> space = ArrayUtil.toSet(1L, 3L, 5L);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

    public void testLess() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, -2, -2);
        assertEquals(5L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
        assertEquals(1L, (long)generator.generate());
    }

    public void testLessOrGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, -2, 2);
        Set<Long> space = ArrayUtil.toSet(1L, 3L, 5L);
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
        assertTrue(space.contains(generator.generate()));
    }

}
