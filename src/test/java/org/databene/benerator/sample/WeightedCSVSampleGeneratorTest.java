package org.databene.benerator.sample;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.databene.commons.CollectionUtil;
import org.databene.commons.Encodings;
import org.databene.commons.converter.ParseFormatConverter;
import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link WeightedCSVSampleGenerator}.<br/><br/>
 * Created: 27.09.2006 23:16:11
 * @since 0.1
 * @author Volker Bergmann
 */
public class WeightedCSVSampleGeneratorTest extends GeneratorTest {

    private static final String FILE_PATH = "org/databene/benerator/csv/dates.csv";

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Test
    public void test() throws ParseException {
    	// prepare
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        ParseFormatConverter<Date> converter = new ParseFormatConverter<Date>(Date.class, format, false);
        WeightedCSVSampleGenerator<Date> generator = new WeightedCSVSampleGenerator<Date>(FILE_PATH, Encodings.UTF_8, converter);
        generator.init(context);
        // run test
        List<Date> expectedDates = CollectionUtil.toList(sdf.parse("01.02.2003"), sdf.parse("02.02.2003"), sdf.parse("03.02.2003"));
        for (int i = 0; i < 10; i++) {
            Date generatedDate = GeneratorUtil.generateNonNull(generator);
            assertTrue("generated date not in expected value set: " + sdf.format(generatedDate),
                    expectedDates.contains(generatedDate));
        }
    }
    
}
