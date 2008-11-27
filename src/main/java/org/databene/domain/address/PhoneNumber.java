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

import org.databene.commons.Escalator;
import org.databene.commons.LoggerEscalator;
import org.databene.commons.NullSafeComparator;

/**
 * Represents a phone number.<br/>
 * <br/>
 * Created: 13.06.2006 07:19:26
 * @author Volker Bergmann
 */
public class PhoneNumber {
    
    private static final Escalator escalator = new LoggerEscalator();
    
    private String countryCode;
    private String areaCode;
    private String localNumber;
    
    private boolean mobile;
    
    // constructors ----------------------------------------------------------------------------------------------------

    public PhoneNumber() {
        this("", "", "");
    }

    public PhoneNumber(String countryCode, String cityCode, String localNumber) {
        this(countryCode, cityCode, localNumber, false);
    }

    public PhoneNumber(String countryCode, String cityCode, String localNumber, boolean mobile) {
        this.countryCode = countryCode;
        this.areaCode = cityCode;
        this.localNumber = localNumber;
        this.mobile = mobile;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String cityCode) {
        this.areaCode = cityCode;
    }

    /**
     * @return the area code
     * @deprecated replaced with {@link #getAreaCode()}
     */
    public String getCityCode() {
        escalator.escalate(getClass().getSimpleName() + ".getCityCode() is deprecated. " +
        		"Use getAreaCode() instead" , getClass(), null);
        return areaCode;
    }

    /**
     * @param areaCode
     * @deprecated replaced with {@link #setAreaCode(String)}
     */
    public void setCityCode(String areaCode) {
        escalator.escalate(getClass().getSimpleName() + ".setCityCode(String) is deprecated. " +
                "Use setAreaCode(String) instead" , getClass(), null);
        this.areaCode = areaCode;
    }

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    /**
     * @return the local number
     * @deprecated replaced with {@link #getLocalNumber()}
     */
    public String getLocalCode() {
        escalator.escalate(getClass().getSimpleName() + ".getLocalCode() is deprecated. " +
                "Use getLocalNumber() instead" , getClass(), null);
        return localNumber;
    }

    /**
     * @param localCode
     * @deprecated replaced with {@link #setLocalCode(String)}
     */
    public void setLocalCode(String localCode) {
        escalator.escalate(getClass().getSimpleName() + ".setLocalCode() is deprecated. " +
                "Use setLocalNumber() instead" , getClass(), null);
        this.localNumber = localCode;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
    
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    public String toString() {
        return "+" + countryCode + '-' + areaCode + '-' + localNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((areaCode == null) ? 0 : areaCode.hashCode());
        result = prime * result
                + ((countryCode == null) ? 0 : countryCode.hashCode());
        result = prime * result
                + ((localNumber == null) ? 0 : localNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PhoneNumber that = (PhoneNumber) obj;
        if (!this.areaCode.equals(that.areaCode))
            return false;
        if (!NullSafeComparator.equals(this.countryCode, that.countryCode))
            return false;
        return (this.localNumber.equals(that.localNumber));
    }
    
}
