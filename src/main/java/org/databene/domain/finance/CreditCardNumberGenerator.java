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

package org.databene.domain.finance;

import org.databene.benerator.primitive.LightweightStringGenerator;
import org.databene.benerator.util.RandomUtil;

/**
 * Creates credit card numbers.<br/><br/>
 * Created at 09.04.2008 12:22:12
 * @since 0.5.1
 * @author Volker Bergmann
 *
 */
public class CreditCardNumberGenerator extends LightweightStringGenerator { // TODO rename to LuhnNumberGenerator
	
	public String generate() {
		// VISA has 16-digits numbers, starting with '4'
		int length = 16;
		StringBuilder builder = new StringBuilder(16).append('4'); 
		for (int i = 1; i < length - 1; i++)
			builder.append((char) (RandomUtil.randomInt('0', '9')));
		builder.append('0');
		char checkDigit = RandomUtil.requiredLuhnDigit(builder);
		builder.setCharAt(length - 1, checkDigit);
		return builder.toString();
	}

}
