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

package org.databene.benerator.wrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.benerator.Generator;
import org.databene.commons.TimeUtil;

/**
 * Created: 11.10.2006 23:12:21
 */
public class ParseFormatGeneratorTest extends GeneratorClassTest {

    public ParseFormatGeneratorTest() {
        super(ParseFormatGenerator.class);
    }

    private static final Date DATE = TimeUtil.date(2001, 3, 4);
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATE_STRING = FORMAT.format(DATE);

    public void testParse() throws ParseException {
        Generator<String> source = new ConstantGenerator<String>(DATE_STRING);
        ParseFormatGenerator<String, Date> generator = new ParseFormatGenerator(Date.class, source, FORMAT);
        Date d = generator.generate();
        assertEquals(DATE, d);
    }
             
}
