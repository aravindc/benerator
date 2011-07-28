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

package org.databene.benerator.primitive;

import java.util.Date;

import org.databene.benerator.primitive.datetime.DateGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.Period;
import org.databene.commons.TimeUtil;

import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the DateGenerator.<br/><br/>
 * Created: 15.03.2008 13:06:24
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class DateGeneratorTest extends GeneratorTest {

	@Test
    public void test() {
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  1), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0, 10), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6, 10), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0,  3), Period.HOUR.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6,  3), Period.HOUR.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6,  3), Period.MILLISECOND.getMillis());
    }

    private void check(Date min, Date max, long granularity) {
        DateGenerator generator = new DateGenerator(min, max, granularity);
        generator.init(context);
        for (int i = 0; i < 10000; i++) {
            Date date = generator.generate();
            assertNotNull(date);
            assertFalse(date.before(min));
            assertFalse(date.after(max));
            long time = date.getTime();
            long time0 = min.getTime();
            assertEquals(0, (time - time0) % granularity);
        }
    }

}
