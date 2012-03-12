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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.distribution.sequence.RandomDoubleGenerator;
import org.databene.benerator.sample.AttachedWeightSampleGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.CollectionUtil;
import org.databene.commons.converter.FormatFormatConverter;
import org.databene.model.data.InstanceDescriptor;
import org.databene.model.data.SimpleTypeDescriptor;
import org.databene.model.data.Uniqueness;
import org.databene.script.WeightedSample;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link FactoryUtil} class.<br/>
 * <br/>
 * Created at 01.07.2009 07:10:51
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class FactoryUtilTest extends GeneratorTest {
	
	@Test
	public void testGetDistribution_default() {
		SimpleTypeDescriptor descriptor = createSimpleType("myType");
		assertNull(FactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.NONE, false, context));
		assertEquals(SequenceManager.EXPAND_SEQUENCE, 
				FactoryUtil.getDistribution(descriptor.getDistribution(), Uniqueness.SIMPLE, true, context));
	}
	
	@Test
	public void testCreateDynamicCountGenerator_default() {
		// given an instance descriptor without limits defined
		InstanceDescriptor descriptor = createInstance("inst");
		// when creating a count generator for this
		Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 1L, 1L, false, context);
		countGenerator.init(context);
		// then it is supposed to only generate null values for indicating an unlimited count
		expectGeneratedSet(countGenerator, 100, new Long[] { null });
	}
	
	@Test
	public void testCreateDynamicCountGenerator_distributed() {
		InstanceDescriptor descriptor = createInstance("inst").withMinCount(2).withMaxCount(4);
		Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 1L, 1L, false, context);
		countGenerator.init(context);
		expectGeneratedSet(countGenerator, 100, 2L, 3L, 4L).withContinuedAvailability();
	}
	
	@Test
	public void testCreateDynamicCountGenerator_minMax() {
		InstanceDescriptor descriptor = createInstance("inst").withMinCount(2).withMaxCount(3);
		Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 1L, 1L, false, context);
		countGenerator.init(context);
		expectGeneratedSet(countGenerator, 20, 2L, 3L).withContinuedAvailability();
	}
	
	@Test
	public void testCreateDynamicCountGenerator_min() {
		// given an instance descriptor with just a lower limit defined
		InstanceDescriptor descriptor = createInstance("inst").withMinCount(6);
		// when creating a count generator for this
		Generator<Long> countGenerator = DescriptorUtil.createDynamicCountGenerator(descriptor, 1L, 1L, false, context);
		countGenerator.init(context);
		// then it is supposed to only generate null values for indicating an unlimited count
		expectGeneratedSet(countGenerator, 100, new Long[] { null });
	}
	

    // formatting generators -------------------------------------------------------------------------------------------

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetConvertingGenerator() {
        Generator<Double> source = new RandomDoubleGenerator(0, 9);
        NumberFormat format = DecimalFormat.getInstance();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        Generator<String> generator = WrapperFactory.applyConverter(
                source, new FormatFormatConverter(Object.class, format, false));
        initAndUseGenerator(generator);
    }

    @Test
    public void testGetMessageGenerator() {
        List<String> salutations = Arrays.asList("Hello", "Hi");
        AttachedWeightSampleGenerator<String> salutationGenerator = new AttachedWeightSampleGenerator<String>(String.class, salutations);
        List<String> names = Arrays.asList("Alice", "Bob", "Charly");
        AttachedWeightSampleGenerator<String> nameGenerator = new AttachedWeightSampleGenerator<String>(String.class, names);
        String pattern = "{0} {1}";
        Generator<String> generator = WrapperFactory.createMessageGenerator(pattern, 0, 12, salutationGenerator, nameGenerator);
        generator.init(context);
        ProductWrapper<String> wrapper = new ProductWrapper<String>();
        for (int i = 0; i < 10; i++) {
            String message = generator.generate(wrapper).unwrap();
            StringTokenizer tokenizer = new StringTokenizer(message, " ");
            assertEquals(2, tokenizer.countTokens());
            assertTrue(salutations.contains(tokenizer.nextToken()));
            assertTrue(names.contains(tokenizer.nextToken()));
        }
    }
    
	@SuppressWarnings("unchecked")
    @Test
    public void testExtractValues() {
		List<Integer> values = FactoryUtil.extractValues(CollectionUtil.toList(
    			new WeightedSample<Integer>(1, 1),
    			new WeightedSample<Integer>(null, 2)));
    	assertEquals(CollectionUtil.toList(1, null), values);
    }

    private <T> void initAndUseGenerator(Generator<T> generator) {
    	generator.init(context);
        for (int i = 0; i < 5; i++) {
            T product = generator.generate(new ProductWrapper<T>()).unwrap();
        	assertNotNull("Generator unexpectedly invalid: " + generator.toString(), product);
        }
    }

}
