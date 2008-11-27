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

import java.util.Arrays;
import java.util.Iterator;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.HeavyweightIterable;
import org.databene.commons.HeavyweightIterator;
import org.databene.commons.iterator.DefaultTypedIterable;
import org.databene.commons.iterator.HeavyweightIterableAdapter;

/**
 * Tests the IteratingGenerator.<br/>
 * <br/>
 * Created: 01.09.2007 17:22:03
 */
public class IteratingGeneratorTest extends GeneratorClassTest {

    public IteratingGeneratorTest() {
        super(IteratingGenerator.class);
    }

    public void testBehaviour() {
        HeavyweightIterableAdapter iterable = new HeavyweightIterableAdapter(Arrays.asList(1, 2));
		DefaultTypedIterable<Integer> hwIterable = new DefaultTypedIterable<Integer>(Integer.class, iterable);
		Generator<Integer> gen = new IteratingGenerator<Integer>(hwIterable);
        assertTrue(gen.available());
        assertEquals(1, (int)gen.generate());
        assertTrue(gen.available());
        assertEquals(2, (int)gen.generate());
        assertFalse(gen.available());
        gen.reset();
        assertTrue(gen.available());
        assertEquals(1, (int)gen.generate());
        assertTrue(gen.available());
        assertEquals(2, (int)gen.generate());
        assertFalse(gen.available());
    }
    
}
