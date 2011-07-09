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

package org.databene.benerator.composite;

import org.databene.benerator.NullableSequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.ArrayUtil;
import org.junit.Test;

/**
 * Tests the {@link UniqueArrayGenerator}.<br/><br/>
 * Created: 16.05.2010 11:00:19
 * @since 0.6.2
 * @author Volker Bergmann
 */
public class UniqueArrayGeneratorTest extends GeneratorTest {

	@SuppressWarnings("unchecked")
    @Test
	public void testNotNull() {
		NullableSequenceTestGenerator<Integer> gen0 = new NullableSequenceTestGenerator<Integer>(1, 2);
		NullableSequenceTestGenerator<Integer> gen1 = new NullableSequenceTestGenerator<Integer>(3, 4);
		UniqueArrayGenerator<Integer> generator = new UniqueArrayGenerator<Integer>(Integer.class, ArrayUtil.toArray(gen0, gen1));
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { 1, 3 },
			new Integer[] { 1, 4 },
			new Integer[] { 2, 3 },
			new Integer[] { 2, 4 }
		);
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void testNull() {
		NullableSequenceTestGenerator<Integer> gen0 = new NullableSequenceTestGenerator<Integer>(null, 1);
		NullableSequenceTestGenerator<Integer> gen1 = new NullableSequenceTestGenerator<Integer>(null, 2);
		UniqueArrayGenerator<Integer> generator = new UniqueArrayGenerator<Integer>(Integer.class, ArrayUtil.toArray(gen0, gen1));
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { null, null },
			new Integer[] { null,    2 },
			new Integer[] {    1, null },
			new Integer[] {    1,    2 }
		);
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void testThreeDigits() {
		NullableSequenceTestGenerator<Integer> gen0 = new NullableSequenceTestGenerator<Integer>(1, 2);
		NullableSequenceTestGenerator<Integer> gen1 = new NullableSequenceTestGenerator<Integer>(3, 4);
		NullableSequenceTestGenerator<Integer> gen2 = new NullableSequenceTestGenerator<Integer>(5, 6);
		UniqueArrayGenerator<Integer> generator = new UniqueArrayGenerator<Integer>(Integer.class, ArrayUtil.toArray(gen0, gen1, gen2));
		generator.init(context);
		expectGeneratedSequence(generator, 
			new Integer[] { 1, 3, 5 },
			new Integer[] { 1, 3, 6 },
			new Integer[] { 1, 4, 5 },
			new Integer[] { 1, 4, 6 },
			new Integer[] { 2, 3, 5 },
			new Integer[] { 2, 3, 6 },
			new Integer[] { 2, 4, 5 },
			new Integer[] { 2, 4, 6 }
		);
	}
	
}
