/*
 * (c) Copyright 2006-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.*;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Tests the {@link SkipGeneratorProxy}.<br/><br/>
 * Created: 11.10.2006 23:10:34
 * @since 0.1
 * @author Volker Bergmann
 */
public class SkipGeneratorProxyTest extends GeneratorTest {

    @Test
    public void testSkip() {
        SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
        SkipGeneratorProxy<Integer> generator = new SkipGeneratorProxy<Integer>(source, 1, 2);
        generator.init(context);
        int value = GeneratorUtil.generateNonNull(generator);
        assertTrue(value == 1 || value == 2);
    }

    @Test
    public void testLimit1() {
        SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2, 3);
        SkipGeneratorProxy<Integer> generator = new SkipGeneratorProxy<Integer>(
        		source, 1, 1, SequenceManager.RANDOM_SEQUENCE, 1);
        generator.init(context);
        Integer value = GeneratorUtil.generateNonNull(generator);
        assertNotNull(value);
        assertTrue(value == 1);
        assertUnavailable(generator);
    }

    @Test
    public void testNonRepetitive() {
        SequenceTestGenerator<Integer> source = new SequenceTestGenerator<Integer>(1, 2);
        SkipGeneratorProxy<Integer> generator = new SkipGeneratorProxy<Integer>(source);
        generator.init(context);
        assertEquals(1, (int) GeneratorUtil.generateNonNull(generator));
        assertEquals(2, (int) GeneratorUtil.generateNonNull(generator));
        assertUnavailable(generator);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testMissingSource() {
        createAndInit(null, 1, 1);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testNegativeMinIncrement() {
        Generator<Integer> source = new ConstantTestGenerator<Integer>(1);
        createAndInit(source, -1, 1);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testMaxIncrementSmallerThanMinIncrement() {
        Generator<Integer> source = new ConstantTestGenerator<Integer>(1);
        createAndInit(source, 1, -1);
    }

    private void createAndInit(Generator<Integer> source, int minIncrement, int maxIncrement) {
        Generator<Integer> generator = new SkipGeneratorProxy<Integer>(source, minIncrement, maxIncrement);
        generator.init(context);
    }
    
}
