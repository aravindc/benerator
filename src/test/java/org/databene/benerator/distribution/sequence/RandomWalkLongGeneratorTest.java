package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.distribution.sequence.RandomWalkLongGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.CollectionUtil;

import java.util.Set;

/**
 * Tests the RandomWalkLongGenerator
 * Created: 18.06.2006 09:11:19
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomWalkLongGeneratorTest extends GeneratorClassTest {

    public RandomWalkLongGeneratorTest() {
        super(RandomWalkLongGenerator.class);
    }

    public void testGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator simpleGenerator = new RandomWalkLongGenerator(1, 5, 1, 1, 1, 1);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(2L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        RandomWalkLongGenerator oddGenerator = new RandomWalkLongGenerator(1, 5, 2, 1, 2, 2);
        assertEquals(1L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(5L, (long)oddGenerator.generate());
    }

    public void testGreaterOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, 0, 2);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

	public void testEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, 0, 0);
        assertEquals(3L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
    }

    public void testLessOrEquals() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 5, -2, 0);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

    public void testLess() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, -2, -2);
        assertEquals(5L, (long)generator.generate());
        assertEquals(3L, (long)generator.generate());
        assertEquals(1L, (long)generator.generate());
    }

    public void testLessOrGreater() throws IllegalGeneratorStateException {
        RandomWalkLongGenerator generator = new RandomWalkLongGenerator(1, 5, 2, 1, -2, 2);
        Set<Long> space = CollectionUtil.toSet(1L, 3L, 5L);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
        assertProductSpace(space, generator);
    }

    private void assertProductSpace(Set<Long> space, RandomWalkLongGenerator generator) {
        Long product = generator.generate();
		assertTrue("Expected one of " + space + ", but found " + product, space.contains(product));
    }

}
