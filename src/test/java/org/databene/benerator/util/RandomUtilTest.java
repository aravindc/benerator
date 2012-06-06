/*
 * (c) Copyright 2006-2012 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.util;

import static org.junit.Assert.*;

import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.RandomUtil;
import org.databene.commons.collection.ObjectCounter;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Created: 11.10.2006 23:07:35
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomUtilTest extends GeneratorTest {
	
	@Test	
    public void testRandomInt() {
        testEqualDistribution(0, 1, 0.1, 3000);
        testEqualDistribution(0, 0, 0.1, 3000);
        testEqualDistribution(-1, -1, 0.1, 3000);
        testEqualDistribution(-1, 1, 0.1, 3000);
    }

	@Test	
    public void testRandomLong() {
        testEqualDistribution( 0L,  1L, 0.1, 3000);
        testEqualDistribution( 0L,  0L, 0.1, 3000);
        testEqualDistribution(-1L, -1L, 0.1, 3000);
        testEqualDistribution(-1L,  1L, 0.1, 3000);
    }
	
	@Test
	public void testRandomFromLiteral() {
		ObjectCounter<Object> counter = new ObjectCounter<Object>(2);
		int n = 3000;
		for (int i = 0; i < n; i++)
			counter.count(RandomUtil.randomFromWeightLiteral("'A'^2,'B'^1"));
		assertEquals(2, counter.getCounts().size());
		assertEquals(n / 3. * 2., counter.getCount("A"), 100);
		assertEquals(n / 3., counter.getCount("B"), 100);
	}
	
	@Test
	public void testRandomFromLiteral_empty() {
		assertNull(RandomUtil.randomFromWeightLiteral(null));
		assertNull(RandomUtil.randomFromWeightLiteral(""));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRandomFromLiteral_negativeWeight() {
		RandomUtil.randomFromWeightLiteral("1^-1,2^-2");
	}
	
    // implementation --------------------------------------------------------------------------------------------------

    private void testEqualDistribution(int min, int max, double tolerance, int iterations) {
        List<Integer> list = new ArrayList<Integer>();
        Set<Integer> expectedSet = new HashSet<Integer>(max - min + 1);
        for (int i = min; i <= max; i++)
            expectedSet.add(i);
        for (int i = 0; i < iterations; i++)
            list.add(RandomUtil.randomInt(min, max));
        checkEqualDistribution(list, tolerance, expectedSet);
    }

    private void testEqualDistribution(long min, long max, double tolerance, int iterations) {
        List<Long> list = new ArrayList<Long>();
        Set<Long> expectedSet = new HashSet<Long>((int)(max - min + 1));
        for (long i = min; i <= max; i++)
            expectedSet.add(i);
        for (int i = 0; i < iterations; i++)
            list.add(RandomUtil.randomLong(min, max));
        checkEqualDistribution(list, tolerance, expectedSet);
    }
	
}
