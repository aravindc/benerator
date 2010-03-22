/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the UniqueFixedLengthStringGenerator.<br/>
 * <br/>
 * Created: 15.11.2007 14:30:28
 * @author Volker Bergmann
 */
public class UniqueFixedLengthStringGeneratorTest extends GeneratorClassTest {

    public UniqueFixedLengthStringGeneratorTest() {
        super(UniqueFixedLengthStringGenerator.class);
    }

    @Test
    public void testZeroLength() {
        expectGeneratedSequence(createAndInit(0), "").withCeasedAvailability();
    }

    @Test
    public void testConstantDigit() {
        expectGeneratedSequence(createAndInit(1, '0'), "0").withCeasedAvailability();
    }

    @Test
    public void testOneBinaryDigit() {
        expectGeneratedSet(createAndInit(1, '0', '1'), "0", "1").withCeasedAvailability();
        expectUniqueProducts(createAndInit(1, '0', '1'), 2).withCeasedAvailability();
    }

    @Test
    public void testTwoBinaryDigits() {
        expectGeneratedSet(createAndInit(2, '0', '1'), "00", "01", "10", "11").withCeasedAvailability();
        expectUniqueProducts(createAndInit(2, '0', '1'), 4).withCeasedAvailability();
    }

    @Test
    public void testTwoAlphaDigits() {
        expectGeneratedSet(createAndInit(2, 'A', 'O'), "AA", "AO", "OA", "OO").withCeasedAvailability();
        expectUniqueProducts(createAndInit(2, 'A', 'O'), 4).withCeasedAvailability();
        expectUniqueProducts(createAndInit(2, 'A', 'B', 'C'), 9).withCeasedAvailability();
    }

    @Test
    public void testLongString() {
        expectUniqueProducts(createAndInit(4, 'A', 'E', 'I', 'O', 'U'), 625).withCeasedAvailability();
    }

    @Test
    public void testMany() {
        UniqueFixedLengthStringGenerator generator = createAndInit(7, '0', '9', '2', '6', '4', '5', '3', '7', '8', '1');
        expectUniqueProducts(generator, 1000).withContinuedAvailability();
    }

    private UniqueFixedLengthStringGenerator createAndInit(int length, char... chars) {
    	return initialize(new UniqueFixedLengthStringGenerator(length, chars));
    }
    
}
