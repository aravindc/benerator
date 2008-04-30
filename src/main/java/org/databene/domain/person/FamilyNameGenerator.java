package org.databene.domain.person;

import java.util.Locale;

import org.databene.benerator.csv.WeightedDatasetCSVGenerator;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:03:56
 * @author Volker Bergmann
 */
public class FamilyNameGenerator extends WeightedDatasetCSVGenerator<String> {

    public FamilyNameGenerator() {
        this(Locale.getDefault().getCountry());
    }

    public FamilyNameGenerator(String datasetName) {
        this(datasetName, 
                "org/databene/dataset/region", 
                "org/databene/domain/person/familyName_{0}.csv");
    }

    public FamilyNameGenerator(String datasetName, String nesting, String fileNamePattern) {
        super(fileNamePattern, datasetName, nesting, "UTF-8");
    }
}
