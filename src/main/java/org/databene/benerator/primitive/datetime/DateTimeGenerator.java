/*
 * (c) Copyright 2008-2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.datetime;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.commons.Period;
import org.databene.commons.TimeUtil;
import org.databene.commons.converter.DateString2DurationConverter;

/**
 * Creates DateTimes with separate date and time distribution characteristics.<br/><br/>
 * Created: 29.02.2008 18:19:55
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DateTimeGenerator extends LightweightDateGenerator {
    
    private DateString2DurationConverter dateConverter = new DateString2DurationConverter();

    private Generator<Long> dateGenerator;
    private Generator<Long> timeOffsetGenerator;
    
    private long minDate;
    private long maxDate;
    private long datePrecision = Period.DAY.getMillis();
    private Distribution dateDistribution;
    
    private long minTime;
    private long maxTime;
    private long timePrecision;
    private Distribution timeDistribution;
    
    boolean dirty;
    
    public DateTimeGenerator() {
        this(
            TimeUtil.add(TimeUtil.today().getTime(), Calendar.YEAR, -1), 
            TimeUtil.today().getTime(), 
            TimeUtil.time(9, 0), 
            TimeUtil.time(17, 0));
    }

    public DateTimeGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
        setMinDate(minDate);
        setMaxDate(maxDate);
        setMinTime(minTime);
        setMaxTime(maxTime);
        setDateDistribution(Sequence.RANDOM);
        setTimeDistribution(Sequence.RANDOM);
        setDatePrecision("00-00-01");
        setTimePrecision(TimeUtil.time(0, 1));
        this.dirty = true;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinDate(Date minDate) {
        this.minDate = minDate.getTime();
    }
    
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate.getTime();
    }
    
    public void setDatePrecision(String datePrecision) {
        this.datePrecision = dateConverter.convert(datePrecision);
    }
    
    public void setDateDistribution(Distribution distribution) {
        this.dateDistribution = distribution;
    }
    
    public void setMinTime(Time minTime) {
        this.minTime = TimeUtil.millisSinceOwnEpoch(minTime);
    }
    
    public void setMaxTime(Time maxTime) {
        this.maxTime = TimeUtil.millisSinceOwnEpoch(maxTime);
    }
    
    public void setTimePrecision(Time timePrecision) {
        this.timePrecision = TimeUtil.millisSinceOwnEpoch(timePrecision);
    }
    
    public void setTimeDistribution(Distribution distribution) {
        this.timeDistribution = distribution;
    }

    // Generator interface ---------------------------------------------------------------------------------------------
    
    @Override
    public void validate() {
        super.validate();
    	// TODO support uniqueness?
    	this.dateGenerator = GeneratorFactory.getNumberGenerator(
    			Long.class, minDate, maxDate, datePrecision, dateDistribution, false, 0);
    	this.timeOffsetGenerator = GeneratorFactory.getNumberGenerator(
    			Long.class, minTime, maxTime, timePrecision, timeDistribution, false, 0);
        dirty = false;
    }
    
    @Override
    public boolean available() {
    	if (dirty)
    		validate();
    	return dateGenerator.available() && timeOffsetGenerator.available();
    }
    
    public Date generate() {
    	if (dirty)
    		validate();
    	return new Date(dateGenerator.generate() + timeOffsetGenerator.generate());
    }

}
