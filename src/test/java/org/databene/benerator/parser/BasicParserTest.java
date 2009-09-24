/*
 * (c) Copyright 2008, 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.parser;

import java.util.Arrays;

import org.databene.benerator.test.Person;
import org.databene.commons.ArrayFormat;
import org.databene.commons.Context;
import org.databene.commons.StringCharacterIterator;
import org.databene.commons.TimeUtil;
import org.databene.commons.bean.DefaultClassProvider;
import org.databene.commons.context.DefaultContext;

import junit.framework.TestCase;

/**
 * Tests the {@link BasicParser}.<br/>
 * <br/>
 * Created at 27.12.2008 07:38:43
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class BasicParserTest extends TestCase {
	
	BasicParser parser = new BasicParser();

	public void testParseName() {
		checkParseName("", "");
		checkParseName("Alice", "Alice");
		checkParseName("Alice", "Alice, Bob");
	}
	
	public void testParseFullyQualifiedName() {
		checkParseFullyQualifiedName("", "");
		checkParseFullyQualifiedName("Alice", "Alice");
		checkParseFullyQualifiedName("Alice", "Alice, Bob");
		checkParseFullyQualifiedName("com.my.Class", "com.my.Class");
		checkParseFullyQualifiedName("com.my.Class", "com.my.Class(bla bla bla)");
	}
	
	/**
	 * Tests context reference
	 */
	public void testParseReference() throws Exception {
		Context context = new DefaultContext();
		context.set("ref", "object");
		String object = (String) parser.resolveConstructionOrReference("ref", new DefaultClassProvider(), context);
		assertEquals("object", object);
	}
	
	/**
	 * Test invocation
	 */
	public void testParseInvocation() throws Exception {
		Invocation expected = new Invocation("action.execute", "Alice", TimeUtil.date(1972, 1, 3), 102, true, "A");
		Invocation result = parser.parseInvocation("action.execute('Alice', 1972-02-03, 102, true, 'A')", new DefaultContext());
		assertEquals(expected, result);
	}
	
	/**
	 * Tests default constructor instantiation
	 */
	public void testParseDefaultConstruction() throws Exception {
		Person person = (Person) parser.resolveConstructionOrReference("org.databene.benerator.test.Person", 
				new DefaultClassProvider(), null);
		assertEquals(new Person(), person);
		person = (Person) parser.resolveConstructionOrReference("org.databene.benerator.test.Person()", 
				new DefaultClassProvider(), null);
		person = (Person) parser.resolveConstructionOrReference("org.databene.benerator.test.Person( )", 
				new DefaultClassProvider(), null);
		assertEquals(new Person(), person);
	}
	
	/**
	 * Test custom constructor instantiation
	 */
	public void testParseCustomConstruction() throws Exception {
		Person person = (Person) parser.resolveConstructionOrReference(
				"org.databene.benerator.test.Person('Alice', 1972-02-03, 102, true, 'A')", 
				new DefaultClassProvider(), 
				null
			);
		Person expected = new Person("Alice", TimeUtil.date(1972, 1, 3), 102, true, 'A');
		assertEquals(expected, person);
	}
	
	/**
	 * Tests property-based construction
	 */
	public void testParsePropertyConstruction() throws Exception {
		Person person = (Person) parser.resolveConstructionOrReference(
				"org.databene.benerator.test.Person[name='Alice', birthDate=1972-02-03, score=102, registered=true, rank='A']", 
				new DefaultClassProvider(),
				null
			);
		Person expected = new Person("Alice", TimeUtil.date(1972, 1, 3), 102, true, 'A');
		assertEquals(expected, person);
	}
	
	public void testParseSeparatedList() {
		checkParseList(null);
		checkParseList("");
		checkParseList("0", 0);
		checkParseList("0,1", 0, 1);
		checkParseList(" 0 , 1 ", 0, 1);
		checkParseList(" Alice , Bob ", "Alice", "Bob");
		checkParseList(" \"Alice\" , 'Bob' ", "\"Alice\"", "'Bob'");
		checkParseList(" 'Alice, Bob' ", "'Alice, Bob'");
		checkParseList(" prefix + ',' + postfix ", "prefix + ',' + postfix");
		checkParseList(" \"Alice's Bob, Charly and Doris\" ", "\"Alice's Bob, Charly and Doris\"");
		checkParseList("A\\\\B", "A\\B");
		checkParseList(" 'Alice\\'s Bob, Charly and Doris' ", "'Alice's Bob, Charly and Doris'");
	}

	// helpers ---------------------------------------------------------------------------------------------------------
	
	private void checkParseName(String out, String in) {
		assertEquals(out, parser.parseName(new StringCharacterIterator(in)));
	}

	private void checkParseFullyQualifiedName(String out, String in) {
		assertEquals(out, parser.parseFullyQualifiedName(new StringCharacterIterator(in)));
	}

	private void checkParseList(String listString, Object... expected) {
		Object[] result = BasicParser.parseSeparatedList(listString, ',');
		assertTrue("Expected " + ArrayFormat.format(expected) 
					+ " - found: " + ArrayFormat.format(result), 
				Arrays.equals(expected, result));
	}
}
