package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.measure.count.ObjectCounter;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:37:05
 */
public class GivenNameGeneratorTest extends GeneratorClassTest {

    public GivenNameGeneratorTest() {
        super(GivenNameGenerator.class);
    }

    public void test() throws IllegalGeneratorStateException {
        ObjectCounter<String> counter = new ObjectCounter<String>(10);
        Generator<String> generator = new GivenNameGenerator();
        for (int i = 0; i < 10; i++)
            counter.count(generator.generate());
        assertTrue(counter.objectSet().size() >= 3);
    }
}
