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

package org.databene.platform.db;

import static org.junit.Assert.assertEquals;

import org.databene.benerator.test.GeneratorTest;
import org.databene.commons.IOUtil;
import org.databene.jdbacl.hsql.HSQLUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link SequenceTableGenerator}.<br/><br/>
 * Created: 09.08.2010 14:51:40
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class SequenceTableGeneratorTest extends GeneratorTest {
	
	static DBSystem db;

	@BeforeClass
	public static void setupDB() {
	    db = new DBSystem("db", HSQLUtil.getInMemoryURL(SequenceTableGeneratorTest.class.getSimpleName()), HSQLUtil.DRIVER, "sa", null);
		db.execute("create table TT ( id int, value int )");
		db.execute("insert into TT (id, value) values (1, 1000)");
		db.execute("insert into TT (id, value) values (2, 2000)");
    }
	
	@Before
	public void setupTable() {
		db.execute("update TT set value = 1000 where id = 1");
		db.execute("update TT set value = 2000 where id = 2");
	}

	@AfterClass
	public static void closeDB() {
		IOUtil.close(db);
	}
	
	@AfterClass
	public static void tearDownDB() {
		db.execute("drop table TT");
	}
	
	@Test
	public void testStatic() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db);
	        generator.setSelector("id = 1");
	        generator.init(context);
	        for (int i = 0; i < 100; i++)
	        	assertEquals(1000 + i, generator.generate().intValue());
	        assertAvailable(generator);
        } finally {
	        IOUtil.close(generator);
        }
	}

	@Test
	public void testDynamicSelector() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db);
	        // the selector makes the generator use row #1 and #2 after each other for generating id values
	        generator.setSelector("{'id = ' + (1 + (num % 2))}");
	        generator.init(context);
	        for (int i = 0; i < 100;) {
		        context.set("num", i);
	        	assertEquals(1000 + i/2, generator.generate().intValue());
	        	i++;
		        context.set("num", i);
	        	assertEquals(2000 + i/2, generator.generate().intValue());
	        	i++;
	        }
	        assertAvailable(generator);
        } finally {
	        IOUtil.close(generator);
        }
	}

	@Test
	public void testParameterizedSelector() {
		SequenceTableGenerator<Integer> generator = null;
		try {
	        generator = new SequenceTableGenerator<Integer>("TT", "value", db, "id = ?");
	        generator.init(context);
        	assertEquals(1000, generator.generateWithParams(1).intValue());
        	assertEquals(2000, generator.generateWithParams(2).intValue());
        	assertEquals(1001, generator.generateWithParams(1).intValue());
        	assertEquals(2001, generator.generateWithParams(2).intValue());
        } finally {
	        IOUtil.close(generator);
        }
	}

}
