/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.primitive.DigitsGenerator;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates {@link Address} objects.<br/>
 * <br/>
 * Created: 11.06.2006 08:07:40
 * @since 0.1
 * @author Volker Bergmann
 */
public class AddressGenerator extends CompositeGenerator<Address> {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AddressGenerator.class);

    private String dataset;
    private CountryGenerator countryGenerator;
    private CityGenerator cityGenerator;
    private StreetNameGenerator streetNameGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public AddressGenerator() {
        this(Country.getDefault().getIsoCode());
    }

    public AddressGenerator(String dataset) {
    	super(Address.class);
        setDataset(dataset);
    }

    public void setCountry(Country country) {
    	setDataset(country.getIsoCode());
    }
    
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

    // Generator interface ---------------------------------------------------------------------------------------------

	@Override
    public void init(GeneratorContext context) {
		assertNotInitialized();
		try {
	        initMembers(context);
		} catch (RuntimeException e) {
			LOGGER.error("", e);
			Country fallBackCountry = Country.getFallback();
			if (!fallBackCountry.getIsoCode().equals(this.dataset)) {
				LOGGER.error("Cannot generate addresses for " + dataset + ", falling back to " + fallBackCountry);
				setCountry(fallBackCountry);
				initMembers(context);
			} else
				throw e;
		}
        super.init(context);
	}

    public Address generate() throws IllegalGeneratorStateException {
    	assertInitialized();
        City city = cityGenerator.generate();
        Country country = city.getCountry();
        Street street = new Street(city, streetNameGenerator.generateForDataset(country.getIsoCode()));
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
        return getClass().getSimpleName() + '[' + dataset + ']';
    }

    // private helpers -------------------------------------------------------------------------------------------------

	private void initMembers(GeneratorContext context) {
	    countryGenerator = registerComponent(new CountryGenerator(dataset));
	    countryGenerator.init(context);
	    cityGenerator = registerComponent(new CityGenerator(dataset));
        cityGenerator.init(context);
        streetNameGenerator = registerComponent(new StreetNameGenerator(dataset));
        streetNameGenerator.init(context);
    }
	
    private PhoneNumber generatePhoneNumber(City city) {
        int localPhoneNumberLength = 10 - city.getAreaCode().length();
        String localCode = DigitsGenerator.generate(localPhoneNumberLength);
        return new PhoneNumber(city.getCountry().getPhoneCode(), city.getAreaCode(), localCode);
    }

}
