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

import org.databene.benerator.distribution.sequence.RandomIntegerGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequenceGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.Generator;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link AlternativeGenerator}.<br/>
 * <br/>
 * Created: 11.10.2006 23:10:34
 * @since 0.1
 * @author Volker Bergmann
 */
public class AlternativeGeneratorTest extends GeneratorTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testNonUnique() {
        Generator<Integer> source1 = new RandomIntegerGenerator(-2, -1);
        Generator<Integer> source2 = new RandomIntegerGenerator(1, 2);
        AlternativeGenerator<Integer> generator = new AlternativeGenerator<Integer>(Integer.class, source1, source2);
        generator.init(context);
        for (int i = 0; i < 100; i++) {
			int product = GeneratorUtil.generateNonNull(generator);
            assertTrue((-2 <= product && product <= -1) || (1 <= product && product <= 2));
        }
    }
    
    @Test
    public void testUniqueOneShotAlternatives() {
        expectUniquelyGeneratedSet(initialize(generator(0)), 0).withCeasedAvailability();
        expectUniquelyGeneratedSet(initialize(generator(0, 1, 2)), 0, 1, 2).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUniqueMultiAlternatives() {
        Generator<Integer>[] gens = new Generator[2];
        gens[0] = new NShotGeneratorProxy<Integer>(new ConstantGenerator<Integer>(2), 1);
        gens[1] = generator(0, 1);
        Generator<Integer> generator = new AlternativeGenerator<Integer>(Integer.class, gens);
        generator.init(context);
        expectUniquelyGeneratedSet(generator, 0, 1, 2).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUniqueManyAlternatives() {
        Generator<Integer>[] gens = new Generator[2];
        gens[0] = new SequenceGenerator<Integer>(Integer.class, 0, 2, 4, 6, 8);
        gens[1] = new SequenceGenerator<Integer>(Integer.class, 1, 3, 5, 7, 9);
        Generator<Integer> generator = new AlternativeGenerator<Integer>(Integer.class, gens);
        generator.init(context);
        expectUniqueGenerations(generator, 10).withCeasedAvailability();
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Generator<Integer> generator(int ... values) {
        Generator<Integer>[] gens = new Generator[values.length];
        for (int i = 0; i < values.length; i++)
            gens[i] = new NShotGeneratorProxy<Integer>(new ConstantGenerator<Integer>(values[i]), 1);
        AlternativeGenerator<Integer> result = new AlternativeGenerator<Integer>(Integer.class, gens);
		return result;
    }
    

}
