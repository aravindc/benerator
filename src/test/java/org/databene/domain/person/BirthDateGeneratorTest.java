package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.TimeUtil;

import java.util.Date;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:14:08
 */
public class BirthDateGeneratorTest extends GeneratorClassTest {

    public BirthDateGeneratorTest() {
        super(BirthDateGenerator.class);
    }

    public void test() throws IllegalGeneratorStateException {
        Date now = new Date();
        BirthDateGenerator generator = new BirthDateGenerator(3, 80);
        for (int i = 0; i < 100; i++) {
            Date birtDate = generator.generate();
            int age = TimeUtil.yearsBetween(birtDate, now);
            assertTrue("Generated birthdate is to new: " + birtDate, age >= 3);
            assertTrue("Generated birthdate is to old: " + birtDate, age <= 80);
        }
    }
}
