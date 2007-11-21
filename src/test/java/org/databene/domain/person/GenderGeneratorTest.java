package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.measure.count.ObjectCounter;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:47:53
 */
public class GenderGeneratorTest extends GeneratorClassTest {

    public GenderGeneratorTest() {
        super(GenderGenerator.class);
    }

    public void test() throws IllegalGeneratorStateException {
        ObjectCounter<Gender> counter = new ObjectCounter<Gender>(10);
        Generator<Gender> generator = new GenderGenerator();
        for (int i = 0; i < 10; i++) {
            Gender generatedGender = generator.generate();
            assertNotNull(generatedGender);
            counter.count(generatedGender);
        }
        assertTrue(counter.objectSet().size() == 2);
    }
}
