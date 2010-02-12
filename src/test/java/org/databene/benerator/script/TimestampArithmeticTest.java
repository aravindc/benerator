/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.script;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;

import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link TimestampArithmetic}.<br/><br/>
 * Created: 14.10.2009 11:24:55
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class TimestampArithmeticTest {

	private static final Timestamp TS_BASE    = TimeUtil.timestamp(2009, 9, 14, 1, 2, 3, 123456789);
	private static final Timestamp TS_OFFSET  = TimeUtil.timestamp(1970, 0,  1, 6, 5, 4, 876543210);
	private static final Timestamp TS_SUM     = TimeUtil.timestamp(2009, 9, 14, 7, 7, 7, 999999999);
	private static final Timestamp TS_OFFSET2 = TimeUtil.timestamp(1970, 0,  1, 0, 0, 0, 999999999);
	private static final Timestamp TS_SUM2    = TimeUtil.timestamp(2009, 9, 14, 1, 2, 4, 123456788);
	private static final long ONE_OUR_MILLIS = 3600L * 1000;
	
	TimestampArithmetic arithmetic = new TimestampArithmetic();
	
	@Test
	public void testGetBaseType() {
		assertEquals(Timestamp.class, arithmetic.getBaseType());
	}
	
	@Test
	public void testAdd_Timestamp() {
		// simple test
		assertEquals(TS_SUM, arithmetic.add(TS_BASE, TS_OFFSET));
		// testing nano overrun
		assertEquals(TS_SUM2, arithmetic.add(TS_BASE, TS_OFFSET2));
	}

	@Test
	public void testAdd_Millis() {
		assertEquals(TimeUtil.add(TS_BASE, Calendar.HOUR, 1), arithmetic.add(TS_BASE, ONE_OUR_MILLIS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdd_IllegalType() {
		arithmetic.add(TS_BASE, new File("test"));
	}

	@Test
	public void testSubtract() {
		// normal test
		assertEquals(TS_BASE, arithmetic.subtract(TS_SUM, TS_OFFSET));
		assertEquals(TS_OFFSET, arithmetic.subtract(TS_SUM, TS_BASE));
		// testing nano underrrun
		assertEquals(TS_BASE, arithmetic.subtract(TS_SUM2, TS_OFFSET2));
		assertEquals(TS_OFFSET2, arithmetic.subtract(TS_SUM2, TS_BASE));
	}

	@Test
	public void testSubtract_Millis() {
		assertEquals(TimeUtil.add(TS_BASE, Calendar.HOUR, -1), arithmetic.subtract(TS_BASE, ONE_OUR_MILLIS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubtract_IllegalType() {
		arithmetic.subtract(TS_BASE, new File("test"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMultiply() {
		arithmetic.multiply(TS_BASE, TS_OFFSET);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDivide() {
		arithmetic.divide(TS_BASE, TS_OFFSET);
	}

}
