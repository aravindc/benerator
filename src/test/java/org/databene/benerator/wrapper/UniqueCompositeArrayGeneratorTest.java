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

package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.benerator.SequenceTestGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.junit.Test;

/**
 * Tests the {@link UniqueCompositeArrayGenerator}.<br/>
 * <br/>
 * Created: 17.11.2007 13:39:04
 * @author Volker Bergmann
 */
public class UniqueCompositeArrayGeneratorTest extends GeneratorClassTest {

    public UniqueCompositeArrayGeneratorTest() {
        super(UniqueCompositeArrayGenerator.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInteger() {
        Generator<Integer>[] sources = new Generator [] {
                new SequenceTestGenerator<Integer>(0, 1),
                new SequenceTestGenerator<Integer>(0, 1),
                new SequenceTestGenerator<Integer>(0, 1)
        };
        UniqueCompositeArrayGenerator<Integer> generator = new UniqueCompositeArrayGenerator<Integer>(Integer.class, sources);
        generator.init(context);
		expectUniqueProducts(generator,  8).withCeasedAvailability();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testString() {
        Generator<String>[] sources = new Generator [] {
                new ConstantGenerator<String>("x"),
                new SequenceTestGenerator<String>("a", "b"),
                new ConstantGenerator<String>("x")
        };
        UniqueCompositeArrayGenerator<String> generator = new UniqueCompositeArrayGenerator<String>(String.class, sources);
        generator.init(context);
		expectUniqueProducts(generator,  2).withCeasedAvailability();
    }

}
