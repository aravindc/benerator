package org.databene.domain.person;

import org.databene.benerator.csv.LocalCSVGenerator;
import org.databene.commons.Encodings;

import java.util.Locale;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 26.06.2006 19:06:23
 */
public class AcademicTitleGenerator extends LocalCSVGenerator<String> {

    public AcademicTitleGenerator() {
        this(Locale.getDefault());
    }

    public AcademicTitleGenerator(Locale locale) {
        super("org/databene/domain/person/title", locale, ".csv", Encodings.UTF_8);
    }
}
