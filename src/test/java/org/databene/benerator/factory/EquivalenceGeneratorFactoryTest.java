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

package org.databene.benerator.factory;

import static org.junit.Assert.*;

import java.util.Date;

import static org.databene.commons.TimeUtil.*;
import static org.databene.commons.Period.*;
import org.junit.Test;

/**
 * Tests the {@link EquivalenceGeneratorFactory}.<br/><br/>
 * Created: 29.08.2011 18:09:46
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class EquivalenceGeneratorFactoryTest {

	@Test
	public void testMidDate_midnights() {
		checkMidDate(date(2011, 7, 29), date(2011, 8, 1), DAY.getMillis(), date(2011, 7, 30));
		checkMidDate(date(2011, 7, 20), date(2011, 7, 25), 2 * DAY.getMillis(), date(2011, 7, 22));
	}
	
	@Test
	public void testMidDate_hours() {
		checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), HOUR.getMillis() / 2, date(2011, 7, 29, 16, 30, 0, 0));
		checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), MINUTE.getMillis(), date(2011, 7, 29, 16, 30, 0, 0));
		checkMidDate(date(2011, 7, 29, 16, 0, 0, 0), date(2011, 7, 29, 17, 0, 0, 0), 20 * MINUTE.getMillis(), date(2011, 7, 29, 16, 20, 0, 0));
	}
	
	@Test
	public void testMidDate_fractionalDays() {
		checkMidDate(date(2011, 7, 29), date(2011, 7, 30), DAY.getMillis() / 2, date(2011, 7, 29, 12, 0, 0, 0));
		checkMidDate(date(2011, 7, 20), date(2011, 7, 30), DAY.getMillis() / 2, date(2011, 7, 25, 0, 0, 0, 0));
		checkMidDate(date(2011, 7, 20), date(2011, 7, 30), DAY.getMillis() * 3 / 2, date(2011, 7, 24, 12, 0, 0, 0));
	}
	
	private void checkMidDate(Date min, Date max, long granularity, Date expected) {
		EquivalenceGeneratorFactory factory = new EquivalenceGeneratorFactory();
		Date result = factory.midDate(min, max, granularity);
		assertEquals(expected, result);
	}
	
}
