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

import org.databene.commons.ArrayUtil;
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

    // TODO v0.5 test with large data amounts

    private static final String FILE_PATH = "org/databene/benerator/csv/dates.csv";

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public void test() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        ParseFormatConverter<Date> converter = new ParseFormatConverter<Date>(Date.class, format);
        SequencedCSVSampleGenerator<Date> generator = new SequencedCSVSampleGenerator<Date>(FILE_PATH, converter);
        List<Date> expectedDates = ArrayUtil.toList(sdf.parse("01.02.2003"), sdf.parse("02.02.2003"), sdf.parse("03.02.2003"));
        for (int i = 0; i < 10; i++) {
            Date generatedDate = generator.generate();
            assertTrue("generated date not in expected value set: " + sdf.format(generatedDate),
                    expectedDates.contains(generatedDate));
        }
    }
}
