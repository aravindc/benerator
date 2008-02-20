/*
 * (c) Copyright 2007 by Volker Bergmann. All rights reserved.
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

package shop;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.databene.benerator.main.Benerator;
import org.databene.commons.IOUtil;
import org.databene.platform.db.DBSystem;

import java.io.IOException;
import java.util.Properties;

/**
 * Tests the shop demo on all supported database systems.<br/>
 * <br/>
 * Created: 20.11.2007 13:24:13
 */
public class ShopTest extends TestCase {
    
    private static final String BENERATOR_FILE = "demo/shop/shop.ben.xml";

    private static final Log logger = LogFactory.getLog(ShopTest.class);

    public void testDB2() throws IOException, InterruptedException {
        checkGeneration("db2");
    }

    public void testDerby() throws IOException, InterruptedException {
        checkGeneration("derby");
    }

    public void testHSQL() throws IOException, InterruptedException {
        checkGeneration("hsql");
    }

    public void testSQLServer() throws IOException, InterruptedException {
        checkGeneration("ms_sql_server");
    }

    public void testMySQL() throws IOException, InterruptedException {
        checkGeneration("mysql");
    }

    public void testOracle() throws IOException, InterruptedException {
        checkGeneration("oracle");
    }

    public void testPostgres() throws IOException, InterruptedException {
        checkGeneration("postgres");
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void checkGeneration(String database) throws IOException, InterruptedException {
        checkGeneration(database, true);
        //checkGeneration(database, false);
    }

    private void checkGeneration(String database, boolean shell) throws IOException, InterruptedException {
        if (shell)
            runFromCommandLine(BENERATOR_FILE, database, "test");
        else
            runAsClass(BENERATOR_FILE, database, "test");
        Properties properties = IOUtil.readProperties("demo/shop/" + database + "/shop." + database + ".properties");
        DBSystem db = new DBSystem(
                "db", 
                properties.getProperty("db_uri"), 
                properties.getProperty("db_driver"), 
                properties.getProperty("db_user"), 
                properties.getProperty("db_password")
        );
        assertEquals(28, db.countEntities("db_category"));
        assertEquals(9,  db.countEntities("db_product"));
        assertEquals(9,  db.countEntities("db_user"));
        assertEquals(6,  db.countEntities("db_customer"));
        assertEquals(22, db.countEntities("db_order"));
        assertEquals(43, db.countEntities("db_order_item"));
    }

    private void runAsClass(String file, String database, String stage) throws IOException {
        System.setProperty("stage", stage);
        System.setProperty("database", database);
        Benerator.main(new String[] { file });
    }

    private void runFromCommandLine(String file, String database, String stage) throws IOException, InterruptedException {
        // TODO v0.4.2 make it run and properly check the result
        String command = "benerator -Ddatabase=" + database + " -Dstage=" + stage + " " + file;
        logger.debug(command);
        Process process = Runtime.getRuntime().exec(command);
        IOUtil.transfer(process.getInputStream(), System.out);
        process.waitFor();
        logger.debug(process.exitValue());
    }
}
