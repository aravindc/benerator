package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.primitive.number.distribution.ShuffleLongGenerator;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 07.06.2006 20:23:39
 */
public class ShuffleLongGeneratorTest extends GeneratorClassTest {

    public ShuffleLongGeneratorTest() {
        super(ShuffleLongGenerator.class);
    }

    public void testInstantiation() throws Exception {
        new ShuffleLongGenerator(0, 10, 1);
    }

    public void testIncrement0() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 0);
        assertEquals(0, (long)generator.generate());
        assertEquals(0, (long)generator.generate());
    }

    public void testIncrement1() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1);
        assertEquals(0, (long)generator.generate());
        assertEquals(1, (long)generator.generate());
        assertEquals(2, (long)generator.generate());
        assertEquals(3, (long)generator.generate());
        assertEquals(0, (long)generator.generate());
    }

    public void testIncrement2() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 2);
        assertEquals(0, (long)generator.generate());
        assertEquals(2, (long)generator.generate());
        assertEquals(1, (long)generator.generate());
        assertEquals(3, (long)generator.generate());
        assertEquals(0, (long)generator.generate());
    }

    public void testIncrement3() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 3);
        assertEquals(0, (long)generator.generate());
        assertEquals(3, (long)generator.generate());
        assertEquals(1, (long)generator.generate());
        assertEquals(2, (long)generator.generate());
        assertEquals(0, (long)generator.generate());
    }

    public void testIncrement4() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 4);
        assertEquals(0, (long)generator.generate());
        assertEquals(1, (long)generator.generate());
        assertEquals(2, (long)generator.generate());
        assertEquals(3, (long)generator.generate());
        assertEquals(0, (long)generator.generate());
    }
}
