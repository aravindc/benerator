package org.databene.domain.person;

import java.util.Locale;

import org.databene.benerator.csv.DataSetCSVGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:13:09
 * @author Volker Bergmann
 */
public class GivenNameGenerator extends DataSetCSVGenerator<String> {

    public GivenNameGenerator() {
        this(Locale.getDefault().getCountry(), Gender.MALE);
    }

    public GivenNameGenerator(String dataSetName, Gender gender) {
        this("org/databene/dataset/region", 
            dataSetName, 
            "org/databene/domain/person/givenName", 
            gender);
    }

    public GivenNameGenerator(String dataSetType, String dataSetName, String baseName, Gender gender) {
        super(dataSetType, dataSetName, genderBaseName(baseName, gender), ".csv", "UTF-8");
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
