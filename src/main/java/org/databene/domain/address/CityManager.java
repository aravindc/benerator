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

package org.databene.domain.address;

import org.databene.document.csv.BeanCSVWriter;
import org.databene.document.csv.CSVLineIterator;
import org.databene.model.data.Entity;
import org.databene.platform.bean.Entity2BeanConverter;
import org.databene.platform.csv.CSVEntityIterator;
import org.databene.commons.*;
import org.databene.commons.iterator.ConvertingIterator;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Reads and perists city files in CSV format (column header = property name).<br/>
 * <br/>
 * Created: 28.07.2007 15:21:12
 * @author Volker Bergmann
 */
public class CityManager {

    private static final Log logger = LogFactory.getLog(CityManager.class);

    private static Set<String> simpleLocatorWords = CollectionUtil.toSet(
            "b.", "bei", "im", "am", "ob", "zum", "sopra", "di", "in");
    private static Set<String[]> complexLocatorWords = CollectionUtil.toSet(
            new String[] {"in", "der"},
            new String[] {"an", "der"},
            new String[] {"ob", "der"}
        );
    private static Set<String> prefixes = CollectionUtil.toSet(
            "St.", "S.", "Alt", "Bad", // CH
            "Markt", "Hofamt", "Maria", "Deutsch", "Moorbad", "Bairisch", "Klein", "Hohe", "Groﬂ", // AT
            "La", "Le", "Les", // CH
            "San", "Santa", "Val", "Monte", "Ponte", "Castel", "Riva", "Villa", // CH
            "Santa Maria"); // TODO v0.6 "Santa Maria" are two words
    private static Set<String> suffixes = CollectionUtil.toSet(
            "Stadt", "Land", // CH
            "Umgebung", "Kurort", "Markt", "Neustadt", "Neudorf", "II", // AT
            "Inferiore", "Superiore"); // CH

    private static Set<String> suspectiveNames = new HashSet<String>();

    public static void readCities(Country country, String filename) throws IOException { // TODO v0.5.3 improve interface
        readCities(country, filename, new HashMap<String, String>());
    }

    public static void readCities(Country country, String filename, Map<String, String> defaults) throws IOException {
    	parseStateFile(country);
        int warnCount = parseCityFile(country, filename, defaults);
        if (warnCount > 0)
            logger.warn(warnCount + " warnings");
/*
        if (suspectiveNames.size() > 0)
            logger.info("Suspective names: " + suspectiveNames);
*/
    }

	private static void parseStateFile(Country country) {
		try {
			Iterator<State> iterator = new ConvertingIterator<Entity, State>(
					new CSVEntityIterator("org/databene/domain/address/state_" + country.getIsoCode() + ".csv", "State", ',', "UTF-8"),
					new Entity2BeanConverter<State>(State.class));
			while (iterator.hasNext()) {
				State state = iterator.next();
				country.addState(state);
			}
		} catch (FileNotFoundException e) {
			logger.warn("No state definition file found:");
		}
	}

	private static int parseCityFile(Country country, String filename, Map<String, String> defaults) throws IOException {
		CSVLineIterator iterator = new CSVLineIterator(filename, ';', "UTF-8");
        String[] header = iterator.next();
        int warnCount = 0;
        while (iterator.hasNext()) {
            String[] cells = iterator.next();
            if (cells.length == 0)
                continue;
            if (logger.isDebugEnabled())
                logger.debug(ArrayFormat.format(";", cells));
            if (cells.length == 1)
                continue;
            Map<String, String> instance = new HashMap<String, String>();
            for (int i = 0; i < cells.length; i++) {
                instance.put(header[i], cells[i]);
            }
            if (logger.isDebugEnabled())
                logger.debug(instance);

            // create/setup state
            String stateId = instance.get("state");
            State state = country.getState(stateId);
            if (state == null) {
                state = new State(stateId);
                String stateName = instance.get("state.name");
				if (stateName != null) {
					stateName = StringUtil.normalizeName(stateName);
                	state.setName(stateName);
				}
				//logger.debug(state.getId() + "," + state.getName());
                country.addState(state);
            }

            String cityIdString = instance.get("municipality");
            CityId cityId;
            if (StringUtil.isEmpty(cityIdString))
                cityIdString = instance.get("city");
            if (!StringUtil.isEmpty(cityIdString)) {
                cityId = parseCityName(cityIdString, stateId, true);
            } else {
                String cityName = instance.get("name");
                String cityNameExtension = instance.get("nameExtension");
                cityId = new CityId(cityName, cityNameExtension);
            }

            // create/setup city
            CityHelper city = (CityHelper) state.getCity(cityId);
            String zipCode = instance.get("zipCode");
            String lang = getValue(instance, "language", defaults);
            if (city == null) {
                String areaCode = getValue(instance, "areaCode", defaults);
                if (StringUtil.isEmpty(areaCode)) {
                    warnCount++;
                    logger.warn("areaCode is not provided for city: '" + cityId);
                }
                city = new CityHelper(state, cityId, CollectionUtil.toList(zipCode), areaCode);
                if (!StringUtil.isEmpty(lang))
                	city.setLanguage(LocaleUtil.getLocale(lang));
                state.addCity(cityId, city);
                city.setState(state);
            } else
                city.addZipCode(zipCode);
        }
		return warnCount;
	}

    public static void persistCities(Country country, String filename) throws IOException {
        // persist city data in standard format
        BeanCSVWriter<City> writer = new BeanCSVWriter<City>(new FileWriter(filename), ';',
                "state.country.isoCode", "state.id", "name", "nameExtension",
                "zipCode", "areaCode", "language");
        for (State state : country.getStates()) {
            for (City city : state.getCities())
                for (String zipCode : city.getZipCodes()) {
                    ((CityHelper)city).setZipCode(zipCode);
                    writer.writeElement(city);
                }
        }
        writer.close();
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private static String getValue(Map<String, String> instance, String key, Map<String, String> defaults) {
        String value = instance.get(key);
        if (value == null)
            value = defaults.get(key);
        return value;
    }
/*
    private static CityId parseCityName(String cityName, String stateId) {
        return parseCityName(cityName, stateId, false);
    }
*/
    static CityId parseCityName(String cityName, String stateId, boolean quiet) {
        // parse city id by pattern
        // Cityname = [Prefix] Name [Extension] [district] [institution]
        // Prefix = 'St.' | 'S.' | 'La' | 'Le' | 'Les' ...
        // Extension = State | '(' Text ')' | Locator
        // Locator = ('b.' | 'im' | 'am' | 'in der' | 'an der' | 'ob' | 'sopra' | 'di') (Word | Words)

        // TODO v0.6 check for double names like Frantschach-St. Gertraud
        // TODO v0.6 make use of district and institution info
    	
    	Assert.notNull(StringUtil.isEmpty(cityName), "name");

        String[] nameParts = StringUtil.tokenize(cityName, ' ');
        // check prefix
        String name = "";
        String extension = "";

//        String district = null;
//        String institution = null;
//        int warnCount = 0;

        // process prefixes
        while (nameParts.length > 1 && prefixes.contains(nameParts[0])) {
            name = append(name, nameParts[0]);
            nameParts = ArrayUtil.remove(nameParts, 0);
        }

        // check for district and institution
        for (int i = 0; i < nameParts.length; i++) {
            if (ParseUtil.isPositiveNumber(nameParts[i])) {
                //district = nameParts[i];
                if (i < nameParts.length - 1) {
                    //String[] institutionParts = ArrayUtil.copyOfRange(nameParts, i + 1, nameParts.length - i - 1);
                    //institution = ArrayFormat.format(" ", institutionParts);
                    nameParts = ArrayUtil.copyOfRange(nameParts, 0, i);
                } else
                    nameParts = ArrayUtil.remove(nameParts, nameParts.length - 1);
                break;
            }
        }

        // check extension
        if (nameParts.length > 1 && nameParts[nameParts.length - 1].equals(stateId)) {
            // state pattern
            extension = nameParts[nameParts.length - 1];
            nameParts = ArrayUtil.remove(nameParts, nameParts.length - 1);
        } else {
            // check for '(' ... ')'
            int bracketStart = -1;
            for (int i = 1; i < nameParts.length; i++) {
                if (nameParts[i].charAt(0) == '(') {
                    bracketStart = i;
                    break;
                }
            }
            if (bracketStart > 0 && nameParts[nameParts.length - 1].endsWith(")")) {
                extension = ArrayFormat.format(" ", ArrayUtil.copyOfRange(nameParts, bracketStart, nameParts.length - bracketStart));
                nameParts = ArrayUtil.copyOfRange(nameParts, 0, bracketStart);
            } else if (nameParts.length >= 4) {
                // check each defined complex locator
                for (String[] locator : complexLocatorWords) {
                    int locatorStartIndex = -1;
                    boolean match = false;
                    // check through each start index
                    for (int startIndex = 1; !match && startIndex < nameParts.length - 2; startIndex++) {
                        // check each locator part from start index
                        if (nameParts.length - startIndex > locator.length) {
                            match = true;
                            for (int i = 0; i < locator.length; i++) {
                                if (!nameParts[startIndex + i].equals(locator[i]))
                                    match = false;
                            }
                        }
                        if (match)
                            locatorStartIndex = startIndex;
                    }
                    if (match) {
                        String[] locatorParts = ArrayUtil.copyOfRange(
                            nameParts,
                            locatorStartIndex,
                            nameParts.length - locatorStartIndex);
                        extension = ArrayFormat.format(" ", locatorParts);
                        nameParts = ArrayUtil.copyOfRange(nameParts, 0, locatorStartIndex);
                        break;
                    }
                }
            }
            if (nameParts.length >= 3) {
                // check for simple locator
                for (int startIndex = 1; startIndex < nameParts.length - 1; startIndex++) {
                    if (simpleLocatorWords.contains(nameParts[startIndex])) {
                        String locationString = ArrayFormat.format(" ", ArrayUtil.copyOfRange(nameParts, startIndex, nameParts.length - startIndex));
                        extension = append(locationString, extension);
                        nameParts = ArrayUtil.copyOfRange(nameParts, 0, startIndex);
                        break;
                    }
                }
            }
        }
        // check for suffix
        if (nameParts.length > 1 && suffixes.contains(nameParts[nameParts.length - 1])) {
            extension = append(extension, nameParts[nameParts.length - 1]);
            nameParts = ArrayUtil.remove(nameParts, nameParts.length - 1);
        }

        // put together the parts
        name = append(name, ArrayFormat.format(" ", nameParts));
        if (nameParts.length != 1) {
            suspectiveNames.add(name);
            if (!quiet)
                logger.info("Double name or possible parsing error: " + name);
        }
        name = StringUtil.normalizeName(name);
        CityId cityId = new CityId(name, extension);
        // check recomposition against original name
        return cityId;
    }

    private static String append(String name, String namePart) {
        return append(name, namePart, " ");
    }

    private static String append(String name, String namePart, String separator) {
        if (StringUtil.isEmpty(name)) {
            if (StringUtil.isEmpty(namePart))
                return "";
            else
                return namePart;
        } else {
            if (StringUtil.isEmpty(namePart))
                return name;
            else
                return name + separator + namePart;
        }
    }

    public static class CityHelper extends City {

        private String zipCode;

        public CityHelper(State state, CityId cityId, List<String> zipCodes, String areaCode) {
            super(state, cityId.getName(), cityId.getNameExtension(), zipCodes, areaCode);
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }

}
