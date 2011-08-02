/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.factory;

import java.util.Locale;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.LocaleUtil;
import org.databene.model.data.Uniqueness;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link RegexGeneratorFactory} with the {@link EquivalenceGeneratorFactory}.<br/><br/>
 * Created: 07.07.2011 13:39:12
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class RegexStringGeneratorFactory_equivalenceTest extends GeneratorTest {

    private static Locale realLocale;
    
    @BeforeClass
    public static void setUpFallbackLocale() throws Exception {
    	realLocale = Locale.getDefault();
        Locale.setDefault(LocaleUtil.getFallbackLocale());
    }
    
    @AfterClass
    public static void restoreRealLocale() {
    	Locale.setDefault(realLocale);
    }

    
    
    // tests -----------------------------------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testNullPattern() throws Exception {
        createGenerator(null, 0, null);
    }

    @Test
    public void testEmptyPattern() throws Exception {
    	expectSequence("", "");
    }

    @Test
    public void testConstant() throws Exception {
    	expectSequence("a", "a");
    	expectSequence("ab", "ab");
    	expectSequence("abc@xyz\\.com", "abc@xyz.com");
    }
    
    @Test
    public void testEscapeCharacters() throws Exception {
        expectSequence("\\-\\+\\*\\.\\?\\,\\&\\^\\$\\\\\\|", "-+*.?,&^$\\|");
        expectSequence("\\(\\)", "()");
        expectSequence("\\[\\]", "[]");
        expectSequence("\\{\\}", "{}");
    }
    
    @Test
    public void testStartEndCharacters() throws Exception {
    	expectSequence("^ABC$", "ABC");
    }

    @Test
    public void testCardinalities() throws Exception {
        expectSequence("a", "a");
        expectSequence("a?", "", "a");
        expectSequence("a*", 0, 6, "", "a", "aaa", "aaaaa", "aaaaaa");
        expectSequence("a+", 0, 8, "a", "aa", "aaaa", "aaaaaaa", "aaaaaaaa");
        expectSequence("[a]", "a");
        expectSequence("a{3}", "aaa");
        expectSequence("a{3,}", 0, 11, "aaa", "aaaa", "aaaaaaa", "aaaaaaaaaa", "aaaaaaaaaaa");
        expectSequence("a{1,6}", "a", "aa", "aaa", "aaaaa", "aaaaaa");
    }
    

    @Test
    public void testRanges() throws Exception {
    	expectSequence("[a-e]", "a", "c", "e");
    	expectSequence("[A-Ea-e]", "A", "C", "E", "a", "c", "e");
        expectSet("[a]{1,2}", "a", "aa");
        expectSet("x[ab]{3}x", "xaaax", "xbbbx");
        expectSet("[01]{2}/[01]{2}", "00/00", "00/11", "11/00", "11/11");
        expectSet("[01]{2,3}", "00", "11", "000", "111");
        expectSet("[0-9]{2}", "00", "55", "99");
    }

    @Test
    public void testPredefinedClasses() throws Exception {
    	expectSequence("\\d", "0", "5", "9");
        expectSequence("\\w", "A", "N", "Z", "a", "n", "z", "0", "5", "9", "_");
    }

    @Test
    public void testCombinations() throws Exception {
        expectSequence("[\\d^0-2]", "3", "6", "9");
        expectSet("[BL][au]", "Ba", "Bu", "La", "Lu");
        expectSet("[1-9]\\d{1,2}", 
        		"10", "15", "19", "100", "155", "199", 
        		"50", "55", "59", "500", "555", "599", 
        		"90", "95", "99", "900", "955", "999");
        expectSet("[F-H][aio]{2}", "Faa", "Fii", "Foo", "Gaa", "Gii", "Goo", "Haa", "Hii", "Hoo");
    }

    @Test
    public void testChoices() throws Exception {
        expectSet("a|b|c|d|e", "a", "b", "c", "d", "e");
        expectSet("alpha|beta", "alpha", "beta");
        expectSet("a{1,2}|b{2,3}", "a", "aa", "bb", "bbb");
    }

    @Test
    public void testGroups() throws Exception {
    	expectSet("(abc){1,3}", "abc", "abcabc", "abcabcabc");
    	expectSet("(ab?){1,2}", "a", "ab", "aa", "aab", "aba", "abab");
        expectSet("x(ab){1,2}x", "xabx", "xababx");
        expectSet("x(a[01]{2}){1,2}x", "xa00x", "xa11x", "xa00a00x", "xa11a00x", "xa00a11x", "xa11a11x");
    }

    @Test
    public void testGroupedChoices() throws Exception {
        expectSet("(a|b|c){1,2}", "a", "b", "c", "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc");
        expectSet("x(a|b)x", "xax", "xbx");
        expectSet("x(a|b){2}x", "xaax", "xabx", "xbax", "xbbx");
        expectSet("x(a|b){1,2}x", "xax", "xbx", "xaax", "xabx", "xbax", "xbbx");
        expectSet("([a]{1,2}|b)", "a", "aa", "b");
        expectSet("x([01]{1,2}|b)x", "x0x", "x1x", "x00x", "x11x", "xbx");
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    
    private void expectSequence(String regex, String... products) {
    	expectSequence(regex, 0, null, products);
    }
    
    private void expectSet(String regex, String... products) {
    	expectUniquelyGeneratedSet(createGenerator(regex, 0, null), products);
    }
    
    private void expectSequence(String regex, int minLength, Integer maxLength, String... products) {
    	expectGeneratedSequence(createGenerator(regex, minLength, maxLength), products).withCeasedAvailability();
    }
    
    private NonNullGenerator<String> createGenerator(String pattern, int minLength, Integer maxLength) {
    	NonNullGenerator<String> generator = RegexGeneratorFactory.create(pattern, minLength, maxLength, 
    			Uniqueness.NONE, new EquivalenceGeneratorFactory());
    	generator.init(context);
		return generator;
    }
    
}
