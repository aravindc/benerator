package org.databene.domain.person;

import org.databene.benerator.LightweightGenerator;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.region.Region;
import org.databene.region.Country;

import java.util.Locale;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:45:13
 */
public class PersonGenerator extends LightweightGenerator<Person> {

    private GenderGenerator genderGen;
    private GivenNameGenerator maleGivenNameGen;
    private GivenNameGenerator femaleGivenNameGen;
    private FamilyNameGenerator familyNameGen;
    private TitleGenerator titleGen;
    private SalutationProvider salutationProvider;

    private BirthDateGenerator birthDateGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public PersonGenerator() {
        this(Country.getDefault(), Locale.getDefault());
    }

    public PersonGenerator(Country country, Locale locale) {
        genderGen = new GenderGenerator();
        Region region = country.getRegion();
        maleGivenNameGen = new GivenNameGenerator(region, Gender.MALE);
        femaleGivenNameGen = new GivenNameGenerator(region, Gender.FEMALE);
        familyNameGen = new FamilyNameGenerator(region);
        titleGen = new TitleGenerator(locale);
        salutationProvider = new SalutationProvider(locale);
        //addressGenerator = new AddressGenerator(country, locale);
        birthDateGenerator = new BirthDateGenerator(15, 105);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setLocale(Locale locale) {
        titleGen.setLocale(locale);
        salutationProvider.setLocale(locale);
    }

    public void setRegion(Region region) {
        maleGivenNameGen.setRegion(region);
        femaleGivenNameGen.setRegion(region);
        familyNameGen.setRegion(region);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<Person> getGeneratedType() {
        return Person.class;
    }

    public Person generate() throws IllegalGeneratorStateException {
        Person person = new Person();
        person.setGender(genderGen.generate());
        if (Gender.MALE.equals(person.getGender()))
            person.setGivenName(maleGivenNameGen.generate());
        else
            person.setGivenName(femaleGivenNameGen.generate());
        person.setFamilyName(familyNameGen.generate());
        person.setSalutation(salutationProvider.salutation(person.getGender()));
        person.setTitle(titleGen.generate());
        person.setBirthDate(birthDateGenerator.generate());
        return person;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + familyNameGen.getRegion() + ']';
    }
}
