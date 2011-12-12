/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.*;

import org.databene.benerator.test.GeneratorTest;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.commons.IOUtil;
import org.databene.jdbacl.dialect.HSQLUtil;
import org.databene.model.data.DataModel;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link QueryGenerator}.<br/><br/>
 * Created: 09.08.2010 13:05:02
 * @since 0.6.4
 * @author Volker Bergmann
 */
public class QueryGeneratorTest extends GeneratorTest {
	
	static DBSystem db;

	@BeforeClass
	public static void setupDB() {
	    db = new DBSystem("db", HSQLUtil.getInMemoryURL(QueryGeneratorTest.class.getSimpleName()), HSQLUtil.DRIVER, "sa", null, new DataModel());
		db.execute("create table TT ( id int, value int )");
		db.execute("insert into TT (id, value) values (1, 1000)");
    }
	
	@Before
	public void setupTable() {
		db.execute("update TT set value = 1000 where id = 1");
	}

	@AfterClass
	public static void closeDB() {
		db.execute("drop table TT");
		IOUtil.close(db);
	}
	
	@Test
	public void testSimple() {
		QueryGenerator<Integer> generator = null;
		try {
	        generator = new QueryGenerator<Integer>("select value from TT", db, true);
	        generator.init(context);
	        assertEquals(1000, GeneratorUtil.generateNonNull(generator).intValue());
	        assertUnavailable(generator);

	        db.execute("update TT set value = 1001 where id = 1");
	        generator.reset();
	        assertEquals(1001, GeneratorUtil.generateNonNull(generator).intValue());
	        assertUnavailable(generator);
        } finally {
	        IOUtil.close(generator);
        }
	}

}
