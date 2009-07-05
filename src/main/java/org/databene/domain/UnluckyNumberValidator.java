/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.databene.commons.StringUtil;
import org.databene.domain.address.Country;

/**
 * Checks if a String contains an 'unlucky' number like 13 in western cultures or 4 in far-east cultures. 
 * See <a href="http://www.knowledgerush.com/kr/encyclopedia/Unlucky_number/">knowledgerush.com</a>,
 * <a href="http://vortex-japan.seesaa.net/article/113266312.html">vortex-japan.seesaa.net</a>,
 * <a href="http://en.wikipedia.org/wiki/Numerology">Wikipedia: Numerology</a> 
 * and <a href="http://en.wikipedia.org/wiki/Numbers_in_Chinese_culture">Wikipedia: Numbers in Chinese culture</a> <br/>
 * <br/>
 * Created at 03.07.2009 07:46:20
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class UnluckyNumberValidator implements ConstraintValidator<UnluckyNumber, String> { // TODO find a suitable package for this class
	// TODO ask Yevgen
	private static final String UNLUCKY_CN = "4,14";
	private static final String LUCKY_CN = "2,8,9,13,99,168,518,5918,814,148,1314,9413"; // TODO if a lucky number contains an unlucky number, it will not be accepted

	private static final String UNLUCKY_JP = "4,9";
	private static final String LUCKY_JP = "8";
	
	private static final String UNLUCKY_WESTERN = "13,616,666";
	private static final String LUCKY_WESTERN = "7";
	
	private static final String UNLUCKY_IT = UNLUCKY_WESTERN + ",17";
	
	private Set<String> luckyNumbers;
	private Set<String> unluckyNumbers;
	private boolean luckyNumberRequired;
	
    public UnluckyNumberValidator() {
    	this(false);
    }

    public UnluckyNumberValidator(boolean luckyNumberRequired) {
    	this.luckyNumberRequired = luckyNumberRequired;
	    Country country = Country.getDefault();
	    if (Country.CHINA.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_CN);
	    	unluckyNumbers = parseNumberSpec(UNLUCKY_CN);
	    } else if (Country.JAPAN.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_JP);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_JP);
	    } else if (Country.ITALY.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_WESTERN);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_IT);
	    } else {
	    	luckyNumbers = parseNumberSpec(LUCKY_WESTERN);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_WESTERN);
	    }
    }
    
	public boolean isLuckyNumberRequired() {
    	return luckyNumberRequired;
    }

	public void setLuckyNumberRequired(boolean luckyNumberRequired) {
    	this.luckyNumberRequired = luckyNumberRequired;
    }
	
	public void setLuckyNumbers(String luckyNumbers) {
		this.luckyNumbers = parseNumberSpec(luckyNumbers);
	}

	public void setUnluckyNumbers(String unluckyNumbers) {
		this.unluckyNumbers = parseNumberSpec(unluckyNumbers);
	}

    public void initialize(UnluckyNumber parameters) {
	    setLuckyNumberRequired(parameters.luckyNumberRequired());
    }

    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (StringUtil.isEmpty(value))
			return false;
		if (containsUnluckyNumber(value))
			return false;
		else if (luckyNumberRequired)
			return containsLuckyNumber(value);
		else
			return true;
    }

	private boolean containsLuckyNumber(String candidate) {
	    for (String test : luckyNumbers)
			if (candidate.contains(test))
				return true;
	    return false;
    }

	private boolean containsUnluckyNumber(String candidate) {
	    for (String test : unluckyNumbers)
			if (candidate.contains(test))
				return true;
	    return false;
    }

    private static Set<String> parseNumberSpec(String spec) {
	    String[] tokens = StringUtil.tokenize(spec, ',');
	    Set<String> set = new HashSet<String>();
	    for (String token: tokens)
	    	set.add(token);
	    return set;
    }

}
