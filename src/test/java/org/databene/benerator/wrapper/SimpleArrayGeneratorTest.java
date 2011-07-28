/*
 * (c) Copyright 2006-2010 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.sample.SampleGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link SimpleArrayGenerator}.<br/><br/>
 * Created: 11.10.2006 23:12:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class SimpleArrayGeneratorTest extends GeneratorClassTest {

    public SimpleArrayGeneratorTest() {
	    super(SimpleArrayGenerator.class);
    }

	@Test
    public void test() {
        check(0, 0);
        check(3, 3);
        check(0, 1);
        check(1, 2);
        check(3, 6);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void check(int minLength, int maxLength) {
        Generator<String> source = new SampleGenerator<String>(String.class, "Alice", "Bob");
        SimpleArrayGenerator<String> generator = new SimpleArrayGenerator<String>(
                source, String.class, minLength, maxLength);
        generator.init(context);
        for (int i = 0; i < 100; i++) {
            String[] product = GeneratorUtil.generateNonNull(generator);
            assertTrue(minLength <= product.length);
            assertTrue(product.length <= maxLength);
        }
    }
    
}
