/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive.datetime;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.ResourceManagerSupport;
import org.databene.benerator.engine.Statement;
import org.databene.benerator.engine.parser.xml.GenerateOrIterateParser;
import org.databene.benerator.test.ConsumerMock;
import org.databene.commons.TimeUtil;
import org.databene.commons.xml.XMLUtil;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the correct interaction of XML parser, 
 * Benerator engine and {@link DateTimeGenerator}.<br/><br/>
 * Created: 04.05.2010 06:13:08
 * @since 0.6.1
 * @author Volker Bergmann
 */
public class DateTimeIntegrationTest {

	private static final Date MIN_DATE = TimeUtil.date(2008, 8, 29);
	private static final Date MAX_DATE = TimeUtil.date(2008, 9,  3);
	private static final int INDIVIDUAL_DATE_COUNT = 4;
	
	private BeneratorContext context;
	private ConsumerMock<Object[]> consumer;
	
	@Before
	public void setUp() {
		context = new BeneratorContext();
		consumer = new ConsumerMock<Object[]>(true);
		context.set("cons", consumer);
	}

	
	
	// test methods ----------------------------------------------------------------------------------------------------
	
	@Test
	public void testDateWithMinMaxAndPrecision() {
		// create DateTimeGenerator from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='500' consumer='cons'>" +
        	"  <value type='date' min='2008-09-29' max='2008-10-02' precision='0000-00-01'/>" +
        	"</generate>");
		List<Object[]> products = consumer.getProducts();
		HashSet<Date> usedDates = new HashSet<Date>();
		for (Object[] product : products) {
			Date date = (Date) product[0];
			assertFalse(date.before(MIN_DATE));
			assertFalse(date.after(MAX_DATE));
			usedDates.add(date);
		}
		assertEquals(INDIVIDUAL_DATE_COUNT, usedDates.size());
	}

	@Test
	public void testDateWithMinAndMax() {
		// create DateTimeGenerator from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='500' consumer='cons'>" +
        	"  <value type='date' min='2008-09-29' max='2008-10-02' />" +
        	"</generate>");
		List<Object[]> products = consumer.getProducts();
		HashSet<Date> usedDates = new HashSet<Date>();
		for (Object[] product : products) {
			Date date = (Date) product[0];
			assertFalse(date.before(MIN_DATE));
			assertFalse(date.after(MAX_DATE));
			usedDates.add(date);
		}
		assertEquals(INDIVIDUAL_DATE_COUNT, usedDates.size());
	}
	
	@Test
	public void testDateWithMin() {
		// create DateTimeGenerator from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='500' consumer='cons'>" +
        	"  <value type='date' min='2008-09-29' />" +
        	"</generate>");
		List<Object[]> products = consumer.getProducts();
		HashSet<Date> usedDates = new HashSet<Date>();
		for (Object[] product : products) {
			Date date = (Date) product[0];
			assertFalse(date.before(MIN_DATE));
			usedDates.add(date);
		}
		assertTrue(usedDates.size() > 10);
	}
	
	@Test
	public void testDateWithMax() {
		// create DateTimeGenerator from XML descriptor
		parseAndExecute(
			"<generate type='entity' count='500' consumer='cons'>" +
        	"  <value type='date' max='2008-10-02' />" +
        	"</generate>");
		List<Object[]> products = consumer.getProducts();
		HashSet<Date> usedDates = new HashSet<Date>();
		for (Object[] product : products) {
			Date date = (Date) product[0];
			assertFalse(date.after(MAX_DATE));
			usedDates.add(date);
		}
		assertTrue(usedDates.size() > 10);
	}

	
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private void parseAndExecute(String xml) {
	    Element element = XMLUtil.parseStringAsElement(xml);
		ResourceManagerSupport resourceManager = new ResourceManagerSupport();
		Statement statement = new GenerateOrIterateParser().parse(element, new Statement[0], resourceManager);
		statement.execute(context);
    }
	
}
