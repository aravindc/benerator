/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.wrapper;

import static org.junit.Assert.*;

import org.databene.benerator.nullable.NullInjectingGeneratorProxy;
import org.databene.benerator.nullable.NullableGenerator;
import org.databene.benerator.sample.ConstantGenerator;
import org.junit.Test;

/**
 * Tests the {@link NullInjectingGeneratorProxy}.<br/><br/>
 * Created: 26.01.2010 11:01:30
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class NullableGeneratorProxyTest {
	
	private static final int N = 1000;

	@Test
	public void testNullQuota0() {
		assertEquals(0, countNulls(createGenerator(0)));
	}

	@Test
	public void testNullQuota1() {
		assertEquals(N, countNulls(createGenerator(1)));
	}
	
	@Test
	public void testNullQuota33Percent() {
		assertEquals(N / 3., countNulls(createGenerator(1./3.)), N/10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeNullQuota() {
		createGenerator(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullQuotaGreaterOne() {
		createGenerator(1.1);
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

	private NullableGenerator<Integer> createGenerator(double nullQuota) {
	    return new NullInjectingGeneratorProxy<Integer>(new ConstantGenerator<Integer>(1), nullQuota);
    }

	private int countNulls(NullableGenerator<Integer> gen) {
	    int nullCount = 0;
		ProductWrapper<Integer> wrapper = new ProductWrapper<Integer>();
		for (int i = 0; i < N; i++) {
			wrapper = gen.generate(wrapper);
			assertNotNull(wrapper);
			if (wrapper.product == null)
				nullCount++;
			else if (wrapper.product != 1)
				fail("null or 1 expected");
		}
		return nullCount;
    }

}
