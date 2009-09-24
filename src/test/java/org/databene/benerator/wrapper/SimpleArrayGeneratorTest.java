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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.sample.SequencedSampleGenerator;
import org.databene.benerator.test.GeneratorClassTest;

/**
 * Created: 11.10.2006 23:12:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class SimpleArrayGeneratorTest extends GeneratorClassTest {

    public SimpleArrayGeneratorTest() {
        super(SimpleArrayGenerator.class);
    }

    public void test() {
        Generator<String> source = new SequencedSampleGenerator<String>(String.class, "Alice", "Bob");
        check(source, 0, 0);
        check(source, 3, 3);
        check(source, 0, 1);
        check(source, 1, 2);
        check(source, 3, 6);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void check(Generator<String> source, int minLength, int maxLength) {
        SimpleArrayGenerator<String> generator = new SimpleArrayGenerator<String>(
                source, String.class, minLength, maxLength);
        for (int i = 0; i < 100; i++) {
            String[] product = generator.generate();
            assertTrue(minLength <= product.length);
            assertTrue(product.length <= maxLength);
        }
    }
}
