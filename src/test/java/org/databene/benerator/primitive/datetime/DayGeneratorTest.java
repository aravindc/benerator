/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.datetime;

import static junit.framework.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.TimeUtil;
import org.junit.Test;

/**
 * Tests the {@link DayGenerator}.<br/><br/>
 * Created: 12.10.2010 21:31:12
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DayGeneratorTest extends GeneratorClassTest {
	
	public DayGeneratorTest() {
		super(DayGenerator.class);
	}

	@Test
    public void testSetup() {
        Date minDate = TimeUtil.date(2010, 7, 6);
		Date maxDate = TimeUtil.date(2010, 8, 8);
		DayGenerator generator = new DayGenerator(
        		minDate, maxDate, SequenceManager.STEP_SEQUENCE, false);
		generator.setGranularity("01-02-03");
        generator.init(context);
        assertEquals(minDate, generator.min);
        assertEquals(maxDate, generator.max);
        assertEquals(1, generator.yearGranularity);
        assertEquals(2, generator.monthGranularity);
        assertEquals(3, generator.dayGranularity);
    }

	@Test
	public void testNormalRange() {
		Date min = TimeUtil.date(2009, 2, 5);
		Date max = TimeUtil.date(2009, 4, 8);
		DayGenerator generator = new DayGenerator(min, max, SequenceManager.RANDOM_SEQUENCE, false);
		generator.init(context);
		for (int i = 0; i < 1000; i++) {
			Date day = generator.generate();
			assertNotNull(day);
			assertFalse(day.before(min));
			assertFalse(day.after(max));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(day);
			assertEquals(0, calendar.get(Calendar.MILLISECOND));
			assertEquals(0, calendar.get(Calendar.SECOND));
			assertEquals(0, calendar.get(Calendar.MINUTE));
			assertEquals(0, calendar.get(Calendar.HOUR));
		}
	}
	
	@Test
	public void testEmptyRange() {
		Date min = TimeUtil.date(2009, 2, 5);
		Date max = TimeUtil.date(2009, 2, 5);
		DayGenerator generator = new DayGenerator(min, max, SequenceManager.RANDOM_SEQUENCE, false);
		generator.init(context);
		for (int i = 0; i < 1000; i++) {
			Date day = generator.generate();
			assertNotNull(day);
			assertEquals(day, min);
		}
	}
	
	@Test
    public void testDateDistribution() {
        Date minDate = TimeUtil.date(2010, 7, 6);
		Date maxDate = TimeUtil.date(2010, 8, 8);
		DayGenerator generator = new DayGenerator(
        		minDate, maxDate, SequenceManager.STEP_SEQUENCE, false);
        generator.init(context);
        for (int i = 0; i < 34; i++) {
            Date date = generator.generate();
            assertNotNull("Generator unavailable after " + i + " generations", date);
            assertFalse("Generated date " + date + " is before min date: " + minDate, date.before(minDate));
            assertFalse(date.after(maxDate));
        }
        assertUnavailable(generator);
    }

}
