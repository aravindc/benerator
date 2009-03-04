package org.databene.domain.person;

import org.databene.benerator.Generator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.util.LightweightGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:45:23
 */
public class GenderGenerator extends LightweightGenerator<Gender> {

    private Generator<Gender> gen;

    public GenderGenerator() {
    	super(Gender.class);
        this.gen = GeneratorFactory.getSampleGenerator(Gender.MALE, Gender.FEMALE);
    }

    public Class<Gender> getGeneratedType() {
        return Gender.class;
    }

    public Gender generate() throws IllegalGeneratorStateException {
        return gen.generate();
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
