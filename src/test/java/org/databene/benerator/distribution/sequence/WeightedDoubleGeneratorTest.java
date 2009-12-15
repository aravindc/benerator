/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.distribution.WeightedDoubleGenerator;
import org.databene.benerator.distribution.function.ConstantFunction;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.CollectionUtil;
import org.junit.Test;

/**
 * Tests the {@link WeightedDoubleGenerator}.<br/><br/>
 * Created: 18.06.2006 15:04:17
 * @since 0.1
 * @author Volker Bergmann
 */
public class WeightedDoubleGeneratorTest extends GeneratorClassTest {

    public WeightedDoubleGeneratorTest() {
        super(WeightedDoubleGenerator.class);
    }

    @Test
    public void testSingleValueGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
                new WeightedDoubleGenerator( 0,  0, 1, new ConstantFunction(1)), 300, CollectionUtil.toSet(0.));
        checkProductSet(
                new WeightedDoubleGenerator( 1,  1, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(1.));
        checkProductSet(
                new WeightedDoubleGenerator(-1, -1, 1, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1.));
    }

    @Test
    public void testDiscreteRangeGeneration() throws IllegalGeneratorStateException {
        checkProductSet(
                new WeightedDoubleGenerator( -1,  0, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1., -0.5, 0.));
        checkProductSet(
                new WeightedDoubleGenerator(-1, 1, 0.5, new ConstantFunction(1)), 300, CollectionUtil.toSet(-1., -0.5, 0., 0.5, 1.));
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testNegativePrecision() throws IllegalGeneratorStateException {
        new WeightedDoubleGenerator( 0,  1, -1, new ConstantFunction(1)).validate(); // negative precision
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testZeroPrecision() throws IllegalGeneratorStateException {
        new WeightedDoubleGenerator( 0,  1,  0, new ConstantFunction(1)).validate(); // precision == 0
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testInvalidRange() throws IllegalGeneratorStateException {
    	new WeightedDoubleGenerator( 2,  1,  1, new ConstantFunction(1)).validate(); // min > max
    }
    
}
