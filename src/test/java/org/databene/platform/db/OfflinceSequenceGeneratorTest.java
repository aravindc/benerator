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

package org.databene.platform.db;

import static org.junit.Assert.*;

import org.databene.benerator.Generator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.db.hsql.HSQLUtil;
import org.junit.Test;

/**
 * Tests the OfflineSequenceGenerator.<br/><br/>
 * Created: 11.11.2009 18:50:58
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class OfflinceSequenceGeneratorTest extends GeneratorClassTest {

	public OfflinceSequenceGeneratorTest() {
	    super(OfflineSequenceGenerator.class);
    }

	@Test
	public void testLifeCycle() throws Exception {
		DBSystem db = new DBSystem("db", 
				HSQLUtil.IN_MEMORY_URL_PREFIX + "benerator", 
				HSQLUtil.DRIVER, HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
		String seq = getClass().getSimpleName();
		try {
			// create sequence and read its value
			db.createSequence(seq);
			long n = db.nextSequenceValue(seq);
			// assure that the generated values are like if they stem from the DB sequence
			Generator<Long> generator = new OfflineSequenceGenerator(db, seq);
			generator.init(context);
			for (int i = 0; i < 10; i++) {
				Long product = generator.generate();
				assertNotNull(product);
				assertEquals(++n, product.longValue());
			}
			// assure that after closing the generator, the DB sequence continues as if it had been used itself
			generator.close();
			assertEquals(n + 1, db.nextSequenceValue(seq));
		} finally {
			db.dropSequence(seq);
		}
	}
	
}
