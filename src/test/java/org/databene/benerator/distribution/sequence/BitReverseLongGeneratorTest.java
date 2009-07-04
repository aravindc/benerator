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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.distribution.sequence.BitReverseLongGenerator;

/**
 * Tests the BitReverseLongGenerator.<br/>
 * <br/>
 * Created: 13.11.2007 15:51:32
 */
public class BitReverseLongGeneratorTest extends GeneratorClassTest {

    public BitReverseLongGeneratorTest() {
        super(BitReverseLongGenerator.class);
    }

    public void testInstantiation() throws Exception {
        new BitReverseLongGenerator(0, 10);
    }

    public void testBasic() throws Exception {
        expectGeneratedSequence(new BitReverseLongGenerator(0, 3),  0L,  2L,  1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseLongGenerator(0, 4),  0L,  4L,  2L, 1L, 3L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseLongGenerator(0, 7),  0L,  4L,  2L, 6L, 1L, 5L, 3L, 7L).withCeasedAvailability();
    }

    public void testShifted() throws Exception {
        expectGeneratedSequence(new BitReverseLongGenerator( 1,  4),  1L,  3L,  2L,  4L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseLongGenerator( 1,  5),  1L,  5L,  3L,  2L, 4L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseLongGenerator(-1,  6), -1L,  3L,  1L,  5L, 0L, 4L, 2L, 6L).withCeasedAvailability();
        expectGeneratedSequence(new BitReverseLongGenerator(-4, -1), -4L, -2L, -3L, -1L).withCeasedAvailability();
    }

    public void testScaled() throws Exception {
        expectGeneratedSequence(new BitReverseLongGenerator( 2,  8, 2),  2L,  6L,  4L,  8L).withCeasedAvailability();
    }

    public void testScaledAndShifted() throws Exception {
        expectGeneratedSequence(new BitReverseLongGenerator( 1,  7, 2),  1L,  5L,  3L,  7L).withCeasedAvailability();
    }

    public void testReset() throws Exception {
        expectGeneratedSequence(new BitReverseLongGenerator( 1,  4),  1L,  3L,  2L).withContinuedAvailability();
    }

}
