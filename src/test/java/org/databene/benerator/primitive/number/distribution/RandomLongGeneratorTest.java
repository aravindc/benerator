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

package org.databene.benerator.primitive.number.distribution;

import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.CollectionUtil;

/**
 * Created: 11.10.2006 23:03:30
 */
public class RandomLongGeneratorTest extends GeneratorClassTest {

    public RandomLongGeneratorTest() {
        super(RandomLongGenerator.class);
    }

    public void testSimple() {
        RandomLongGenerator generator = new RandomLongGenerator(0, 1);
        checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(0L, 1L));
    }

    public void testPrecision() {
        RandomLongGenerator generator = new RandomLongGenerator(-2, 2, 2);
        checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(-2L, 0L, 2L));
    }

    public void testPrecisionOffset() {
        RandomLongGenerator generator = new RandomLongGenerator(-1, 3, 2);
        checkEqualDistribution(generator, 3000, 0.1, CollectionUtil.toSet(-1L, 1L, 3L));
    }
}
