/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.primitive.number.adapter.LongGenerator;
import org.databene.benerator.util.LightweightGenerator;
import org.databene.commons.TimeUtil;
import org.databene.commons.converter.DateString2DurationConverter;
import org.databene.model.function.Sequence;
import org.databene.model.function.String2DistributionConverter;

/**
 * Creates DateTimes with separate date and time distribution characteristics.<br/><br/>
 * Created: 29.02.2008 18:19:55
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DateTimeGenerator extends LightweightGenerator<Date> {
    
    private DateString2DurationConverter dateConverter = new DateString2DurationConverter();

    private LongGenerator dateGenerator = new LongGenerator();
    private LongGenerator timeGenerator = new LongGenerator();
    
    public DateTimeGenerator() {
        this(
            TimeUtil.add(TimeUtil.today().getTime(), Calendar.YEAR, -1), 
            TimeUtil.today().getTime(), 
            TimeUtil.time(9, 0), 
            TimeUtil.time(17, 0));
    }

    public DateTimeGenerator(Date minDate, Date maxDate, Time minTime, Time maxTine) {
	    super(Date.class);
        setMinDate(minDate);
        setMaxDate(maxDate);
        setMinTime(minTime);
        setMaxTime(maxTine);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setMinDate(Date minDate) {
        dateGenerator.setMin(minDate.getTime());
    }
    
    public void setMaxDate(Date maxDate) {
        dateGenerator.setMax(maxDate.getTime());
    }
    
    public void setDatePrecision(String datePrecision) {
        dateGenerator.setPrecision(dateConverter.convert(datePrecision));
    }
    
    public void setDateDistribution(String distribution) {
        dateGenerator.setDistribution(Sequence.getInstance(distribution, true));
    }
    
    public void setMinTime(Time minTime) {
        timeGenerator.setMin(minTime.getTime());
    }
    
    public void setMaxTime(Time maxTime) {
        timeGenerator.setMax(maxTime.getTime());
    }
    
    public void setTimePrecision(Time timePrecision) {
        timeGenerator.setPrecision(timePrecision.getTime());
    }
    
    public void setTimeDistribution(String distribution) {
        timeGenerator.setDistribution(String2DistributionConverter.parse(distribution, null, null)); // TODO v0.6 support script expressions
    }

    // Generator interface ---------------------------------------------------------------------------------------------
    
    public Date generate() {
        return new Date(dateGenerator.generate() + timeGenerator.generate());
    }

}
