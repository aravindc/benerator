/*
 * (c) Copyright 2013 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.databene.benerator.test.BeneratorIntegrationTest;
import org.databene.benerator.test.ConsumerMock;
import org.databene.commons.LocaleUtil;
import org.databene.commons.TimeUtil;
import org.databene.model.data.Entity;
import org.junit.Test;

/**
 * Testing XLS imports.<br/><br/>
 * Created: 24.01.2013 15:46:57
 * @since 0.8.0
 * @author Volker Bergmann
 */
public class XLSIntegrationTest extends BeneratorIntegrationTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testDefault() throws Exception {
		ConsumerMock con = new ConsumerMock(true);
		context.set("con", con);
		parseAndExecute("<iterate type='dummy' source='org/databene/benerator/engine/xls/types.xls' consumer='con'/>");
		List<Entity> products = (List<Entity>) con.getProducts();
		assertEquals(1, products.size());
		assertTypesValues("Alice", 123L, TimeUtil.date(2008, 11, 31), TimeUtil.date(2008, 11, 31, 13, 45, 0, 0), products.get(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFormatted() throws Exception {
		ConsumerMock con = new ConsumerMock(true);
		context.set("con", con);
		LocaleUtil.runInLocale(Locale.US, new Runnable() {
			public void run() {
				parseAndExecute("<iterate type='dummy' source='org/databene/benerator/engine/xls/types.xls' format='formatted' consumer='con'/>");
			}});
		List<Entity> products = (List<Entity>) con.getProducts();
		assertEquals(1, products.size());
		assertTypesValues("Alice", "123", "2008-Dec-31", "13:45", products.get(0));
	}
	
	
	
	// private helper methods ------------------------------------------------------------------------------------------
	
	private static void assertTypesValues(Object name, Object number, Object date, Object time, Entity entity) {
		assertEquals(name, entity.get("name"));
		assertEquals(number, entity.get("number"));
		assertEquals(date, entity.get("a_date"));
		assertEquals(time, entity.get("a_time"));
	}
	
}
