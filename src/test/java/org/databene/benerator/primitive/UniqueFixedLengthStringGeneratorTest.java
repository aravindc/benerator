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

package org.databene.benerator.primitive;

import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the UniqueFixedLengthStringGenerator.<br/>
 * <br/>
 * Created: 15.11.2007 14:30:28
 */
public class UniqueFixedLengthStringGeneratorTest extends GeneratorClassTest {

    public UniqueFixedLengthStringGeneratorTest() {
        super(UniqueFixedLengthStringGenerator.class);
    }

    public void testZeroLength() {
        expectGeneratedSequence(new UniqueFixedLengthStringGenerator(0), "").withCeasedAvailability();
    }

    public void testConstantDigit() {
        expectGeneratedSequence(new UniqueFixedLengthStringGenerator(1, '0'), "0").withCeasedAvailability();
    }

    public void testOneBinaryDigit() {
        expectGeneratedSet(new UniqueFixedLengthStringGenerator(1, '0', '1'), "0", "1").withCeasedAvailability();
        expectUniqueProducts(new UniqueFixedLengthStringGenerator(1, '0', '1'), 2).withCeasedAvailability();
    }

    public void testTwoBinaryDigits() {
        expectGeneratedSet(new UniqueFixedLengthStringGenerator(2, '0', '1'), "00", "01", "10", "11").withCeasedAvailability();
        expectUniqueProducts(new UniqueFixedLengthStringGenerator(2, '0', '1'), 4).withCeasedAvailability();
    }

    public void testTwoAlphaDigits() {
        expectGeneratedSet(new UniqueFixedLengthStringGenerator(2, 'A', 'O'), "AA", "AO", "OA", "OO").withCeasedAvailability();
        expectUniqueProducts(new UniqueFixedLengthStringGenerator(2, 'A', 'O'), 4).withCeasedAvailability();
        expectUniqueProducts(new UniqueFixedLengthStringGenerator(2, 'A', 'B', 'C'), 9).withCeasedAvailability();
    }

    public void testLongString() {
        expectUniqueProducts(new UniqueFixedLengthStringGenerator(4, 'A', 'E', 'I', 'O', 'U'), 625).withCeasedAvailability();
    }

    public void testMany() {
        UniqueFixedLengthStringGenerator generator = new UniqueFixedLengthStringGenerator(7, '0', '9', '2', '6', '4', '5', '3', '7', '8', '1');
        expectUniqueProducts(generator, 1000).withContinuedAvailability();
    }

}
