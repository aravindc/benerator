/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.us;

import org.databene.benerator.test.GeneratorClassTest;
import org.databene.domain.us.SSNGenerator;
import org.databene.domain.us.SSNValidator;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link SSNGenerator}.<br/>
 * <br/>
 * Created at 17.11.2008 07:49:03
 * @since 0.5.6
 * @author Volker Bergmann
 */

public class SSNGenerator_USTest extends GeneratorClassTest {

	public SSNGenerator_USTest() {
		super(SSNGenerator.class);
	}

	@Test
	public void testDefault() {
		SSNGenerator generator = new SSNGenerator();
		assertEquals(String.class, generator.getGeneratedType());
		SSNValidator validator = new SSNValidator();
		for (int i = 0; i < 1000; i++) {
			String ssn = generator.generate();
			assertTrue("Invalid SSN: " + ssn, validator.isValid(ssn, null));
		}
	}

}
