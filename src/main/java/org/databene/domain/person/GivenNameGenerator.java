package org.databene.domain.person;

import java.util.Locale;

import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.commons.Encodings;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:13:09
 * @author Volker Bergmann
 */
public class GivenNameGenerator extends WeightedDatasetCSVGenerator<String> {

    public GivenNameGenerator() {
        this(Locale.getDefault().getCountry(), Gender.MALE);
    }

    public GivenNameGenerator(String datasetName, Gender gender) {
        this(datasetName, 
            "/org/databene/dataset/region", 
            "/org/databene/domain/person/givenName", 
            gender);
    }

    public GivenNameGenerator(String datasetName, String nesting, String baseName, Gender gender) {
        super(genderBaseName(baseName, gender) + "_{0}.csv", datasetName, nesting, Encodings.UTF_8);
    }

    private static String genderBaseName(String baseName, Gender gender) {
        if (gender == Gender.FEMALE)
            return baseName + "_female";
        else if (gender == Gender.MALE)
            return baseName + "_male";
        else
            throw new IllegalArgumentException("Gender: " + gender);
    }

}
