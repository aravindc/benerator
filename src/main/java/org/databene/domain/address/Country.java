/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.commons.ConfigurationError;
import org.databene.commons.LocaleUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.collection.OrderedNameMap;
import org.databene.document.csv.CSVLineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.IOException;

/**
 * Represents a country and provides constants for most bigger countries.
 * Country information is read from the file org/databene/domain/address/country.csv.<br/>
 * <br/>
 * Created: 11.06.2006 08:15:37
 */
public class Country {

    private static final String DEFAULT_PHONE_CODE = "[2-9][0-9][0-9]";

	private static final String DEFAULT_MOBILE_PHONE_PATTERN = "[1-9][0-9][0-9]";

	private static final Log logger = LogFactory.getLog(Country.class);

    private static String FILE_NAME = "org/databene/domain/address/country.csv";

    private static Map<String, Country> instances = new HashMap<String, Country>(250);

    static {
        parseConfigFile();
    }

    // German speaking countries
    public static final Country GERMANY = getInstance("DE");
    public static final Country AUSTRIA = getInstance("AT");
    public static final Country SWITZERLAND = getInstance("CH");
    public static final Country LIECHTENSTEIN = getInstance("LI");

    // BeNeLux
    public static final Country BELGIUM = getInstance("BE");
    public static final Country NETHERLANDS = getInstance("NL");
    public static final Country LUXEMBURG = getInstance("LU");

    // Northern Europe
    public static final Country DENMARK = getInstance("DK");
    public static final Country FINLAND = getInstance("FI");
    public static final Country IRELAND = getInstance("IE");
    public static final Country ICELAND = getInstance("IS");
    public static final Country NORWAY = getInstance("NO");
    public static final Country SWEDEN = getInstance("SE");
    public static final Country UNITED_KINGDOM = getInstance("GB");
    public static final Country GREAT_BRITAIN = getInstance("GB");

    // Southern Europe
    public static final Country ITALY = getInstance("IT");
    public static final Country SAN_MARINO = getInstance("SM");
    public static final Country MALTA = getInstance("MT");
    public static final Country FRANCE = getInstance("FR");
    public static final Country MONACO = getInstance("MC");
    public static final Country ANDORRA = getInstance("AD");
    public static final Country SPAIN = getInstance("ES");
    public static final Country PORTUGAL = getInstance("PT");

    // South-East Europe
    public static final Country GREECE = getInstance("GR");
    public static final Country CYPRUS = getInstance("CY");
    public static final Country TURKEY = getInstance("TR");

    // Eastern Europe
    public static final Country ALBANIA = getInstance("AL");
    public static final Country BOSNIA_AND_HERZEGOVINA = getInstance("BA");
    public static final Country BULGARIA = getInstance("BG");
    public static final Country BELARUS = getInstance("BY");
    public static final Country CZECH_REPUBLIC = getInstance("CZ");
    public static final Country ESTONIA = getInstance("EE");
    public static final Country CROATIA = getInstance("HR");
    public static final Country HUNGARY = getInstance("HU");
    public static final Country LITHUANIA = getInstance("LT");
    public static final Country LATVIA = getInstance("LV");
    public static final Country POLAND = getInstance("PL");
    public static final Country ROMANIA = getInstance("RO");
    public static final Country RUSSIA = getInstance("RU");
    public static final Country SLOVENIA = getInstance("SI");
    public static final Country SLOVAKIA = getInstance("SK");
    public static final Country UKRAINE = getInstance("UA");

    // Near East
    public static final Country UNITED_ARAB_EMIRATES = getInstance("AE");
    public static final Country AFGHANISTAN = getInstance("AF");
    public static final Country BAHRAIN = getInstance("BH");
    public static final Country ISRAEL = getInstance("IL");
    public static final Country IRAN = getInstance("IR");
    public static final Country IRAQ = getInstance("IQ");
    public static final Country JORDAN = getInstance("JO");
    public static final Country KAZAKHSTAN = getInstance("KZ");
    public static final Country PAKISTAN = getInstance("PK");
    public static final Country QATAR = getInstance("QA");
    public static final Country SAUDI_ARABIA = getInstance("SA");
    
    // Africa
    public static final Country ALGERIA = getInstance("AL");
    public static final Country EGYPT = getInstance("EG");
    public static final Country GHANA = getInstance("GH");
    public static final Country KENYA = getInstance("KE");
    public static final Country SOUTH_AFRICA = getInstance("ZA");
    
    // North America
    public static final Country USA = getInstance("US");
    public static final Country US = USA;
    public static final Country CANADA = getInstance("CA");
    
    // Central America
    public static final Country BAHAMAS = getInstance("BS");
    public static final Country MEXICO = getInstance("MX");
    
    // South America
    public static final Country ARGENTINA = getInstance("AR");
    public static final Country BRAZIL = getInstance("BR");
    public static final Country CHILE = getInstance("CL");
    public static final Country ECUADOR = getInstance("EC");
    
    // Asia
    public static final Country CHINA = getInstance("CN");
    public static final Country INDONESIA = getInstance("ID");
    public static final Country INDIA = getInstance("IN");
    public static final Country JAPAN = getInstance("JP");
    public static final Country KOREA_PR = getInstance("KP");
    public static final Country KOREA_R = getInstance("KR");
    public static final Country MALAYSIA = getInstance("MY");
    public static final Country SINGAPORE = getInstance("SG");
    public static final Country THAILAND = getInstance("TH");
    public static final Country TAIWAN = getInstance("TW");
    public static final Country VIETNAM = getInstance("VN");

    // Australia
    public static final Country NEW_ZEALAND = getInstance("NZ");
    public static final Country AUSTRALIA = getInstance("AU");

    private static Country defaultCountry;

    static {
        defaultCountry = Country.getInstance(LocaleUtil.getDefaultCountryCode());
    }

    private static void parseConfigFile() {
        CSVLineIterator iterator = null;
        try {
            iterator = new CSVLineIterator(FILE_NAME, ',', true);
            logger.debug("Parsing country setup file " + FILE_NAME);
            while (iterator.hasNext()) {
                String[] cells = iterator.next();
                String isoCode = cells[0];
                String defaultLocale = (cells.length > 1 && !StringUtil.isEmpty(cells[1]) ? cells[1].trim() : "en");
                String phoneCode = (cells.length > 2 ? cells[2].trim() : null);
                String mobilCodePattern = (cells.length > 3 ? cells[3].trim() : DEFAULT_MOBILE_PHONE_PATTERN);
                String name = (cells.length > 4 ? cells[4].trim() : null);
                Country country = new Country(isoCode, defaultLocale, phoneCode, mobilCodePattern, name);
                if (logger.isDebugEnabled())
                    logger.debug("parsed " + country);
            }
        } catch (IOException e) {
            throw new ConfigurationError("Country definition file could not be processed. ", e);
        } finally {
            if (iterator != null)
                iterator.close();
        }
    }

    private String isoCode;
    private String name;
    private String phoneCode;
    private String mobileCodePattern;
    private Locale countryLocale;
    private Locale defaultLanguage;
    private Map<String, State> states;

    private Country(String isoCode, String defaultLanguage, String phoneCode, String mobilCodePattern, String name) {
        this.isoCode = isoCode;
        this.defaultLanguage = LocaleUtil.getLocale(defaultLanguage);
        this.phoneCode = phoneCode;
        this.countryLocale = new Locale(LocaleUtil.getLocale(defaultLanguage).getLanguage(), isoCode);
        this.mobileCodePattern = mobilCodePattern;
        this.states = new OrderedNameMap<State>();
        this.name = (name != null ? name : countryLocale.getDisplayCountry(Locale.US));
        instances.put(isoCode, this);
    }

    public String getIsoCode() {
        return isoCode;
    }

    /** Returns the English name */
    public String getName() {
        return name;
    }

    /** Returns the name in the user's {@link Locale} */
    public String getDisplayName() {
        return countryLocale.getDisplayCountry(Locale.getDefault());
    }

    /** Returns the name in the country's own {@link Locale} */
    public String getLocalName() {
        return countryLocale.getDisplayCountry(new Locale(defaultLanguage.getLanguage()));
    }

    public Locale getDefaultLanguage() {
    	return defaultLanguage;
    }
    
    public String getPhoneCode() {
        return phoneCode;
    }

    public String getMobileCodePattern() {
        return mobileCodePattern;
    }

    public State getState(String stateId) {
        return states.get(stateId);
    }

    public Collection<State> getStates() {
        return states.values();
    }

    public void addState(State state) {
        state.setCountry(this);
        states.put(state.getId(), state);
    }

    public static Collection<Country> getInstances() {
        return instances.values();
    }

    /**
     * Retrieves a country from the country configuration file.
     * @param isoCode the ISO code of the country to retrieve
     * @return if it is a predfined country, an instance with the configured data is returned,
     * else one with the specified ISO code and default settings, e.g. phoneCode 'UNKNOWN'.
     */
    public static Country getInstance(String isoCode) {
        Country country = instances.get(isoCode.toUpperCase());
        if (country == null)
            country = new Country(isoCode, Locale.getDefault().getLanguage(), DEFAULT_PHONE_CODE, DEFAULT_MOBILE_PHONE_PATTERN, null);
        return country;
    }

    public static Country getDefault() {
        return Country.defaultCountry;
    }

    public static void setDefault(Country country) {
        Country.defaultCountry = country;
    }

	public static Country getFallback() {
		return Country.US;
	}
	
    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
	public String toString() {
        return getName();
    }

	@Override
	public int hashCode() {
		return isoCode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Country other = (Country) obj;
		return isoCode.equals(other.isoCode);
	}

}
