package org.databene.benerator.sample;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.databene.commons.ArrayUtil;
import org.databene.model.converter.ParseFormatConverter;
import org.databene.benerator.GeneratorClassTest;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 26.07.2007 18:16:11
 */
public class SequencedCSVSampleGeneratorTest extends GeneratorClassTest {

    public SequencedCSVSampleGeneratorTest() {
        super(SequencedCSVSampleGenerator.class);
    }

    // TODO v0.4 test with large data amounts

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
