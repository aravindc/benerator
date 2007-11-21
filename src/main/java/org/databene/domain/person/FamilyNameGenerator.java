package org.databene.domain.person;

import org.databene.benerator.csv.RegionalCSVGenerator;
import org.databene.region.Region;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:03:56
 */
public class FamilyNameGenerator extends RegionalCSVGenerator<String> {

    public FamilyNameGenerator() {
        this(Region.getDefault());
    }

    public FamilyNameGenerator(Region region) {
        super("org/databene/domain/person/familyName", region, ".csv");
    }
}
