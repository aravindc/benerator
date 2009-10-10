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

package org.databene.domain.person;

import org.junit.Test;
import static junit.framework.Assert.*;

import java.util.Locale;

/**
 * Tests the {@link SalutationProvider}.<br/><br/>
 * Created: 09.06.2006 22:14:08
 * @since 0.1
 * @author Volker Bergmann
 */
public class SalutationProviderTest {

	@Test
    public void testWesternLocales() {
        check(Locale.GERMAN,  "Frau", "Herr");
        check(Locale.FRENCH,  "Mme",  "M.");
        check(Locale.ENGLISH, "Mrs.", "Mr.");
    }
    
	@Test
    public void testCyrillicLocales() {
        check(new Locale("ru"), "Госпожа", "Господин");
        check(new Locale("cs"), "Paní", "Pan");
    }

	@Test
    public void testFallback() {
        check(new Locale("xx"), "Mrs.", "Mr.");
    }

    private void check(Locale locale, String femaleSalutation, String maleSalutation) {
        SalutationProvider provider = new SalutationProvider(locale);
        assertEquals(femaleSalutation, provider.salutation(Gender.FEMALE));
        assertEquals(maleSalutation, provider.salutation(Gender.MALE));
    }
    
}
