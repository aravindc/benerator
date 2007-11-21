/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.region;
import org.databene.commons.ConfigurationError;
import org.databene.commons.ArrayUtil;
import org.databene.commons.OrderedMap;
import org.databene.document.csv.CSVLineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.IOException;

/**
 * Represents a country, provides constants for most bigger countries and serves as a generator for city object.
 * Country information is read from the file org/databene/domain/address/country.csv.<br/>
 * <br/>
 * Created: 11.06.2006 08:15:37
 */
public class Country {

    private static final Log logger = LogFactory.getLog(Country.class);

    private static String FILE_NAME = "org/databene/domain/address/country.csv";

    private static Map<String, Country> instances = new HashMap<String, Country>(250);

    static {
        parseConfigFile();
    }

    public static final Country GERMANY = getInstance("DE");
    public static final Country AUSTRIA = getInstance("AT");
    public static final Country SWITZERLAND = getInstance("CH");
    public static final Country LIECHTENSTEIN = getInstance("LI");

    public static final Country BELGIUM = getInstance("BE");
    public static final Country NETHERLANDS = getInstance("NL");
    public static final Country LUXEMBURG = getInstance("LU");

    public static final Country ITALY = getInstance("IT");
    public static final Country FRANCE = getInstance("FR");
    public static final Country SPAIN = getInstance("ES");
    public static final Country PORTUGAL = getInstance("PT");
    public static final Country ANDORRA = getInstance("AD");

    public static final Country GREECE = getInstance("gr");

    public static final Country SLOWENIA = getInstance("SI");
    public static final Country CZECH_REPUBLIC = getInstance("CZ");
    public static final Country HUNGARY = getInstance("HR");
    public static final Country POLAND = getInstance("PL");
    public static final Country RUSSIA = getInstance("RU");
    public static final Country ROMANIA = getInstance("RO");
    public static final Country BULGARIA = getInstance("BG");
    public static final Country CROATIA = getInstance("HR");
    public static final Country BOSNIA_AND_HERZEGOVINA = getInstance("BA");
    public static final Country TURKEY = getInstance("TR");
    public static final Country ESTONIA = getInstance("EE");
    public static final Country LITHUANIA = getInstance("LT");
    public static final Country LATVIA = getInstance("LV");

    public static final Country UNITED_KINGDOM = getInstance("UK");
    public static final Country DENMARK = getInstance("DK");
    public static final Country SWEDEN = getInstance("SE");
    public static final Country NORWAY = getInstance("NO");
    public static final Country FINLAND = getInstance("FI");
    public static final Country IRELAND = getInstance("IE");
    public static final Country ICELAND = getInstance("IS");

    public static final Country USA = getInstance("US");
    public static final Country JAPAN = getInstance("JP");

    private static Country defaultCountry;

    static {
        defaultCountry = Country.getInstance(Locale.getDefault().getCountry().toLowerCase());
    }

    private static void parseConfigFile() {
        CSVLineIterator iterator = null;
        try {
            iterator = new CSVLineIterator(FILE_NAME, ',', true);
            logger.debug("Parsing country setup file " + FILE_NAME);
            while (iterator.hasNext()) {
                String[] cells = iterator.next();
                String isoCode = cells[0];
                String phoneCode = (cells.length > 1 ? cells[1] : null);
                String[] mobilCodes = (cells.length > 2 ?
                        ArrayUtil.copyOfRange(cells, 2, cells.length - 2) : new String[] { "???" });
                Country country = new Country(isoCode, phoneCode, mobilCodes);
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
    private Region region;
    private String phoneCode;
    private String[] mobileCodes;
    private Locale countryLocale;
    private Map<String, State> states;

    private Country(String isoCode, String phoneCode, String ... mobileCodes) {
        this.isoCode = isoCode;
        this.phoneCode = phoneCode;
        this.countryLocale = new Locale("xx", isoCode);
        this.region = new BasicRegion(isoCode);
        this.mobileCodes = mobileCodes;
        this.states = new OrderedMap<String, State>();
        instances.put(isoCode, this);
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return countryLocale.getDisplayCountry(Locale.getDefault());
    }
/*
    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
    }
*/
    public String getPhoneCode() {
        return phoneCode;
    }

    public String[] getMobileCodes() {
        return mobileCodes;
    }

    public Region getRegion() {
        return region;
    }

    public State getState(String stateId) {
        return states.get(stateId);
    }

    public Collection<State> getStates() {
        return states.values();
    }

    public void addState(String id, State state) {
        state.setCountry(this);
        states.put(id, state);
    }

    public String toString() {
        return getName();
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
            country = new Country(isoCode, "UNKNOWN");
        return country;
    }

    public static Country getDefault() {
        return defaultCountry;
    }

}
