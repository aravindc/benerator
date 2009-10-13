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

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link DateArithmetic}.<br/><br/>
 * Created: 13.10.2009 17:34:36
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DateArithmeticTest {
	
	private static final Date DATE     = TimeUtil.date(2009, 9, 13);
	private static final Time TIME     = TimeUtil.time(17, 36, 37, 389);
	private static final Date DATETIME = TimeUtil.date(2009, 9, 13, 17, 36, 37, 389);
	private static final long ONE_DAY_MILLIS = 24L * 3600 * 1000;
	
	DateArithmetic arithmetic = new DateArithmetic();
	
	@Test
	public void testGetBaseType() {
		assertEquals(Date.class, arithmetic.getBaseType());
	}
	
	@Test
	public void testAdd() {
		assertEquals(DATETIME, arithmetic.add(DATE, TIME));
		assertEquals(TimeUtil.add(DATE, Calendar.DATE, 1), arithmetic.add(DATE, ONE_DAY_MILLIS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAdd_IllegalType() {
		arithmetic.add(DATE, new File("test"));
	}

	@Test
	public void testSubtract() {
		assertEquals(DATE, arithmetic.subtract(DATETIME, TIME));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubtract_IllegalType() {
		arithmetic.subtract(DATE, new File("test"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testMultiply() {
		arithmetic.multiply(DATE, TIME);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDivide() {
		arithmetic.divide(DATE, TIME);
	}

}
