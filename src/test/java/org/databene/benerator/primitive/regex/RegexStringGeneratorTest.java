package org.databene.benerator.primitive.regex;

import java.util.regex.Pattern;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.LocaleUtil;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 20.08.2006 09:21:19
 */
public class RegexStringGeneratorTest extends GeneratorClassTest {

    private static Log logger = LogFactory.getLog(RegexStringGeneratorTest.class);

    public RegexStringGeneratorTest() {
        super(RegexStringGenerator.class);
    }

    protected void setUp() throws Exception {
        Locale.setDefault(LocaleUtil.getFallbackLocale());
    }

    public void testNullPattern() {
        checkRegexGeneration(null);
    }

    public void testEmptyPattern() {
        checkRegexGeneration("");
    }

    public void testConstant() {
        assertEquals("a", new RegexStringGenerator("a").generate());
        assertEquals("ab", new RegexStringGenerator("ab").generate());
        assertEquals("abc@xyz.com", new RegexStringGenerator("abc@xyz\\.com", 16).generate());
    }

    public void testCardinalities() {
        checkRegexGeneration("a");
        checkRegexGeneration("a?");
        checkRegexGeneration("a*");
        checkRegexGeneration("a+");
        checkRegexGeneration("[a]");
        checkRegexGeneration("a{3}");
        checkRegexGeneration("a{3,}");
        checkRegexGeneration("a{3,5}");
    }

    public void testRanges() {
        checkRegexGeneration("[a-c]");
        checkRegexGeneration("[a-cA-C]");

    }

    public void testPredefinedClasses() {
        checkRegexGeneration("\\d");
        checkRegexGeneration("\\s");
        checkRegexGeneration("\\w");
    }

    public void testCombinations() {
        checkRegexGeneration("[^\\w]");

        checkRegexGeneration("\\w+\\d+");
        checkRegexGeneration("[BL][au]");
        checkRegexGeneration("[1-9]\\d{0,3}");
        checkRegexGeneration("[F-H][aeiou]{2}");

        checkRegexGeneration("\\+[1-9]\\d{1,2}/\\d{1,5}/\\d{5,8}");
        checkRegexGeneration("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
    }

    public void testGroups() {
        checkRegexGeneration("(abc){1,3}");
        checkRegexGeneration("(a+b?c*){1,3}");
    }

    public void testAlternatives() {
        checkRegexGeneration("(a|b|c){1,3}");
        checkRegexGeneration("(([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])");
    }

    public void testUniqueCharSets() {
        expectUniqueFromSet(new RegexStringGenerator("[a]{1,2}", null, 30, true),
                "a", "aa").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("x[ab]{3}x", null, 30, true),
                "xaaax", "xaabx", "xabax", "xabbx", "xbaax", "xbabx", "xbbax", "xbbbx").withCeasedAvailability();
        expectUniqueProducts(new RegexStringGenerator("[01]{2}/[01]{2}", null, 30, true), 16).withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("[01]{2,3}", null, 30, true),
                "00", "01", "10", "11", "000", "001", "010", "011", "100", "101", "110", "111").withCeasedAvailability();
        expectUniqueProducts(new RegexStringGenerator("0[0-9]{2,4}/[1-9][0-9]5", null, 30, true), 5).withContinuedAvailability();
    }

    public void testUniqueGroups() {
        expectUniqueFromSet(new RegexStringGenerator("x(ab){1,2}x", null, 30, true), "xabx", "xababx").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("x(a[01]{2}){1,2}x", null, 30, true),
                "xa00x", "xa01x", "xa10x", "xa11x",
                "xa00a00x", "xa01a00x", "xa10a00x", "xa11a00x",
                "xa00a01x", "xa01a01x", "xa10a01x", "xa11a01x",
                "xa00a10x", "xa01a10x", "xa10a10x", "xa11a10x",
                "xa00a11x", "xa01a11x", "xa10a11x", "xa11a11x"
        ).withCeasedAvailability();
    }

    public void testUniqueAlternatives() {
        expectUniqueFromSet(new RegexStringGenerator("x(a|b)x", null, 30, true), "xax", "xbx").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("x(a|b){2}x", null, 30, true), "xaax", "xabx", "xbax", "xbbx").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("x(a|b){1,2}x", null, 30, true), "xax", "xbx", "xaax", "xabx", "xbax", "xbbx").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("([a]{1,2}|b)", null, 30, true), "a", "aa", "b").withCeasedAvailability();
        expectUniqueFromSet(new RegexStringGenerator("x([01]{1,2}|b)x", null, 30, true),
                "x0x", "x1x", "x00x", "x01x", "x10x", "x11x", "xbx").withCeasedAvailability();
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    private void checkRegexGeneration(String regex) {
        logger.debug("checking generation for regex '" + regex + "'");
        RegexStringGenerator generator = new RegexStringGenerator(regex);
        checkRegexGeneration(generator, regex, 0, 255, true);
    }

    public static void checkRegexGeneration(Generator<String> generator, String regex, int minLength, int maxLength, boolean nullable) {
        String message = "Generation failed for: '" + regex + "'";
        for (int i = 0; i < 3; i++) {
            String output = generator.generate();
            logger.debug("checking sample '" + output + "' against regex '" + regex + "'");
            if (!nullable)
                assertNotNull(output);
            if (regex == null)
                assertNull(output);
            else {
                if (regex.length() == 0)
                    assertEquals("", output);
                else
                    assertTrue(message, Pattern.matches(regex, output));
                assertTrue(output.length() >= minLength);
                assertTrue(output.length() <= maxLength);
            }
        }
    }
}
