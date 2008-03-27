package org.databene.benerator.primitive.datetime;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.databene.commons.TimeUtil;

import junit.framework.TestCase;

public class DateTimeGeneratorTest extends TestCase {

    public void testInvalidSettings() {
        new DateTimeGenerator();
    }
    
    public void testGeneration() {
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time(12, 0), TimeUtil.time(12, 00));
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), TimeUtil.time( 0, 0), TimeUtil.time(23, 59));
        check(TimeUtil.date(2008, 6, 5), TimeUtil.date(2008, 6, 25), TimeUtil.time( 9, 0), TimeUtil.time(17,  0));
    }
    
    private void check(Date minDate, Date maxDate, Time minTime, Time maxTime) {
        DateTimeGenerator generator = new DateTimeGenerator();
        generator.setMinDate(minDate);
        generator.setMaxDate(maxDate);
        generator.setMinTime(minTime);
        generator.setMaxTime(maxTime);
        Date maxResult = new Date(maxDate.getTime() + maxTime.getTime());
        for (int i = 0; i < 1000; i++) {
            Date date = generator.generate();
            assertFalse(date.before(minDate));
            assertFalse(date.after(maxResult));
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
        }
    }
}
