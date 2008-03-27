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

import org.databene.benerator.GeneratorClassTest;
import org.databene.benerator.primitive.number.adapter.BigDecimalGenerator;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

/**
 * Tests the BigDecimalGenerator.<br/><br/>
 * Created: 09.10.2006 20:33:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class BigDecimalGeneratorTest extends GeneratorClassTest {

    public BigDecimalGeneratorTest() {
        super(BigDecimalGenerator.class);
    }

    public void testDefaultConstructor() {
        new BigDecimalGenerator();
    }

    public void testScale() {
        BigDecimalGenerator generator = new BigDecimalGenerator();
        checkScale(generator, new BigDecimal("1"), 0);
        checkScale(generator, new BigDecimal("0.1"), 1);
        checkScale(generator, new BigDecimal("0.5"), 1);
        checkScale(generator, new BigDecimal("0.01"), 2);
        checkScale(generator, new BigDecimal("10"), 0);
    }

    public void testUniformDistribution() {
        checkUniformDistribution("-0.02", "0.02", "0.01", 10000, 5, "-0.02", "-0.01", "0.00", "0.01", "0.02");
        checkUniformDistribution("-0.1", "0.1", "0.05", 10000, 0.1, "-0.10", "-0.05", "0.00", "0.05", "0.10");
        checkUniformDistribution("-2", "2", "2", 10000, 0.1, "-2", "0", "2");
        checkUniformDistribution("1", "5", "2", 10000, 0.1, "1", "3", "5");
        checkUniformDistribution("-5", "-1", "2", 10000, 0.1, "-5", "-3", "-1");
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkScale(BigDecimalGenerator generator, BigDecimal precision, int expectedScale) {
        generator.setPrecision(precision);
        assertEquals(expectedScale, (Object)generator.getFractionDigits());
    }

    private void checkUniformDistribution(String min, String max, String precision,
                                          int sampleCount, double tolerance, String ... expectedValuesAsString) {
        Set<BigDecimal> expectedValues = new HashSet<BigDecimal>(expectedValuesAsString.length);
        for (String s : expectedValuesAsString)
            expectedValues.add(new BigDecimal(s));
        checkEqualDistribution(
                BigDecimalGenerator.class, new BigDecimal(min), new BigDecimal(max), new BigDecimal(precision),
                sampleCount, tolerance, expectedValues);
    }

}
