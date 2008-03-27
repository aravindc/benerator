package org.databene.domain.person;

import java.util.Locale;

import org.databene.benerator.csv.DataSetCSVGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:03:56
 */
public class FamilyNameGenerator extends DataSetCSVGenerator<String> {

    public FamilyNameGenerator() {
        this(Locale.getDefault().getCountry());
    }

    public FamilyNameGenerator(String dataSetName) {
        this("org/databene/dataset/region", 
                dataSetName, 
                "org/databene/domain/person/familyName");
    }

    public FamilyNameGenerator(String dataSetType, String dataSetName, String baseName) {
        super(dataSetType, dataSetName, baseName, ".csv", "UTF-8");
    }
}
