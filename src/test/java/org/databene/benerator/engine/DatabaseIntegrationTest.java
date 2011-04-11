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

package org.databene.benerator.engine;

import static org.junit.Assert.*;

import java.util.List;

import org.databene.benerator.test.ConsumerMock;
import org.databene.jdbacl.hsql.HSQLUtil;
import org.databene.model.data.DataModel;
import org.databene.model.data.Entity;
import org.databene.platform.db.DBSystem;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for Benerator's database support.<br/><br/>
 * Created: 24.05.2010 17:52:58
 * @since 0.6.2
 * @author Volker Bergmann
 */
public class DatabaseIntegrationTest extends BeneratorIntegrationTest {

	private DBSystem db; 
	private ConsumerMock<Entity> consumer;
	
	
	
	@Before
	public void setUp() throws Exception {
		consumer = new ConsumerMock<Entity>(true);
		context.set("cons", consumer);
		String dbUrl = HSQLUtil.getInMemoryURL(getClass().getSimpleName());
		db = new DBSystem("db", dbUrl, HSQLUtil.DRIVER, 
				HSQLUtil.DEFAULT_USER, HSQLUtil.DEFAULT_PASSWORD);
		db.setSchema("PUBLIC");
		db.execute("drop table referer if exists");
		db.execute("drop table referee if exists");
		db.execute("create table referee (id int, n int, primary key (id))");
		db.execute("insert into referee (id, n) values (2, 2)");
		db.execute("insert into referee (id, n) values (3, 3)");
		db.execute(
				"create table referer ( " +
				"	id int," +
				"	referee_id int," +
				"	primary key (id)," +
				"   constraint referee_fk foreign key (referee_id) references referee (id))");
		context.set("db", db);
		DataModel.getDefaultInstance().addDescriptorProvider(db);
	}

	
	
	// test methods ----------------------------------------------------------------------------------------------------

	@Test
	public void testScriptResolution() {
		context.set("tblName", "referee");
		parseAndExecute("<evaluate id='refCount' target='db'>{'select count(*) from ' + tblName}</evaluate>");
		assertEquals(2, ((Number) context.get("refCount")).intValue());
	}

	@Test
	public void testDbRef_default_nullable() {
		parseAndExecute("<generate type='referer' count='3' consumer='cons'/>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertNull(product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_default_not_null_defaultOneToOne() {
		context.setDefaultOneToOne(true);
		parseAndExecute(
				"<generate type='referer' consumer='cons'>" +
	        	"  <reference name='referee_id' nullable='false' source='db' />" + // TODO should source='db' be optional?
	        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(2, products.size());
		for (Entity product : products) {
			int ref = (Integer) product.get("referee_id");
			assertTrue(ref == 2 || ref == 3);
		}
	}

	@Test
	public void testDbRef_default_not_null_defaultOneToMany() {
		context.setDefaultOneToOne(false);
		parseAndExecute(
				"<generate type='referer' count='3' consumer='cons'>" +
	        	"  <reference name='referee_id' nullable='false' source='db' />" + // TODO should source='db' be optional?
	        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			int ref = (Integer) product.get("referee_id");
			assertTrue(ref == 2 || ref == 3);
		}
	}

	
	/** Test for bug #3025805 */
	@Test
	public void testDbRef_distribution() {
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <reference name='referee_id' targetType='referee' source='db' distribution='new org.databene.benerator.distribution.function.ExponentialFunction(-0.5)' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			int ref = (Integer) product.get("referee_id");
			assertTrue(ref == 2 || ref == 3);
		}
	}

	@Test
	public void testDbRef_values() {
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <reference name='referee_id' values='1' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertEquals(1, product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_constant() {
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <reference name='referee_id' constant='1' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertEquals(1, product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_constant_script() {
		context.set("rid", 2);
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <reference name='referee_id' constant='{rid}' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertEquals(2, product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_attribute_constant_script() {
		context.set("rid", 2);
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <attribute name='referee_id' constant='{rid}' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertEquals(2, product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_script() {
		context.set("rid", 2);
		parseAndExecute(
			"<generate type='referer' count='3' consumer='cons'>" +
        	"  <reference name='referee_id' script='rid + 1' />" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		for (Entity product : products) {
			assertEquals(3, product.get("referee_id"));
		}
	}

	@Test
	public void testDbRef_explicit_selector() {
		context.set("key", 2);
		parseAndExecute(
			"<generate type='referer' consumer='cons'>" +
        	"  <reference name='referee_id' source='db' " +
        	"	  selector=\"{ftl:select id from referee where id=${key}}\" " +
        	"     nullable='false'/>" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(1, products.size());
		assertEquals(2, products.get(0).get("referee_id"));
	}

	@Test
	public void testDbRef_explicit_subSelector() {
		context.set("key", 2);
		parseAndExecute(
			"<generate type='referer' consumer='cons' count='3'>" +
        	"  <reference name='referee_id' source='db' " +
        	"	  subSelector='{ftl:select id from referee order by id}' " +
        	"     nullable='false'/>" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(3, products.size());
		assertEquals(2, products.get(0).get("referee_id"));
		assertEquals(2, products.get(1).get("referee_id"));
		assertEquals(2, products.get(2).get("referee_id"));
	}

	@Test
	public void testDbRef_entity_selector() {
		context.set("key", 2);
		parseAndExecute(
			"<generate type='referer' consumer='cons'>" +
        	"  <reference name='referee_id' source='db' " +
        	"	  selector='{ftl:id=${key}}' " +
        	"     nullable='false'/>" +
        	"</generate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(1, products.size());
		assertEquals(2, products.get(0).get("referee_id"));
	}

	@Test
	public void testDb_iterate_this() {
		parseAndExecute(
			"<iterate type='referee' source='db' consumer='cons'>" +
        	"  <attribute name='n' source='db' " +
        	"	  selector=\"{{'select n+1 from referee where id = ' + this.id}}\" cyclic='true' />" +
        	"</iterate>");
		List<Entity> products = consumer.getProducts();
		assertEquals(2, products.size());
		assertEquals(3, products.get(0).get("n"));
		assertEquals(4, products.get(1).get("n"));
	}
	
}
