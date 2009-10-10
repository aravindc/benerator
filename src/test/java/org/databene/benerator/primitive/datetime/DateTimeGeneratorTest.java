package org.databene.benerator.primitive.datetime;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.commons.TimeUtil;
import org.junit.Test;
import static junit.framework.Assert.*;

public class DateTimeGeneratorTest extends GeneratorClassTest {
	
    public DateTimeGeneratorTest() {
	    super(DateTimeGenerator.class);
    }

	static final int N = 100;

	@Test
    public void testInvalidSettings() {
        new DateTimeGenerator();
    }
    
	@Test
    public void testMinMax() {
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time(12, 0), TimeUtil.time(12, 00));
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time( 0, 0), TimeUtil.time(23, 59));
        check(TimeUtil.date(2008, 6, 5), TimeUtil.date(2008, 6, 25), TimeUtil.time( 9, 0), TimeUtil.time(17,  0));
    }

	@Test
    public void testDateDistribution() {
    	int minYear = 2008;
    	int maxYear = 2008;
    	int hour = 1;
    	int minute = 2;
    	int second = 3;
    	int millisecond = 4;
    	
        DateTimeGenerator generator = createGenerator(
        		TimeUtil.date(minYear, 7, 6), 
        		TimeUtil.date(maxYear, 8, 8),
        		TimeUtil.time(hour, minute, second, millisecond), 
        		TimeUtil.time(hour, minute, second, millisecond));
        Date minDate = TimeUtil.date(minYear, 7, 6, hour, minute, second, millisecond);
        Date maxDate = TimeUtil.date(maxYear, 8, 8, hour, minute, second, millisecond);
        generator.setDatePrecision("0000-00-01");
        generator.setDateDistribution(Sequence.STEP);
        assertTrue(generator.available());
        for (int i = 0; i < N && generator.available(); i++) {
            Date date = generator.generate();
            assertFalse("Generated date " + date + " is before min date: " + minDate, date.before(minDate));
            assertFalse(date.after(maxDate));
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            assertEquals(hour, cal.get(Calendar.HOUR));
            assertEquals(minute, cal.get(Calendar.MINUTE));
            assertEquals(second, cal.get(Calendar.SECOND));
            assertEquals(millisecond, cal.get(Calendar.MILLISECOND));
        }
    }
    
    // private helpers ---------------------------------------------------------
    
    private void check(Date minDate, Date maxDate, Time minTime, Time maxTime) {
        DateTimeGenerator generator = createGenerator(minDate, maxDate, minTime, maxTime);
        Date maxResult = new Date(maxDate.getTime() + maxTime.getTime());
        for (int i = 0; i < N; i++) {
            Date date = generator.generate();
            assertFalse(date.before(minDate));
            assertFalse(date.after(maxResult));
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
        }
    }

	private DateTimeGenerator createGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
	    DateTimeGenerator generator = new DateTimeGenerator();
        generator.setMinDate(minDate);
        generator.setMaxDate(maxDate);
        generator.setMinTime(minTime);
        generator.setMaxTime(maxTime);
	    return generator;
    }
	
}
