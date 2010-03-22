/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.primitive;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.util.LuhnUtil;
import org.databene.benerator.wrapper.GeneratorProxy;
import org.databene.commons.StringUtil;

/**
 * Generates numbers that pass a Luhn test.<br/><br/>
 * Created: 18.10.2009 10:08:09
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class LuhnGenerator extends GeneratorProxy<String> {
	
	public LuhnGenerator() {
	    this("", 1, 10);
    }
	
	public LuhnGenerator(String prefix, int length) {
	    this(prefix, length, length);
    }

	public LuhnGenerator(String prefix, int minLength, int maxLength) {
	    super(new DigitsGenerator(minLength, maxLength, prefix));
    }

	@Override
    public String generate() throws IllegalGeneratorStateException {
		String number = super.generate();
		char checkDigit = LuhnUtil.requiredCheckDigit(number);
		if (StringUtil.lastChar(number) == checkDigit)
			return number;
		else
			return number.substring(0, number.length() - 1) + checkDigit;
    }

}
