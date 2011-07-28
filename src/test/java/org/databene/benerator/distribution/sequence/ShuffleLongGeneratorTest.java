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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.distribution.sequence.ShuffleLongGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link ShuffleLongGenerator}.<br/><br/>
 * Created: 07.06.2006 20:23:39
 * @since 0.1
 * @author Volker Bergmann
 */
public class ShuffleLongGeneratorTest extends GeneratorClassTest {

    public ShuffleLongGeneratorTest() {
        super(ShuffleLongGenerator.class);
    }

    @Test
    public void testInstantiation() throws Exception {
        new ShuffleLongGenerator();
        new ShuffleLongGenerator(0, 10);
        new ShuffleLongGenerator(0, 10, 1, 1);
    }

    @Test(expected = InvalidGeneratorSetupException.class)
    public void testIncrement0() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 0);
        generator.init(context);
    }

	@Test
    public void testIncrement1() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 1);
        generator.init(context);
        expectGeneratedSequence(generator, 0L, 1L, 2L, 3L).withCeasedAvailability();
    }

    @Test
    public void testIncrement2() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 2);
        generator.init(context);
        expectGeneratedSequence(generator, 0L, 2L, 1L, 3L).withCeasedAvailability();
    }

    @Test
    public void testIncrement3() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 3);
        generator.init(context);
        expectGeneratedSequence(generator, 0L, 3L, 1L, 2L).withCeasedAvailability();
    }

    @Test
    public void testIncrement4() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 4);
        generator.init(context);
        expectGeneratedSequence(generator, 0L, 1L, 2L, 3L).withCeasedAvailability();
    }

    @Test
    public void testReset() throws Exception {
        ShuffleLongGenerator generator = new ShuffleLongGenerator(0, 3, 1, 2);
        generator.init(context);
        expectGeneratedSequence(generator, 0L, 2L, 1L, 3L).withCeasedAvailability();
    }

}
