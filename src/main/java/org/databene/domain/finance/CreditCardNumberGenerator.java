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

package org.databene.domain.finance;

import org.databene.benerator.util.LightweightGenerator;
import org.databene.benerator.util.SimpleRandom;

/**
 * Creates credit card numbers.<br/><br/>
 * Created at 09.04.2008 12:22:12
 * @since 0.5.1
 * @author Volker Bergmann
 *
 */
public class CreditCardNumberGenerator extends LightweightGenerator<String> {
	
	public CreditCardNumberGenerator() {
		super(String.class);
	}

	public String generate() {
		char[] digits = new char[16];
		digits[0] = '4'; // VISA has 16-digits numbers
		for (int i = 1; i < 15; i++)
			digits[i] = (char) ('0' + SimpleRandom.randomInt(0, 9));
		digits[15] = '0';
		int sum = luhnSum(digits);
		digits[15] = (sum % 10 == 0 ? '0' : (char)('0' + 10 - (sum % 10)));
		return new String(digits);
	}

	private int luhnSum(char[] digits) {
		int sum = 0;
		int parity = digits.length % 2;
		for (int i = digits.length - 1; i >= 0; i--) {
			int digit = digits[i] - '0';
			if (i % 2 == parity)
				digit *= 2;
			sum += digit > 9 ? digit - 9 : digit;
		}
		return sum;
	}
	
}
