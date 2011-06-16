/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import java.util.Calendar;
import java.util.Date;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.distribution.Distribution;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.util.ThreadSafeGenerator;
import org.databene.commons.BeanUtil;
import org.databene.commons.ConfigurationError;
import org.databene.commons.TimeUtil;

/**
 * Generates dates with a granularity of days, months or years.<br/><br/>
 * Created: 12.10.2010 20:57:18
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DayGenerator extends ThreadSafeGenerator<Date> {

	protected Date min;
	protected Date max;
	protected Distribution distribution;
	protected boolean unique;
	
	protected int yearGranularity;
	protected int monthGranularity;
	protected int dayGranularity;
	
	private Calendar minCalendar;
	private Generator<Integer> multiplierGenerator;

	public DayGenerator() {
		this(TimeUtil.date(TimeUtil.currentYear() - 5, 0, 1),
			TimeUtil.today(),
			SequenceManager.RANDOM_SEQUENCE,
			false);
	}

	public DayGenerator(Date min, Date max, Distribution distribution, boolean unique) {
	    this.min = min;
	    this.max = max;
	    this.distribution = distribution;
	    this.unique = unique;
	    this.yearGranularity = 0;
	    this.monthGranularity = 0;
	    this.dayGranularity = 1;
    }

	public void setMin(Date min) {
    	this.min = min;
    }

	public void setMax(Date max) {
    	this.max = max;
    }

	public void setPrecision(String precisionSpec) {
		String[] tokens = precisionSpec.split("-");
		if (tokens.length != 3)
			throw new ConfigurationError("Illegal date granularity spec: " + precisionSpec);
		this.yearGranularity = Integer.parseInt(tokens[0]);
		this.monthGranularity = Integer.parseInt(tokens[1]);
		this.dayGranularity = Integer.parseInt(tokens[2]);
	}
	
	public void setDistribution(Distribution distribution) {
    	this.distribution = distribution;
    }

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public Class<Date> getGeneratedType() {
	    return Date.class;
    }

	@Override
	public synchronized void init(GeneratorContext context) {
		this.minCalendar = TimeUtil.calendar(min);
		int count = 0;
		Calendar calendar = (Calendar) minCalendar.clone();
		do {
			// TODO v0.6.7 tune performance
			calendar.add(Calendar.YEAR, yearGranularity);
			calendar.add(Calendar.MONTH, monthGranularity);
			calendar.add(Calendar.DAY_OF_MONTH, dayGranularity);
			count++;
		} while (!max.before(calendar.getTime()));
		multiplierGenerator = GeneratorFactory.getNumberGenerator(
				Integer.class, 0, count - 1, 1, distribution, unique);
		multiplierGenerator.init(context);
	    super.init(context);
	}
	
	public Date generate() {
		assertInitialized();
		Integer multiplier = multiplierGenerator.generate();
		if (multiplier == null)
			return null;
		Calendar calendar = (Calendar) minCalendar.clone();
		calendar.add(Calendar.YEAR,         multiplier * yearGranularity );
		calendar.add(Calendar.MONTH,        multiplier * monthGranularity);
		calendar.add(Calendar.DAY_OF_MONTH, multiplier * dayGranularity  );
		return calendar.getTime();
    }

	@Override
	public void reset() {
		multiplierGenerator.reset();
		super.reset();
	}
	
	@Override
	public void close() {
		multiplierGenerator.close();
		super.close();
	}
	
	@Override
	public String toString() {
		return BeanUtil.toString(this);
	}
	
}
