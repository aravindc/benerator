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

import org.databene.platform.db.DBSystem;
import org.junit.Test;

/**
 * Tests the DB Sanity integration.<br/><br/>
 * Created: 29.11.2010 16:04:18
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class DBSanity4BeneratorIntegrationTest extends BeneratorIntegrationTest {

	private static final String ENVIRONMENT = "DBSanityIntegrationTest";
	private static String XML = "<dbsanity environment='" + ENVIRONMENT + "' />";
	
	@Test
	public void testSuccess() {
		context.setContextUri("target/test-classes/org/databene/benerator/engine");
		DBSystem db = new DBSystem("db", "jdbc:hsqldb:mem:DBSanityIntegrationTest", "org.hsqldb.jdbcDriver", "sa", null);
		db.execute("create table table1 (id int)");
		db.execute("insert into table1 (id) values (1)");
		context.set("db", db);
		parseAndExecute(XML);
	}
	
}
