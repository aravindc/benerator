/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.distribution.sequence.BitReverseNaturalNumberGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the BitReverseNaturalNumberGeneratorTest<br/><br/>
 * Created: 13.11.2007 13:10:39
 * @author Volker Bergmann
 */
public class BitReverseNaturalNumberGeneratorTest extends GeneratorClassTest {

    public BitReverseNaturalNumberGeneratorTest() {
        super(BitReverseNaturalNumberGenerator.class);
    }

    @Test
    public void testSequences() throws Exception {
        expectGeneratedSequence(initialize(new BitReverseNaturalNumberGenerator(3)),  0L,  2L,  1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(initialize(new BitReverseNaturalNumberGenerator(4)),  0L,  4L,  2L, 1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(initialize(new BitReverseNaturalNumberGenerator(7)),  0L,  4L,  2L, 6L, 1L, 5L, 3L, 7L).withCeasedAvailability();
    }

    @Test
    public void testCoverage() throws Exception {
    	expectUniquelyGeneratedSet(initialize(new BitReverseNaturalNumberGenerator(2)), 0L, 1L, 2L).withCeasedAvailability();
    	expectUniquelyGeneratedSet(initialize(new BitReverseNaturalNumberGenerator(9)), 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
                .withCeasedAvailability();
    }

}
