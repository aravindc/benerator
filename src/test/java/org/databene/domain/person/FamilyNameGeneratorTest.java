package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.measure.count.ObjectCounter;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:16:06
 */
public class FamilyNameGeneratorTest extends GeneratorClassTest {

    public FamilyNameGeneratorTest() {
        super(FamilyNameGenerator.class);
    }

    public void test() throws IllegalGeneratorStateException {
        ObjectCounter<String> counter = new ObjectCounter<String>(10);
        Generator<String> generator = new FamilyNameGenerator();
        for (int i = 0; i < 10; i++)
            counter.count(generator.generate());
        assertTrue(counter.objectSet().size() >= 3);
    }
}
