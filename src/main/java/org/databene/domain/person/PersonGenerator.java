package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.domain.address.Country;

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
        String countryCode = country.getIsoCode();
        maleGivenNameGen = new GivenNameGenerator(countryCode, Gender.MALE);
        femaleGivenNameGen = new GivenNameGenerator(countryCode, Gender.FEMALE);
        familyNameGen = new FamilyNameGenerator(countryCode);
        titleGen = new TitleGenerator(locale);
        salutationProvider = new SalutationProvider(locale);
        birthDateGenerator = new BirthDateGenerator(15, 105);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setLocale(Locale locale) {
        titleGen.setLocale(locale);
        salutationProvider.setLocale(locale);
    }

    public void setDataSet(String dataSetName) {
        maleGivenNameGen = new GivenNameGenerator(dataSetName, Gender.MALE);
        femaleGivenNameGen = new GivenNameGenerator(dataSetName, Gender.FEMALE);
        familyNameGen = new FamilyNameGenerator(dataSetName);
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
        return getClass().getSimpleName();
    }
}
