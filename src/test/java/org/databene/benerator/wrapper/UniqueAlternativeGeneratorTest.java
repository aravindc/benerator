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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequenceGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the UniqueAlternativeGenerator.<br/>
 * <br/>
 * Created: 18.11.2007 07:19:21
 * @author Volker Bergmann
 */
public class UniqueAlternativeGeneratorTest extends GeneratorClassTest {

    public UniqueAlternativeGeneratorTest() {
        super(UniqueAlternativeGenerator.class);
    }

    @Test
    public void testOneShotAlternatives() {
        expectUniqueFromSet(initialize(generator(0)), 0).withCeasedAvailability();
        expectUniqueFromSet(initialize(generator(0, 1, 2)), 0, 1, 2).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultiAlternatives() {
        Generator<Integer>[] gens = new Generator[2];
        gens[0] = new NShotGeneratorProxy<Integer>(new ConstantGenerator<Integer>(2), 1);
        gens[1] = generator(0, 1);
        Generator<Integer> generator = new UniqueAlternativeGenerator<Integer>(Integer.class, gens);
        generator.init(context);
        expectUniqueFromSet(generator, 0, 1, 2).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testManyAlternatives() {
        Generator<Integer>[] gens = new Generator[2];
        gens[0] = new SequenceGenerator<Integer>(Integer.class, 0, 2, 4, 6, 8);
        gens[1] = new SequenceGenerator<Integer>(Integer.class, 1, 3, 5, 7, 9);
        Generator<Integer> generator = new UniqueAlternativeGenerator<Integer>(Integer.class, gens);
        generator.init(context);
        expectUniqueGenerations(generator, 10).withCeasedAvailability();
    }
    
    // helpers ---------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Generator<Integer> generator(int ... values) {
        Generator<Integer>[] gens = new Generator[values.length];
        for (int i = 0; i < values.length; i++)
            gens[i] = new NShotGeneratorProxy<Integer>(new ConstantGenerator<Integer>(values[i]), 1);
        return new UniqueAlternativeGenerator<Integer>(Integer.class, gens);
    }
    
    static class NShotGeneratorProxy<E> extends GeneratorProxy<E> {

        private long shots;

        private long remainingShots;

        public NShotGeneratorProxy(Generator<E> source, long shots) {
            super(source);
            this.shots = shots;
            this.remainingShots = shots;
        }

        @Override
        public E generate() {
        	E product;
            if (remainingShots <= 0 || (product = super.generate()) == null)
                return null;
            this.remainingShots--;
            return product;
        }

        @Override
        public void reset() {
            super.reset();
            remainingShots = shots;
        }

        @Override
        public void close() {
            super.close();
            remainingShots = 0;
        }
    }

}
