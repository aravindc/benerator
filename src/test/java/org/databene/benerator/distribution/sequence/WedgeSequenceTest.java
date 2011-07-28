/*
 * (c) Copyright 2007-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.sequence.WedgeLongGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.junit.Test;

/**
 * Tests the 'wedge' sequence.<br/>
 * <br/>
 * Created: 13.11.2007 14:20:39
 * @author Volker Bergmann
 */
public class WedgeSequenceTest extends GeneratorTest {

	@Test
    public void testLongGranularity1() throws Exception {
        expectGeneratedSequence(longGenerator(1L, 3L, 1L),  1L,  3L,  2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator( 1L,  4L, 1L),  1L,  4L,  2L,  3L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-3L, -1L, 1L), -3L, -1L, -2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-4L, -1L, 1L), -4L, -1L, -3L, -2L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-1L,  1L, 1L), -1L,  1L,  0L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-1L,  2L, 1L), -1L,  2L,  0L,  1L).withCeasedAvailability();
    }

	@Test
    public void testLongGranularity5() throws Exception {
        expectGeneratedSequence(longGenerator(  1L, 11L, 5L),   1L, 11L,   6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(  1L, 16L, 5L),   1L, 16L,   6L, 11L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-11L, -1L, 5L), -11L, -1L,  -6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-16L, -1L, 5L), -16L, -1L, -11L, -6L).withCeasedAvailability();
        expectGeneratedSequence(longGenerator(-11L,  4L, 5L), -11L,  4L,  -6L, -1L).withCeasedAvailability();
    }

	@Test
    public void testDoubleGranularity1() throws Exception {
        expectGeneratedSequence(doubleGenerator(1., 3., 1.),  1.,  3.,  2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator( 1.,  4., 1.),  1.,  4.,  2.,  3.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-3., -1., 1.), -3., -1., -2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-4., -1., 1.), -4., -1., -3., -2.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.,  1., 1.), -1.,  1.,  0.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.,  2., 1.), -1.,  2.,  0.,  1.).withCeasedAvailability();
    }

	@Test
    public void testDoubleGranularity5() throws Exception {
        expectGeneratedSequence(doubleGenerator(  1., 11., 5.),   1., 11.,   6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(  1., 16., 5.),   1., 16.,   6., 11.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-11., -1., 5.), -11., -1.,  -6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-16., -1., 5.), -16., -1., -11., -6.).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-11.,  4., 5.), -11.,  4.,  -6., -1.).withCeasedAvailability();
    }

	@Test
    public void testDoubleGranularity0_5() throws Exception {
        expectGeneratedSequence(doubleGenerator( 0.5,  1.5, 0.5),  0.5,  1.5,  1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator( 0.5,  2.0, 0.5),  0.5,  2.0,  1.0,  1.5).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.5, -0.5, 0.5), -1.5, -0.5, -1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-2.0, -0.5, 0.5), -2.0, -0.5, -1.5, -1.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-0.5,  0.5, 0.5), -0.5,  0.5, 0.0).withCeasedAvailability();
        expectGeneratedSequence(doubleGenerator(-1.0,  0.5, 0.5), -1.0,  0.5, -0.5,  0.0).withCeasedAvailability();
    }

    private Generator<Long> longGenerator(long min, long max, long granularity) {
        return initialize(new WedgeLongGenerator(min,  max, granularity));
    }

    private Generator<Double> doubleGenerator(double min, double max, double granularity) {
        return initialize(new WedgeDoubleGenerator(min, max, granularity));
    }

}
