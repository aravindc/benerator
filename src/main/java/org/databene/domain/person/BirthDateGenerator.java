package org.databene.domain.person;

import java.util.Date;
import java.util.Calendar;

import org.databene.benerator.primitive.DateGenerator;
import org.databene.benerator.Sequence;
import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.LightweightGenerator;
import org.databene.commons.TimeUtil;
import org.databene.commons.Period;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 13.06.2006 07:15:03
 */
public class BirthDateGenerator extends LightweightGenerator<Date> {

    private int minAgeYears;
    private int maxAgeYears;

    private DateGenerator dateGenerator;

    public BirthDateGenerator() {
        this(18, 80);
    }

    public BirthDateGenerator(int minAgeYears, int maxAgeYears) {
        this.minAgeYears = minAgeYears;
        this.maxAgeYears = maxAgeYears;
        Date today = TimeUtil.today().getTime();
        Calendar min = TimeUtil.calendar(today);
        min.add(Calendar.YEAR, -maxAgeYears);
        Calendar max = TimeUtil.calendar(today);
        max.add(Calendar.YEAR, -minAgeYears);
        dateGenerator = new DateGenerator(min.getTime(), max.getTime(), Period.DAY.getMillis());
        dateGenerator.setDistribution(Sequence.RANDOM);
    }

    public Class<Date> getGeneratedType() {
        return Date.class;
    }

    public Date generate() throws IllegalGeneratorStateException {
       return dateGenerator.generate();
    }

    public String toString() {
        return getClass().getSimpleName() + "[minAgeYears=" + minAgeYears + ", maxAgeYears=" + maxAgeYears + ']';
    }
}
