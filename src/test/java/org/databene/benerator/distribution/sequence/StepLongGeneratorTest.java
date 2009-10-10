/*
 * (c) Copyright 2007-2009 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.sequence.StepLongGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link StepLongGenerator}.<br/><br/>
 * Created: 26.07.2007 18:11:19
 * @author Volker Bergmann
 */
public class StepLongGeneratorTest extends GeneratorClassTest {

    public StepLongGeneratorTest() {
        super(StepLongGenerator.class);
    }

    @Test
    public void testIncrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, 1);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(2L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, 2);
        assertEquals(1L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(5L, (long)oddGenerator.generate());
    }

    @Test
    public void testDecrement() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5, -1);
        assertEquals(5L, (long)simpleGenerator.generate());
        assertEquals(4L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        StepLongGenerator oddGenerator = new StepLongGenerator(1, 5, -2);
        assertEquals(5L, (long)oddGenerator.generate());
        assertEquals(3L, (long)oddGenerator.generate());
        assertEquals(1L, (long)oddGenerator.generate());
    }

    @Test
    public void testPrecision() throws IllegalGeneratorStateException {
        StepLongGenerator simpleGenerator = new StepLongGenerator(1, 5);
        simpleGenerator.setPrecision(2L);
        assertEquals(1L, (long)simpleGenerator.generate());
        assertEquals(3L, (long)simpleGenerator.generate());
        assertEquals(5L, (long)simpleGenerator.generate());
    }

}
