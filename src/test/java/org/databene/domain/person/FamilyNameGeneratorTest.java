package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.measure.count.ObjectCounter;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link FamilyNameGenerator}.<br/><br/>
 * Created: 09.06.2006 22:16:06
 * @author Volker Bergmann
 */
public class FamilyNameGeneratorTest extends GeneratorClassTest {

    public FamilyNameGeneratorTest() {
        super(FamilyNameGenerator.class);
    }

    @Test
    public void test() throws IllegalGeneratorStateException {
        ObjectCounter<String> counter = new ObjectCounter<String>(10);
        Generator<String> generator = new FamilyNameGenerator();
        for (int i = 0; i < 10; i++)
            counter.count(generator.generate());
        assertTrue(counter.objectSet().size() >= 3);
    }
    
}
