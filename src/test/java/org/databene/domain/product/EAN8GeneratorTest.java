package org.databene.domain.product;

import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.Validator;
import org.databene.commons.validator.UniqueValidator;
import org.junit.Test;

public class EAN8GeneratorTest extends GeneratorClassTest {

    public EAN8GeneratorTest() {
        super(EAN8Generator.class);
    }

    @Test
    public void testNonUnique() {
        expectGenerations(new EAN8Generator(false), 100, (Validator<?>) new EANValidator());
    }

    @Test
    public void testUnique() {
        expectGenerations(new EAN8Generator(true), 10000, new EANValidator(), new UniqueValidator<Object>());
    }
    
}
