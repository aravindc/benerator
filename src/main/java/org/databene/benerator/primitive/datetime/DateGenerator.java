/*
 * (c) Copyright 2006 by Volker Bergmann. All rights reserved.
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

import org.databene.benerator.primitive.number.adapter.LongGenerator;
import org.databene.benerator.Generator;
import org.databene.commons.Period;
import org.databene.model.function.Distribution;
import org.databene.model.function.Sequence;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * creates date values by a LongGenerator.
 * <br/>
 * Created: 07.06.2006 22:54:28
 * @see LongGenerator
 */
public class DateGenerator implements Generator<Date> {
    
    /** The generator to use for generating millisecond values */
    private LongGenerator source;

    // constructors ----------------------------------------------------------------------------------------------------

    /** Initializes the generator to create days within about the last 80 years with a one-day resolution */
    public DateGenerator() {
        this(defaultStartDate(), currentDay(), Period.DAY.getMillis());
    }

    /** Initializes the generator to create dates with a uniform distribution */
    public DateGenerator(Date min, Date max, long precision) {
        this(min, max, precision, Sequence.RANDOM);
    }

    /** Initializes the generator to create dates of a Sequence or WeightFunction */
    public DateGenerator(Date min, Date max, long precision, Distribution distribution) {
        source = new LongGenerator(
                (min != null ? min.getTime() : Long.MIN_VALUE),
                (max != null ? max.getTime() : Long.MAX_VALUE),
                precision,
                distribution
        );
    }

    // v0.5.7 TODO make the sequence contain the variation params and remove this method
    public DateGenerator(Date min, Date max, long precision, Distribution distribution, Date variation1, Date variation2) {
        source = new LongGenerator(
                (min != null ? min.getTime() : Long.MIN_VALUE),
                (max != null ? max.getTime() : Long.MAX_VALUE),
                precision,
                distribution,
                (variation1 != null ? variation1.getTime() + TimeZone.getDefault().getRawOffset() : 1000L),
                (variation2 != null ? variation2.getTime() + TimeZone.getDefault().getRawOffset() : 1000L)
        );
    }

    // config properties -----------------------------------------------------------------------------------------------

    /** Returns the earliest date to generate */
    public Date getMin() {
        return new Date(source.getMin());
    }

    /** Sets the earliest date to generate */
    public void setMin(Date min) {
        source.setMin(min.getTime());
    }

    /** Returns the latest date to generate */
    public Date getMax() {
        return new Date(source.getMax());
    }

    /** Sets the latest date to generate */
    public void setMax(Date max) {
        source.setMax(max.getTime());
    }

    /** Returns the date precision in milliseconds */
    public Long getPrecision() {
        return source.getPrecision();
    }

    /** Sets the date precision in milliseconds */
    public void setPrecision(Long precision) {
        source.setPrecision(precision);
    }

    /** Returns the distribution used */
    public Distribution getDistribution() {
        return source.getDistribution();
    }

    /** Sets the distribution to use */
    public void setDistribution(Distribution distribution) {
        source.setDistribution(distribution);
    }

    /** Returns the first sequence-specific variation parameter */
    public Long getVariation1() {
        return source.getVariation1();
    }

    /** Sets the first sequence-specific variation parameter */
    public void setVariation1(Long varation1) {
        source.setVariation1(varation1);
    }

    /** Returns the second sequence-specific variation parameter */
    public Long getVariation2() {
        return source.getVariation2();
    }

    /** Sets the second sequence-specific variation parameter */
    public void setVariation2(Long variation2) {
        source.setVariation2(variation2);
    }

    // source interface ---------------------------------------------------------------------------------------------

    public Class<Date> getGeneratedType() {
        return Date.class;
    }

    public void validate() {
        source.validate();
    }

    /** Generates a Date by creating a millisecond value from the source generator and wrapping it into a Date */
    public Date generate() {
        return new Date(source.generate());
    }

    public void reset() {
        source.reset();
    }

    public void close() {
        source.close();
    }

    public boolean available() {
        return source.available();
    }

    // implementation --------------------------------------------------------------------------------------------------

    /** Returns the default start date as 80 years ago */
    private static Date defaultStartDate() {
        return new Date(currentDay().getTime() - 80L * 365 * Period.DAY.getMillis());
    }

    /** Returns the current day as Date value rounded to midnight */
    private static Date currentDay() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH),
                calendar.get(GregorianCalendar.DAY_OF_MONTH),
                0,
                0,
                0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return new Date();
    }

    @Override
    public String toString() {
    	return getClass().getSimpleName() + '[' + getMin() + '-' + getMax() + ']';
    }
}
