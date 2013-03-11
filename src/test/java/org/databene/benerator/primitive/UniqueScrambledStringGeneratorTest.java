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

package org.databene.benerator.primitive;

import static org.junit.Assert.*;

import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link UniqueScrambledStringGenerator}.<br/><br/>
 * Created: 11.03.2013 22:03:47
 * @since 0.8.1
 * @author Volker Bergmann
 */
public class UniqueScrambledStringGeneratorTest extends GeneratorTest {
	
	@Test
	public void test() {
		for (int n = 1; n < 5; n++) {
			UniqueScrambledStringGenerator generator = new UniqueScrambledStringGenerator(CollectionUtil.toSet('0', '1'), n, n + 1);
			generator.init(new DefaultBeneratorContext());
			for (int i = 0; i < 30; i++) {
				int exp = (int) Math.round(Math.pow(2, n)) + (int) Math.round(Math.pow(2, n + 1));
				for (int c = 0; c < exp; c++) {
					assertNotNull(generator.generate());
				}
				assertNull(generator.generate());
				generator.reset();
			}
			
		}
		
	}
	
}
