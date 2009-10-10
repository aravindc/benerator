/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.script;

import org.databene.benerator.test.Person;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.TimeUtil;
import org.databene.commons.context.DefaultContext;
import org.databene.commons.expression.ExpressionUtil;

import junit.framework.TestCase;

/**
 * Tests the {@link BeneratorScriptParser}.<br/>
 * <br/>
 * Created at 05.10.2009 19:02:05
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class BeneratorScriptParserTest extends TestCase {

	private String stringProp;
	private int intProp;

    public BeneratorScriptParserTest() {
	    super();
    }

    public BeneratorScriptParserTest(String name) {
	    super(name);
    }

	public BeneratorScriptParserTest(String stringProp, int intProp) {
		this.stringProp = stringProp;
		this.intProp = intProp;
    }

	public void testNullLiteral() throws Exception {
		checkExpression(null, "null");
	}

	public void testBooleanLiteral() throws Exception {
		checkExpression(true,  "true");
		checkExpression(false, "false");
	}

	public void testIntLiteral() throws Exception {
		checkExpression(1, "1");
		checkExpression(0, "0");
		checkExpression(1000000000, "1000000000");
		checkExpression(Integer.MAX_VALUE, String.valueOf(Integer.MAX_VALUE));
	}

	public void testLongLiteral() throws Exception {
		checkExpression(123456789012345L, "123456789012345");
		long border = Integer.MAX_VALUE + 1L;
		checkExpression(border, String.valueOf(border));
	}

	public void testDoubleLiteral() throws Exception {
		checkExpression(0., "0.0");
		checkExpression(1.5, "1.5");
		checkExpression(100., "1E+2");
		checkExpression(125., "1.25E+2");
	}

	public void testStringLiteral() throws Exception {
		checkExpression("Test", "'Test'");
		checkExpression("", "''");
	}
	
	public void testConstructor() throws Exception {
		checkExpression("", "new java.lang.String()");
		checkExpression("Test", "new java.lang.String('Test')");
		checkExpression("Test", "new java.lang.String(new java.lang.String('Test'))");
	}
	
	public void testBeanConstruction() throws Exception {
		checkExpression(new BeneratorScriptParserTest("Alice", 23), 
				"new " + getClass().getName() + "[stringProp='Alice', intProp=23]");
	}
	
	public void testStaticInvocation() throws Exception {
		checkExpression("it works!", getClass().getName() + ".exclamate('it works')");
	}
	
	public void testReference() throws Exception {
		Context context = new DefaultContext();
		context.set("testString", "Hello");
		checkExpression("Hello", "testString", context);
	}
	
	public void testReferenceInvocation() throws Exception {
		Context context = new DefaultContext();
		context.set("testString", "Hello");
		checkExpression(5, "testString.length()", context);
	}
	
	public void testObjectInvocation() throws Exception {
		checkExpression(3, "'123'.length()");
	}
	
	public void testArrayIndex() throws Exception {
		Context context = new DefaultContext();
		context.set("testArray", new String[] { "Alice", "Bob", "Charly" });
		checkExpression("Bob", "testArray[1]", context);
	}
	
	public void testListIndex() throws Exception {
		Context context = new DefaultContext();
		context.set("testList", CollectionUtil.toList("Alice", "Bob", "Charly"));
		checkExpression("Bob", "testList[1]", context);
	}
	
	public void testMapIndex() throws Exception {
		Context context = new DefaultContext();
		context.set("testMap", CollectionUtil.buildMap("Alice", 23, "Bob", 34, "Charly", 45));
		checkExpression(34, "testMap['Bob']", context);
	}
	
	public void testStringIndex() throws Exception {
		checkExpression('e', "'Hello'[1]");
	}
	
	public void testSubCall() throws Exception {
		checkExpression('l', "'Hello'.substring(1,3).charAt(1)");
	}
	
	public void testStaticSubField() throws Exception {
		checkExpression("hi!!", getClass().getName() + ".staticStringAttrib");
	}
	
	public void testSubField() throws Exception {
		Context context = new DefaultContext();
		context.set("tc", this);
		checkExpression("hi!", "tc.stringAttrib", context);
		checkExpression("hi", "tc.pubField.text", context);
		checkExpression("hi", "new " + getClass().getName() + "().pubField.text");
	}
	
	public void testCast() throws Exception {
		checkExpression(1L, "100000000002 - 100000000001");
		checkExpression(1, "(int) (100000000002 - 100000000001)");
		checkExpression(TimeUtil.date(2009, 9, 6), "(date) '2009-10-06'");
		checkExpression(TimeUtil.date(2009, 9, 6), "(java.util.Date) '2009-10-06'");
		checkExpression("1", "(java.lang.String) 1");
		checkExpression(1, "(java.lang.Integer) 1");
		checkExpression(1, "(int) 1");
		checkExpression(TimeUtil.time(18, 19, 20), "(time) '18:19:20'");
	}
	
	public void testNegation() throws Exception {
		checkExpression(-1, "-1");
		checkExpression(-3, "- (1 + 2)");
	}
	
	public void testBitwiseComplement() throws Exception {
		checkExpression(-2, "~1");
		checkExpression(-4, "~ (1 + 2)");
	}
	
	public void testLogicalComplement() throws Exception {
		checkExpression(false, "! true");
		checkExpression(true, "! (1 + 2 < 2)");
	}
	
	public void testMultipication() throws Exception {
		checkExpression(35, "7 * 5");
		checkExpression(36, "2 + 7 * 5 - 1");
		checkExpression(4.5, "1.5 * 3");
	}
	
	public void testDivision() throws Exception {
		checkExpression(2, "6 / 3");
		checkExpression(2., "6.0 / 3.0");
		checkExpression(3, "7 / 2");
		checkExpression(3.5, "7.0 / 2.0");
	}
	
	public void testModulo() throws Exception {
		checkExpression(2, "11 % 3");
		checkExpression(0, "3 % 3");
		checkExpression(0, "0 % 2");
		checkExpression(0, "10 % 1");
	}
	
	public void testStringSum() throws Exception {
		checkExpression("", "'' + ''");
		checkExpression("", "'' + null");
		checkExpression("Test123", "'Test' + '123'");
		checkExpression("Test123", "'Test' + 123");
		checkExpression("123Test", "123 + 'Test'");
		checkExpression("Test123true", "'Test' + 123 + true");
		checkExpression("implemented at 2009-10-08", "'implemented at ' + (date) '2009-10-08'");
	}
	
	public void testNumberSum() throws Exception {
		checkExpression(0, "0 + 0");
		checkExpression(2, "1 + 1");
		checkExpression(100000000001L, "100000000000 + 1");
		checkExpression(5, "(byte) 3 + (byte) 2)");
		checkExpression(5, "3 + (byte) 2)");
		// TODO check with other types
	}
	
	public void testDateSum() throws Exception {
		checkExpression(TimeUtil.date(1970, 0, 1), "(date) '1970-01-01' + 0");
		checkExpression(TimeUtil.date(1970, 0, 2), "(date) '1970-01-01' + (long) 1000 * 3600 * 24");
		checkExpression(TimeUtil.date(1970, 0, 2), "(long) 1000 * 3600 * 24 + (date) '1970-01-01'");
	}
	
	public void testDateTimeSum() throws Exception {
		checkExpression(TimeUtil.date(1970, 0, 1, 18, 19, 20, 0), "(date) '1970-01-01' + (time) '18:19:20'");
		checkExpression(TimeUtil.date(1970, 0, 1, 18, 19, 20, 0), "(time) '18:19:20' + (date) '1970-01-01'");
		checkExpression(TimeUtil.date(2009, 9, 8, 18, 19, 20, 0), "(date) '2009-10-08' + (time) '18:19:20'");
		checkExpression(TimeUtil.date(2009, 9, 8, 18, 19, 20, 0), "(time) '18:19:20' + (date) '2009-10-08'");
	}
	
	public void testTimestampSum() throws Exception {
		checkExpression(TimeUtil.timestamp(1970, 0, 1, 0, 0, 0, 0), "(timestamp) '1970-01-01' + 0");
		checkExpression(TimeUtil.timestamp(1970, 0, 2, 0, 0, 0, 0), "(timestamp) '1970-01-01' + (long) 1000 * 3600 * 24");
	}
	
	public void testDateDifference() throws Exception {
		checkExpression(TimeUtil.date(1970, 0, 1), "(date) '1970-01-02' - (long) 1000 * 3600 * 24");
	}
	
	public void testTimestampDifference() throws Exception {
		checkExpression(TimeUtil.timestamp(1970, 0, 1, 0, 0, 0, 0), "(timestamp) '1970-01-02' - (long) 1000 * 3600 * 24");
	}
	
	public void testParenthesis() throws Exception {
		checkExpression(1, "6 - 3 - 2");
		checkExpression(1, "(6 - 3) - 2");
		checkExpression(5, "6 - (3 - 2)");
	}
	
	public void testLeftShift() throws Exception {
		checkExpression(  4, " 1 << 2");
		checkExpression(-32, "-4 << 3");
	}
	
	public void testRightShift() throws Exception {
		checkExpression( 1, "   2  >> 1");
		checkExpression( 4, "  32  >> 3");
		checkExpression(-4, "(-32) >> 3");
	}
	
	public void testRightShift2() throws Exception {
		checkExpression(4, "32 >>> 3");
	}
	
	public void testEquals() throws Exception {
		checkExpression(false, "2 == 1");
		checkExpression(true,  "2 == 2");
	}
	
	public void testNotEquals() throws Exception {
		checkExpression(false, "2 != 2");
		checkExpression(true,  "2 != 1");
	}
	
	public void testLessOrEqual() throws Exception {
		checkExpression(false, "2 <= 1");
		checkExpression(true,  "2 <= 2");
		checkExpression(true,  "2 <= 3");
	}
	
	public void testGreaterOrEqual() throws Exception {
		checkExpression(true,  "2 >= 1");
		checkExpression(true,  "2 >= 2");
		checkExpression(false, "2 >= 3");
	}
	
	public void testLess() throws Exception {
		checkExpression(false, "2 < 1");
		checkExpression(false, "2 < 2");
		checkExpression(true,  "2 < 3");
	}
	
	public void testGreater() throws Exception {
		checkExpression(true, "2 > 1");
		checkExpression(false, "2 > 2");
		checkExpression(false, "2 > 3");
	}
	
	public void testAnd() throws Exception {
		checkExpression(1, "1 & 1");
		checkExpression(0, "1 & 2");
		checkExpression(1, "1 & 1 & 1");
	}
	
	public void testExclusiveOr() throws Exception {
		checkExpression(0, "1 ^ 1");
		checkExpression(3, "1 ^ 2");
	}
	
	public void testInclusiveOr() throws Exception {
		checkExpression(1, "1 | 1");
		checkExpression(3, "1 | 2");
	}
	
	public void testConditionalAnd() throws Exception {
		checkExpression(false, "false && false");
		checkExpression(false, "true  && false");
		checkExpression(false, "false && true");
		checkExpression(true,  "true  && true");
	}
	
	public void testConditionalOr() throws Exception {
		checkExpression(false, "false || false");
		checkExpression(true,  "true  || false");
		checkExpression(true,  "false || true");
		checkExpression(true,  "true  || true");
	}
	
	public void testConditionalExpression() throws Exception {
		checkExpression(1, "true ? 1 : 2");
		checkExpression(2, "false ? 1 : 2");
		checkExpression(2, "(false ? 1 : 2)");
		checkExpression("2>1!", "(2 > 1 ? '2>1!' : 'error')");
		checkExpression("4", "(2 > 1 ? (4 > 3 ? '4' : '3') : (7 < 6 ? 6 : 7))");
	}
	
	public void testObjectSpecByRef() throws Exception {
		Context context = new DefaultContext();
		context.set("greeting", "Howdy");
		checkBeanSpec("Howdy", "greeting", context);
	}
	
	public void testObjectSpecByClass() throws Exception {
		checkBeanSpec("", "java.lang.String");
	}
	
	public void testObjectSpecByConstructor() throws Exception {
		checkBeanSpec("Test", "new java.lang.String('Test')");
	}
	
	public void testObjectSpecByProperties() throws Exception {
		checkBeanSpec(new BeneratorScriptParserTest("Alice", 24), 
				"new " + getClass().getName() + "[stringProp='Alice', intProp=24]");
	}
	
	public void testObjectSpecList() throws Exception {
		Expression<?>[] expressions = BeneratorScriptParser.parseBeanSpecList("java.lang.String," + getClass().getName());
		Object[] values = ExpressionUtil.evaluateAll(expressions, new DefaultContext());
		assertEquals(2, values.length);
		assertEquals("", values[0]);
		assertTrue(values[1].getClass() == this.getClass());
	}
	
	// tests migrated from BasicParserTest ---------------------------------------
	
	public void testParseCustomConstruction() throws Exception {
		checkBeanSpec(new Person("Alice", TimeUtil.date(1972, 1, 3), 102, true, 'A'),
				"new org.databene.benerator.test.Person('Alice', (date) '1972-02-03', 102, true, 'A')");
	}
	
	/**
	 * Tests property-based construction
	 */
	public void testParsePropertyConstruction() throws Exception {
		checkBeanSpec(new Person("Alice", TimeUtil.date(1972, 1, 3), 102, true, 'A'),
				"new org.databene.benerator.test.Person[name='Alice', birthDate=(date) '1972-02-03', score=102, " +
				"registered=true, rank='A']");
	}

	
	
	// test members to be read or called from the tested script expressions --------------------------------------------
	
	public static String exclamate(String arg) {
		return arg + "!";
	}
	
	public static String staticStringAttrib = "hi!!";

	public String stringAttrib = "hi!";

	public PubField pubField = new PubField();
	
	public static class PubField {
		public String text = "hi";
	}
	
	public void setStringProp(String stringProp) {
    	this.stringProp = stringProp;
    }

	public void setIntProp(int intProp) {
    	this.intProp = intProp;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    BeneratorScriptParserTest that = (BeneratorScriptParserTest) obj;
	    return (this.intProp == that.intProp && this.stringProp.equals(that.stringProp));
    }
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[stringProp=" + stringProp + ", intProp=" + intProp + "]";
	}

	// private helpers -------------------------------------------------------------------------------------------------

	private void checkExpression(Object expected, String script) throws Exception {
    	checkExpression(expected, script, new DefaultContext());
    }
    
    private void checkExpression(Object expected, String script, Context context) throws Exception {
	    Expression<?> expression = BeneratorScriptParser.parseExpression(script);
		Object actual = expression.evaluate(context);
		assertEqual(expected, actual, script);
    }
    
    private void checkBeanSpec(Object expected, String script) throws Exception {
    	checkBeanSpec(expected, script, new DefaultContext());
    }
    
    private void checkBeanSpec(Object expected, String script, Context context) throws Exception {
	    Expression<?> expression = BeneratorScriptParser.parseBeanSpec(script);
		Object actual = expression.evaluate(context);
		assertEqual(expected, actual, script);
    }

    private void assertEqual(Object expected, Object actual, String script) {
	    if (expected != null)
			assertEquals(expected.getClass(), actual.getClass());
		else
			assertNull(script + " is expected to evaluate as null, but was of type " + BeanUtil.simpleClassName(actual), actual);
		assertEquals(expected, actual);
    }
    
}
