/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import java.util.*;

import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.StringUtil;

/**
 * Represents a city.<br/>
 * <br/>
 * Created: 11.06.2006 08:19:23
 * @author Volker Bergmann
 */
public class City {
	
	private static final Escalator escalator = new LoggerEscalator();

    private String name;
    private String nameExtension;
    private SortedSet<String> zipCodes;
    private String areaCode;
    private State state;
    private int inhabitants;
    private Locale language;

    public City(State state, String name, String addition, Collection<String> zipCodes, String areaCode) {
        if (areaCode == null)
            throw new IllegalArgumentException("Area Code is null for " + name);
        this.state = state;
        this.name = name;
        this.nameExtension = addition;
        this.zipCodes = new TreeSet<String>(zipCodes);
        this.areaCode = areaCode;
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }

    public Collection<String> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(Collection<String> zipCodes) {
        this.zipCodes.clear();
        this.zipCodes.addAll(zipCodes);
    }

    public void addZipCode(String zipCode) {
        zipCodes.add(zipCode);
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String phoneCode) {
        this.areaCode = phoneCode;
    }

    @Deprecated
    public String getPhoneCode() {
    	escalator.escalate("The 'phoneCode' property is deprecated, please use 'areaCode' instead", City.class, "Called setPhoneCode()");
        return getAreaCode();
    }

    @Deprecated
    public void setPhoneCode(String areaCode) {
    	escalator.escalate("The 'phoneCode' property is deprecated, please use 'areaCode' instead", City.class, "Called getPhoneCode()");
        setAreaCode(areaCode);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public int getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(int inhabitants) {
        this.inhabitants = inhabitants;
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return name + (StringUtil.isEmpty(nameExtension) ? "" : (Character.isLetter(nameExtension.charAt(0)) ? " " : "") + nameExtension);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final City that = (City) o;
        if (!this.name.equals(that.name))
        	return false;
        if (!NullSafeComparator.equals(this.nameExtension, that.nameExtension))
        	return false;
        return NullSafeComparator.equals(this.state, that.state);
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + NullSafeComparator.hashCode(nameExtension);
        result = 29 * result + NullSafeComparator.hashCode(state);
        return result;
    }
}
