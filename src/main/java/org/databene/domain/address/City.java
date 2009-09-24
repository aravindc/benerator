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

import java.util.*;

import org.databene.commons.ArrayUtil;
import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.NullSafeComparator;
import org.databene.commons.StringUtil;

/**
 * Represents a city.<br/>
 * <br/>
 * Created: 11.06.2006 08:19:23
 * @since 0.1
 * @author Volker Bergmann
 */
public class City {
	
	private static final Escalator escalator = new LoggerEscalator();
	
    private String name;
    private String nameExtension;
    private String[] postalCodes;
    private String areaCode;
    private State state;
    private Locale language;
    private int population;

    public City(State state, String name, String addition, String[] postalCodes, String areaCode) {
        if (areaCode == null)
            throw new IllegalArgumentException("Area Code is null for " + name);
        this.state = state;
        this.name = name;
        this.nameExtension = addition;
        this.postalCodes = (postalCodes != null ? postalCodes : new String[0]);
        this.areaCode = areaCode;
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }

    public String[] getPostalCodes() {
        return postalCodes;
    }

    public void setPostalCodes(String[] postalCodes) {
        this.postalCodes = postalCodes;
    }

    public void addPostalCode(String postalCode) {
        postalCodes = ArrayUtil.append(postalCodes, postalCode);
    }

    /** @deprecated use property postalCodes */
    @Deprecated
    public String[] getZipCodes() {
    	escalator.escalate("property City.zipCode is deprecated, use City.postalCode instead", City.class, "Invoked getZipCodes()");
        return getPostalCodes();
    }

    /** @deprecated use property postalCodes */
    @Deprecated
    public void setZipCodes(String[] zipCodes) {
    	escalator.escalate("property City.zipCode is deprecated, use City.postalCode instead", City.class, "Invoked setZipCodes()");
        this.postalCodes = zipCodes;
    }

    /** @deprecated use property postalCodes */
    @Deprecated
    public void addZipCode(String zipCode) {
    	escalator.escalate("property City.zipCode is deprecated, use City.postalCode instead", City.class, "Invoked addZipCode()");
        postalCodes = ArrayUtil.append(postalCodes, zipCode);
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String phoneCode) {
        this.areaCode = phoneCode;
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

    public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
    public String toString() {
        return name + (StringUtil.isEmpty(nameExtension) ? "" : (Character.isLetter(nameExtension.charAt(0)) ? " " : "") + nameExtension);
    }

    @Override
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

    @Override
    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 29 * result + NullSafeComparator.hashCode(nameExtension);
        result = 29 * result + NullSafeComparator.hashCode(state);
        return result;
    }
}
