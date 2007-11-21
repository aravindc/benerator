package org.databene.domain.address;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.LightweightGenerator;
import org.databene.benerator.primitive.regex.RegexStringGenerator;
import org.databene.region.*;

/**
 * Creates Addresses.<br/>
 * <br/>
 * Created: 11.06.2006 08:07:40
 *
 * Generation order from dependencies: country -> city -> street -> housenumber -> zipCode
 */
public class AddressGenerator extends LightweightGenerator<Address> {

    private Country country;
    private CityGenerator cityGenerator;
    private MobilePhoneCodeGenerator mobilePhoneCodeGenerator;
    private StreetNameGenerator streetNameGenerator;
    private RegexStringGenerator localPhoneNumberGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public AddressGenerator() {
        this(Country.getDefault());
    }

    public AddressGenerator(Country country) {
        this.country = country;
        this.cityGenerator = new CityGenerator(country);
        this.streetNameGenerator = new StreetNameGenerator(Region.getInstance(country.getIsoCode()));
        this.mobilePhoneCodeGenerator = new MobilePhoneCodeGenerator(country);
        this.localPhoneNumberGenerator = new RegexStringGenerator("[1-9]\\d{5}");
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<Address> getGeneratedType() {
        return Address.class;
    }

    public Address generate() throws IllegalGeneratorStateException {
        City city = cityGenerator.generate();
        Street street = new Street(city, streetNameGenerator.generate());
        String[] data = street.generateHouseNumberWithZipCode(); // TODO v0.4 make street name generator fit the locale
        String houseNumber = data[0];
        String zipCode = data[1];
        PhoneNumber privatePhone = generatePhoneNumber(city);
        PhoneNumber officePhone = generatePhoneNumber(city);
        PhoneNumber mobilePhone = mobilePhoneCodeGenerator.generate();
        PhoneNumber fax = generatePhoneNumber(city);
        return new Address(street.getName(), houseNumber, zipCode, city.getName(), country, privatePhone, officePhone, mobilePhone, fax);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return getClass().getSimpleName() + '[' + country + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private PhoneNumber generatePhoneNumber(City city) {
        int localPhoneCodeLength = 9 - city.getPhoneCode().length();
        localPhoneNumberGenerator.setPattern("[1-9]\\d{" + (localPhoneCodeLength - 1) + '}');
        String localCode = localPhoneNumberGenerator.generate();
        return new PhoneNumber(country.getPhoneCode(), city.getPhoneCode(), localCode, false);
    }
}
