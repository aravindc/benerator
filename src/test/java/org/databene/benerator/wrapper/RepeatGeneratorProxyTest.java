/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the RepeatGeneratorProxy.<br/>
 * <br/>
 * Created: 01.09.2007 11:05:04
 */
public class RepeatGeneratorProxyTest extends GeneratorClassTest {

    public RepeatGeneratorProxyTest() {
        super(RepeatGeneratorProxy.class);
    }

    public void testNonRepeating() {
        Generator<Integer> generator = new SequenceTestGenerator<Integer>(1, 2);
        generator = new RepeatGeneratorProxy<Integer>(generator, 0L, 0L);
        assertTrue(generator.available());
        assertEquals(1, (int)generator.generate());
        assertTrue(generator.available());
        assertEquals(2, (int)generator.generate());
        assertFalse(generator.available());
    }

    public void testOneRepetition() {
        Generator<Integer> generator = new SequenceTestGenerator<Integer>(1, 2);
        generator = new RepeatGeneratorProxy<Integer>(generator, 1L, 1L);
        assertTrue(generator.available());
        assertEquals(1, (int)generator.generate());
        assertTrue(generator.available());
        assertEquals(1, (int)generator.generate());
        assertTrue(generator.available());
        assertEquals(2, (int)generator.generate());
        assertTrue(generator.available());
        assertEquals(2, (int)generator.generate());
        assertFalse(generator.available());
    }
}
