/*
 * (c) Copyright 2013 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link AcademicTitleGenerator}.<br/><br/>
 * Created: 17.03.2013 18:47:40
 * @since 0.8.2
 * @author Volker Bergmann
 */
public class AcademicTitleGeneratorTest {
	
	Set<String> GERMAN_TITLES  = CollectionUtil.toSet("Dr.", "Prof. Dr.");
	
	@Test
	public void test() {
		AcademicTitleGenerator gen = new AcademicTitleGenerator(Locale.GERMANY);
		gen.init(new DefaultBeneratorContext());
		int titleUsedCount = 0;
		int n = 1000;
		for (int i = 0; i < n; i++) {
			String title = gen.generate(new ProductWrapper<String>()).unwrap();
			if (title != null) {
				titleUsedCount++;
				assertTrue(GERMAN_TITLES.contains(title));
			}
		}
		assertTrue(titleUsedCount > 0);
		assertTrue(titleUsedCount < n / 2);
	}
	
}
