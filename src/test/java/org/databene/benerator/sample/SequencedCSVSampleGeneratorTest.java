/*
 * (c) Copyright 2008 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.databene.commons.CollectionUtil;
import org.databene.commons.converter.AnyConverter;
import org.databene.commons.converter.ParseFormatConverter;
import org.databene.benerator.GeneratorClassTest;

/**
 * Tests the {@link SequencedCSVSampleGenerator}.<br/>
 * Created: 26.07.2007 18:16:11
 * @author Volker Bergmann
 */
public class SequencedCSVSampleGeneratorTest extends GeneratorClassTest {

    public SequencedCSVSampleGeneratorTest() {
        super(SequencedCSVSampleGenerator.class);
    }

    private static final String DATE_FILE_PATH = "org/databene/benerator/csv/dates.csv";
    private static final String EMPTY_FILE_PATH = "org/databene/benerator/csv/empty.csv";

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public void testSmallSet() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        ParseFormatConverter<Date> converter = new ParseFormatConverter<Date>(Date.class, format);
        SequencedCSVSampleGenerator<Date> generator = new SequencedCSVSampleGenerator<Date>(DATE_FILE_PATH, converter);
        List<Date> expectedDates = CollectionUtil.toList(sdf.parse("01.02.2003"), sdf.parse("02.02.2003"), sdf.parse("03.02.2003"));
        for (int i = 0; i < 100; i++) {
            Date generatedDate = generator.generate();
            assertTrue("generated date not in expected value set: " + sdf.format(generatedDate),
                    expectedDates.contains(generatedDate));
        }
    }

    public void testBigSet() throws ParseException {
        SequencedCSVSampleGenerator<Integer> generator 
        	= new SequencedCSVSampleGenerator<Integer>(EMPTY_FILE_PATH, new AnyConverter<String,Integer>(Integer.class));
        generator.validate();
        for (int i = 0; i < 200000; i++)
        	generator.addValue(i % 100);
        for (int i = 0; i < 100; i++) {
            int product = generator.generate();
            assertTrue("generated value not in expected value range: " + product, 0 <= product && product <= 99);
        }
    }
}
