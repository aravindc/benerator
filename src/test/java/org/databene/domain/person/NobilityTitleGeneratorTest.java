/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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
import java.util.Set;

import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {NobilityTitleGenerator}.<br/><br/>
 * Created: 11.02.2010 12:55:50
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class NobilityTitleGeneratorTest extends GeneratorTest {
	
	private static final int N = 10000;
	private static final Set<String> GERMAN_TITLES = CollectionUtil.toSet("Baron", "Graf", "Prinz", "König");
	private static final Set<String> ENGLISH_TITLES = CollectionUtil.toSet("Baron", "Count", "Prince", "King");

	@Test
	public void testLocale() {
		checkLocale(Locale.GERMANY, 0.1, GERMAN_TITLES);
		checkLocale(Locale.ENGLISH, 0.1, ENGLISH_TITLES);
	}

	@Test
	public void testNobilityQuotaZero() {
		checkLocale(Locale.GERMANY, 0., GERMAN_TITLES);
	}

	@Test
	public void testNobilityQuotaOne() {
		checkLocale(Locale.GERMANY, 1., GERMAN_TITLES);
	}

	private void checkLocale(Locale locale, double nobilityQuota, Set<String> expectedTitles) {
	    int nobCount = 0;
		NobilityTitleGenerator generator = new NobilityTitleGenerator(Gender.MALE, locale);
		generator.setNobleQuota(nobilityQuota);
		generator.init(context);
		for (int i = 0; i < N; i++) {
			String title = GeneratorUtil.generateNullable(generator);
			if (title.length() > 0) {
				assertTrue(expectedTitles.contains(title));
				nobCount++;
			}
		}
		if (nobilityQuota == 0)
			assertEquals(0, nobCount);
		else if (nobilityQuota == 1)
			assertEquals(N, nobCount);
		else
			assertEquals(generator.getNobleQuota(), ((double) nobCount) / N, 0.1);
    }
	
}
