/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import java.util.Arrays;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.commons.ArrayFormat;
import org.databene.commons.ArrayUtil;

import junit.framework.TestCase;

/**
 * Tests the {@link SeedGenerator}.<br/>
 * <br/>
 * Created at 12.07.2009 09:16:46
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class SeedGeneratorTest extends TestCase {
	
	private static final Character[] SAMPLE1 = { '0', '1', '2' };
	private static final Character[] SAMPLE2 = { '0', '1', '1' };
	private static final Character[] SAMPLE3 = { '0', '0', '1' };

	public void testEmpty() {
		SeedGenerator<Character> generator = new SeedGenerator<Character>(Character.class, 1);
		try {
			generator.generate();
			fail(IllegalGeneratorStateException.class.getSimpleName() + " expected");
		} catch (IllegalGeneratorStateException e) {
			// that's expected
		}
	}

	public void testDepth0() {
		try {
			new SeedGenerator<Character>(Character.class, 0);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// that's expected
		}
	}

	public void testDepth1() {
		checkGenerator(1);
	}

	public void testDepth3() {
		checkGenerator(3);
	}

	public void testDepth4() {
		checkGenerator(4);
	}

	public void testDepth5() {
		checkGenerator(5);
	}

	private void checkGenerator(int depth) {
	    SeedGenerator<Character> gen1 = new SeedGenerator<Character>(Character.class, depth);
        gen1.addSample(SAMPLE1);
        gen1.addSample(SAMPLE2);
        gen1.addSample(SAMPLE3);
		SeedGenerator<Character> gen = gen1;
		//gen.printState();
		for (int i = 0; i < 100; i++) {
	        Character[] sequence = gen.generate();
		    //System.out.println(ArrayFormat.format(sequence));
	        checkSequence(sequence, depth);
        }
    }

	private void checkSequence(Character[] sequence, int depth) {
	    String seqString = ArrayFormat.format(sequence);
	    assertNotNull(sequence);
	    assertTrue(sequence.length > 0);
	    for (Character c : sequence)
	    	assertTrue(c >= '0' && c <= '2');
	    if (depth > 1) {
	    	assertEquals('0', (char) sequence[0]);
		    char lastAtom = ArrayUtil.lastElement(sequence);
			assertTrue("Expected last atom to be '1' or '2': " + seqString, lastAtom == '1' || lastAtom == '2');
		    assertTrue(ArrayUtil.contains(sequence, '0'));
		    assertTrue(ArrayUtil.contains(sequence, '1'));
	    }
	    if (depth >= 4) {
	        assertTrue(
		        	Arrays.deepEquals(sequence, SAMPLE1) ||
		        	Arrays.deepEquals(sequence, SAMPLE2) ||
		        	Arrays.deepEquals(sequence, SAMPLE3)
		        );
	    }
    }
    
}
