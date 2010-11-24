package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.TimeUtil;

import java.util.Date;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link BirthDateGenerator}.<br/><br/>
 * Created: 09.06.2006 22:14:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class BirthDateGeneratorTest extends GeneratorClassTest {

    public BirthDateGeneratorTest() {
        super(BirthDateGenerator.class);
    }

    @Test
    public void test() throws IllegalGeneratorStateException {
        Date now = new Date();
        BirthDateGenerator generator = new BirthDateGenerator(3, 80);
        generator.init(context);
        for (int i = 0; i < 1000; i++) {
            Date birtDate = generator.generate();
            int age = TimeUtil.yearsBetween(birtDate, now);
            assertTrue("Generated birthdate is to new: " + birtDate, age >= 3);
            assertTrue("Generated birthdate is to old: " + birtDate, age <= 80);
        }
    }
    
}
