package org.databene.domain.product;

import org.databene.benerator.GeneratorClassTest;
import org.databene.model.validator.UniqueValidator;

public class EAN8GeneratorTest extends GeneratorClassTest {

    public EAN8GeneratorTest() {
        super(EAN8Generator.class);
    }

    public void testNonUnique() {
        expectGenerations(new EAN8Generator(false), 100, new EANValidator());
    }

    public void testUnique() {
        expectGenerations(new EAN8Generator(true), 10000, new EANValidator(), new UniqueValidator<String>());
    }
}
