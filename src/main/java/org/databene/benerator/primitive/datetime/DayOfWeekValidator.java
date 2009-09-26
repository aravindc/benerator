/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.databene.commons.Assert;
import org.databene.commons.TimeUtil;
import org.databene.commons.Validator;

/**
 * Filters {@link Date}s by their day of week.
 * All days of the week are supported by default. 
 * Attention: The weekday array begins with Monday (as defined in ISO_8601), 
 * not with Sunday (as used in {@link java.util.Calendar}).<br/>
 * <br/>
 * Created at 23.09.2009 17:51:52
 * @since 0.6.0
 * @author Volker Bergmann
 * @see <a href="http://en.wikipedia.org/wiki/ISO_8601#Week_dates">ISO 8601</a>
 */

public class DayOfWeekValidator implements Validator<Date> { // TODO rename Validator to Filter?
// TODO merge with javax.validation?
	
	/** 
	 * holds a flag for each weekday that tells if it is supported. 
	 */
	private boolean daysOfWeekSupported[];
	
    public DayOfWeekValidator() {
    	this.daysOfWeekSupported = new boolean[7];
    	Arrays.fill(daysOfWeekSupported, true);
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public void setDaysOfWeekSupported(boolean... daysOfWeekSupported) {
    	Assert.equals(7, daysOfWeekSupported.length, getClass().getName() + ".day");
    	System.arraycopy(daysOfWeekSupported, 0, this.daysOfWeekSupported, 0, 7);
    }
    
    public void setWeekdaysSupported(boolean weekdaySupported) {
    	Arrays.fill(daysOfWeekSupported, 0, 5, weekdaySupported);
    }
    
    public void setWeekendsSupported(boolean weekendSupported) {
    	daysOfWeekSupported[6] = weekendSupported;
    	daysOfWeekSupported[5] = weekendSupported;
    }
    
	public boolean valid(Date candidate) {
	    int isoDayOfWeek = dayOfWeekIndex(candidate);
		return daysOfWeekSupported[isoDayOfWeek];
    }

    static int dayOfWeekIndex(Date candidate) {
	    Calendar calendar = TimeUtil.calendar(candidate);
		int javaDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int isoDayOfWeek = (javaDayOfWeek == Calendar.SUNDAY ? 6 : javaDayOfWeek - Calendar.MONDAY);
	    return isoDayOfWeek;
    }

}
