/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.person;

import static org.junit.Assert.*;

import java.util.Locale;

import org.databene.commons.Locales;
import org.junit.Test;

/**
 * Tests the {@link PersonFormatter}.<br/><br/>
 * Created: 22.02.2010 13:50:13
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class PersonFormatterTest {
	
	@Test
	public void testWestern() {
		check("Herr Urs Züggeli", new Locale("de", "CH", "ZU"), "Herr", "Urs", "Züggeli");
		check("Herr Urs Züggeli", new Locale("de"), "Herr", "Urs", "Züggeli");
	}

	@Test
	public void testEastern() {
		check("Lee Bruce", Locales.CHINESE, null, "Bruce", "Lee");
	}

	private void check(String expected, Locale locale, String salutation, String givenName, String familyName) {
		Person person = new Person(locale);
		person.setSalutation(salutation);
		person.setGivenName(givenName);
		person.setFamilyName(familyName);
		String actual = PersonFormatter.getInstance(locale).format(person);
		assertEquals(expected, actual);
	}
	
}
