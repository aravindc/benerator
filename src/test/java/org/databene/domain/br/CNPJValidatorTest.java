/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.br;

import org.databene.commons.validator.SimpleValidatorTest;
import org.junit.Test;

/**
 * Tests the CPNJValidator.<br/><br/>
 * Created: 17.10.2009 08:24:46
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class CNPJValidatorTest extends SimpleValidatorTest<CharSequence> {

	public CNPJValidatorTest() {
	    super(new CNPJValidator());
    }

	@Test
	public void testValidNumbers() {
		assertValid("16701716000156");
	}

	@Test
	public void testIllegalNumbers() {
		assertInvalid(null);
		assertInvalid("");
		assertInvalid("0");
		assertInvalid("1234567890123456789");
	}
	
	// TODO implement and test validation of formatted CNPJ numbers

	@Test
	public void testInvalidNumbers() {
		assertInvalid("16701716000157"); // ultimate verification digit wrong
		assertInvalid("16701716000166"); // penultimate verification digit wrong
	}

}
