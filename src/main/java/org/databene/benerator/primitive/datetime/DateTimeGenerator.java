/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.commons.TimeUtil;

/**
 * Creates DateTimes with separate date and time distribution characteristics.<br/><br/>
 * Created: 29.02.2008 18:19:55
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DateTimeGenerator extends CompositeGenerator<Date> {
    
    private DayGenerator dateGenerator;
    private Generator<Long> timeOffsetGenerator;
    
    Date minDate;
    Date maxDate;
    String datePrecision;
    Distribution dateDistribution;
    
    long minTime;
    long maxTime;
    long timePrecision;
    Distribution timeDistribution;
    
    public DateTimeGenerator() {
        this(
            TimeUtil.add(TimeUtil.today(), Calendar.YEAR, -1), 
            TimeUtil.today(), 
            TimeUtil.time(9, 0), 
            TimeUtil.time(17, 0));
    }

    public DateTimeGenerator(Date minDate, Date maxDate, Time minTime, Time maxTime) {
    	super(Date.class);
        setMinDate(minDate);
        setMaxDate(maxDate);
        setMinTime(minTime);
        setMaxTime(maxTime);
        setDateDistribution(SequenceManager.RANDOM_SEQUENCE);
        setTimeDistribution(SequenceManager.RANDOM_SEQUENCE);
        setDatePrecision("00-00-01");
        setTimePrecision(TimeUtil.time(0, 1));
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
    
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public void setDatePrecision(String datePrecision) {
    	this.datePrecision = datePrecision;
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
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	this.dateGenerator = registerComponent(
    			new DayGenerator(minDate, maxDate, dateDistribution, false));
    	dateGenerator.setPrecision(datePrecision);
    	this.dateGenerator.init(context);
    	this.timeOffsetGenerator = registerComponent(context.getGeneratorFactory().createNumberGenerator(
    			Long.class, minTime, maxTime, timePrecision, timeDistribution, false));
    	this.timeOffsetGenerator.init(context);
        super.init(context);
    }

    public Date generate() {
    	assertInitialized();
    	Date dateGeneration = dateGenerator.generate();
    	Long timeOffsetGeneration = timeOffsetGenerator.generate();
    	if (dateGeneration!= null && timeOffsetGeneration != null)
    		return new Date(dateGeneration.getTime() + timeOffsetGeneration);
    	else
    		return null;
    }

}
