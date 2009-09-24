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

package org.databene.benerator.parser;

import java.util.BitSet;

import org.databene.commons.ArrayBuilder;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Context;
import org.databene.commons.Expression;
import org.databene.commons.ParseException;
import org.databene.commons.StringCharacterIterator;
import org.databene.commons.StringUtil;
import org.databene.commons.bean.ClassProvider;
import org.databene.commons.converter.LiteralParser;

/**
 * Provides basic parsing facilities.<br/>
 * <br/>
 * Created at 27.12.2008 07:11:23
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class BasicParser {
	
	private static final Object[] EMPTY_ARRAY = { };

	private static final Object NOT_AN_ELEMENT = new Object();
	
	private BitSet nameCharacters;
	
	public BasicParser() {
		initNameCharacters();
	}

	public void addNameCharacter(char c) {
		nameCharacters.set(c);
	}

	public Invocation parseInvocation(String code, Context context) throws ParseException {
		StringCharacterIterator iterator = new StringCharacterIterator(code);
		String fqName = parseFullyQualifiedName(iterator);
		iterator.skipWhitespace();
		Object[] params = parseInvocationParameters(iterator);
		params = preprocessParams(params, context);
		return new Invocation(fqName, params);
	}
	
	public Object resolveConstructionOrReference(String text, ClassProvider classProvider, Context context) throws ParseException {
		return resolveConstructionOrReference(new StringCharacterIterator(text), classProvider, context);
	}

	public Object resolveConstructionOrReference(StringCharacterIterator iterator, ClassProvider classProvider, Context context) 
			throws ParseException {
		Expression<?> expression = parseConstructionOrReference(iterator, classProvider, context);
		return expression.evaluate(context);
	}

	public Expression<?> parseConstructionOrReference(
			StringCharacterIterator iterator, ClassProvider classProvider, Context context) throws ParseException {
		
		// parse fully qualified name
		String classNameOrRef = parseFullyQualifiedName(iterator);
		iterator.skipWhitespace();

		// check if it's a reference
		if (context != null) {
			Object ref = (context.get(classNameOrRef));
			if (ref != null)
				return new Reference(classNameOrRef, context);
		}

		// now it must be a class - parse construction details
		return parseConstructionDetails(iterator, classNameOrRef, classProvider, context);
	}

	public Construction parseConstruction(
			String code, ClassProvider classProvider, Context context) throws ParseException {
		StringCharacterIterator iterator = new StringCharacterIterator(code);
		String className = parseFullyQualifiedName(iterator);
		iterator.skipWhitespace();
		return parseConstructionDetails(iterator, className, classProvider, context);
	}

	private Construction parseConstructionDetails(
			StringCharacterIterator iterator, String className, ClassProvider classProvider, Context context) {
		if (!iterator.hasNext() || iterator.peekNext() == ',')
			return new Construction(className, classProvider);

		// find out specific construction type
		char next = iterator.peekNext();
		if (next == '(') {
			Object[] params = parseConstructorParams(iterator, context);
			if (params.length > 0)
				return new ParametrizedConstruction(className, classProvider, params);
			else
				return new Construction(className, classProvider);
		} else if (next == '[')
			return parseBeanProperties(className, iterator, classProvider);
		else
			throw new ParseException("Unexpected character: " + next, 1,
					iterator.index());
	}

	private BeanPropertyConstruction parseBeanProperties(
			String className, StringCharacterIterator iterator, ClassProvider classProvider) {
		
		BeanPropertyConstruction construction = new BeanPropertyConstruction(className, classProvider);
		
		// parse properties
		iterator.assertNext('[');
		Object[] properties = parseCommaSeparatedList(iterator, ',', ']');
		iterator.assertNext(']');
		
		// set properties
		for (Object prop : properties) {
			String propertyDef = (String) prop;
			int eq = propertyDef.indexOf('=');
			if (eq < 0)
				throw new ConfigurationError("Assignment missing for property definition: " + propertyDef);
			String propertyName = propertyDef.substring(0, eq).trim();
			Object propertyValue = unquote(propertyDef.substring(eq + 1).trim());
			construction.addProperty(propertyName, propertyValue);
		}
		
		// done!
		return construction;
	}

	private Object[] parseConstructorParams(StringCharacterIterator iterator, Context context) {
		Object[] parameters = parseInvocationParameters(iterator);
		return preprocessParams(parameters, context);
	}
/*
	public Object resolveConstruction(String text, ClassProvider classProvider) throws ParseException {
		return resolveConstruction(new StringCharacterIterator(text), classProvider);
	}
	
	public Object resolveConstruction(StringCharacterIterator iterator, ClassProvider classProvider) throws ParseException {
		return resolveConstructionOrReference(iterator, classProvider, null);
	}
*/
	public String parseFullyQualifiedName(StringCharacterIterator iterator) {
		int start = iterator.index();
		boolean done = false;
		do {
			String namePart = parseName(iterator);
			done = StringUtil.isEmpty(namePart);
			if (!done)
				done = !(iterator.peekNext() == '.');
			if (!done)
				iterator.next();
		} while (!done);
		return iterator.parsedSubstring(start);
	}

	public String parseName(StringCharacterIterator iterator) {
		int start = iterator.index();
		while (iterator.hasNext() && isNameCharacter(iterator.peekNext()))
			iterator.next();
		return iterator.parsedSubstring(start);
	}

	public boolean isNameCharacter(char c) {
		if (c >= 256)
			return false;
		return nameCharacters.get(c);
	}

	/**
	 * parses a comma-separated list, supporting quotes (", ') and escaping (\', \", \\, \t, \r, \n).
	 * each token in the list is interpreted as a literal and returned as corresponding object, if possible. 
	 * Escapes are resolved, quotes remain in the returned String.
	 * @param text the comma-separated string to parse
	 * @return an Object array of the list content, parsed as literals, e.g. Number, Date, String.
	 */
	public static Object[] parseSeparatedList(String text, char separator) {
		text = StringUtil.trim(text);
		if (StringUtil.isEmpty(text))
			return EMPTY_ARRAY;
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		return parseCommaSeparatedList(iterator, separator, (char) 0);
	}

	// private helpers -------------------------------------------------------------------------------------------------

	private void initNameCharacters() {
		this.nameCharacters = new BitSet(256);
		for (char c = 'A'; c <= 'Z'; c++)
			nameCharacters.set(c);
		for (char c = 'a'; c <= 'z'; c++)
			nameCharacters.set(c);
		for (char c = '0'; c <= '9'; c++)
			nameCharacters.set(c);
		nameCharacters.set('_');
		nameCharacters.set('$');
	}
	
	private static Object[] parseCommaSeparatedList(StringCharacterIterator iterator, char separator, char rightParenthesis) {
		ArrayBuilder<Object> builder = new ArrayBuilder<Object>(Object.class);
		int start = iterator.index();
		int lastNonWs = start;
		char quoteMode = leadingQuote(iterator);
		boolean escapeOccurred = false;
		boolean escapeMode = false;
		while (iterator.hasNext()) {
			char c = iterator.next();
			if ((c == separator || c == rightParenthesis) && quoteMode == 0 && !escapeMode) {
				Object element = parseListElement(iterator, start, lastNonWs, escapeOccurred, escapeMode);
				if (!NOT_AN_ELEMENT.equals(element))
					builder.add(element);
				iterator.skipWhitespace();
				start = iterator.index();
				escapeOccurred = false;
				if (c == rightParenthesis) {
					iterator.pushBack();
					break;
				} else
					continue;
			}
			
			if (c == '\\' && !escapeMode)
				escapeMode = escapeOccurred = true;
			else if (escapeMode)
				escapeMode = false;
			else if (isQuote(c)) {
				if (quoteMode == c)
					quoteMode = 0;
				else if (quoteMode == 0)
					quoteMode = c;
			}
			if (!Character.isWhitespace(c))
				lastNonWs = iterator.index();
		}
		if (start < iterator.index()) {
			Object element = parseListElement(iterator, start, lastNonWs, escapeOccurred, escapeMode);
			if (!NOT_AN_ELEMENT.equals(element))
				builder.add(element);
		}
		return builder.toArray();
	}
/*	
	private <T extends Object> T resolveConstructorCall(Class<T> type, StringCharacterIterator iterator) {
		Object[] parameters = parseInvocationParameters(iterator);
		parameters = preprocessParams(parameters);
		return BeanUtil.newInstance(type, parameters);
	}
*/
	private Object[] parseInvocationParameters(StringCharacterIterator iterator) {
		iterator.assertNext('(');
		Object[] parsedParameters = parseCommaSeparatedList(iterator, ',', ')');
		iterator.assertNext(')');
		return parsedParameters;
	}

	private Object[] preprocessParams(Object[] parsedParams, Context context) {
		Object[] result = new Object[parsedParams.length];
		for (int i = 0; i < parsedParams.length; i++) {
			Object param = parsedParams[i];
			if (param instanceof String) {
				String s = (String) param;
				if (isQuoted(s))
					param = unquote(s);
				else if (context.contains(s))
					param = context.get(s);
				else
					param = s;
			}
			result[i] = param;
		}
		return result;
	}

	private Object unquote(String value) {
		return (isQuoted(value) ? value.substring(1, value.length() - 1) : value);
	}

	private boolean isQuoted(String s) {
	    return (s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""));
    }

	private static Object parseListElement(StringCharacterIterator iterator, int from, int to, 
			boolean escapeOccurred, boolean escapeModeActive) {
		if (escapeModeActive)
			throw new IllegalStateException("Token ended in escape mode");
		if (from == to)
			return NOT_AN_ELEMENT;
		Object result = LiteralParser.parse(iterator.substring(from, to));
		if (result instanceof String) {
			result = ((String) result).trim();
			if (((String) result).length() == 0)
				return NOT_AN_ELEMENT;
			if (escapeOccurred)
				result = StringUtil.unescape((String) result);
		}
		return result;
	}

	private static char leadingQuote(StringCharacterIterator iterator) {
		if (!iterator.hasNext())
			return 0;
		char c = iterator.next();
		if (isQuote(c))
			return c;
		else {
			iterator.pushBack();
			return 0;
		}
	}

	private static boolean isQuote(char c) {
		return (c == '"' || c == '\'');
	}

	public static Object parseLiteral(String text) {
		return LiteralParser.parse(text);
	}

}
