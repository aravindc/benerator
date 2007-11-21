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
import org.databene.benerator.main.Benerator;

import java.io.IOException;

/**
 * TODO documentation.<br/>
 * <br/>
 * Created: 20.11.2007 13:24:13
 */
public class ShopTest extends TestCase {

    public void testDB2() throws IOException {
        perform("shop/populate_db.db2.xml");
    }

    public void testDerby() throws IOException {
        perform("shop/populate_db.derby.xml");
    }

    public void testHSQL() throws IOException {
        perform("shop/populate_db.hsql.xml");
    }

    public void testSQLServer() throws IOException {
        perform("shop/populate_db.ms_sql_server.xml");
    }

    public void testMySQL() throws IOException {
        perform("shop/populate_db.mysql.xml");
    }

    public void testOracle() throws IOException {
        perform("shop/populate_db.oracle.xml");
    }

    public void testPostgres() throws IOException {
        perform("shop/populate_db.postgres.xml");
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void perform(String uri) throws IOException {
        Benerator.main(new String[] {uri});
    }
}
