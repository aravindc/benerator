package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2007 by Volker Bergmann
 * Created: 26.07.2007 18:41:19
 */
public class StepDoubleGeneratorTest extends GeneratorClassTest {

    public StepDoubleGeneratorTest() {
        super(StepDoubleGenerator.class);
    }

    public void testIncrement() throws IllegalGeneratorStateException {
        StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, 1);
        assertEquals(1., simpleGenerator.generate());
        assertEquals(2., simpleGenerator.generate());
        assertEquals(3., simpleGenerator.generate());
        StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, 2);
        assertEquals(1., oddGenerator.generate());
        assertEquals(3., oddGenerator.generate());
        assertEquals(5., oddGenerator.generate());
    }

    public void testDecrement() throws IllegalGeneratorStateException {
        StepDoubleGenerator simpleGenerator = new StepDoubleGenerator(1, 5, -1);
        assertEquals(5., simpleGenerator.generate());
        assertEquals(4., simpleGenerator.generate());
        assertEquals(3., simpleGenerator.generate());
        StepDoubleGenerator oddGenerator = new StepDoubleGenerator(1, 5, -2);
        assertEquals(5., oddGenerator.generate());
        assertEquals(3., oddGenerator.generate());
        assertEquals(1., oddGenerator.generate());
    }

}
