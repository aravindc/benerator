/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.domain.address;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.primitive.regex.RegexStringGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates {@link Address} objects.<br/>
 * <br/>
 * Created: 11.06.2006 08:07:40
 * @since 0.1
 * @author Volker Bergmann
 */
public class AddressGenerator extends LightweightGenerator<Address> {
	
	private static Logger logger = LoggerFactory.getLogger(AddressGenerator.class);

    private Country country;
    private CityGenerator cityGenerator;
    private StreetNameGenerator streetNameGenerator;
    private RegexStringGenerator localPhoneNumberGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public AddressGenerator() {
        this(Country.getDefault());
    }

    public AddressGenerator(Country country) {
        setCountry(country);
    }

    public void setCountry(Country country) {
    	this.country = country;
    }
    
	public void setDataset(String dataset) {
		try {
			setCountry(Country.getInstance(dataset));
		} catch (RuntimeException e) {
			Country fallBackCountry = Country.getFallback();
			if (!fallBackCountry.equals(country)) {
				logger.error("Cannot generate addresses for " + country + ", falling back to " + fallBackCountry);
				setCountry(fallBackCountry);
			} else
				throw e;
		}
	}

    // Generator interface ---------------------------------------------------------------------------------------------

	@Override
    public void init(BeneratorContext context) {
		assertNotInitialized();
		try {
	        initMembers(context);
		} catch (RuntimeException e) {
			Country fallBackCountry = Country.getFallback();
			if (!fallBackCountry.equals(country)) {
				logger.error("Cannot generate addresses for " + country + ", falling back to " + fallBackCountry);
				setCountry(fallBackCountry);
				initMembers(context);
			} else
				throw e;
		}
        super.init(context);
	}

    public Class<Address> getGeneratedType() {
	    return Address.class;
    }

    public Address generate() throws IllegalGeneratorStateException {
    	assertInitialized();
        City city = cityGenerator.generate();
        Street street = new Street(city, streetNameGenerator.generate());
        String[] data = street.generateHouseNumberWithZipCode(); // TODO v0.7 make street name generator fit the locale
        String houseNumber = data[0];
        String zipCode = data[1];
        PhoneNumber privatePhone = generatePhoneNumber(city);
        PhoneNumber officePhone = generatePhoneNumber(city);
        PhoneNumber mobilePhone = country.generateMobileNumber(city);
        PhoneNumber fax = generatePhoneNumber(city);
        return new Address(street.getName(), houseNumber, zipCode, city, city.getState().getId(), country, privatePhone, officePhone, mobilePhone, fax);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
	public String toString() {
        return getClass().getSimpleName() + '[' + country + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

	private void initMembers(BeneratorContext context) {
	    cityGenerator = new CityGenerator(country);
        cityGenerator.init(context);
        streetNameGenerator = new StreetNameGenerator(country.getIsoCode());
        streetNameGenerator.init(context);
        localPhoneNumberGenerator = new RegexStringGenerator("[1-9]\\d{5}");
        localPhoneNumberGenerator.init(context);
    }
	
    private PhoneNumber generatePhoneNumber(City city) {
        int localPhoneNumberLength = 10 - city.getAreaCode().length();
        localPhoneNumberGenerator.setPattern("[2-9]\\d{" + (localPhoneNumberLength - 1) + '}');
        String localCode = localPhoneNumberGenerator.generate();
        return new PhoneNumber(country.getPhoneCode(), city.getAreaCode(), localCode);
    }

}
