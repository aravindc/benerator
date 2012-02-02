/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.benerator.dataset;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link CompositeDatasetGenerator}.<br/><br/>
 * Created: 09.03.2011 16:28:23
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class CompositeDatasetGeneratorTest extends GeneratorTest {

	/** dataset 'one' with value 1 */
	AtomicDatasetGenerator<Integer> one; 

	/** dataset 'two' with value 2 */
	AtomicDatasetGenerator<Integer> two;
	
	/** dataset 'small' composed of 'one' and 'two' */
	CompositeDatasetGenerator<Integer> small;
	
	/** dataset 'ten' with value 10 */
	AtomicDatasetGenerator<Integer> ten;
	
	/** dataset 'large' composed of 'ten' */
	CompositeDatasetGenerator<Integer> large;
	
	/** dataset any composed of 'small' and 'large' */
	CompositeDatasetGenerator<Integer> any;

	@Before
	public void prepareNumberSets() {
		one = new AtomicDatasetGenerator<Integer>(new ConstantGenerator<Integer>(1), "num", "one", 1); 
		two = new AtomicDatasetGenerator<Integer>(new ConstantGenerator<Integer>(2), "num", "two", 1);
		
		small = new CompositeDatasetGenerator<Integer>("num", "small", false);
		small.addSubDataset(one, 1);
		small.addSubDataset(two, 2);
		
		ten = new AtomicDatasetGenerator<Integer>(new ConstantGenerator<Integer>(10), "num", "ten", 1);
		
		large = new CompositeDatasetGenerator<Integer>("num", "large", false);
		large.addSubDataset(ten, 1);

		any = new CompositeDatasetGenerator<Integer>("num", "any", false);
		any.addSubDataset(small, 1);
		any.addSubDataset(large, 1);	
		any.init(context);
	}

	@After
	public void closeNumberSets() {
		any.close();
	}
	
	@Test
	public void testSingleAtomic() {
		Map<Integer, AtomicInteger> productCounts = countProducts(large, 1000);
		assertEquals(1000, productCounts.get(10).intValue()); // all values must be '10'
	}
	
	@Test
	public void testComposedAtomics() {
		Map<Integer, AtomicInteger> productCounts = countProducts(small, 1000);
		assertEquals(1000, productCounts.get(1).intValue() + productCounts.get(2).intValue()); // total count of 1000 '1' and '2' values
		assertEquals(666., productCounts.get(2).doubleValue(), 70); // 66.6% of the values must be 2
	}
	
	@Test
	public void testNestedComposites() {
		Map<Integer, AtomicInteger> productCounts = countProducts(any, 10000);
		System.out.println(productCounts);
		assertEquals(10000, productCounts.get(1).intValue() + productCounts.get(2).intValue() + productCounts.get(10).intValue()); // total count of 1000 '1' and '2' values
		assertEquals(1666., productCounts.get( 1).doubleValue(), 300); // about 16% of the values must be 1
		assertEquals(3333., productCounts.get( 2).doubleValue(), 300); // about 33% of the values must be 2
		assertEquals(5000., productCounts.get(10).doubleValue(), 300); // about 50% of the values must be 10
	}
	
	@Test
	public void testGenerateDataset() {
		assertEquals(1, any.generateForDataset("one").intValue());
		assertEquals(2, any.generateForDataset("two").intValue());
		int smallValue = any.generateForDataset("small").intValue();
		assertTrue(smallValue == 1 || smallValue == 2);
		assertEquals(10, any.generateForDataset("ten").intValue());
		assertEquals(10, any.generateForDataset("large").intValue());
		int anyValue = any.generateForDataset("any").intValue();
		assertTrue(anyValue == 1 || anyValue == 2 || anyValue == 10);
	}

	/* TODO v0.8 support limited/unique generators
	@Test
	public void testLimitedSources() {
		AtomicDatasetGenerator<Integer> five = new AtomicDatasetGenerator<Integer>(new OneShotGenerator<Integer>(5), "num", "five"); 
		AtomicDatasetGenerator<Integer> six = new AtomicDatasetGenerator<Integer>(new ConstantGenerator<Integer>(6), "num", "six");
		CompositeDatasetGenerator<Integer> medium = new CompositeDatasetGenerator<Integer>("num", "medium");
		medium.addSubDataset(five, 1);
		medium.addSubDataset(six, 1);
		medium.init(context);
		// check first generation
		Integer product = medium.generate();
		assertNotNull(product);
		assertTrue(product == 5 || product == 6);
		// check second generation
		product = medium.generate();
		assertNotNull(product);
		assertTrue(product == 5 || product == 6);
		// assert unavailable
		assertNull(medium.generate());
		medium.close();
	}
	*/
}
