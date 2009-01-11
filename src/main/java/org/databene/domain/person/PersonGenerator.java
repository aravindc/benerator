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

package org.databene.domain.person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.domain.address.Country;

import java.util.Locale;

/**
 * Generates {@link Person} beans.<br/>
 * <br/>
 * Created: 09.06.2006 21:45:13
 * @since 0.1
 * @author Volker Bergmann
 */
public class PersonGenerator extends LightweightGenerator<Person> {

	private static Log logger = LogFactory.getLog(PersonGenerator.class);

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
        this(country.getIsoCode(), locale);
    }

    public PersonGenerator(String datasetName, Locale locale) {
    	super(Person.class);
        init(datasetName, locale);
    }

	private void init(String datasetName, Locale locale) {
		genderGen = new GenderGenerator();
        birthDateGenerator = new BirthDateGenerator(15, 105);
        titleGen = new TitleGenerator(locale);
        salutationProvider = new SalutationProvider(locale);
		init(datasetName);
	}

	private void init(String datasetName) {
		try {
	        maleGivenNameGen = new GivenNameGenerator(datasetName, Gender.MALE);
	        femaleGivenNameGen = new GivenNameGenerator(datasetName, Gender.FEMALE);
	        familyNameGen = new FamilyNameGenerator(datasetName);
		} catch (RuntimeException e) {
			Country fallBackCountry = Country.getFallBack();
			if (!fallBackCountry.getIsoCode().equals(datasetName)) {
				logger.error("Cannot generate addresses for " + datasetName + ", falling back to " + fallBackCountry);
				init(fallBackCountry.getIsoCode());
			} else
				throw e;
		}
	}

    // properties ------------------------------------------------------------------------------------------------------

	public Locale getLocale() {
		return titleGen.getLocale();
	}

	public void setLocale(Locale locale) {
        titleGen.setLocale(locale);
        salutationProvider.setLocale(locale);
    }

	public String getDataset() {
		return familyNameGen.getDataset();
	}

    public void setDataset(String datasetName) {
        maleGivenNameGen = new GivenNameGenerator(datasetName, Gender.MALE);
        femaleGivenNameGen = new GivenNameGenerator(datasetName, Gender.FEMALE);
        familyNameGen = new FamilyNameGenerator(datasetName);
    }

    // Generator interface ---------------------------------------------------------------------------------------------

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

    @Override
	public String toString() {
        return getClass().getSimpleName();
    }
}
