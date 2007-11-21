package org.databene.domain.person;

import org.databene.benerator.csv.LocalCSVGenerator;

import java.util.Locale;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 26.06.2006 19:06:23
 */
public class TitleGenerator extends LocalCSVGenerator<String> {

    public TitleGenerator() {
        this(Locale.getDefault());
    }

    public TitleGenerator(Locale locale) {
        super("org/databene/domain/person/title", locale, ".csv");
    }
}
