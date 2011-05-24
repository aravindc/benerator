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

package org.databene.domain.person;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.dataset.DatasetBasedGenerator;
import org.databene.benerator.dataset.ProductFromDataset;
import org.databene.benerator.primitive.BooleanGenerator;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.commons.Converter;
import org.databene.domain.address.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Generates {@link Person} beans.<br/>
 * <br/>
 * Created: 09.06.2006 21:45:13
 * @since 0.1
 * @author Volker Bergmann
 */
public class PersonGenerator extends CompositeGenerator<Person> implements DatasetBasedGenerator<Person> {

	private static Logger logger = LoggerFactory.getLogger(PersonGenerator.class);
	
    private static final String REGION_NESTING = "org/databene/dataset/region";

	private String datasetName;
	private Locale locale;
	
    private GenderGenerator genderGen;
    private GivenNameGenerator maleGivenNameGen;
    private GivenNameGenerator femaleGivenNameGen;
    private Generator<Boolean> secondNameTest;
    private FamilyNameGenerator familyNameGen;
    private Map<String, Converter<String, String>> femaleFamilyNameConverters;
    private AcademicTitleGenerator acadTitleGen;
    private NobilityTitleGenerator maleNobilityTitleGen;
    private NobilityTitleGenerator femaleNobilityTitleGen;
    private SalutationProvider salutationProvider;

    private BirthDateGenerator birthDateGenerator;
    private EMailAddressBuilder emailGenerator;

    // constructors ----------------------------------------------------------------------------------------------------

    public PersonGenerator() {
        this(Country.getDefault().getIsoCode(), Locale.getDefault());
    }

    public PersonGenerator(String datasetName, Locale locale) {
    	super(Person.class);
        this.datasetName = datasetName;
        this.locale = locale;
		genderGen = registerComponent(new GenderGenerator(0.5));
        birthDateGenerator = registerComponent(new BirthDateGenerator(15, 105));
        this.femaleFamilyNameConverters = new HashMap<String, Converter<String, String>>();
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinAgeYears(int minAgeYears) {
    	birthDateGenerator.setMinAgeYears(minAgeYears);
    }
    
    public void setMaxAgeYears(int maxAgeYears) {
    	birthDateGenerator.setMaxAgeYears(maxAgeYears);
    }
    
	public double getFemaleQuota() {
		return genderGen.getFemaleQuota();
	}
	
	public void setFemaleQuota(double femaleQuota) {
		this.genderGen.setFemaleQuota(femaleQuota);
	}
	
	public double getNobleQuota() {
		return maleNobilityTitleGen.getNobleQuota();
	}
	
	public void setNobleQuota(double nobleQuota) {
		maleNobilityTitleGen.setNobleQuota(nobleQuota);
		femaleNobilityTitleGen.setNobleQuota(nobleQuota);
	}
	
	public Locale getLocale() {
		return acadTitleGen.getLocale();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
    }

	public String getNesting() {
		return REGION_NESTING;
	}
	
	public String getDataset() {
		return datasetName;
	}

    public void setDataset(String datasetName) {
    	this.datasetName = datasetName;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void init(GeneratorContext context) {
		secondNameTest = registerComponent(new BooleanGenerator(0.2));
		secondNameTest.init(context);
		genderGen.init(context);
        birthDateGenerator.init(context);
        acadTitleGen = registerComponent(new AcademicTitleGenerator(locale));
        acadTitleGen.init(context);
        acadTitleGen.setLocale(locale);
        maleNobilityTitleGen = registerComponent(new NobilityTitleGenerator(Gender.MALE, locale));
        maleNobilityTitleGen.init(context);
        femaleNobilityTitleGen = registerComponent(new NobilityTitleGenerator(Gender.FEMALE, locale));
        femaleNobilityTitleGen.init(context);
        salutationProvider = new SalutationProvider(locale);
        salutationProvider.setLocale(locale);

		try {
	        initMembersWithDataset(context);
		} catch (RuntimeException e) {
			Country fallBackCountry = Country.getFallback();
			if (!fallBackCountry.getIsoCode().equals(datasetName)) {
				logger.error("Error initializing " + getClass().getSimpleName(), e);
				logger.error("Cannot generate persons for " + datasetName + ", falling back to " + fallBackCountry);
				this.datasetName = fallBackCountry.getIsoCode();
				initMembersWithDataset(context);
			} else
				throw e;
		}
        super.init(context);
    }
    
    public Person generate() throws IllegalGeneratorStateException {
	    return generateForDataset(randomDataset());
    }

	public ProductFromDataset<Person> generateWithDatasetInfo() {
	    String usedDataset = randomDataset();
	    Person person = generateForDataset(usedDataset);
        return new ProductFromDataset<Person>(person, REGION_NESTING, usedDataset);
	}

	public Person generateForDataset(String datasetToUse) {
    	assertInitialized();
        Person person = new Person(acadTitleGen.getLocale());
        person.setGender(genderGen.generate());
        GivenNameGenerator givenNameGenerator 
        	= (Gender.MALE.equals(person.getGender()) ? maleGivenNameGen : femaleGivenNameGen);
        String givenName = givenNameGenerator.generateForDataset(datasetToUse);
		person.setGivenName(givenName);
        if (secondNameTest.generate()) {
        	do {
        		person.setSecondGivenName(givenNameGenerator.generateForDataset(datasetToUse));
        	} while (person.getGivenName().equals(person.getSecondGivenName()));
        }
        String familyName = familyNameGen.generateForDataset(datasetToUse);
		if (Gender.FEMALE.equals(person.getGender()))
			familyName = getFemaleFamilyNameConverter(datasetToUse).convert(familyName);
		person.setFamilyName(familyName);
        person.setSalutation(salutationProvider.salutation(person.getGender()));
        person.setAcademicTitle(acadTitleGen.generate());
        Generator<String> nobTitleGenerator 
    		= (Gender.MALE.equals(person.getGender()) ? maleNobilityTitleGen : femaleNobilityTitleGen);
        person.setNobilityTitle(nobTitleGenerator.generate());
        person.setBirthDate(birthDateGenerator.generate());
        person.setEmail(emailGenerator.generate(givenName, familyName));
        return person;
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

    private String randomDataset() {
    	return maleGivenNameGen.generateWithDatasetInfo().dataset;
    }

	private Converter<String, String> getFemaleFamilyNameConverter(String usedDataset) {
	    Converter<String, String> result = femaleFamilyNameConverters.get(usedDataset);
	    if (result == null) {
	    	result = new FemaleFamilyNameConverter(datasetName);
	    	femaleFamilyNameConverters.put(usedDataset, result);
	    }
	    return result;
	}

	private void initMembersWithDataset(GeneratorContext context) {
	    maleGivenNameGen = registerComponent(new GivenNameGenerator(datasetName, Gender.MALE));
	    maleGivenNameGen.init(context);
	    femaleGivenNameGen = registerComponent(new GivenNameGenerator(datasetName, Gender.FEMALE));
	    femaleGivenNameGen.init(context);
	    familyNameGen = registerComponent(new FamilyNameGenerator(datasetName));
	    familyNameGen.init(context);
	    emailGenerator = new EMailAddressBuilder(datasetName);
	    emailGenerator.init(context);
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
        return getClass().getSimpleName();
    }

}
