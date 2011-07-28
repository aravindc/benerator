/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.Uniqueness;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link GeneratorFactoryUtil} class.<br/>
 * <br/>
 * Created at 01.07.2009 07:10:51
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class GeneratorFactoryUtilTest extends GeneratorTest {
	
	@Test
	public void testGetDistribution_default() {
		SimpleTypeDescriptor descriptor = new SimpleTypeDescriptor("myType");
		BeneratorContext context = new BeneratorContext(null);
		assertNull(GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.NONE, false, context));
		assertEquals(SequenceManager.EXPAND_SEQUENCE, 
				GeneratorFactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.SIMPLE, true, context));
	}

	@Test
	public void testGetCountGenerator_default() {
		InstanceDescriptor descriptor = new InstanceDescriptor("inst");
		Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(descriptor, false, context);
		countGenerator.init(context);
		assertNull(GeneratorUtil.generateNonNull(countGenerator));
	}
	
	@Test
	public void testGetCountGenerator_distributed() {
		InstanceDescriptor descriptor = new InstanceDescriptor("inst").withMinCount(2).withMaxCount(4);
		Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(descriptor, false, context);
		countGenerator.init(context);
		expectGeneratedSet(countGenerator, 100, 2L, 3L, 4L).withContinuedAvailability();
	}
	
	@Test
	public void testGetCountGenerator_minMax() {
		InstanceDescriptor descriptor = new InstanceDescriptor("inst").withMinCount(2).withMaxCount(3);
		Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(descriptor, false, context);
		countGenerator.init(context);
		expectGeneratedSet(countGenerator, 20, 2L, 3L).withContinuedAvailability();
	}
	
	@Test
	public void testGetCountGenerator_min() {
		InstanceDescriptor descriptor = new InstanceDescriptor("inst").withMinCount(6);
		Generator<Long> countGenerator = GeneratorFactoryUtil.getCountGenerator(descriptor, false, context);
		countGenerator.init(context);
		assertNull(GeneratorUtil.generateNonNull(countGenerator));
	}
	
}
