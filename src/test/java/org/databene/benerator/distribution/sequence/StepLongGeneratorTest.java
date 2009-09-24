package org.databene.benerator.distribution.sequence;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.distribution.sequence.StepLongGenerator;
import org.databene.benerator.test.GeneratorClassTest;

/**
 * Created: 26.07.2007 18:11:19
 */
public class StepLongGeneratorTest extends GeneratorClassTest {

    public StepLongGeneratorTest() {
        super(StepLongGenerator.class);
    }

    public void testIncrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, 1);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(2L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, 2);
        assertEquals(1L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(5L, (long)oddGenerator.generate());
    }

    public void testDecrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, -1);
        assertEquals(5L, (long)simpleGenerator.generate());
        assertEquals(4L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, -2);
        assertEquals(5L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(1L, (long)oddGenerator.generate());
    }

    public void testPrecision() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5);
        simpleGenerator.setPrecision(2L);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        assertEquals(5L, (long)simpleGenerator.generate());
    }

}
