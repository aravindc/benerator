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
        Date now = TimeUtil.today();
        BirthDateGenerator generator = new BirthDateGenerator(3, 12);
        generator.init(context);
        for (int i = 0; i < 1000; i++) {
            Date birthDate = generator.generate();
            int age = TimeUtil.yearsBetween(birthDate, now);
            assertTrue("Generated birth date is too new: " + birthDate, age >= 3);
            assertTrue("Generated birth date is too old: " + birthDate, age <= 12);
        }
    }
    
}
