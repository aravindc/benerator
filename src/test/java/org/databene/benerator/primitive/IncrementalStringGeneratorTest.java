/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link IncrementalStringGenerator}.<br/><br/>
 * Created: 02.08.2011 10:48:41
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class IncrementalStringGeneratorTest extends GeneratorTest {

	@Test
	public void testGranularity1() {
		IncrementalStringGenerator generator = new IncrementalStringGenerator(CollectionUtil.toSet('A', 'B'), 1, 3, 1);
		initialize(generator);
		expectGeneratedSequence(generator, 
				"A", "B",  
				"AA", "AB", "BA", "BB", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
	@Test
	public void testGranularity2() {
		IncrementalStringGenerator generator = new IncrementalStringGenerator(CollectionUtil.toSet('A', 'B'), 1, 3, 2);
		initialize(generator);
		expectGeneratedSequence(generator, 
				"A", "B", 
				"AAA", "AAB", "ABA", "ABB", "BAA", "BAB", "BBA", "BBB");
	}
	
}
