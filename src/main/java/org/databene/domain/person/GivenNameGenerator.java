package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.LightweightGenerator;
import org.databene.benerator.csv.RegionalCSVGenerator;
import org.databene.region.Region;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:13:09
 */
public class GivenNameGenerator extends LightweightGenerator<String> {

    private RegionalCSVGenerator<String> realGenerator;

    public GivenNameGenerator() {
        this(Region.getDefault(), Gender.MALE);
    }

    public GivenNameGenerator(Region region, Gender gender) {
        if (gender == Gender.FEMALE)
            realGenerator = new RegionalCSVGenerator<String>("org/databene/domain/person/givenName_female", region, ".csv");
        else if (gender == Gender.MALE)
            realGenerator = new RegionalCSVGenerator<String>("org/databene/domain/person/givenName_male", region, ".csv");
        else
            throw new IllegalArgumentException("Gender: " + gender);
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    public void setRegion(Region region) {
        realGenerator.setRegion(region);
    }

    public String generate() throws IllegalGeneratorStateException {
        return realGenerator.generate();
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + realGenerator.getRegion() + ']';
    }
}
