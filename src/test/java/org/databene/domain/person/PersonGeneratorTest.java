package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.GeneratorClassTest;
import org.databene.region.Country;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:14:08
 */
public class PersonGeneratorTest extends GeneratorClassTest {

    private static final Log logger = LogFactory.getLog(PersonGeneratorTest.class);

    public PersonGeneratorTest() {
        super(PersonGenerator.class);
    }

    public void test() throws IllegalGeneratorStateException {
        PersonGenerator generator = new PersonGenerator(Country.GERMANY, Locale.GERMANY);
        for (int i = 0; i < 10; i++) {
            logger.debug(generator.generate());
        }
    }
}
