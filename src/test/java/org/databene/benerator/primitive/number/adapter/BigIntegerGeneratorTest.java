/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.number.adapter;

import java.math.BigInteger;
import java.util.Set;
import java.util.HashSet;

import org.databene.benerator.GeneratorClassTest;

/**
 * Created: 04.10.2006 19:27:39
 */
public class BigIntegerGeneratorTest extends GeneratorClassTest {

    public BigIntegerGeneratorTest() {
        super(BigIntegerGenerator.class);
    }

    public void testDefaultConstructor() {
        new BigIntegerGenerator();
    }

    public void testDefaults() {
        BigIntegerGenerator generator = new BigIntegerGenerator();
        assertEquals("min is expected to be " + Long.MIN_VALUE, Long.MIN_VALUE, generator.getMin().longValue());
        assertEquals("max is expected to be " + Long.MAX_VALUE, Long.MAX_VALUE, generator.getMax().longValue());
        assertEquals("precision is expected to be 1", 1L, generator.getPrecision().longValue());
        assertEquals("variation1 is expected to be 1", 1L, generator.getVariation1().longValue());
        assertEquals("variation2 is expected to be 1", 1L, generator.getVariation2().longValue());
    }

    public void testUniformDistribution() {
        checkUniformDistribution("-2",  "2", "1", 10000, 0.1, "-2", "-1", "0", "1", "2");
        checkUniformDistribution("-2",  "2", "2", 10000, 0.1, "-2",  "0", "2");
        checkUniformDistribution("-2", "-1", "1", 10000, 0.1, "-2", "-1");
        checkUniformDistribution( "3",  "5", "2", 10000, 0.1,  "3", "5");
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkUniformDistribution(String min, String max, String precision,
                                          int sampleCount, double tolerance, String ... expectedValuesAsString) {
        Set<BigInteger> expectedValues = new HashSet<BigInteger>(expectedValuesAsString.length);
        for (String s : expectedValuesAsString)
            expectedValues.add(new BigInteger(s));
        checkEqualDistribution(
                BigIntegerGenerator.class, new BigInteger(min), new BigInteger(max), new BigInteger(precision),
                sampleCount, tolerance, expectedValues);
    }

}
