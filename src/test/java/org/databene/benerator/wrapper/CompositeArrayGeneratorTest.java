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

import org.databene.benerator.ConstantTestGenerator;
import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;

import java.util.Arrays;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link MultiSourceArrayGenerator}.<br/><br/>
 * Created: 11.10.2006 23:12:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class CompositeArrayGeneratorTest extends GeneratorTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testNonUnique() {
        ConstantTestGenerator<Integer> source1 = new ConstantTestGenerator<Integer>(1);
        ConstantTestGenerator<Integer> source2 = new ConstantTestGenerator<Integer>(2);
        MultiSourceArrayGenerator<Integer> generator = new MultiSourceArrayGenerator<Integer>(Integer.class, false, source1, source2);
        generator.init(context);
        Integer[] EXPECTED_ARRAY = new Integer[] {1, 2};
        for (int i = 0; i < 10; i++)
            assertTrue(Arrays.equals(EXPECTED_ARRAY, GeneratorUtil.generateNonNull(generator)));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testUnique() {
        Generator<Integer> source1 = new SequenceTestGenerator<Integer>(1, 2);
        Generator<Integer> source2 = new SequenceTestGenerator<Integer>(3, 4);
        MultiSourceArrayGenerator<Integer> generator = new MultiSourceArrayGenerator<Integer>(Integer.class, true, source1, source2);
        generator.init(context);
        expectUniqueProducts(generator, 4).withCeasedAvailability();
    }
    
}
