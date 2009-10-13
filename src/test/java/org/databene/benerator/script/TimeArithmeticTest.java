/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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
import java.sql.Time;
import java.util.Calendar;

import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link TimeArithmetic}.<br/><br/>
 * Created: 13.10.2009 18:00:06
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class TimeArithmeticTest {

	private static final Time TIME_BASE   = TimeUtil.time(1, 2, 3, 456);
	private static final Time TIME_OFFSET = TimeUtil.time(6, 5, 4, 321);
	private static final Time TIME_SUM    = TimeUtil.time(7, 7, 7, 777);
	private static final long ONE_OUR_MILLIS = 3600L * 1000;
	
	TimeArithmetic arithmetic = new TimeArithmetic();
	
	@Test
	public void testGetBaseType() {
		assertEquals(Time.class, arithmetic.getBaseType());
	}
	
	@Test
	public void testAdd_Time() {
		assertEquals(TIME_SUM, arithmetic.add(TIME_BASE, TIME_OFFSET));
	}

	@Test
	public void testAdd_Millis() {
		assertEquals(TimeUtil.add(TIME_BASE, Calendar.HOUR, 1), arithmetic.add(TIME_BASE, ONE_OUR_MILLIS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdd_IllegalType() {
		arithmetic.add(TIME_BASE, new File("test"));
	}

	@Test
	public void testSubtract() {
		assertEquals(TIME_BASE, arithmetic.subtract(TIME_SUM, TIME_OFFSET));
		assertEquals(TIME_OFFSET, arithmetic.subtract(TIME_SUM, TIME_BASE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubtract_IllegalType() {
		arithmetic.subtract(TIME_BASE, new File("test"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMultiply() {
		arithmetic.multiply(TIME_BASE, TIME_OFFSET);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDivide() {
		arithmetic.divide(TIME_BASE, TIME_OFFSET);
	}

}
