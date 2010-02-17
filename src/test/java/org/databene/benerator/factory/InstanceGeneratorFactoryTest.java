/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import org.databene.benerator.Generator;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.databene.model.data.IdDescriptor;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.junit.Test;

/**
 * Tests the {@link InstanceGeneratorFactory}.<br/>
 * <br/>
 * Created at 27.08.2008 13:55:03
 * @since 0.5.5
 * @author Volker Bergmann
 */
public class InstanceGeneratorFactoryTest extends GeneratorTest {
	
	/**
	 * Test unique generation based on random sequence.
	 * <attribute distribution="random" unique="true"/>
	 */
	@Test
	public void testUniqueRandom() {
		SimpleTypeDescriptor type = new SimpleTypeDescriptor(null, "int").withMin("1").withMax("3").withDistribution("random");
		InstanceDescriptor instance = new InstanceDescriptor("n", type).withUnique(true);
		Generator<Integer> generator = createInstanceGenerator(instance);
		expectGeneratedSet(generator, 1, 2, 3).withCeasedAvailability();
	}
	
	@Test
	public void testDefaultId() {
		IdDescriptor descriptor = new IdDescriptor("id", "int");
		Generator<Integer> generator = createInstanceGenerator(descriptor);
		expectGeneratedSet(generator, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).withContinuedAvailability();
	}
	
	@SuppressWarnings("unchecked")
    private Generator<Integer> createInstanceGenerator(InstanceDescriptor instance) {
		BeneratorContext context = new BeneratorContext();
		return (Generator<Integer>) InstanceGeneratorFactory.createSingleInstanceGenerator(instance, context);
	}

}
